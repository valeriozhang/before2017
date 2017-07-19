#include "unistd.h"
#include "stdlib.h"
#include "stdio.h"
#include "semaphore.h"//semaphore lib
#include "sys/mman.h" //memory management
#include "fcntl.h" //file handling
#define MY_SHM "/tmp"

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

int fd;//
Shared* shared_mem;//
int job_ID;
int pages;

int setup_shared_memory(){ //given creating shared mem
    fd = shm_open(MY_SHM, O_RDWR, 0666);
    if(fd == -1){
        printf("shm_open() failure\n");
        exit(1);
    }
}

int attach_shared_memory(){ //given attach shared mem
    shared_mem = (Shared*) mmap(NULL, sizeof(Shared), PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if(shared_mem == MAP_FAILED){
        printf("mmap() failure\n");
        exit(1);
    }

    return 0;
}

void insert_job(Job* job){ 
    (*shared_mem).arr[shared_mem->data] = (*job); //insert job into queue
    shared_mem->data += 1;
}

int main(int argc, char* argv[]) {
    if (argc == 3){
		if(atoi(argv[1]) < 1 || atoi(argv[2]) < 1){
			printf("Parameters need to be correctly formated: a JOB_ID > 0 AND Pages > 0 \n Using default client ID of 0 and pages of 1\n");
		}
		else{
			job_ID = atoi(argv[1]);
			pages = atoi(argv[2]);
			printf("Using JOB_ID: %i and PAGES: %i\n", job_ID, pages);
		}
	}
    else{
        printf("Please enter 3 arguments where the last 2 are positive ints. \n");
        exit(1);
    }

    setup_shared_memory();
    attach_shared_memory();

    Job job = {job_ID, pages}; //add job descriptors

    sem_wait(&shared_mem->full); //lock sem
    sem_wait(&shared_mem->mutex); //lock sem
    
    insert_job(&job); //add a job to queue
    printf("JOB_ID: %i with PAGES: %i has been added to the buffer\n", job.iD, job.pages);

    sem_post(&shared_mem->empty); //unlock sem
    sem_post(&shared_mem->mutex); //unlock sem

    return 0;
}
