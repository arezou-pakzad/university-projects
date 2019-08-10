#ifndef USERPROG_SYSCALL_H
#define USERPROG_SYSCALL_H

#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>

struct file;

void syscall_init (void);
void assert_valid_user_address (const void *address);
void assert_valid_user_buffer (const void *start, size_t size);
void assert_valid_user_string (const char *start);
void assert_valid_args (uint32_t *args, size_t count);

void exit (int status);
int practice (int i);
void halt (void);
int exec (const char *cmd_line);
int wait (int pid);

bool create (const char *file, unsigned initial_size);
bool remove (const char *file);
int open (const char *file);
int filesize (int fd);
int read (int fd, void *buffer, unsigned length);
int write (int fd, const void *buffer, unsigned length);
void seek (int fd, unsigned position);
unsigned tell (int fd);
void close (int fd);

int add_file_to_current_thread(struct file *file_entry);
struct file_descriptor *get_file_from_current_thread (int fd);
void close_all_files_of_current_thread (void);

void cache_reset(void);
int cache_hit_count(void);
int cache_miss_count(void);

int cache_read_count(void);

int cache_write_count(void);

#endif /* userprog/syscall.h */
