#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string>
#include <string.h>
#include <vector>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>

#define MAX_GRID_SIZE 65535
#define MAX_BLOCK_SIZE_X 32              // which points
#define MAX_BLOCK_SIZE_Y 4               // which cluster/dimension (first one for clustering, second one for computing centroids)
#define MAX_BLOCK_SIZE_Z 8               // which dimension
#define MAX_THREADS_PER_BLOCK 1024

#define FILE_NAME "points.txt"

using namespace std;

__global__ void kmeans(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int *converged);
__global__ void initialize(int *clusters, int n, int k);
__device__ float euclidean_dist(float *p1, float *p2, int d);
vector<string> split(string str, char delimiter);
__global__ void assign_cluster(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int *converged);
__global__ void compute_centroids(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int reset);

int main(){
        /* Read FILE_NAME with the following format:
           First line specifies n: the number of points, d: the dimension of the points, and k: the number of clusters, in the same order.
           The following n lines will contain the value of the nodes in each d dimension in each line. 
         */
        int n, d, k; // parameters
        float *points; // value of the points
        int *clusters; // each point's cluster a.k.a the final result
        int *converged; // whether the program is finished yet or not

        // open the file
        ifstream file (FILE_NAME);
        if (!file.is_open()){
                cout << "couldn't open file" << endl;
                return 0;
        }
        // read the parameters
        string line;
        getline(file, line);
        vector<string> params = split(line, ' ');
        n = stoi(params[0]);
        d = stoi(params[1]);
        k = stoi(params[2]);
        // read the points
        points = (float *)malloc(n * d * sizeof(float));
        for (int i = 0; i < n; i++){
                getline(file, line);
                vector<string> values = split(line, ' ');
                for (int j = 0; j < d; j++){
                        points[i * d + j] = stof(values[j]);
                }
        }
        file.close();

        /* Allocate memory on host for the clusters and the convergence state. */
        clusters = (int *) malloc(n * sizeof(int));
        converged = (int *) malloc(sizeof(int));

        /* Allocate memory on device */
        float *points_dev, *centroids_dev;
        int *clusters_dev, *clusters_size_dev;
        int *converged_dev;

        cudaMalloc((void **) &points_dev, n * d * sizeof(float));
        cudaMalloc((void **) &centroids_dev, k * d * sizeof(float));
        cudaMalloc((void **) &clusters_dev, n * sizeof(int));
        cudaMalloc((void **) &clusters_size_dev, k * sizeof(int));
        cudaMalloc((void **) &converged_dev, sizeof(int));

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

        /* Compute the block size for launching the 'assign_cluster' kernel */
        int ac_block_size_x = (n > MAX_BLOCK_SIZE_X) ? MAX_BLOCK_SIZE_X : n;
        int ac_block_size_y = (k > MAX_BLOCK_SIZE_Y) ? MAX_BLOCK_SIZE_Y : k;
        int ac_block_size_z = (d > MAX_BLOCK_SIZE_Z) ? MAX_BLOCK_SIZE_Z : d;

        /* Final computation of sizes */
        dim3 cc_block_dim(cc_block_size_x, cc_block_size_y);
        dim3 ac_block_dim(ac_block_size_x, ac_block_size_y, ac_block_size_z);

        /* because the first time, there is no need to reset centroids and clusters_size */
        int reset = 0;
        cudaDeviceSynchronize();
        while (1){
                // initialize the converged flag
                memset(converged, 0, sizeof(int));
                cudaMemcpy(converged_dev, converged, sizeof(int), cudaMemcpyHostToDevice);

                // start the first kernel
                compute_centroids<<<grid_size, cc_block_dim>>>(points_dev, centroids_dev, clusters_dev, clusters_size_dev, n, d, k, reset);
                cudaDeviceSynchronize();

                // start the second kernel 
                assign_cluster<<<grid_size, ac_block_dim>>>(points_dev, centroids_dev, clusters_dev, clusters_size_dev, n, d, k, converged_dev);
                cudaDeviceSynchronize();

                // check converged flag
                cudaMemcpy(converged, converged_dev, sizeof(int), cudaMemcpyDeviceToHost);
                if (*converged == 0)
                        break;
                reset = 1;
        }

        /* Copy the results back from device to host */
        cudaMemcpy(clusters, clusters_dev, n * sizeof(int), cudaMemcpyDeviceToHost);

        /* do whatever you want with the results :)) */

        /* Free the memory */
        cudaFree(points_dev);
        cudaFree(centroids_dev);
        cudaFree(clusters_dev);
        cudaFree(clusters_size_dev);
        cudaFree(converged_dev);
        free(points);
        free(clusters);
        free(converged);
}

vector<string>
split(string str, char delimiter){
        vector<string> internal;
        string word = "";
        for (int i = 0; i < str.size(); i++){
                char letter = str[i];
                if (letter == delimiter || letter == '\n'){
                        internal.push_back(word);
                        word = "";
                } else {
                        word += letter;
                }
        }
        internal.push_back(word);

        return internal;
}

__device__ float
euclidean_dist(float *p1, float *p2, int d){
        float dist = 0;
        for (int i = 0; i < d; i++){
                float dif = p1[i] - p2[i];
                dist += dif * dif;
        }
        return dist;

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
        int t_point = threadIdx.x, p_stride = blockDim.x * gridDim.x;
        int t_dim = threadIdx.y, d_stride = blockDim.y;

        /* reset centroids and their sizes */
        if (reset){
                if (threadIdx.x < k){
                        clusters_size[threadIdx.x] = 0;
                        for (int i = t_dim; i < d; i += d_stride){
                                centroids[threadIdx.x * d + i] = 0.0;
                        }
                }
        }
        __syncthreads();

        /* Each thread handles one (or a few) dimensions of one (or a few) points  */
        for (int i = t_point; i < n; i += p_stride){                                     // one (or a few) points
                int t_cluster = clusters[i];                                             // this point's cluster
                clusters_size[t_cluster]++;                                              // increase the size of this cluster
                for (int j = t_dim; j < d; j += d_stride){                               // one (or a few) dimensions
                        centroids[t_cluster * d + j] += points[i * d + j];               // this point's value added to this cluster's value
                }
        }
}


/* This function assigns clusters to points based on centroids */
__global__ void
assign_cluster(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int *converged){
        int t_point = threadIdx.x, t_cluster = threadIdx.y, t_dim = threadIdx.z;
        int p_stride = blockDim.x * gridDim.x, d_stride = blockDim.z;
        int t_converged = 0;

        __shared__ float tmp_points[MAX_BLOCK_SIZE_X * MAX_BLOCK_SIZE_Z];               // copying the points to shared memory
        __shared__ int tmp_clusters_size[MAX_BLOCK_SIZE_Y];
        __shared__ float distances[MAX_BLOCK_SIZE_X * MAX_BLOCK_SIZE_Y];                // the distance coresponding to each <point, centroid> pair
        __shared__ int block_converged;                                                 // flag indicating whether this block has converged or not

        tmp_clusters_size[t_cluster] = clusters_size[t_cluster];                        // copy the cluster sizes to shared memory 
        __syncthreads();

        /* Each thread handles one (or a few) dimensions of one (or a few) points next to one clusters */
        for (int i = t_point; i < n; i += p_stride){                                    // one (or a few) points

                /* Copy the points to shared memory */
                for (int j = t_dim; j < d; j += d_stride){
                        tmp_points[(i % p_stride) * d + j] = points[i * d + j];
                }
                __syncthreads();

                /* Calculate the <point, centroid> distances */
                for (int j = t_dim; j < n; j += d_stride){
                        float t_dist = (tmp_points[(i % p_stride) * d + j] - centroids[t_cluster * d + j]) / tmp_clusters_size[t_cluster] ;
                        distances[i * k + t_cluster] += t_dist * t_dist;
                }
                __syncthreads();

                /* assign the cluster */
                if (t_cluster == 0 && t_dim == 0){
                        /* compute minimum distance and coresponding cluster */
                        float min_dist = distances[i * k];
                        int curr_cluster = 0;
                        for (int l = 1; l < k; l++){
                                if (distances[i * k + l] < min_dist){
                                        min_dist = distances[i * k + l];
                                        curr_cluster = l;
                                }
                        }
                        /* set convergence flag and new (maybe) cluster */
                        if (clusters[i] != curr_cluster){
                                clusters[i] = curr_cluster;
                                t_converged = -1;
                        }
                }
                __syncthreads();
        }

        /* One thread checks the convergence of its assigned points */
        if (t_cluster == 0 && t_dim == 0){
                if (t_converged == -1){
                        block_converged = -1;
                }
        }
        __syncthreads();

        /* One thread from this block sets the convergence flag */
        if (t_point == 0 && t_cluster == 0 && t_dim == 0){
                if (block_converged == -1){
                        *converged = -1;
                }
        }
}


// THIS FUNCTION IS OLD!!!! 
__global__ void
kmeans(float *points, float *centroids, int *clusters, int *clusters_size, int n, int d, int k, int *converged){
        /* Compute the centroids */
        int start = threadIdx.x, stride = blockDim.x * gridDim.x;
        for (int i = start; i < n; i += stride){
                int cluster_i = clusters[i];
                clusters_size[cluster_i]++;
                for (int j = 0; j < d; j++){
                        centroids[cluster_i * d + j] += points[i * d + j];
                }
        }


        // TODO: GLOBAL SYNCHRONIZATION

        for (int i = start; i < k; i += stride){
                for (int j = 0; j < d; j++){
                        centroids[i * d + j] /= clusters_size[i];
                }
        }

        // TODO: GLOBAL SYNCHRONIZATION

        /* Compute each point's new cluster */
        for (int i = start; i < n; i += stride){
                // copy the current point to local memory to avoid constantly accessing global memory
                float *curr_point = (float *) malloc(d * sizeof(float));
                for (int l = 0; l < d; l++){
                        curr_point[l] = points[i * d + l];
                }
                float min_dif = euclidean_dist(curr_point, centroids, d), curr_dif = 0.0;
                int curr_cluster = 0;
                for (int j = 1; j < k; j++){
                        curr_dif = euclidean_dist(curr_point, centroids + j * d, d);
                        if (curr_dif < min_dif){
                                min_dif = curr_dif;
                                curr_cluster = j;
                        }
                }
                // TODO: a more efficient way of changing converged. 
                if (curr_cluster != clusters[i]){
                        *converged = -1;
                        clusters[i] = curr_cluster;
                }
        }
}
