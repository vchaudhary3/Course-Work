////////////////////////////////////////////////////////////////////////////////
// Main File:        mem.c
// This File:        mem.c
// Other Files:
// Semester:         CS 354 Spring 2018
//
// Author:           Vedantika Chaudhary
// Email:            vchaudhary3@wisc.edu
// CS Login:         vedantika
//
/////////////////////////// OTHER SOURCES OF HELP //////////////////////////////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          Identify persons by name, relationship to you, and email.
//                   Describe in detail the the ideas and help they provided.
//
// Online sources:   avoid web searches to solve your problems, but if you do
//                   search, be sure to include Web URLs and description of
//                   of any information you find.
////////////////////////////////////////////////////////////////////////////////
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <string.h>
#include "mem.h"

/*
 * This structure serves as the header for each allocated and free block
 * It also serves as the footer for each free block
 * The blocks are ordered in the increasing order of addresses
 */
typedef struct blk_hdr {

    int size_status;

    /*
    * Size of the block is always a multiple of 8
    * => last two bits are always zero - can be used to store other information
    *
    * LSB -> Least Significant Bit (Last Bit)
    * SLB -> Second Last Bit
    * LSB = 0 => free block
    * LSB = 1 => allocated/busy block
    * SLB = 0 => previous block is free
    * SLB = 1 => previous block is allocated/busy
    *
    * When used as the footer the last two bits should be zero
    */

    /*
    * Examples:
    *
    * For a busy block with a payload of 20 bytes (i.e. 20 bytes data + an additional 4 bytes for header)
    * Header:
    * If the previous block is allocated, size_status should be set to 27
    * If the previous block is free, size_status should be set to 25
    *
    * For a free block of size 24 bytes (including 4 bytes for header + 4 bytes for footer)
    * Header:
    * If the previous block is allocated, size_status should be set to 26
    * If the previous block is free, size_status should be set to 24
    * Footer:
    * size_status should be 24
    *
    */

} blk_hdr;

/* Global variable - This will always point to the first block
 * i.e. the block with the lowest address */
blk_hdr *first_blk = NULL;

/*
 * Function for allocating 'size' bytes
 * Returns address of allocated block on success
 * Returns NULL on failure
 * Here is what this function should accomplish
 * - Check for sanity of size - Return NULL when appropriate
 * - Round up size to a multiple of 8
 * - Traverse the list of blocks and allocate the best free block which can accommodate the requested size
 * - Also, when allocating a block - split it into two blocks
 * Tips: Be careful with pointer arithmetic
 */
void* Mem_Alloc(int size) {

    // MAX_ALLOC_SIZE
    int MAX_ALLOC_SIZE = 4080;
    // final size of blaock after adding padding
    int final_Size;
    // size of current block
    int curr_size;
    // status of current block
    int curr_status;
    // pointer to current block
    blk_hdr *current = NULL;
    // best block
    blk_hdr *best_blk = NULL;
    // best block size
    int best_blk_size = 5000;// very big to make sure that it gets decremented
    // best block status
    int best_blk_status = 1000;
    // to make sure that best is assigned at least once

    if (size < 0){
      return NULL;
    }
    // getting final size
    final_Size = size + sizeof(blk_hdr);
    int padding;
    if (final_Size%8 == 0){
        padding = 0;
    }
    else{
        padding = 8 - (final_Size % 8);
    }
    final_Size = final_Size + padding;

    if (final_Size > MAX_ALLOC_SIZE){
      return NULL;
    }
    // pointing to first block
    current = first_blk;

    // finding best_fit block
    while (current->size_status!=1 ){
      curr_status = current->size_status & 3;
      
      curr_size = current->size_status - curr_status;

      if (curr_status == 0 || curr_status == 2){
          if (curr_size < best_blk_size && curr_size >= final_Size){
            best_blk = current;
            best_blk_size = curr_size;
            best_blk_status = curr_status;
          }
      }
      current = (blk_hdr*) ((char*)current + curr_size);
    }

    // if no free block of enough size if found then exit
    if (best_blk == NULL || best_blk_size == 5000 || best_blk_status == 1000 ){
      return NULL;
    }

    // allocating requested memory
    blk_hdr *allocated_blk = NULL;
    blk_hdr *allocated_blk_hrd = NULL;
    int alloc_size = final_Size;

    // previous block is allocated so allocated block should store that
    if (best_blk_status == 2){
      alloc_size = alloc_size +2;
    }

    // Case 1: Splitting needed
    if (best_blk_size > final_Size){

      int rem_size = best_blk_size - final_Size;
      //remaining free block cannot be less than 8 bytes
      if (rem_size < 8){
        return NULL;
      }

      allocated_blk_hrd = best_blk;
      allocated_blk_hrd->size_status = alloc_size + 1;
      // have ptr point to payload and not blk_hdr
      allocated_blk = (blk_hdr*)((char*)best_blk + sizeof(blk_hdr));

      // updating values in next remining free block
      blk_hdr *next_blk = (blk_hdr*) ((char*)best_blk + final_Size);
      next_blk->size_status = rem_size + 2;// since previous block is now allocated

      blk_hdr *footer = (blk_hdr*)((char*)next_blk + rem_size - sizeof(blk_hdr));
      footer->size_status = rem_size;
    }
    // Case 2: Splitting not needed
    else{
      allocated_blk_hrd = best_blk;
      allocated_blk_hrd->size_status = alloc_size + 1;
      // have ptr point to payload and not blk_hdr
      allocated_blk = (blk_hdr*) ((char*)best_blk + sizeof(blk_hdr));

      // update next blk
      blk_hdr *next_blk = (blk_hdr*) ((char*)best_blk + best_blk_size);
        next_blk->size_status = next_blk->size_status + 2;
    }
    return (void*)allocated_blk;
}

/*
 * Function for freeing up a previously allocated block
 * Argument - ptr: Address of the block to be freed up
 * Returns 0 on success
 * Returns -1 on failure
 * Here is what this function should accomplish
 * - Return -1 if ptr is NULL or not within the range of memory allocated by Mem_Init()
 * - Return -1 if ptr is not 8 byte aligned or if the block is already freed
 * - Mark the block as free
 * - Coalesce if one or both of the immediate neighbours are free
 */
int Mem_Free(void *ptr) {

    // return -1 if ptr is NULL
    if (ptr == NULL){
      return -1;
    }

    /*Ignore*/
    // first_block + 4 >= (cuz points to accessible memoru so should have a block before it)
    // (char*)first_block + total_mem_size - 16


    // validity of address by pointer
    blk_hdr *ptr1 =first_blk;
    while (!(ptr1->size_status == 1)) {
        ptr1 = (blk_hdr *)((char *)ptr1 +
                    ((ptr1->size_status) - (ptr1->size_status%8)));
    }
    blk_hdr *end_mark = ptr;
    // Return if ptr is less than first_blk or greater than or equal to end_mark
    if ((int)ptr < (unsigned long int)first_blk
                   &&
                   (unsigned long int)ptr >= (int)end_mark) {
        return -1;
    }


    // return -1 if ptr if not double aligned
    ptr1 = (blk_hdr*) ((char*)ptr - sizeof(blk_hdr));
   // tempSizeStatus = tempHdrPtr->size_status;
   int  realSize = ptr1->size_status - (ptr1->size_status%8);

    if ((((int)ptr)%8) != 0 && ((realSize & 1) == 0)) {
        return -1;
    }


    blk_hdr *toFree_blk_hdr = (blk_hdr*) ((char*)ptr - sizeof(blk_hdr));
    int toFree_blk_status = (toFree_blk_hdr->size_status) & 3;
    /*if toFree_blk_status = 3 then prev blk is allocated so forget about it
      if toFree_blk_status = 1 then that means previous is free so coalesce it*/
    int toFree_blk_size = (toFree_blk_hdr->size_status) - toFree_blk_status;
    blk_hdr *toFree_blk_ftr = (blk_hdr*)((char*)toFree_blk_hdr + toFree_blk_size - sizeof(blk_hdr));

    // if current block is already free
    if (toFree_blk_status == 0 || toFree_blk_status == 2){
      return -1;
    }
    // the block is not free so we need to free it and coalesce surrounding blocks
    else {
      // info about next blk can be acquired regardless of its status
      blk_hdr* next_blk_hrd = (blk_hdr*)((char*)toFree_blk_hdr + toFree_blk_size);
      int next_blk_status = (next_blk_hrd->size_status) & 3;
      /*If next_blk_status = 2 then next blk is free so coalesce it
        if next_blk_status = 3 then next blk is allocated so ignore it*/
      int next_blk_size = (next_blk_hrd->size_status) - next_blk_status;
      blk_hdr* next_blk_ftr = NULL;
      // ftr of nxt blk is assigned only if it is free
      if (next_blk_status == 2){
        next_blk_ftr = (blk_hdr*)((char*)next_blk_hrd + next_blk_size - sizeof(blk_hdr));
      }

      // info about prev blk assigned only if prev is free
      blk_hdr* prev_blk_ftr = NULL;
      blk_hdr* prev_blk_hrd = NULL;
      int prev_blk_status;
      int prev_blk_size;
      if (toFree_blk_status == 1){
        prev_blk_ftr = (blk_hdr*)((char*)toFree_blk_hdr - sizeof(blk_hdr));
        prev_blk_size = prev_blk_ftr->size_status;
        prev_blk_hrd = (blk_hdr*)((char*)toFree_blk_hdr - prev_blk_size);
        prev_blk_status = (prev_blk_hrd->size_status) & 3;
      }

      // if both next and prev are allocated
      if (toFree_blk_status == 3 && next_blk_status == 3){
        // to indicate that current is free and prev is allocated
        toFree_blk_status = 2;
        // updating size-status of current
        toFree_blk_hdr->size_status = toFree_blk_size + toFree_blk_status;
        // updating ftr of current (free) blk
        toFree_blk_ftr->size_status = toFree_blk_size;

        // to indicate that next is allocated but current is free
        next_blk_status = 1;
        // updating size_status of next blk
        next_blk_hrd->size_status = next_blk_size + next_blk_status;
      }
      // if next is allocated and prev is free
      else if (toFree_blk_status == 1 && next_blk_status == 3){
        // total free blk is prev + curr
        int total_free = toFree_blk_size + prev_blk_size;
        // ftr of free blk is toFree_blk_ftr
        toFree_blk_ftr->size_status = total_free;

        // updating status of next blk
        next_blk_status = 1;
        // updating size_status of next blk
        next_blk_hrd->size_status = next_blk_size + next_blk_status;

        // updating size_status of prev blk
        prev_blk_hrd->size_status = total_free + prev_blk_status;
      }
      // if next if free and prev is allocated
      else if (toFree_blk_status == 3 && next_blk_status == 2){
        // update toFree_blk_status
        toFree_blk_status = 2;
        // total free size if curr + next
        int total_free = toFree_blk_size + next_blk_size;
        // updating size_status of free blk
        toFree_blk_hdr->size_status = total_free + toFree_blk_status;

        // updating ftr of next
        next_blk_ftr->size_status = total_free;
      }
      // if both next and prev are free
      else if (toFree_blk_status == 1 && next_blk_status == 2){
        // calculating total free size
        int total_free = prev_blk_size + toFree_blk_size + next_blk_size;
        // updating prev hdr
        prev_blk_hrd->size_status = total_free + prev_blk_status;
        // updating next_blk_ftr
        next_blk_ftr->size_status = total_free;
      }
      return 0;
    }
}

/*
 * Function used to initialize the memory allocator
 * Not intended to be called more than once by a program
 * Argument - sizeOfRegion: Specifies the size of the chunk which needs to be allocated
 * Returns 0 on success and -1 on failure
 */
int Mem_Init(int sizeOfRegion) {
    int pagesize;
    int padsize;
    int fd;
    int alloc_size;
    void* space_ptr;
    blk_hdr* end_mark;
    static int allocated_once = 0;

    if (0 != allocated_once) {
        fprintf(stderr,
        "Error:mem.c: Mem_Init has allocated space during a previous call\n");
        return -1;
    }
    if (sizeOfRegion <= 0) {
        fprintf(stderr, "Error:mem.c: Requested block size is not positive\n");
        return -1;
    }

    // Get the pagesize
    pagesize = getpagesize();

    // Calculate padsize as the padding required to round up sizeOfRegion
    // to a multiple of pagesize
    padsize = sizeOfRegion % pagesize;
    padsize = (pagesize - padsize) % pagesize;

    alloc_size = sizeOfRegion + padsize;

    // Using mmap to allocate memory
    fd = open("/dev/zero", O_RDWR);
    if (-1 == fd) {
        fprintf(stderr, "Error:mem.c: Cannot open /dev/zero\n");
        return -1;
    }
     space_ptr = mmap(NULL, alloc_size, PROT_READ | PROT_WRITE, MAP_PRIVATE,
                    fd, 0);
    if (MAP_FAILED == space_ptr) {
        fprintf(stderr, "Error:mem.c: mmap cannot allocate space\n");
        allocated_once = 0;
        return -1;
    }

     allocated_once = 1;

    // for double word alignement and end mark
    alloc_size -= 8;

    // To begin with there is only one big free block
    // initialize heap so that first block meets
    // double word alignement requirement
    first_blk = (blk_hdr*) space_ptr + 1;
    end_mark = (blk_hdr*)((void*)first_blk + alloc_size);

    // Setting up the header
    first_blk->size_status = alloc_size;

    // Marking the previous block as busy
    first_blk->size_status += 2;

    // Setting up the end mark and marking it as busy
    end_mark->size_status = 1;

    // Setting up the footer
    blk_hdr *footer = (blk_hdr*) ((char*)first_blk + alloc_size - 4);
    footer->size_status = alloc_size;
    return 0;
}

/*
 * Function to be used for debugging
 * Prints out a list of all the blocks along with the following information for each block
 * No.      : serial number of the block
 * Status   : free/busy
 * Prev     : status of previous block free/busy
 * t_Begin  : address of the first byte in the block (this is where the header starts)
 * t_End    : address of the last byte in the block
 * t_Size   : size of the block (as stored in the block header)(including the header/footer)
 */
void Mem_Dump() {
    int counter;
    char status[5];
    char p_status[5];
    char *t_begin = NULL;
    char *t_end = NULL;
    int t_size;

    blk_hdr *current = first_blk;
    counter = 1;

    int busy_size = 0;
    int free_size = 0;
    int is_busy = -1;

    fprintf(stdout, "************************************Block list***\
                    ********************************\n");
    fprintf(stdout, "No.\tStatus\tPrev\tt_Begin\t\tt_End\t\tt_Size\n");
    fprintf(stdout, "-------------------------------------------------\
                    --------------------------------\n");

    while (current->size_status != 1) {
        t_begin = (char*)current;
        t_size = current->size_status;

        if (t_size & 1) {
            // LSB = 1 => busy block
            strcpy(status, "Busy");
            is_busy = 1;
            t_size = t_size - 1;
        } else {
            strcpy(status, "Free");
            is_busy = 0;
        }

        if (t_size & 2) {
           strcpy(p_status, "Busy");
            t_size = t_size - 2;
        } else {
            strcpy(p_status, "Free");
        }

        if (is_busy)
            busy_size += t_size;
        else
            free_size += t_size;

        t_end = t_begin + t_size - 1;

        fprintf(stdout, "%d\t%s\t%s\t0x%08lx\t0x%08lx\t%d\n", counter, status,
        p_status, (unsigned long int)t_begin, (unsigned long int)t_end, t_size);

        current = (blk_hdr*)((char*)current + t_size);
        counter = counter + 1;
    }

    fprintf(stdout, "---------------------------------------------------\
                    ------------------------------\n");
    fprintf(stdout, "***************************************************\
                    ******************************\n");
    fprintf(stdout, "Total busy size = %d\n", busy_size);
    fprintf(stdout, "Total free size = %d\n", free_size);
    fprintf(stdout, "Total size = %d\n", busy_size + free_size);
    fprintf(stdout, "***************************************************\
                    ******************************\n");
    fflush(stdout);

    return;
}

