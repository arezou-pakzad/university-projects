#include <syscall.h>
#include <string.h>
#include "tests/lib.h"
#include "tests/main.h"

#define BUF_LEN 32
#define INT_LEN 16

void
test_main (void)
{
  pid_t first_pid, second_pid;
  CHECK ((first_pid = exec ("child-arg-pid 3")) > 1, "exec wait for 3");

  char child_command[BUF_LEN];
  strlcpy (child_command, "child-arg-pid ", BUF_LEN);
  
  char pid_str[INT_LEN];
  int pid_copy = first_pid;
  int idx = 0;
  do
    {
      pid_str[idx++] = '0' + pid_copy % 10;
      pid_copy /= 10;
    } while (pid_copy);
  pid_str[idx] = '\0';
  strlcat(child_command, pid_str, BUF_LEN);
  CHECK ((second_pid = exec (child_command)) > 1, "exec wait for first_pid");
  msg("wait(first): %d", wait(first_pid));
  msg("wait(second): %d", wait(second_pid));
}
