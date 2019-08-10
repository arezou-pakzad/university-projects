#include <arpa/inet.h>
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <pthread.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <unistd.h>

#include "libhttp.h"
#include "wq.h"

#define MAX_LENGTH  1024
#define MAX_DATA    10000

int *sema;
wq_t work_queue;
int num_threads;
int server_port;
char *server_files_directory;
char *server_proxy_hostname;
int server_proxy_port;

typedef struct thread_argument{
    int tid;
    void (*function)(int, int);
} thread_argument;

typedef struct route_argument{
    int input_socket;
    int output_socket;
    int tid;
} route_argument;

void http_not_found_response(int fd) {
  http_start_response(fd, 404);
  http_send_header(fd, "Content-Type", "text/html");
  http_send_header(fd, "Server", "httpserver/1.0");
  http_end_headers(fd);
  http_send_string(fd,
                   "<center>"
                   "<h1>404 Not Found</h1>"
                   "</center>");
}

void read_and_send_data(int fd, int file_descriptor) {
  char data[MAX_DATA];
  ssize_t data_size;
  while ((data_size = read(file_descriptor, data, MAX_DATA - 1)) > 0) {
    http_send_data(fd, data, (size_t)data_size);
  }
}

void http_send_file(int fd, char *file_path, int file_descriptor, struct stat *file_buffer) {
  char content_length[20];
  sprintf(content_length, "%lu", (long unsigned int)file_buffer->st_size);
  http_start_response(fd, 200);
  http_send_header(fd, "Content-Type", http_get_mime_type(file_path));
  http_send_header(fd, "Content-Length", content_length);
  http_end_headers(fd);
  read_and_send_data(fd, file_descriptor);
}

int http_send_index(int fd, char *file_path) {
  int file_descriptor;
  char file_index[MAX_LENGTH];
  strcpy(file_index, file_path);
  strcat(file_index, "/index.html");
  struct stat stat1;
  stat(file_index, &stat1);
  if ((file_descriptor = open(file_index, O_RDONLY)) != -1)
    http_send_file(fd, file_index, file_descriptor, &stat1);
  return file_descriptor;
}

void http_send_directory(int fd, char *file_path) {
  DIR *dir;
  struct dirent *ent;
  char *link_path = (char*) malloc(MAX_LENGTH);
  char *link_str = (char*) malloc(MAX_LENGTH);
  struct stat file_stat;

  if ((dir = opendir(file_path)) != NULL) {
    http_start_response(fd, 200);
    http_send_header(fd, "Content-Type", "text/html");
    http_end_headers(fd);
    http_send_string(fd, "<!DOCTYPE html>\n<html>\n<head>\n</head>\n<body>\n");
    http_send_string(fd, "<a href=\"../\">Parent directory</a><br />\n");

    while ((ent = readdir (dir)) != NULL) {
      strcpy(link_path, file_path);
      strcat(link_path, "/");
      strcat(link_path, ent->d_name);
      stat(link_path, &file_stat);
      if (S_ISDIR(file_stat.st_mode) != 0){
          sprintf(link_str, "<a href=\"%s/\"><p>%s</p></a><br />\n", ent->d_name, ent->d_name);
          http_send_string(fd, link_str);
      }
      else if (S_ISREG(file_stat.st_mode) != 0){
        sprintf(link_str, "<a href=\"./%s\"><p>%s</p></a><br />\n", ent->d_name, ent->d_name);
        http_send_string(fd, link_str);
      }
      else {
        http_not_found_response(fd);
        free(link_path);
        free(link_str);
        return;
      }
    }
    http_send_string(fd, "</body>\n</html>");

    closedir (dir);
  } else {
    http_not_found_response(fd);
  }
  free(link_path);
  free(link_str);
}

void handle_files_request(int fd, int tid) {

  struct http_request *request = http_request_parse(fd);

  if (request == NULL){
    http_not_found_response(fd);
    return;
  }

  char request_dir[MAX_LENGTH];
  strcpy(request_dir, server_files_directory);
  strcat(request_dir, request->path);

  struct stat file_buffer;
  int file_descriptor;

  if (stat(request_dir, &file_buffer) == 0) {
    if (S_ISREG(file_buffer.st_mode) != 0) {
      file_descriptor = open(request_dir, O_RDONLY);
      if (file_descriptor != -1)
        http_send_file(fd, request_dir, file_descriptor, &file_buffer);
    } else if (S_ISDIR(file_buffer.st_mode) != 0) {
      if (http_send_index(fd, request_dir) < 0)
        http_send_directory(fd, request_dir);
    } else {
      http_not_found_response(fd);
    }
  } else {
    http_not_found_response(fd);
  }

}

void *route_service(void * args){
  route_argument *value = (route_argument *)args;
  int input_socket = value->input_socket;
  int output_socket = value->output_socket;
  int tid = value->tid;

  char data[MAX_DATA];
  ssize_t length;

  while (sema[tid] == 0 && (length = (ssize_t) read(input_socket, data, sizeof(data) - 1)) > 0){
    http_send_data(output_socket, data, (size_t)length);
  }

  sema[tid]++;
  return NULL;
}

void handle_proxy_request(int fd, int tid) {

  /*
  * The code below does a DNS lookup of server_proxy_hostname and
  * opens a connection to it. Please do not modify.
  */

  struct sockaddr_in target_address;
  memset(&target_address, 0, sizeof(target_address));
  target_address.sin_family = AF_INET;
  target_address.sin_port = htons(server_proxy_port);

  struct hostent *target_dns_entry = gethostbyname2(server_proxy_hostname, AF_INET);

  int client_socket_fd = socket(PF_INET, SOCK_STREAM, 0);
  if (client_socket_fd == -1) {
    fprintf(stderr, "Failed to create a new socket: error %d: %s\n", errno, strerror(errno));
    exit(errno);
  }

  if (target_dns_entry == NULL) {
    fprintf(stderr, "Cannot find host: %s\n", server_proxy_hostname);
    exit(ENXIO);
  }

  char *dns_address = target_dns_entry->h_addr_list[0];

  memcpy(&target_address.sin_addr, dns_address, sizeof(target_address.sin_addr));
  int connection_status = connect(client_socket_fd, (struct sockaddr*) &target_address,
                                  sizeof(target_address));

  if (connection_status < 0) {
    /* Dummy request parsing, just to be compliant. */
    http_request_parse(fd);

    http_start_response(fd, 502);
    http_send_header(fd, "Content-Type", "text/html");
    http_end_headers(fd);
    http_send_string(fd, "<center><h1>502 Bad Gateway</h1><hr></center>");
    return;

  }

  pthread_t forward_route;
  pthread_t backward_route;


  route_argument *forward_arg = malloc(sizeof(route_argument));
  route_argument *backward_arg = malloc(sizeof(route_argument));

  forward_arg->input_socket = fd;
  forward_arg->output_socket = client_socket_fd;
  forward_arg->tid = tid;

  backward_arg->input_socket = client_socket_fd;
  backward_arg->output_socket = fd;
  backward_arg->tid = tid;

  pthread_attr_t attributes;
  pthread_attr_init(&attributes);

  sema[tid] = 0;

  pthread_create(&forward_route, NULL, route_service, (void *)forward_arg);
  pthread_create(&backward_route, NULL, route_service, (void *)backward_arg);

  while (sema[tid] == 0);

  pthread_cancel(forward_route);
  pthread_cancel(backward_route);

  close(client_socket_fd);
  free(forward_arg);
  free(backward_arg);
}

void *thread_service(void * args) {
  thread_argument *value = (thread_argument *) args;
  int fd = 0;
  void (*request_handler)(int, int) = value->function;
  while(1){
    fd = wq_pop(&work_queue);
    request_handler(fd, value->tid);
    close(fd);
  }
  return NULL;
}

void init_thread_pool(int num_threads, void (*request_handler)(int, int)) {
  pthread_t thread_pool[num_threads];
  pthread_attr_t attributes;
  pthread_attr_init(&attributes);
  sema = calloc((size_t) num_threads, sizeof(int));
  int i = 0;
  for (i = 0; i < num_threads; i++){
    thread_argument *args;
    args = malloc(sizeof(thread_argument));
    args->function = request_handler;
    args->tid = i;
    pthread_create(&thread_pool[i], &attributes, thread_service, (void *)args);
  }

}

void serve_forever(int *socket_number, void (*request_handler)(int, int)) {

  struct sockaddr_in server_address, client_address;
  size_t client_address_length = sizeof(client_address);
  int client_socket_number;

  *socket_number = socket(PF_INET, SOCK_STREAM, 0);
  if (*socket_number == -1) {
    perror("Failed to create a new socket");
    exit(errno);
  }

  int socket_option = 1;
  if (setsockopt(*socket_number, SOL_SOCKET, SO_REUSEADDR, &socket_option,
                 sizeof(socket_option)) == -1) {
    perror("Failed to set socket options");
    exit(errno);
  }

  memset(&server_address, 0, sizeof(server_address));
  server_address.sin_family = AF_INET;
  server_address.sin_addr.s_addr = INADDR_ANY;
  server_address.sin_port = htons(server_port);

  if (bind(*socket_number, (struct sockaddr *) &server_address,
           sizeof(server_address)) == -1) {
    perror("Failed to bind on socket");
    exit(errno);
  }

  if (listen(*socket_number, 1024) == -1) {
    perror("Failed to listen on socket");
    exit(errno);
  }

  printf("Listening on port %d...\n", server_port);

  wq_init(&work_queue, num_threads);
  init_thread_pool(num_threads, request_handler);

  while (1) {
    client_socket_number = accept(*socket_number,
                                  (struct sockaddr *) &client_address,
                                  (socklen_t *) &client_address_length);
    if (client_socket_number < 0) {
      perror("Error accepting socket");
      continue;
    }

    printf("Accepted connection from %s on port %d\n",
           inet_ntoa(client_address.sin_addr),
           client_address.sin_port);

//    if (num_threads == 1){
//      request_handler(client_socket_number, 0);
//      close(client_socket_number);
//    } else {
//      wq_push(&work_queue, client_socket_number);
//    }

    wq_push(&work_queue, client_socket_number);
//    close(client_socket_number);

    printf("Accepted connection from %s on port %d\n",
           inet_ntoa(client_address.sin_addr),
           client_address.sin_port);
  }

  shutdown(*socket_number, SHUT_RDWR);
  close(*socket_number);
}

int server_fd;
void signal_callback_handler(int signum) {
  printf("Caught signal %d: %s\n", signum, strsignal(signum));
  printf("Closing socket %d\n", server_fd);
  if (close(server_fd) < 0) perror("Failed to close server_fd (ignoring)\n");
  exit(0);
}

char *USAGE =
        "Usage: ./httpserver --files www_directory/ --port 8000 [--num-threads 5]\n"
        "       ./httpserver --proxy inst.eecs.berkeley.edu:80 --port 8000 [--num-threads 5]\n";

void exit_with_usage() {
  fprintf(stderr, "%s", USAGE);
  exit(EXIT_SUCCESS);
}

int main(int argc, char **argv) {
  signal(SIGINT, signal_callback_handler);

  signal(SIGPIPE, SIG_IGN);

  /* Default settings */
  server_port = 8000;
  num_threads = 1;
  void (*request_handler)(int, int) = NULL;

  int i;
  for (i = 1; i < argc; i++) {
    if (strcmp("--files", argv[i]) == 0) {
      request_handler = handle_files_request;
      free(server_files_directory);
      server_files_directory = argv[++i];
      if (!server_files_directory) {
        fprintf(stderr, "Expected argument after --files\n");
        exit_with_usage();
      }
    } else if (strcmp("--proxy", argv[i]) == 0) {
      request_handler = handle_proxy_request;

      char *proxy_target = argv[++i];
      if (!proxy_target) {
        fprintf(stderr, "Expected argument after --proxy\n");
        exit_with_usage();
      }

      char *colon_pointer = strchr(proxy_target, ':');
      if (colon_pointer != NULL) {
        *colon_pointer = '\0';
        server_proxy_hostname = proxy_target;
        server_proxy_port = atoi(colon_pointer + 1);
      } else {
        server_proxy_hostname = proxy_target;
        server_proxy_port = 80;
      }
    } else if (strcmp("--port", argv[i]) == 0) {
      char *server_port_string = argv[++i];
      if (!server_port_string) {
        fprintf(stderr, "Expected argument after --port\n");
        exit_with_usage();
      }
      server_port = atoi(server_port_string);
    } else if (strcmp("--num-threads", argv[i]) == 0) {
      char *num_threads_str = argv[++i];
      if (!num_threads_str || (num_threads = atoi(num_threads_str)) < 1) {
        fprintf(stderr, "Expected positive integer after --num-threads\n");
        exit_with_usage();
      }
    } else if (strcmp("--help", argv[i]) == 0) {
      exit_with_usage();
    } else {
      fprintf(stderr, "Unrecognized option: %s\n", argv[i]);
      exit_with_usage();
    }
  }

  if (server_files_directory == NULL && server_proxy_hostname == NULL) {
    fprintf(stderr, "Please specify either \"--files [DIRECTORY]\" or \n"
                    "                      \"--proxy [HOSTNAME:PORT]\"\n");
    exit_with_usage();
  }

  serve_forever(&server_fd, request_handler);

  return EXIT_SUCCESS;
}
