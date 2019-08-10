#include <pthread.h>
#include <libspe2.h>
#include <time.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>
#include <sys/time.h> 

#define NUM_SPES 4
#define N 8       // bakhshpazir bar 4
#define M 8       // bakhshpazir bar 4
#define K 8       // bakhshpazir bar 4

float in1[N * M]  __attribute__((aligned(16)));

float in2[M * K]  __attribute__((aligned(16)));


float out[N * K] __attribute__((aligned(16)));

typedef struct {
  unsigned long long  ea_in1;
  unsigned long long  ea_in2;
  unsigned long long  ea_out;
  unsigned int        n;
  unsigned int        m;
  unsigned int        k;
  unsigned int        size;
  int                 pad[2];
} thread_args;

thread_args thread_params[NUM_SPES] __attribute__((aligned(16)));

typedef struct {
  spe_context_ptr_t spe_ctx;
  pthread_t pthread;
  thread_args *argp;
  int index;
} spu_data_t;

spe_program_handle_t * handle_spu[NUM_SPES];

spu_data_t data[NUM_SPES];

void *spu_pthread(void *arg) {
  spu_data_t * data_pthread = (spu_data_t *)arg;

  uint32_t entry = SPE_DEFAULT_ENTRY;

  if(spe_context_run(data_pthread->spe_ctx , &entry, 0, data_pthread->argp ,NULL,NULL)<0)
  {
    perror ("Failed running context"); exit (1);
  }

  pthread_exit(NULL);
}

int main() {

  uint32_t num;

  int i, j;

  printf("A: \n");
  for (i = 0; i < N; i++) {
    for (j = 0; j < M; j++){
      in1[i * M + j] = rand() % 10;
      printf("%0.0f   ", in1[i * M + j]);
    }
    printf("\n");
  }

  printf("\nB: \n");
  for (i = 0; i < M; i++) {
    for (j = 0; j < K; j++){
      in2[i * K + j] = rand() % 10;
      printf("%0.0f   ", in2[i * K + j]);
    }
    printf("\n");
  }

  for(num = 0; num < NUM_SPES; ++num) {
    if((data[num].spe_ctx = spe_context_create (0, NULL))==NULL)
    {
      perror("Failed creating context "); exit(1);
    }

    if (!(handle_spu[num] = spe_image_open("./spu/spu_mult")))
    {
      perror("Fail opening image"); exit(1);
    }

    if(spe_program_load (data[num].spe_ctx, handle_spu[num]))
    {
      perror("Failed loading program"); exit(1);
    }
  }

  int tile_size = N / NUM_SPES;

  struct timespec t1, t2;

  clock_gettime(CLOCK_MONOTONIC, &t1); 

  for (num=0; num < NUM_SPES; ++num) {
    thread_params[num].ea_in1 = (unsigned long) &in1[num * tile_size * M];
    thread_params[num].ea_in2 = (unsigned long) &in2[0];
    thread_params[num].ea_out = (unsigned long) &out[num * tile_size * K];
    thread_params[num].n = N;
    thread_params[num].m = M;
    thread_params[num].k = K;
    thread_params[num].size = tile_size;

    data[num].argp = &thread_params[num];
    data[num].index = num;

    if(pthread_create(&data[num].pthread, NULL, &spu_pthread, &data[num]))
    {
      perror("Failed creating thread"); exit(1);
    }
  }

  for( num=0; num<NUM_SPES; ++num) {
    if (pthread_join (data[num].pthread, NULL))
    {
      perror("Failed joining thread"); exit (1);
    }
  }

  clock_gettime(CLOCK_MONOTONIC, &t2); 

  double time_taken; 
    time_taken = (t2.tv_sec - t1.tv_sec) * 1e9; 
    time_taken = (time_taken + (t2.tv_nsec - t1.tv_nsec)) * 1e-9; 

  for(num = 0 ; num < NUM_SPES; ++num) {
    spe_context_destroy(data[num].spe_ctx);
    spe_image_close(handle_spu[num]);
  }

  printf("\nA * B = C: \n");
  for (i = 0; i < N; i++) {
    for (j = 0; j < K; j++){
      printf("%0.0f   ", out[i * K + j]);
    }
    printf("\n");
  }

  printf("\n\nExecution Time: %0.9f\n", time_taken);


  return 0;
}

