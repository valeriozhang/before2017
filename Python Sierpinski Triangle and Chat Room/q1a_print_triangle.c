#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>



int main(int args, char *argv[] ){
        int i;
        int space;
        int k=0;
	int val;
	val = atoi(argv[1]);

	if ( val == 0 ) {
	printf("Error: Height must be integer \n");
	return 0;
			}
	
	if ( val < 1) {
	printf("Error: Height is too small \n");
	return 0;
			}


	if (args == 2 && val > 0){
	for(i=1;i<=val;++i){

        for(space=1;space <= val-i; ++space){

           printf("  ");}

        while(k!=2*i-1){

           printf("* ");

           ++k;}
        k=0;

        printf("\n");

    }

    return 0;
}
}










