#include "unistd.h"
#include "stdlib.h"
#include "stdio.h"
#include "semaphore.h"//semaphore lib
#include "sys/mman.h" //memory management
#include "fcntl.h" //file handling      
#define MY_SHM "/tmp" //creating a temp shared memory

typedef struct { //creating a typedef Job for job info
    int iD;
    int pages;
} Job;

typedef struct { //creating a typedef Shared for shared jobs
    sem_t mutex;
    sem_t empty;
    sem_t full;
    int data;
    int max;
    Job arr[0];
} Shared;
                      
int fd;
int errno;
int bufferSize = 5;
Shared* shared_mem;

static int keepRunning = 1;

int setup_shared_memory(){ //creates a temp shared memory
    fd = shm_open(MY_SHM, O_CREAT | O_RDWR, S_IRWXU);
    if(fd == -1){
        printf("shm_open() failure\n");
        exit(1);
    }
    ftruncate(fd, sizeof(Shared));
}

int attach_shared_memory(){ //creating an attached memory
    shared_mem = (Shared*)  mmap(NULL, sizeof(Shared), PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if(shared_mem == MAP_FAILED){
        printf("mmap() failure\n");
        exit(1);
    }
    return 0;
}

int init_shared_memory(){ //initilizes a shared memory
    shared_mem->data = 0; //data holds queued items
    shared_mem->max = bufferSize; //max length is equal to user specified buffersize ./printer x
    sem_init(&(shared_mem->mutex), 1, 1); //init sem to mutex
    sem_init(&(shared_mem->empty), 1, 0); //init sem to empty buff
    sem_init(&(shared_mem->full), 1, 0); //init sem to full buff
    for(int i = 0; i < bufferSize; i++){ // when the buffer is full
    	sem_post(&shared_mem->full); //unlocks sem
    }
}

void sleepy(Job* job){
    sleep((*job).pages); // sleep for number of pages
    printf("Completed printing: JOB_ID %i -- %i PAGES.\n", (*job).iD, (*job).pages);
}

void print_jobs_queued(Job* job){ 
    printf("JOB_ID %i is queued with %i PAGES...\n", (*job).iD, (*job).pages); //print job id x with x pages
}

void dequeue(){
    for(int i = 0; i < shared_mem->data; i++){
        shared_mem->arr[i] = shared_mem->arr[i+1]; //remove the first queued item when processing it
    }
    shared_mem->data--;
}

int main(int argc, char* argv[]) {
    if(argc < 2){
        printf("Enter a buffer size for the printer client. Try again.\n"); //need buffer arg
	exit(0);
    }
    else{ //if args entered is <2
        int tmp = atoi(argv[1]); //take second arg as buffer size
        if(tmp > 0){
            bufferSize = tmp;
        }
        else{
            printf("INVALID LENGTH: using default buffer length of 5\n");
        }
    }

    setup_shared_memory();
    attach_shared_memory();
    init_shared_memory();

    for (int i=1; i < bufferSize+1; i++) {
        Job theJob = {1, 1};
        shared_mem->arr[i] = theJob;
        printf("Buffer %d\n", i);
    }

    //Infinite loop
    while (keepRunning) {
        sem_wait(&shared_mem->empty); //locks sem
        sem_wait(&shared_mem->mutex); //locks sem

        Job qJob = shared_mem->arr[0]; //point to first item queued

        int i;
        printf("\nCurrent buffer:\n");
        for(i = 0; i < shared_mem->data; i++){
            printf("> Jobs: %i,		JOB_ID: %i,		PAGES: %i\n", i, shared_mem->arr[i].iD, shared_mem->arr[i].pages);
        }

        print_jobs_queued(&qJob); //print queued jobs
        sleepy(&qJob); //sleep after print queue
        dequeue(); //dequeue

        if(shared_mem->data == 0){ //empty buffer
            printf("Empty buffer; awaiting next process...\n\n"); 
        }

        sem_post(&shared_mem->mutex); //unlocks sem
        sem_post(&shared_mem->full); //unlocks sem

    }
    return 0;
}
