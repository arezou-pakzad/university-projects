#include "userprog/syscall.h"
#include <stdio.h>
#include <syscall-nr.h>
#include "threads/interrupt.h"
#include "threads/thread.h"
#include "threads/malloc.h"
#include "threads/vaddr.h"
#include "userprog/pagedir.h"
#include "userprog/process.h"
#include "devices/shutdown.h"
#include "filesys/filesys.h"
#include "filesys/file.h"
#include "devices/input.h"
#include "filesys/buffer-cache.h"
#include "devices/block.h"

struct file_descriptor {
  struct file *file;
  int fd;
  struct list_elem elem;
};

struct lock global_filesys_lock;

static void syscall_handler (struct intr_frame *);

void
syscall_init (void)
{
  lock_init(&global_filesys_lock);
  intr_register_int (0x30, 3, INTR_ON, syscall_handler, "syscall");
}

static void
syscall_handler (struct intr_frame *f)
{
  uint32_t *args = ((uint32_t *) f->esp);
  assert_valid_args (args, 1);
  switch (args[0])
    {
      case SYS_EXIT:
        assert_valid_args (args, 2);
        exit (args[1]);
        break;
      case SYS_EXEC:
        assert_valid_args (args, 2);
        assert_valid_user_string ((const char *) args[1]);
        f->eax = exec ((const char *) args[1]);
        break;
      case SYS_WAIT:
        assert_valid_args (args, 2);
        f->eax = wait (args[1]);
        break;
      case SYS_HALT:
        halt ();
        break;
      case SYS_PRACTICE:
        assert_valid_args (args, 2);
        f->eax = practice (args[1]);
        break;
      case SYS_CREATE:
        assert_valid_args (args, 3);
        assert_valid_user_string ((const char *) args[1]);
        f->eax = create ((const char *) args[1], (unsigned) args[2]);
        break;
      case SYS_REMOVE:
        assert_valid_args (args, 2);
        assert_valid_user_string ((const char *) args[1]);
        f->eax = remove ((const char *) args[1]);
        break;
      case SYS_OPEN:
        assert_valid_args (args, 2);
        assert_valid_user_string ((const char *) args[1]);
        f->eax = open ((const char *) args[1]);
        break;
      case SYS_FILESIZE:
        assert_valid_args (args, 2);
        f->eax = filesize ((int) args[1]);
        break;
      case SYS_READ:
        assert_valid_args (args, 4);
        assert_valid_user_buffer ((void *) args[2], (size_t) args[3]);
        f->eax = read ((int) args[1], (void *) args[2], (unsigned) args[3]);
        break;
      case SYS_WRITE:
        assert_valid_args (args, 4);
        assert_valid_user_buffer ((void *) args[2], (size_t) args[3]);
        f->eax = write ((int) args[1], (void *) args[2], (unsigned) args[3]);
        break;
      case SYS_SEEK:
        assert_valid_args (args, 3);
        seek ((int) args[1], (unsigned) args[2]);
        break;
      case SYS_TELL:
        assert_valid_args (args, 2);
        f->eax = tell ((int) args[1]);
        break;
      case SYS_CLOSE:
        assert_valid_args (args, 2);
        close ((int) args[1]);
        break;
      case SYS_CACHE_RESET:
        cache_reset();
        break;
      case SYS_CACHE_HIT_COUNT:
        f->eax = cache_hit_count();
        break;
      case SYS_CACHE_MISS_COUNT:
        f->eax = cache_miss_count();
        break;
      case SYS_CACHE_READ_COUNT:
        f->eax = block_read_count(fs_device);
        break;
      case SYS_CACHE_WRITE_COUNT:
        f->eax = block_write_count(fs_device);
        break;
      default:
        printf ("Unknown System call number: %d\n", args[0]);
    }
}

void
exit (int status)
{
  struct thread *t = thread_current ();
  printf("%s: exit(%d)\n", (char *) &t->name, status);
  
  if (t->info != NULL)
    t->info->exit_status = status;
  
  thread_exit ();
}

int
exec (const char *cmd_line)
{
  lock_acquire (&global_filesys_lock);
  int pid = process_execute (cmd_line);
  lock_release (&global_filesys_lock);
  return pid;
}

int
wait (int pid)
{
  return process_wait (pid);
}

void
halt ()
{
  shutdown_power_off();
}

int
practice (int i)
{
  return ++i;
}

bool
create (const char *file, unsigned initial_size)
{
  lock_acquire (&global_filesys_lock);
  bool status = filesys_create (file, initial_size,false);
  lock_release (&global_filesys_lock);
  return status;
}

bool
remove (const char *file)
{
  lock_acquire (&global_filesys_lock);
  bool status = filesys_remove (file);
  lock_release (&global_filesys_lock);
  return status;
}

int
open (const char *file)
{
  lock_acquire (&global_filesys_lock);
  struct file *file_entry = filesys_open (file);
  if (file_entry == NULL)
    {
      lock_release(&global_filesys_lock);
      return -1;
    }
  int fdnum = add_file_to_current_thread(file_entry);
  lock_release (&global_filesys_lock);
  return fdnum;
}

int
filesize (int fd)
{
  lock_acquire (&global_filesys_lock);
  struct file_descriptor *file_entry = get_file_from_current_thread (fd);
  if (file_entry)
    {
      int size = file_length (file_entry->file);
      lock_release (&global_filesys_lock);
      return size;
    }
  lock_release (&global_filesys_lock);
  return -1;
}

int
read (int fd, void *buffer, unsigned length)
{
  lock_acquire (&global_filesys_lock);
  if (fd == STDIN_FILENO)
  {
    unsigned i;
    char *input_data = (char *) buffer;
    for (i = 0; i < length; i++)
      {
        input_data[i] = input_getc();
      }
    lock_release (&global_filesys_lock);
    return length;
  }

  struct file_descriptor *file_entry = get_file_from_current_thread (fd);
  if (file_entry)
    {
      int num_read = file_read(file_entry->file, buffer, length);
      lock_release (&global_filesys_lock);
      return num_read;
    }
  lock_release (&global_filesys_lock);
  return -1;
}

int
write (int fd, const void *buffer, unsigned length)
{
  lock_acquire (&global_filesys_lock);
  if (fd == STDOUT_FILENO)
  {
    putbuf(buffer, length);
    lock_release (&global_filesys_lock);
    return length;
  }

  struct file_descriptor *file_entry = get_file_from_current_thread (fd);
  if (file_entry)
    {
      int num_written = file_write(file_entry->file, buffer, length);
      lock_release (&global_filesys_lock);
      return num_written;
    }
  lock_release (&global_filesys_lock);
  return -1;
}

void
seek (int fd, unsigned position)
{
  lock_acquire (&global_filesys_lock);
  struct file_descriptor *file_entry = get_file_from_current_thread (fd);
  if (file_entry)
    {
      file_seek (file_entry->file, position);
    }
  lock_release (&global_filesys_lock);
}

unsigned
tell (int fd)
{
  lock_acquire (&global_filesys_lock);
  struct file_descriptor *file_entry = get_file_from_current_thread (fd);
  if (file_entry)
    {
      unsigned pos = file_tell (file_entry->file);
      lock_release (&global_filesys_lock);
      return pos;
    }
  lock_release (&global_filesys_lock);
  return -1;
}

void
close (int fd)
{
  lock_acquire (&global_filesys_lock);
  struct file_descriptor *file_entry = get_file_from_current_thread (fd);
  if (file_entry)
    {
      file_close (file_entry->file);
      list_remove (&file_entry->elem);
      free (file_entry);
    }
  lock_release (&global_filesys_lock);
}

int
add_file_to_current_thread (struct file *file_entry)
{
  struct file_descriptor *new_fd = malloc (sizeof (struct file_descriptor));
  new_fd->file = file_entry;
  new_fd->fd = thread_current ()->next_fd++;
  list_push_back(&thread_current ()->open_files, &new_fd->elem);
  return new_fd->fd;
}

struct file_descriptor *
get_file_from_current_thread (int fd)
{
  struct list_elem *elem;
  for (elem = list_begin (&thread_current ()->open_files);
       elem != list_end (&thread_current ()->open_files);
       elem = list_next (elem))
    {
      struct file_descriptor *file_entry = list_entry (elem, struct file_descriptor, elem);
      if (fd == file_entry->fd)
        {
          return file_entry;
        }
    }
  return NULL;
}

void
close_all_files_of_current_thread ()
{
  struct list_elem *elem, *next_elem;
  elem = list_begin (&thread_current ()->open_files);
  while (elem != list_end (&thread_current ()->open_files))
  {
    next_elem = list_next (elem);
    struct file_descriptor *file_entry = list_entry (elem, struct file_descriptor, elem);
    close(file_entry->fd);
    elem = next_elem;
  }
}

void
assert_valid_user_address (const void *address)
{
  bool valid = address != NULL && is_user_vaddr (address) &&
    pagedir_get_page (thread_current ()->pagedir, address);
  if (!valid)
    {
      exit (-1);
    }
}

void
assert_valid_user_buffer (const void *start, size_t size)
{
  char *address = (char *) start;
  while (size--)
    {
      assert_valid_user_address (address);
      address++;
    }
}

void
assert_valid_user_string (const char *start)
{
  do
    {
      assert_valid_user_address (start);
      start++;
    } while (*start);
}

void
assert_valid_args (uint32_t *args, size_t count) {
  assert_valid_user_buffer ((void *) args, count * sizeof (uint32_t));
}

void
cache_reset(){
  cache_flush();
  cache_reset_hit_miss();
}

int
cache_hit_count(){
  return cache_hit;
}

int
cache_miss_count(){
  return cache_miss;
}



