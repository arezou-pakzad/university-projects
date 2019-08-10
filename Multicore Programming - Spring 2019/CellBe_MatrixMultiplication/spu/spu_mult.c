#include <spu_mfcio.h>
#include <stdio.h>
#include <spu_intrinsics.h>

#define MAX_BUFSIZE 256

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

thread_args args __attribute__((aligned(16)));

  float in1_spe[MAX_BUFSIZE]  __attribute__((aligned(16)));
  float in2_spe[MAX_BUFSIZE]  __attribute__((aligned(16)));
  float out_spe[MAX_BUFSIZE] __attribute__((aligned(16)));


int main (unsigned long long spe, uint64_t argp, unsigned long long envp) {
  int tag = 1;
  int i, j, k;

  vector float *vin1 = (vector float*)(in1_spe);
  vector float *vin2 = (vector float*)(in2_spe);
  vector float *vout = (vector float*)(out_spe);

  vector unsigned char mask0 = (vector unsigned char) {0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
  vector unsigned char mask1 = (vector unsigned char) {4, 5, 6, 7, 4, 5, 6, 7, 4, 5, 6, 7, 4, 5, 6, 7};
  vector unsigned char mask2 = (vector unsigned char) {8, 9,10,11, 8, 9,10,11, 8, 9,10,11, 8, 9,10,11};
  vector unsigned char mask3 = (vector unsigned char) {12,13,14,15,12,13,14,15,12,13,14,15,12,13,14,15};

  spu_mfcdma64(&args, mfc_ea2h(argp), mfc_ea2l(argp),
                 sizeof(thread_args), tag, MFC_GET_CMD);
  spu_writech(MFC_WrTagMask, 1 << tag);
  spu_mfcstat(MFC_TAG_UPDATE_ALL);


  spu_mfcdma64(vin1, mfc_ea2h(args.ea_in1), mfc_ea2l(args.ea_in1),
                 args.size * args.m * sizeof(float), tag, MFC_GET_CMD);
  spu_writech(MFC_WrTagMask, 1 << tag);
  spu_mfcstat(MFC_TAG_UPDATE_ALL);


  spu_mfcdma64(vin2, mfc_ea2h(args.ea_in2), mfc_ea2l(args.ea_in2),
                 args.m * args.k * sizeof(float), tag, MFC_GET_CMD);
  spu_writech(MFC_WrTagMask, 1 << tag);
  spu_mfcstat(MFC_TAG_UPDATE_ALL);

  for (i = 0; i < args.size; i++) {
    for (j = 0; j < args.m; j += 4) {
      for (k = 0; k < args.k / 4; k++) {
        vout[i * args.k / 4 + k] = spu_add(vout[i * args.k / 4 + k], spu_mul(spu_shuffle(vin1[i * args.m / 4 + j/4], vin1[i * args.m / 4 + j/4], mask0), vin2[j * args.k / 4 + k]));
        vout[i * args.k / 4 + k] = spu_add(vout[i * args.k / 4 + k], spu_mul(spu_shuffle(vin1[i * args.m / 4 + j/4], vin1[i * args.m / 4 + j/4], mask1), vin2[(j + 1) * args.k / 4 + k]));
        vout[i * args.k / 4 + k] = spu_add(vout[i * args.k / 4 + k], spu_mul(spu_shuffle(vin1[i * args.m / 4 + j/4], vin1[i * args.m / 4 + j/4], mask2), vin2[(j + 2) * args.k / 4 + k]));
        vout[i * args.k / 4 + k] = spu_add(vout[i * args.k / 4 + k], spu_mul(spu_shuffle(vin1[i * args.m / 4 + j/4], vin1[i * args.m / 4 + j/4], mask3), vin2[(j + 3) * args.k / 4 + k]));
      }
    }
  }


  spu_mfcdma64(vout, mfc_ea2h(args.ea_out), mfc_ea2l(args.ea_out),
                 args.size * args.k * sizeof(float), tag, MFC_PUT_CMD);
  spu_writech(MFC_WrTagMask, 1 << tag);
  spu_mfcstat(MFC_TAG_UPDATE_ALL);


  return 0;
}

