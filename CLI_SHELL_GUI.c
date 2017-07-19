	#include <stdio.h>
	#include <unistd.h>
	#include <string.h>
	#include <stdlib.h>
	#include <sys/wait.h> //implements waitpid()
	#include <fcntl.h>	//implements open() close()
	#include <math.h>

	//
	// This code is given for illustration purposes. You need not include or follow this
	// strictly. Feel free to writer better or bug free code. This example code block does not
	// worry about deallocating memory. You need to ensure memory is allocated and deallocated
	// properly so that your shell works without leaking memory.
	//
	struct jobEntry { //for each job take in each 
	  char *comcalls[20]; //each command used
	  struct jobEntry *nxtJob; //point to space for next job
	  int count; //count
	  pid_t pid; //creates pid to allow
	  int id; //creates id 1-10 for history fun
	};

	struct histEntry { //create a struct to implement history must take in 
	  char *comcalls[20]; //each history entry
	  int   valid; //if it exists
	  int   id; //id for history item
	  int   count; //count for history items
	};

	int getComm(char *prompt, char *args[], int *background){ //get command from prompt
	    int length, i = 0; 
	    char *token, *loc;
	    char *line;
	    size_t linecap = 0;

	    printf("%s", prompt);
	    length = getline(&line, &linecap, stdin);

	    if (length <= 0) {
	        exit(-1);
	    }


	    if ((loc = index(line, '&'))  != NULL) {
	        *background = 1; //background exists
	        *loc = ' ';
	    } else
	        *background = 0;

	    while ((token = strsep(&line, " \t\n"))  != NULL) {
	        for (int j = 0; j < strlen(token); j++)
	            if (token[j] <= 32)
	                token[j] = '\0';
	        if (strlen(token) > 0)
	            args[i++] = token;
	    }
	    args[i++] = NULL;

	    return i;
	}

	int logCom(struct histEntry history[], char *cmd[], int id, int valid, int cnt){ //logs coms into history array 
		//and increase index
	  struct histEntry newEntry;

	  memcpy(newEntry.comcalls, cmd, 50); //copy command string
	  newEntry.id = id; //set id of copied history item to new obj
	  newEntry.valid = valid; //copy valid
	  newEntry.count = cnt; //copy count

	  if(id <= 10){ //limit to 10 items in history
	    history[id-1] = newEntry; //make the index equal to the history call
	  }
	  else{
	    for (int i=0; i<9; i++){ //if more than 10 items
	      history[i] =  history[i+1]; //begins increment the history index by one
	    }
	    history[9] = newEntry; 
	  }
	  return 0;
	}

	void free_args(char *args[], int cnt){
    	for (int i = 0; i < cnt; i++)
        args[i] = NULL;     
	}
	int logJob(struct jobEntry *jobParam, char *cmd[], int count, pid_t pid, int id){  //jobs
		//add job to
	  struct jobEntry *job; //new job
	  job = jobParam; //equal jobParam
	  while(job -> nxtJob != NULL){ //reset pointer to next job if it exists
	    job = job -> nxtJob;
	  }
	  job -> nxtJob = malloc(sizeof(struct jobEntry)); //release memory
	  job = job -> nxtJob; //
	  memcpy(job -> comcalls, cmd, 50); //make copy of command
	  //make the new job equal to the old job
	  job -> id = id;
	  job -> pid = pid;
	  job -> count = count;
	  job -> nxtJob = NULL; //remove nextjob
	  return 0;
	}

	void pullHistory(struct histEntry history[], int id){ 
	  printf("Your last 10 history items:\n");
	  for(int i = 0; i < id-1 && i < 10; i++){
	    printf("!%d:  ", history[i].id);
	      for(int j = 0; j < history[i].count-1; j++){ 
	      	printf("%s ", history[i].comcalls[j]);
	      }
	      	printf("\n");
	    }
	}

	void completedJobs(struct jobEntry *head){ //after completion remove job for queue
	  struct jobEntry *ptr = head;
	  struct jobEntry *temp = ptr;
	  int status;
	  int reset;

	  while(ptr -> nxtJob  != NULL){ //reset pointer to next job
	    ptr = ptr -> nxtJob;
	    reset = waitpid(ptr -> pid, &status, WNOHANG); //reset if no child has exited

	    if( reset  != 0){ //move pointer to next job
	      temp -> nxtJob = ptr -> nxtJob;
	    }

	    else{
	      temp = ptr; //keep pointer as is
	    }
	  }
	}

	void viewJobs(struct jobEntry *head){
	  struct jobEntry *ptr;
	  completedJobs(head);
	  ptr = head;
	  printf("Your current jobs queued:\n");

	  while(ptr -> nxtJob  != NULL){
	    ptr = ptr -> nxtJob;
	    printf("job id %d: ",ptr -> id);

	    for(int j = 0; j<(ptr -> count)-1; j++){
	        printf("%s ", ptr -> comcalls[j]);
	      }

	    printf("\n");
	  }
	}

	void Pipe(char *args[20], char *command[40]){
		int fd[2];
		pid_t pid;

		pipe(fd);

		switch (pid = fork()) { 
		case 0: //kid
				dup2(fd[1], 1);	 //duplicate write file
				close(fd[0]); //close write
				execvp(args[0], args);  //exec args
				close(fd[1]); //close write
				exit(EXIT_SUCCESS);
		default: //dad
				dup2(fd[0], 0); //dup read
				close(fd[1]); //close read
				int s;
				waitpid(pid, &s, 0);
				execvp(command[0], command); //exec command
				close(fd[0]);
				exit(EXIT_SUCCESS);
		case -1:
				printf("something went wrong");
				exit(EXIT_FAILURE);

		}
	}

	int main()
	{
	    struct histEntry history[10];
	    struct jobEntry *jEntryPtr;
	    jEntryPtr = malloc(sizeof(struct jobEntry));
	    jEntryPtr -> count = 0;
	    jEntryPtr -> nxtJob = NULL;
	    int histInd = 1;
	    int bgid = 1;
	    char *args[20];
	    int bg,status, cnt, pullHistEntry, stdoutHolder, fd;
	    pid_t pid;
	    int notvalid, redirect;

	    /* the steps can be..:
		(1) fork a child process using fork()
		(2) the child process will invoke execvp()
		(3) if background is not specified, the parent will wait,
		otherwise parent starts the next command... */

	    while(1){
	      notvalid = 0;
	      redirect = 0;
	      cnt = getComm("\n>>  ", args, &bg); //get commmands

	      //HISTORY FUN
	      //need to take args[0] as a string parse it, look for the "1", remove the "!", and continue
	      //  F
	      //	I
	      //		N
	      //			I
	      //				S
	      //					H
	      //							BELOW
	      if (args[0] == '\0') { //prevent enter from breaking
	      	continue;
	      }

	      if(args[0][0] == '!'){
	      	pullHistEntry = atoi(args[0]+1); //pullHistEntry = history values
	      	printf("%i", pullHistEntry);

	  		//pullHistEntry = atoi(args[0]);

	      if(pullHistEntry > 0 && args[1] == NULL){ //reusing history commands
	        for(int i = 0;  i < histInd; i++){
	          if(pullHistEntry == history[i].id && history[i].valid == 1){ //if pullHistEntry exists in history table
	            memcpy(args, history[i].comcalls,50); //copy command calls
	            notvalid = 0;
	            break;
	          }
	          else if(pullHistEntry < histInd - 10 || pullHistEntry >= histInd){
	            printf("no command found in history.\n");
	            notvalid = 1;
	            break;
	          }
	          else if(history[i].valid == 0) {
	            printf("error with command call.\n");
	            notvalid = 1;
	            break;
	          }
	        }
	      }
	  }

	      if (strcmp(args[0],"cd") == 0 && args[1]  != NULL && args[2] == NULL){
	        int reset=0;
	        char path[100], *ptr;
	        long size;
	        size = pathconf(".",_PC_PATH_MAX);
	        ptr = getcwd(path, (size_t)size);
	        printf("%s\n", ptr);
	        strcat(path,"/");
	        strcat(path, args[1]);
	        printf("Your current directory now is: %s\n", ptr);
	        reset = chdir(ptr);
	        if(!reset){
	        	logCom(history, args, histInd, 1, cnt);
	          }
	        else{
	        	logCom(history, args, histInd, 0, cnt);
	          }

	        histInd++;
	        notvalid = 1;
	      }

	      if (strcmp(args[0], "exit") == 0 && args[1] == NULL){ //exit when exit is typed
	        printf("exiting...\n");
	        return 0;
	      }

	      if (strcmp(args[0], "jobs") == 0 && args[1] == NULL){ //print current queued jobs when jobs is typed
	        viewJobs(jEntryPtr);
	        logCom(history, args, histInd, 1, cnt);
	        notvalid = 1;
	        histInd++;
	      }

	      if (strcmp(args[0], "fg") == 0 && args[1] != NULL && args[2] == NULL){ //bring bg jobs to fg
	        struct jobEntry *ptr;
	        int bg2fg = atoi(args[1]); //take in first arg
	        completedJobs(jEntryPtr); //remove the first job
	        ptr = jEntryPtr; //reset pointer

	        while (ptr -> nxtJob != NULL){ //reset pointer to nxt job
	          ptr = ptr -> nxtJob;

	          if(ptr -> id == bg2fg){ //if nxt job is item wanted to bring to fg wait for pid

	            while(waitpid(ptr -> pid, &status, 0) != ptr -> pid);

	            if(!status){
	              logCom(history, args, histInd, 1, cnt);
	            }

	            else{
	              logCom(history, args, histInd, 0, cnt);
	            }
	          }
	        }

	        histInd++;
	        notvalid = 1;

	      }

	      if(cnt >= 4){ //redirect 

	      	if (strcmp(args[cnt-3],">") == 0 && args[0]  != NULL && args[cnt-2]  != NULL && args[cnt-1] == NULL){
	        redirect = 1;
	        fd = open(args[cnt-2], O_RDWR | O_APPEND | O_TRUNC | O_CREAT , S_IRUSR | S_IWUSR | S_IXUSR | S_IRGRP | S_IWGRP | S_IWOTH | S_IROTH);
	        stdoutHolder = dup(1);
	        dup2(fd, 1);
	        args[cnt-3] = NULL;
	        cnt = cnt - 2;

	   		 }
	  	}

	        //pipe	      
			//add pipe code here strcmp ("|")

	  	/*
		  	int p, fk;
		  	char *LHS[50];
		  	char *RHS[50];
		  	int c;

		  	for ( int i = 0 ; i < cnt; i++){
		  		if ((strcmp(args[i], "|") == 0)){
		  			c = i
		  	  		fk = 1;
		  	  		LHS[i] = args[i];
		  	  		}
		  	  		for (int i = c; i < cnt-; i++){
		  	  			RHS[i] = args[i];;
		  	  		}
		  	  	}
		  	 Pipe(LHS[i], RHS[i]);

		*/
	

          	///

		  
	      printf("\n");

	      if(!notvalid){
	        pid = fork(); //

	        if(pid == 0){

	          if (strcmp(args[0], "pwd") == 0 && args[1] == NULL){ //print current working dir
	            long size;
	            char *buffer;
	            char *ptr;
	            size = pathconf(".",_PC_PATH_MAX);

	            if((buffer = (char *)malloc((size_t)size))  != NULL){
	              ptr = getcwd(buffer, (size_t)size);
	            
	            printf("%s\n", ptr);
	            free(buffer); //deallocate memory
	            exit(0);
	        }
	          }

	          if (strcmp(args[0], "history") == 0 && args[1] == NULL){ //print history
	            pullHistory(history,histInd); 
	            exit(0);
	          }

	          if(execvp(*args, args) < 0){ //after for execvp will change program associated with child proc
	            exit(1);
	          }
	        }

	        else{

	          if(bg){ //if job in background
	            logJob(jEntryPtr, args, cnt, pid, histInd); //log job 
	            logCom(history, args, histInd, 1, cnt); //log command to history
	          }

	          else{
	            while(waitpid(pid, &status, 0) != pid); //waitpid

	            if(!status){
	              logCom(history, args, histInd, 1, cnt);
	            }

	            else{
	              logCom(history, args, histInd, 0, cnt);

	            }
	          }
	        }

	        histInd++;
	      }
	      if(redirect){

	        dup2(stdoutHolder,1);
	        close(fd);

	      }
	      printf("\n");

		}
	}