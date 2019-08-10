#include <random.h>
#include <syscall.h>
#include <tests/lib.h>
#include <tests/main.h>
#include <userprog/syscall.h>

#define BLOCK_SIZE 512
#define WRITE_SIZE 100000
static char buf[BLOCK_SIZE];

void
test_main(void){
  random_bytes(buf, sizeof(buf));

  msg("creating cache_test");
  CHECK (create ("cache_test", 0), "create \"cache_test\"");

  msg("opening cache_test");
  int fd;
  CHECK ((fd = open ("cache_test")) > 1, "open \"cache_test\"");

  int init_read_count = cache_read_count();
  int init_write_count = cache_write_count();

  msg ("writing 100 kB to cache_test");
  int i;
  for (i = 0; i < WRITE_SIZE; ++i) {
    int status;
    if ((status = write(fd, buf, sizeof(buf))) != sizeof(buf)){
      fail("couldn't write %zu bytes to cache_test, exit status = %d",
           sizeof(buf), status);
    }
  }

  int cur_read_count = cache_read_count();
  int cur_write_cound = cache_write_count();

  int total_read_count = cur_read_count - init_read_count;
  int total_write_count = cur_write_cound - init_write_count;

  msg("closing cache_test");
  close(fd);

  if (total_read_count == 0 && total_write_count == 200){
    msg("YOUR CACHE IS THE BESTTTTTTT!");
  }else {
    msg("YOUR CACHE FAILED AT LIFE. Sucks to be you.");
    msg("reads = %d, writes = %d", total_read_count, total_write_count);
  }
}