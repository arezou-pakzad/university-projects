#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <semaphore.h>
#include <string.h>

#define COUNT 10

long synch_thread;
sem_t *synch_semaphore;
sem_t **semaphores;

int num_threads;
int* thread_id;
pthread_t *threads;

void* Thread_work(void* rank) {
  int i, j;
  for (i = 0; i < COUNT; i++) {
    int thread_id = *(int*) rank;
    printf("Thread %d passed iteration %d\n", thread_id, i);

    sem_wait(synch_semaphore);
    synch_thread++;
    sem_post(synch_semaphore);

    if (synch_thread == num_threads){
      synch_thread = 0;
      for (j = 0; j < num_threads; j++) {
        if (j != thread_id){
          sem_post(semaphores[j]);
        }
      }
    }
    else{
      sem_wait(semaphores[thread_id]);
    }
  }
  pthread_exit(NULL);
}

int main(void) {
  int i;

  printf("Enter number of threads: \n");
  scanf("%d", &num_threads);

  synch_thread = 0;

  thread_id = (int *) malloc(num_threads * sizeof(int));
  threads = malloc(num_threads * sizeof(pthread_t));


  semaphores = malloc(num_threads * sizeof(sem_t *));

  for (i = 0; i < num_threads; i++){
    thread_id[i] = i;

    char semaphore_name[25] = "semaphore";
    char semaphore_num[10];
    snprintf(semaphore_num, 10, "%d", i);
    strcat(semaphore_name, semaphore_num);
    sem_unlink(semaphore_name);

    semaphores[i] = sem_open(semaphore_name, O_CREAT|O_EXCL, S_IRWXU, 0);
  }

  char semaphore_name[20] = "synch_semaphore";
  sem_unlink(semaphore_name);
  synch_semaphore = sem_open(semaphore_name, O_CREAT|O_EXCL, S_IRWXU, 1);

  pthread_attr_t attr;
  pthread_attr_init(&attr);
  pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);

  for (i = 0; i < num_threads; i++){
    if (pthread_create(&threads[i], &attr, Thread_work, &(thread_id[i])) != 0)
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
  sem_close(synch_semaphore);
  pthread_exit(NULL);
}


