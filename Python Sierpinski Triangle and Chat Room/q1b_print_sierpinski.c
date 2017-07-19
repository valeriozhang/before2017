#include <stdlib.h>
#include <ctype.h>
#include <stdio.h>
#include <math.h>

int make_square(int height, int fractallvl);
int make_triangle(int height, int a, int k, int b, int fractallvl, char square[height][height]);
int printtriangle(int height,char square[height][height*2-1]);

int triangles(int height, int a, int k, int b, int fractallvl, char square[height][height*2-1]){
int i,j;
int z = (2*a-1)/2;
        if (fractallvl == 0) {
                for (i = b; i < a + b; i++){
                        for (j = k ; j < (a*2-1) + k ; j++){
                                if ( j < ( z - i + k + b ) || j > ( z + i + k - b)){
                                        square[i][j]= ' ';
                                                        }
                                else {
                                square[i][j] = '*';
                                        }
                                }
                }
        }
else {
triangles(height, a/2, (a-1)/2 + 1 + k, 0 + b, fractallvl-1, square);
triangles(height, a/2, (a-1)/8 - (a-1)/8 + k, a/2 + b, fractallvl - 1,square);
triangles(height, a/2, (a-1)+1+k, a/2 + b, fractallvl-1, square);
}
return 0;
} 

int maketriangles(int height, char square[height][height*2-1]){
int i,j;

for (i = 0; i < height; i++){
        for (j = 0; j< height*2-1; j++){
                printf("%c", square[i][j]);
                        }
printf("\n");
}
return 0;
}

int emptysquare(int height, int fractallvl) {
		char square[height][height*2-1];
		int i, j;
	for (i=0; i<height; i++) {
				for( j=0; j<height*2-1; j++){
					square[i][j] = ' ';
						}			
	}
	triangles(height,height,0,0,fractallvl,square);
	maketriangles(height, square);
	return 0;
	}

int main(int argc, char *argv[]){
        int val1 = atoi(argv[1]);
        int val2 = atoi(argv[2]);
        if (val1==0 || val2 == 0){
                printf("Error: height and fractal levels must be integer");
                return 0;
                                }

        if ((val1 >> val2) << val2 != val1) {
                printf("error: height must be divisible by their power of fractal levels");
                        }
        else{
        emptysquare(val1, val2);
                }
                        }

