#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <stdlib.h>
#include <string.h>

int* messages;
int* thread_id;
pthread_t *threads;
int num_threads;
sem_t **semaphores;

void* send_message (void* rank) {
  int thread_id = *(int*) rank;
  int receiver_id;
  int sender_id;

  sender_id = (thread_id + num_threads - 1) % num_threads;
  receiver_id = (thread_id + 1) % num_threads;

  messages[receiver_id] = 1;

  sem_post(semaphores[receiver_id]);

  sem_wait(semaphores[sender_id]);

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

  semaphores = malloc(num_threads * sizeof(sem_t));

  for (i = 0; i < num_threads; i++){
    thread_id[i] = i;

    char semaphore_name[25] = "semaphore";
    char semaphore_num[10];
    snprintf(semaphore_num, 10, "%d", i);
    strcat(semaphore_name, semaphore_num);
    sem_unlink(semaphore_name);

    semaphores[i] = sem_open(semaphore_name, O_CREAT|O_EXCL, S_IRWXU, 0);
  }

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
  for (i = 0; i < num_threads; i++) {
    sem_close(semaphores[i]);
  }
  pthread_exit(NULL);

}