#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include "omp.h"
#define size 1000
#define max_num_threads 4
#define num_buckets 4 
#define data_range 400


struct bucket
{
	int num_elems;	// number of elements in bucket
	int start_ind;	// start index to store bucket elements in sorted_data array
	int store_ind;	// index where next element of bocket will be stored in sorted_data array
};

void print_list(int * x, int n) {
   int i;
   for (i = 0; i < n; i++) {
      printf("%d ",x[i]);
   } 
   printf("\n");
}

int compare_function (const void * a, const void * b) {
 return ( *(int*)a - *(int*)b );
}

void bucketSort(int* data, int* sorted_data) {
	struct bucket* buckets;
	buckets = (struct bucket *) calloc(num_buckets * max_num_threads, sizeof(struct bucket));

	int bucket_width = data_range / num_buckets;

	int all_num_elems[num_buckets];	// number of elements for each global bucket
	int all_start_ind[num_buckets];		// start index of each global bucket in sorted_data array
	memset(all_num_elems, 0, sizeof(int)*num_buckets);
    memset(all_start_ind, 0, sizeof(int)*num_buckets);

    omp_set_num_threads(max_num_threads);

    int bucket_sum;		// number of elements in a bucket

	#pragma omp parallel private(bucket_sum) 
	{
	    int ind, i;
		int bucket_ind;
		int thread_id = omp_get_thread_num();

		#pragma omp for private(ind, bucket_ind)
		for (i = 0; i < size; i++) {
			ind = data[i] / bucket_width;
			if (ind > num_buckets - 1)
				ind = num_buckets - 1;
			bucket_ind = ind + thread_id * num_buckets;
			buckets[bucket_ind].num_elems++;

		}

		bucket_sum = 0;
		#pragma omp barrier
		
		for (i = thread_id; i < max_num_threads * num_buckets; i += num_buckets) {
			bucket_sum += buckets[i].num_elems;
		}

		all_num_elems[thread_id] = bucket_sum;

		#pragma omp barrier

		#pragma omp single
		{
			for (i = 1; i < num_buckets; i++){
				all_start_ind[i] = all_start_ind[i - 1] + all_num_elems[i - 1];
				buckets[i].start_ind = buckets[i-1].start_ind + all_num_elems[i - 1];
				buckets[i].store_ind = buckets[i-1].store_ind + all_num_elems[i - 1];
			}
	
		}


		#pragma omp parallel private(i)
		{
			for (i = thread_id + num_buckets; i < (num_buckets * max_num_threads); i += max_num_threads) {
				buckets[i].start_ind = buckets[i - num_buckets].start_ind + buckets[i - num_buckets].num_elems;
				buckets[i].store_ind = buckets[i - num_buckets].store_ind + buckets[i - num_buckets].num_elems;
			}
		}

		#pragma omp barrier

		#pragma omp for private(ind, bucket_ind)
		for (i = 0; i < size; i++) {
			ind = data[i] / bucket_width;
			if (ind > num_buckets - 1)
				ind = num_buckets - 1;
			bucket_ind = ind + thread_id * num_buckets;
			ind = buckets[bucket_ind].store_ind++;

			sorted_data[ind] = data[i];
		}

		#pragma omp barrier

		#pragma omp for
		for (i = 0; i < num_buckets; i++)
			qsort(sorted_data + all_start_ind[i], all_num_elems[i], sizeof(int), compare_function);
	}
}

int main(void)
{
	int* data;
	data = (int*) malloc(sizeof(int) * size);

	int i;

	for(i = 0; i < size; i++)
		data[i] = rand() % ((int)(data_range / 2)) + 1;

	print_list(data, size);

	int* sorted_data;
	sorted_data = (int*) malloc(sizeof(int) * size);

	double start, end;
	start = omp_get_wtime();

	bucketSort(data, sorted_data);


	end = omp_get_wtime();
	printf("Runtime = %g\n", end - start);

	print_list(sorted_data, size);

	return 0;
}
