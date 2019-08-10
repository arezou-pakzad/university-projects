
#include <stdbool.h>
#include <devices/block.h>
#include <filesys/filesys.h>
#include <threads/synch.h>
#include <debug.h>

#define CACHE_SIZE 64
//#include "off_t.h" //TODO: Need?

int cache_miss;
int cache_hit;

struct cache_cell {
  uint8_t block_data[BLOCK_SECTOR_SIZE];    /* The block's data. One byte. */
  block_sector_t sector_idx;                /* The block device sector's index */

  bool dirty;                               /* Used for implementing write back policy */
  bool recently_accessed;                   /* Used for eviction. */
  bool valid;                               /* basically equals block_date != NULL */
  int num_using;                            /* Used to guarantee that currently open blocks will not be evicted */

  struct lock cache_lock;
};


void cache_init(void);

void cache_flush(void);

void cache_reset_hit_miss(void);

int cache_search(block_sector_t sector_idx);

int cache_fetch(block_sector_t sector_idx);

int cache_evict(void);

void cache_read(block_sector_t sector_idx, int offset, void *buffer, int size);

void cache_write(block_sector_t sector_idx, int offset, void *buffer, int size);



