#include <random.h>
#include <syscall.h>
#include <tests/lib.h>
#include <tests/main.h>
#include <userprog/syscall.h>

#define BLOCK_SIZE 512
#define BLOCKS_NUM 20
static char buf[BLOCK_SIZE];

void
test_main(void){
  random_bytes(buf, sizeof(buf));

  msg("creating ctest");
  CHECK (create ("ctest.dat", 10), "create ctest");

  msg("opening ctest");
  int fd;
  CHECK ((fd = open ("ctest.dat")) > 1, "open ctest");

  msg("writing to ctest");
  int i = 0;
  for (; i < BLOCKS_NUM; ++i) {
    int status;
    if ((status = write(fd, buf, sizeof(buf))) != sizeof(buf)){
      fail("couldn't write %zu bytes to ctest, exit status = %d",
           sizeof(buf), status);
    }
  }
  msg("closing ctest");
  close(fd);

  msg("reseting buffer-cache");
  cache_reset();

  msg("opening ctest");
  CHECK ((fd = open ("ctest.dat")) > 1, "open ctest");

  msg("reading from ctest");
  for (i = 0; i < BLOCKS_NUM; i++){
    int status;
    if ((status = read(fd, buf, sizeof(buf))) != sizeof(buf)){
      fail("could't read %zu bytes from ctest, exit status = %d",
           sizeof(buf), status);
    }
  }

  msg("closing ctest");
  close(fd);

  int hit_count = cache_hit_count();
  int miss_count = cache_miss_count();
  int hit_rate = (100 * hit_count) / (hit_count + miss_count);

  msg("opening ctest");
  CHECK ((fd = open ("ctest.dat")) > 1, "open ctest");

  msg("reading from ctest");
  for (i = 0; i < BLOCKS_NUM; ++i) {
    int status;
    if ((status = read(fd, buf, sizeof(buf))) != sizeof(buf)){
      fail("could't read %zu bytes from ctest, exit status = %d",
           sizeof(buf), status);
    }
  }

  msg("closing ctest");
  close(fd);

  msg("removing ctest");
  remove("ctest.dat");

  int new_hit_count = cache_hit_count() - hit_count;
  int new_miss_count = cache_miss_count() - miss_count;
  int new_hit_rate = (100 * new_hit_count) / (new_hit_count + new_miss_count);

  if (new_hit_rate > hit_rate){
    msg("YOUR CACHE IS THE BESTTTTTTT!");
  } else{
    msg("YOUR CACHE FAILED AT LIFE. Sucks to be you.");
  }
}