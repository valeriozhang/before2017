#include <stdio.h>
#include <math.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>
/* it was working but not anymore don't know why */

FILE *readfiles;
char src [100], destination2[100];

	void receive(char* src, char* chat){
		FILE* buffer= fopen(src, "r");
			if (buffer != NULL){
				fgets(src, 100, buffer);
				strcpy(destination2, src);
				fclose(buffer);
			}
		while (1){
			FILE* buffer1 = fopen (src, "r");
			if (buffer1 == NULL){
				continue;
			}
		fgets(src, 100, buffer1);
		if (strcmp( src, destination2) != 0)
		{
		printf("received: %s", src);
		break;
		}
		fclose(buffer1);
		}
		}

 void send(char* destination, char* chat){
                printf("Send: ");
                char src[100], chat2[100];
                destination2 [0] = '\0';
                fgets(src,100,stdin);
                strcat(chat2, "[");
                strcat(chat2, chat);
                strcat(chat2, "]");
                strcat(chat2, src);
                FILE* buffer = fopen(destination, "w");
                fputs(chat2, buffer);
                fclose(buffer);
                        }

	int main(int argc, char *argv[]){
		if (argc != 4){
			printf("Your input is not correct \n");
			return(0);
		}
		char src[100], destination[100], chat[100];
		strcpy(src, argv[1]);
		strcpy(destination, argv[2]);
		strcpy(chat, argv[3]);
		readfiles = fopen(src, "r");
		int basis = 0;
	while (readfiles == NULL){
	if (basis == 0){
		printf("No destinations in inbox. \n");
		basis++;
			}
		send(destination, chat);
		receive(destination, chat);
		fclose(readfiles);
		readfiles = fopen(src, "r");
				}
		basis = 0;
	while (readfiles != NULL){
	if (basis == 0){
		printf("Received: ");
		FILE* buffer2 = fopen(src, "r");
		char buffer3[100];
		fgets (buffer3, 100, buffer2);
		printf ("%s", buffer3);
		fclose(buffer2);
		basis++;
		}
	send(src, chat);
	receive(src, chat);
	fclose(readfiles);
	readfiles = fopen(src, "r");
		}
	}	
