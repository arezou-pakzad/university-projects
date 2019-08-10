#include <stdio.h>
#include <stdlib.h>
#include <cuda_runtime_api.h>
#include <iostream>
#include <string>
#include <cstdlib>
#include <vector>
#include <fstream>

using namespace std;

//void write_array_to_file(int *array, int dim, string file_name);
void print_array(int *array, int size);
__device__ void merge(int *array, int size, int *copy);
__global__ void mergesort(int *array, int size, int *temp, int level);

int main(){
        /* Initialize n here */
        int n = 1 << 10;

        /* Make a random array of size n */
        int *array;
        array = (int *)malloc(n * sizeof(int));
        for (int i = 0; i < n; i++){
                array[n - i] = i;
        }

        /* Allocate memory on device */
        int *array_dev, *copy_dev;
        cudaMalloc((void **) &array_dev, n * sizeof(int));
        cudaMalloc((void **) &copy_dev, n * sizeof(int));

        cudaMemcpy(array_dev, array, n * sizeof(int), cudaMemcpyHostToDevice);

        cudaDeviceSetLimit(cudaLimitDevRuntimeSyncDepth, 4);
        mergesort<<<1, 1>>>(array_dev, n, copy_dev, 0);
        cudaDeviceSynchronize();

        /* Copy results back to host */
        cudaMemcpy(array, array_dev, n * sizeof(int), cudaMemcpyDeviceToHost);

        print_array(array, n);

        cudaFree(array_dev);
        cudaFree(copy_dev);
        free(array);
}


void print_array(int *array, int size){
        for (int i = 0; i < size; i++)
                cout << array[i] << " ";
        cout << endl;
}

__device__
void merge(int *array, int size, int *copy){
        int left = 0, right = size / 2, c_ind = 0;

        for (c_ind = 0; left < size / 2 && right < size; c_ind++){
                if (array[left] < array[right]){
                        copy[c_ind] = array[left];
                        left++;
                } else {
                        copy[c_ind] = array[right];
                        right++;
                }
        }

        while (left < size / 2){
                copy[c_ind] = array[left];
                left++;
                c_ind++;
        }
        while (right < size){
                copy[c_ind] = array[right];
                right++;
                c_ind++;
        }

        for (int i = 0; i < size; i++)
                array[i] = copy[i];
}

__device__
void bubble_sort(int *array, int size){
        int sorted = 0, temp;
        while (!sorted) {
                sorted = 1;
                for (int i = 1; i < size; i++){
                        if (array[i - 1] > array[i]){
                                temp = array[i - 1];
                                array[i - 1] = array[i];
                                array[i] = temp;
                                sorted = 0;
                        }
                }
        }
}


__global__
void mergesort(int *array, int size, int *copy, int level){
        if (size < 2) {
                return;
        }
        if (level == 3){
                bubble_sort(array, size);
                return;
        }

        mergesort<<<1, 1>>>(array, size / 2, copy, level + 1);
        mergesort<<<1, 1>>>(array + size / 2, size - size / 2, copy, level + 1);

        cudaDeviceSynchronize();

        merge(array, size, copy);
}
