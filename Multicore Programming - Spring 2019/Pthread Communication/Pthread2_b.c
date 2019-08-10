#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

#define COUNT 10

long synch_thread;
pthread_mutex_t synch_thread_lock;
pthread_cond_t synch_thread_convar;
int num_threads;
int* thread_id;
pthread_t *threads;

void* Thread_work(void* rank) {
  int i;
  for (i = 0; i < COUNT; i++) {
    int thread_id = *(int*) rank;
    printf("Thread %d passed iteration %d\n", thread_id, i);

    pthread_mutex_lock(&synch_thread_lock);
    synch_thread++;
    if (synch_thread == num_threads) {
      synch_thread = 0;
      pthread_cond_broadcast(&synch_thread_convar);
    } else {
      pthread_cond_wait(&synch_thread_convar, &synch_thread_lock);
    }
    pthread_mutex_unlock(&synch_thread_lock);

  }
  pthread_exit(NULL);
}

int main(void) {
  int i;

  printf("Enter number of threads: \n");
  scanf("%d", &num_threads);

  pthread_mutex_init(&synch_thread_lock, NULL);
  pthread_cond_init(&synch_thread_convar, NULL);

  synch_thread = 0;

  thread_id = (int *) malloc(num_threads * sizeof(int));
  threads = malloc(num_threads * sizeof(pthread_t));


  for (i = 0; i < num_threads; i++){
    thread_id[i] = i;
  }

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
  pthread_mutex_destroy(&synch_thread_lock);
  pthread_cond_destroy(&synch_thread_convar);
  pthread_exit(NULL);
}



