
#include <string.h>
#include <filesys/buffer-cache.h>


//struct lock *cache_lock;         /* to ensure synchronization when bringing a block to cache. */


static struct cache_cell cache[CACHE_SIZE];

int clock_idx;

void
cache_init()
{
  int i;
  clock_idx = 0;
  for (i = 0; i < CACHE_SIZE ; ++i) {
    lock_init(&(cache[i].cache_lock));
    cache[i].valid = 0;
    cache[i].num_using = 0;
    cache[i].sector_idx = 0;
    cache[i].dirty = 0;
    cache[i].recently_accessed = false;
  }
}

void
cache_flush(){
  int i = 0;
  for (i = 0; i < CACHE_SIZE; ++i){
    if (cache[i].valid == 1 && cache[i].dirty == 1){
      lock_acquire(&cache[i].cache_lock);
      block_write(fs_device, cache[i].sector_idx, cache[i].block_data);
      cache[i].dirty = 0;
      cache[i].valid = 0;
      lock_release(&cache[i].cache_lock);
    }
  }
}

void cache_reset_hit_miss(){
  cache_miss = 0;
  cache_hit = 0;
}

int
cache_search(block_sector_t sector_idx)
{
  int i;
  for (i = 0; i < CACHE_SIZE; ++i) {
    lock_acquire(&(cache[i].cache_lock));
    if (cache[i].sector_idx == sector_idx && cache[i].valid == 1){
      cache_hit++;
      lock_release(&(cache[i].cache_lock));
      return i;
    }
    lock_release(&(cache[i].cache_lock));
  }
  cache_miss++;
  return -1;
}

int
cache_fetch(block_sector_t sector_idx)
{
  int idx = cache_search(sector_idx);
  if (idx == -1){
    idx = cache_evict();
    if (cache[idx].dirty == 1){
      lock_acquire(&(cache[idx].cache_lock));
      block_write(fs_device, cache[idx].sector_idx, cache[idx].block_data);
      lock_release(&(cache[idx].cache_lock));
    }
    cache[idx].valid = 1;
    cache[idx].dirty = 0;
    cache[idx].sector_idx = sector_idx;
    cache[idx].recently_accessed = 1;
    cache[idx].num_using = 1;
    lock_acquire(&(cache[idx].cache_lock));
    block_read(fs_device, sector_idx, cache[idx].block_data);
    lock_release(&(cache[idx].cache_lock));
    return idx;
  }
  cache[idx].recently_accessed = 1;
  cache[idx].num_using += 1;
  return idx;
}

int
cache_evict()
{
  int i;
  for (i = clock_idx; true ; i = (i + 1)%CACHE_SIZE) {
    if (cache[i].valid == 0){
      clock_idx = i;
      return i;
    }
    if (cache[i].num_using <= 0){
      if (cache[i].recently_accessed){
        cache[i].recently_accessed = 0;
      } else {
        clock_idx = i;
        return i;
      }
    }
    clock_idx = i;
  }
}

void
cache_read(block_sector_t sector_idx, int offset, void *buffer, int size)
{
  int idx = cache_fetch(sector_idx);
  memcpy(buffer, cache[idx].block_data + offset, (size_t) size);
  cache[idx].num_using -= 1;
  cache[idx].recently_accessed = true;
}

void
cache_write(block_sector_t sector_idx, int offset, void *buffer, int size)
{
  int idx = cache_fetch(sector_idx);
  memcpy(cache[idx].block_data + offset, buffer, (size_t) size);
  cache[idx].dirty = 1;
  cache[idx].num_using -= 1;
  cache[idx].recently_accessed = 1;
}