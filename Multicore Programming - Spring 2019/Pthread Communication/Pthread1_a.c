#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

int* messages;
int* thread_id;
pthread_t *threads;
int num_threads;

void* send_message (void* rank) {
  int thread_id = *(int*) rank;
  int receiver_id;
  int sender_id;

  sender_id = (thread_id + num_threads - 1) % num_threads;
  receiver_id = (thread_id + 1) % num_threads;

  messages[receiver_id] = 1;

  if (messages[sender_id] != 1) {
    printf("%d: No Message from %d\n", thread_id, sender_id);
  } else {
    printf("Hello to %d from %d\n", thread_id, sender_id);
  }

  pthread_exit(NULL);
}

int main(void) {
  int i;

  printf("Enter number of threads: \n");
  scanf("%d", &num_threads);

  messages = (int *) calloc((size_t) num_threads, sizeof(int));
  thread_id = (int *) malloc(num_threads * sizeof(int));
  threads = malloc(num_threads * sizeof(pthread_t));

  for (i = 0; i < num_threads; i++)
    thread_id[i] = i;

  pthread_attr_t attr;
  pthread_attr_init(&attr);
  pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);

  for (i = 0; i < num_threads; i++){
    if (pthread_create(&threads[i], &attr, send_message, &(thread_id[i])) != 0)
      perror("pthread_create error in thread creation\n");
  }

  for (i = 0; i < num_threads; i++) {
    if (pthread_join(threads[i], NULL) != 0){
      perror("pthread_join error in thread joining\n");
    }
  }

  pthread_attr_destroy(&attr);
  pthread_exit(NULL);

}