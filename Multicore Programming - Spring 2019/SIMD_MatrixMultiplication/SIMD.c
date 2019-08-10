#include <emmintrin.h>
#include <stdio.h>
#include <string.h>
#include <time.h>


#define N 64
#define M 64
#define K 64


void mult_matrix(float** a, float** b, float** res, int n, int m, int k) {
	__m128 a1, b1, b2, b3, b4, tmp, res1, zero;

	zero = _mm_setzero_ps();

	for (int i = 0; i < n; ++i) {
		for (int j = 0; j < k; j += 4) {
			_mm_store_ps(res[i] + j, zero);
		}
	}

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < m; j += 4) {
			a1 = _mm_load_ps(a[i] + j);
			for (int t = 0; t < k; t += 4) {
				b1 = _mm_load_ps(b[j] + t);
				b2 = _mm_load_ps(b[j + 1] + t);
				b3 = _mm_load_ps(b[j + 2] + t);
				b4 = _mm_load_ps(b[j + 3] + t);

				res1 = _mm_load_ps(res[i] + t);

				res1 = _mm_add_ps(res1, _mm_mul_ps(_mm_shuffle_ps(a1, a1, 0x00), b1));
				res1 = _mm_add_ps(res1, _mm_mul_ps(_mm_shuffle_ps(a1, a1, 0x55), b2));
				res1 = _mm_add_ps(res1, _mm_mul_ps(_mm_shuffle_ps(a1, a1, 0xaa), b3));
				res1 = _mm_add_ps(res1, _mm_mul_ps(_mm_shuffle_ps(a1, a1, 0xff), b4));
				
				_mm_store_ps(res[i] + t, res1);
			}
		}

	}
}

int main()                                                                                                                                                                                  
{
	float **a;
  	float **b;
  	float **c;

  	a = (float **)malloc(N * sizeof(float *));
  	b = (float **)malloc(M * sizeof(float *));
  	c = (float **)malloc(N * sizeof(float *));

  	for (int i = 0; i < N; i++)
  		a[i] = (float *)malloc(M * sizeof(float));

  	for (int j = 0; j < M; j++)
  		b[j] = (float *)malloc(K * sizeof(float));
 
  	for (int k = 0; k < N; k++)
  		c[k] = (float *)malloc(K * sizeof(float));

 	for (int i = 0; i < N; ++i) {
 		for (int j = 0; j < M; ++j) {
 			a[i][j] = rand() % 10;
 		}
 	}

 	for (int i = 0; i < M; ++i) {
 		for (int j = 0; j < K; ++j) {
 			b[i][j] = rand() % 10;
 		}
 	}

 	clock_t start, end;
    double time_used;
    start = clock();

 	mult_matrix(a, b, c, N, M, K);

 	end = clock();
    time_used = ((double) (end - start)) / CLOCKS_PER_SEC;

 	printf("A: \n");
 	for (int i = 0; i < N; ++i) {
 		for (int j = 0; j < M; ++j) {
 			printf("%.0f  ", a[i][j]);
 		}
 		printf("\n");
 	}

 	printf("\n");
 	printf("B: \n");
 	for (int i = 0; i < M; ++i) {
 		for (int j = 0; j < N; ++j) {
 			printf("%.0f  ", b[i][j]);
 		}
 		printf("\n");
 	}

 	printf("\n");
 	printf("A * B = C: \n");
    for (int i = 0; i < N; ++i) {
 		for (int j = 0; j < N; ++j) {
 			printf("%.0f  ", c[i][j]);
 		}
 		printf("\n");
 	}

 	printf("\n");
    printf("\nExecution Time: %f\n", time_used);                                                                                                                                                                 
                                                                                                                                                                                                
 	return 0;
}