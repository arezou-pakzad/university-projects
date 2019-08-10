#include <stdlib.h>
#include "wq.h"
#include "utlist.h"

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t not_empty_cond = PTHREAD_COND_INITIALIZER;
pthread_cond_t not_full_cond = PTHREAD_COND_INITIALIZER;

/* Initializes a work queue WQ. */
void wq_init(wq_t *wq, int num_threads) {

  pthread_mutex_lock(&mutex);
  wq->size = 0;
  wq->head = NULL;
  wq->max_size = num_threads;
  pthread_mutex_unlock(&mutex);
}

/* Remove an item from the WQ. This function should block until there
 * is at least one item on the queue. */
int wq_pop(wq_t *wq) {

  pthread_mutex_lock(&mutex);
  while (wq->size <= 0)
    pthread_cond_wait(&not_empty_cond, &mutex);

  wq_item_t *wq_item = wq->head;
  int client_socket_fd = wq->head->client_socket_fd;
  wq->size--;
  DL_DELETE(wq->head, wq->head);

  free(wq_item);
  pthread_cond_signal(&not_full_cond);
  pthread_mutex_unlock(&mutex);
  return client_socket_fd;
}

/* Add ITEM to WQ. */
void wq_push(wq_t *wq, int client_socket_fd) {

  pthread_mutex_lock(&mutex);
  while (wq->size >= wq->max_size)
    pthread_cond_wait(&not_full_cond, &mutex);

  wq_item_t *wq_item = calloc(1, sizeof(wq_item_t));
  wq_item->client_socket_fd = client_socket_fd;
  DL_APPEND(wq->head, wq_item);
  wq->size++;
  pthread_cond_signal(&not_empty_cond);
  pthread_mutex_unlock(&mutex);
}
