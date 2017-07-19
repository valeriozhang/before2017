#!/usr/bin/python
import sys
import struct
import copy
import cProfile
import ctypes

linklib = ctypes.cdll.LoadLibrary('libfast_filter.so')
img_in = open( sys.argv[1], 'rb' )
img_in.seek(0,2)
size = img_in.tell() #reads current position pointer gives file size
print size
img_data = [i for i in range(size)] #creates array of size img_in
img_in.seek(0,0) #resets pointer

img_data = img_in.read() #reads img in into img data
out_img_data = " " * len(img_data) #creates out_img data
filter_width = int(sys.argv[3]) #filter width is odd integer

filter_weights = [] #creates empty array
	
for i in range(0, filter_width*filter_width): #iterates through filters
	filter_weights.append(float(sys.argv[4+i])) #appends to array
	print filter_weights[i] #checks

CFloatArrayType = ctypes.c_float * len(filter_weights) #changes to ctypes
cfloat_array_instance = CFloatArrayType( *filter_weights) #changes to ctypes
	
linklib.doFiltering( img_data, cfloat_array_instance, filter_width, out_img_data); #filter function
done = open(sys.argv[2], 'wb') #open file
done.write(out_img_data) #writes to opened file
