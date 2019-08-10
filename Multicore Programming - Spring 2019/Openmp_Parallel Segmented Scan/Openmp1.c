#include <stdio.h> 
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "omp.h"

#define size 1000000
#define max_num_threads 20

void print_list(int * x, int n) {
   int i;
   for (i = 0; i < n; i++) {
      printf("%d ",x[i]);
   } 
}

void segmented_scan(int* input, int* flags, int* output) {
	int i, j, tmp;

	omp_set_num_threads(max_num_threads);

	#pragma omp parallel private(j, tmp)
  	{
    	#pragma omp for
    	for (i = 0; i < size; i++) {
      		if (flags[i] == 1){
        		tmp = input[i];
        		j = i + 1;
        		while (j < size && flags[j] == 0) {
          			tmp += input[j];
          			output[j] = tmp;
          			j++;
        		}
      		}
    	}
  	}
	
}

int main(void)
{
	int* input;
	int* output;
	int* flags;

	int i;

	input = (int*) malloc(sizeof(int) * size);
	output = (int*) calloc(size, sizeof(int));
	flags = (int*) calloc(size, sizeof(int));

	for (i = 1; i <= size; i++){
		input[i - 1] = i;
	}

	for (i = 0; i < size; i += 1000)
		flags[i] = 1;


	printf("input: ");
	print_list(input, size);
	printf("\n");
	printf("flags: ");
	print_list(flags, size);
	printf("\n");

	memcpy(output, input, sizeof(int) * size);

	double start, end;
	start = omp_get_wtime();

	segmented_scan(input, flags, output);

	end = omp_get_wtime();

	printf("output: ");
	print_list(output, size);
	printf("\n");

	printf("Runtime = %g\n", end - start);

	return 0;
}
