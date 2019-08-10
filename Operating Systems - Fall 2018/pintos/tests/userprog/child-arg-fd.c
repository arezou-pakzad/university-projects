#include <stdio.h>
#include <stdlib.h>
#include "tests/lib.h"

const char *test_name = "child-arg-fd";

int
main (int argc, char *argv[])
{
  msg ("argc = %d", argc);
  int fd = atoi(argv[1]);
  char ch;
  int result = read (fd, &ch, 1);
  msg ("read returned %d", result);
  return 0;
}
