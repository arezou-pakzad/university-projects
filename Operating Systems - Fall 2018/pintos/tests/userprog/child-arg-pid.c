#include <stdio.h>
#include <stdlib.h>
#include "tests/lib.h"

const char *test_name = "child-arg-pid";

int
main (int argc, char *argv[])
{
  msg ("argc = %d", argc);
  int pid = atoi(argv[1]);
  int result = wait (pid);
  msg ("wait returned %d", result);
  return 0;
}
