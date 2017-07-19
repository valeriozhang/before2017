#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "fast_filter.h"

int main(int argc, char *argv[]){
        int i;
        unsigned char *img_data1;

        FILE *img_in = NULL; //create pointer to empty file
        img_in = fopen(argv[1], "rb"); //open file read and binary
        if (img_in == NULL) { //check
                return 1;
        }

        //fwrite(img_data, bmp_header, 1, img_data);
        fseek(img_in, 0 , SEEK_END); //read through data giving size
        int size = ftell(img_in); //size is given
	rewind(img_in); //reset pointer
//	int temp = ftell(img_in);
//	printf("%i", temp);
//	printf("%i", size);
        unsigned char *img_data = (unsigned char*) malloc(size); //create empty array
        unsigned char *out_img_data = (unsigned char*) malloc(size); //create empty array

      FILE *img_out = NULL; //empty file
       img_out = fopen(argv[2], "wb"); //write binary into file imgout
         if (img_out == NULL) {
                return 1;
        }

        fread(img_data, size, 1 , img_in); //read through file

//      printf("%d\n", out_img_data);
        int filter_width = atoi(argv[3]); //filter width atoi with argument 3
        float filter_weights[argc-4]; //filter weights

        //for loop for iterating through photo
        for( i = 0 ; i < (argc-4); i = i + 1){ //iterate through filter
                filter_weights[i] = (float) atof(argv[i+4]); //going through filters
//		printf("%f", filter_weights[i]);
        }
//	printf("%i", filter_width);
        doFiltering( img_data, filter_weights, filter_width, out_img_data); //pass through filtering
 //	printf("fsfs");
        fwrite(out_img_data, 1, size, img_out); //write out to img
        fclose(img_in);
        fclose(img_out);
        return 0;
}

