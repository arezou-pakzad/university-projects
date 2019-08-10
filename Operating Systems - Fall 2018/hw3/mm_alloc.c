#include "mm_alloc.h"
#include <stdlib.h>
#include <stdbool.h>
#include <memory.h>
#include <unistd.h>

s_block_ptr head = NULL;

void* mm_malloc(size_t size)
{
    if(size <= 0)
        return NULL;
    if(head == NULL){
        if ((head = sbrk(size + BLOCK_SIZE)) == (void *) -1){
            head = NULL;
            return NULL;
        }
        head->size = size;
        head->is_free = false;
        head->prev = NULL;
        head->next = NULL;
        head->ptr = &(head->data[0]);
        memset(head->ptr, 0, size);
        return head->ptr;
    }
    s_block_ptr p = head;
    s_block_ptr last_block = head;
    while(p != NULL) {
        if(p->is_free && p->size >= size){
            split_block(p, size);
            memset(p->ptr, 0, size);
            return p->ptr;
        }
        last_block = p;
        p = p->next;
    }

    s_block_ptr new_block = last_block->ptr + last_block->size;
    if (sbrk(size + BLOCK_SIZE) == (void*) -1){
        return NULL;
    }
    new_block->size = size;
    new_block->next = NULL;
    new_block->prev = last_block;
    last_block->next = new_block;
    new_block->is_free = false;
    new_block->ptr = &(new_block->data[0]);
    memset(new_block->ptr, 0, size);
    return new_block->ptr;
}

void split_block (s_block_ptr b, size_t s) {
    if (b == NULL)
        return;

    if ((size_t)(b->size - s) <  BLOCK_SIZE){
        b->size = s;
        b->is_free = false;
        b->ptr = &(b->data[0]);
        return;
    }

    s_block_ptr split_block = b->ptr + s;
    split_block->size = b->size - s - BLOCK_SIZE;
    split_block->is_free = true;
    split_block->prev = b;
    split_block->next = b->next;
    split_block->ptr = &(split_block->data[0]);

    if(b->next != NULL)
        b->next->prev = split_block;

    b->size = s;
    b->is_free = false;
    b->next = split_block;
    b->ptr = &(b->data[0]);
}

void* mm_realloc(void* ptr, size_t size)
{
    if (size <= 0){
        mm_free(ptr);
        return NULL;
    }

    if (ptr == NULL)
        return mm_malloc(size);

    s_block_ptr src_block = get_block(ptr);
    void *dest_block = mm_malloc(size);

    if (src_block == NULL)
        return NULL;

    if (dest_block == NULL)
        return NULL;

    if (size < src_block->size)
        memcpy(dest_block, ptr, size);
    else
        memcpy(dest_block, ptr, src_block->size);

    mm_free(ptr);

    return dest_block;
}

void mm_free(void* ptr)
{
    if (ptr == NULL)
        return;

    s_block_ptr to_be_freed = get_block(ptr);
    if (to_be_freed == NULL)
        return;

    to_be_freed->is_free = true;
    fusion(to_be_freed);
}

s_block_ptr fusion(s_block_ptr b) {
    if (b == NULL)
        return NULL;

    if (b->next != NULL) {
        if (b->next->is_free) {
            b->size = b->next->ptr - b->ptr + b->next->size;
            b->next = b->next->next;
            if (b->next != NULL)
                b->next->prev = b;
        }
    }

    if (b->prev != NULL){
        if (b->prev->is_free) {
            b->prev->size = b->ptr - b->prev->ptr + b->size;
            b->prev->next = b->next;
            if (b->next != NULL)
                b->next->prev = b->prev;
            return b->prev->ptr;
        }
    }

    return b->ptr;
}


s_block_ptr get_block (void *p) {
    if (head == NULL)
        return NULL;

    if (p == NULL)
        return NULL;

    if (p < head->ptr || p > sbrk(0))
        return NULL;

    s_block_ptr this_block = (s_block_ptr)(p - (void *)BLOCK_SIZE);

    if (this_block->ptr != &(this_block->data[0]))
        return NULL;

    return this_block;
}