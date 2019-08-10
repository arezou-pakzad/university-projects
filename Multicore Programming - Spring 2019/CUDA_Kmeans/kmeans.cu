#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string>
#include <string.h>
#include <vector>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>
#include <random>

#define MAX_GRID_SIZE 4294967295
#define MAX_BLOCK_SIZE_X 32              // which points
#define MAX_BLOCK_SIZE_Y 8               // which cluster/dimension (first one for clustering, second one for computing centroids)
#define MAX_BLOCK_SIZE_Z 4               // which dimension
#define MAX_THREADS_PER_BLOCK 1024

#define MAX_ITER 32
#define FILE_NAME "points.txt"

using namespace std;

__global__ void initialize(int *clusters, int n, int k);
__global__ void compute_centroids(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int reset);
__global__ void assign_cluster(float *points, float *centroids, int *clusters, int *clusters_size, float *distances, int n, int d, int k, int *converged, int reset);

int main(){
        /* Read FILE_NAME with the following format:
           First line specifies n: the number of points, d: the dimension of the points, and k: the number of clusters, in the same order.
           The following n lines will contain the value of the nodes in each d dimension in each line.
         */

        int n = 1 << 20, d = 4, k = 8; // parameters
        float *points; // value of the points
        int *clusters; // each point's cluster a.k.a the final result
        int *converged; // whether the program is finished yet or not

        /* generate random numbers */
        points = (float *) malloc(n * d * sizeof(float));
        for (int i = 0; i < n; i++){
                for (int j = 0; j < d; j++){
                        float f = (float) rand() / RAND_MAX;
                        points[i * d + j] = f * 100.0;
                }
        }

        clusters = (int *) malloc(n * sizeof(int));
        converged = (int *) malloc(sizeof(int));

        /* Start overall timer */
        cudaEvent_t start_overall, stop_overall;
        cudaEventCreate(&start_overall);
        cudaEventCreate(&stop_overall);
        cudaEventRecord(start_overall, 0);

        /* Allocate memory on device */
        float *points_dev, *centroids_dev, *distances_dev;
        int *clusters_dev, *clusters_size_dev;
        int *converged_dev;

        cudaMalloc((void **) &points_dev, n * d * sizeof(float));
        cudaMalloc((void **) &centroids_dev, k * d * sizeof(float));
        cudaMalloc((void **) &clusters_dev, n * sizeof(int));
        cudaMalloc((void **) &clusters_size_dev, k * sizeof(int));
        cudaMalloc((void **) &converged_dev, sizeof(int));
        cudaMalloc((void **) &distances_dev, n * k * sizeof(float));

        /* Copy the points from host to device */
        cudaMemcpy(points_dev, points, n * d * sizeof(float), cudaMemcpyHostToDevice);

        /* Compute the block size and grid size for initialization */
        int block_size = (n > MAX_THREADS_PER_BLOCK) ? MAX_THREADS_PER_BLOCK : n;
        int temp_grid_size = (n + block_size - 1) / block_size;
        int grid_size = (temp_grid_size > MAX_GRID_SIZE) ? MAX_GRID_SIZE : temp_grid_size;

        /* Initialize the clusters */
        initialize<<<grid_size, block_size>>>(clusters_dev, n, k);

        /* Compute the block size for launching the 'compute_centroids' kernel */
        int cc_block_size_x = (n > MAX_BLOCK_SIZE_X) ? MAX_BLOCK_SIZE_X : n;
        int cc_block_size_y = (d > MAX_BLOCK_SIZE_Y * MAX_BLOCK_SIZE_Z) ? (MAX_BLOCK_SIZE_Y * MAX_BLOCK_SIZE_Z) : d;
        int cc_temp_grid_size = (n + cc_block_size_x - 1) / cc_block_size_x;
        int cc_grid_size = (cc_temp_grid_size > MAX_GRID_SIZE) ? MAX_GRID_SIZE : cc_temp_grid_size;

        /* Compute the block size for launching the 'assign_cluster' kernel */
        int ac_block_size_x = (n > MAX_BLOCK_SIZE_X) ? MAX_BLOCK_SIZE_X : n;
        int ac_block_size_y = (k > MAX_BLOCK_SIZE_Y) ? MAX_BLOCK_SIZE_Y : k;
        int ac_block_size_z = (d > MAX_BLOCK_SIZE_Z) ? MAX_BLOCK_SIZE_Z : d;
        int ac_temp_grid_size = (n + ac_block_size_x - 1) / ac_block_size_x;
        int ac_grid_size = (ac_temp_grid_size > MAX_GRID_SIZE) ? MAX_GRID_SIZE : ac_temp_grid_size;

        /* Final computation of sizes */
        dim3 cc_block_dim(cc_block_size_x, cc_block_size_y);
        dim3 ac_block_dim(ac_block_size_x, ac_block_size_y, ac_block_size_z);

        /* because the first time, there is no need to reset centroids and clusters_size */
        int reset = 0;
        cudaDeviceSynchronize();

        /* Start execution timer */
        cudaEvent_t start_exec, stop_exec;
        cudaEventCreate(&start_exec);
        cudaEventCreate(&stop_exec);
        cudaEventRecord(start_exec, 0);

        int iter = 0;
        while (1){
                iter++;
                if (iter > MAX_ITER)
                        break;

                //initialize the convergence rate
                cudaMemset(converged_dev, 0, sizeof(int));

                // start the first kernel
                compute_centroids<<<cc_grid_size, cc_block_dim>>>(points_dev, centroids_dev, clusters_dev, clusters_size_dev, n, d, k, reset);
                cudaDeviceSynchronize();

                // start the second kernel
                cudaMemset(distances_dev, 0, n * k * sizeof(float));
                assign_cluster<<<ac_grid_size, ac_block_dim>>>(points_dev, centroids_dev, clusters_dev, clusters_size_dev, distances_dev, n, d, k, converged_dev, reset);
                cudaDeviceSynchronize();

                // check converged flag
                cudaMemcpy(converged, converged_dev, sizeof(int), cudaMemcpyDeviceToHost);
                if (*converged == 0)
                        break;
                reset = 1;
        }

        cudaEventRecord(stop_exec, 0);
        cudaEventSynchronize(stop_exec);

        /* Copy the results back from device to host */
        cudaMemcpy(clusters, clusters_dev, n * sizeof(int), cudaMemcpyDeviceToHost);
        cudaEventRecord(stop_overall, 0);
        cudaEventSynchronize(stop_overall);

        /* do whatever you want with the results :)) */
        float overall_time, execution_time;
        cudaEventElapsedTime(&overall_time, start_overall, stop_overall);
        cudaEventElapsedTime(&execution_time, start_exec, stop_exec);
        cout << "n: " << n << "\tk: " << k << "\td: " << d << endl;
        cout << "converged after " << iter << " iterations" << endl;
        cout << "time for executing kmeans: " << execution_time << "ms" << endl;
        cout << "time for entire run (allocation, initialization, etc): " << overall_time << "ms" << endl;

        /* Free the memory */
        cudaFree(points_dev);
        cudaFree(centroids_dev);
        cudaFree(clusters_dev);
        cudaFree(clusters_size_dev);
        cudaFree(converged_dev);
        free(points);
        free(clusters);
        free(converged);
        cudaEventDestroy(start_overall);
        cudaEventDestroy(stop_overall);
        cudaEventDestroy(start_exec);
        cudaEventDestroy(stop_exec);
}

__global__ void
initialize(int *clusters, int n, int k){
        int start = threadIdx.x, stride = blockDim.x * gridDim.x;
        for (int i = start; i < n; i += stride){
                clusters[i] = i % k;
        }
}

__global__ void
compute_centroids(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int reset){
        int t_point = threadIdx.x + blockDim.x * blockIdx.x, p_stride = blockDim.x * gridDim.x;
        int t_dim = threadIdx.y, d_stride = blockDim.y;

        /* reset centroids and their sizes */
        if (reset){
                if (threadIdx.x < k){
                        clusters_size[threadIdx.x] = 0;
                        for (int i = t_dim; i < d; i += d_stride){
                                centroids[threadIdx.x * d + i] = 0.0;
                        }
                }
        __syncthreads();
        }

        /* Each thread handles one (or a few) dimensions of one (or a few) points  */
        for (int i = t_point; i < n; i += p_stride){                                     // one (or a few) points
                int t_cluster = clusters[i];                                             // this point's cluster
                atomicAdd(clusters_size + t_cluster, 1);                                 // increase the size of this cluster
                for (int j = t_dim; j < d; j += d_stride){                               // one (or a few) dimensions
                        atomicAdd(centroids + t_cluster * d + j,  points[i * d + j]);    // this point's value added to this cluster's value
                }
        }
}

/* This function assigns clusters to points based on centroids */
__global__ void
assign_cluster(float *points, float *centroids, int *clusters, int *clusters_size, float* distances, int n, int d, int k, int *converged, int reset){
        int t_point = threadIdx.x + blockIdx.x * blockDim.x, t_cluster = threadIdx.y, t_dim = threadIdx.z;
        int p_stride = blockDim.x * gridDim.x, c_stride = blockDim.y, d_stride = blockDim.z;
        int t_converged = 0;
        __shared__ int block_converged;

        /* Each thread handles one (or a few) dimensions of one (or a few) points next to one (or a few) clusters */
        for (int i = t_point; i < n; i += p_stride){                                    // one (or a few) points
                for (int j = t_cluster; j < k; j += c_stride){                          // one (or a few) clusters
                        for (int l = t_dim; l < d; l += d_stride){                      // one (or a few) dimensions
                                /* Calculate the <point, centroid> distances */
                                float t_dist = points[i * d + l] - centroids[j * d + l] / clusters_size[j];
                                atomicAdd(distances + i * k + j, t_dist * t_dist);
                        }
                }
                __syncthreads();

                /* Calculate minimum distancd for this point and assign (maybe) new cluster. */
                if (t_cluster == 0 && t_dim == 0){
                        float min_distance = distances[i * k];
                        int min_cluster = 0;
                        for (int j = 1; j < k; j++){
                                if (distances[i * k + j] < min_distance){
                                        min_distance = distances[i * k + j];
                                        min_cluster = j;
                                }
                        }
                        if (min_cluster != clusters[i]){
                                clusters[i] = min_cluster;
                                t_converged = -1;
                        }
                }
        }

        /* handle convergence */
        if (t_cluster == 0 && t_dim == 0){
                if (t_converged == -1){
                        block_converged = -1;
                }
        }

        __syncthreads();
        if (t_point == 0 && t_cluster == 0 && t_dim == 0){
                if (block_converged == -1){
                        *converged = -1;
                        block_converged = 0;
                }
        }
}
