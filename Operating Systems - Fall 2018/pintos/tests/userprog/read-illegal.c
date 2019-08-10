#include <syscall.h>
#include <string.h>
#include "tests/lib.h"
#include "tests/main.h"

#define BUF_LEN 32
#define INT_LEN 16

void
test_main (void)
{
  int handle;
  CHECK ((handle = open ("sample.txt")) > 1, "open \"sample.txt\"");

  char child_command[BUF_LEN];
  strlcpy (child_command, "child-arg-fd ", BUF_LEN);
  
  char fd_str[INT_LEN];
  int fd_copy = handle;
  int idx = 0;
  do
    {
      fd_str[idx++] = '0' + fd_copy % 10;
      fd_copy /= 10;
    } while (fd_copy);
  fd_str[idx] = '\0';
  strlcat(child_command, fd_str, BUF_LEN);
  msg ("wait(exec()) = %d", wait (exec (child_command)));
}
