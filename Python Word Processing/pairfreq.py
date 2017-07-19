#!/usr/bin/env python
import pickle
import sys
from sys import argv
import re
from string import *

script, arg1 = argv #takes 2 arguments
#conditions
try:
        sys.argv[1]
except IndexError:
        print('doesnt work insert 2 arguments\n')
        exit()



with open(arg1, 'r') as content_file: #open file
	textsplit = content_file.read() #read it
	textsplit = textsplit.lower() #lowercase it
word_list = textsplit.split() #split file put into var word_lists

textsplit = re.sub(r"[^\w\s]+", "", textsplit).split() #remove white space

#print textsplit

freq_dic = {} #creates empty dictionary
 
for i in range( 0, len(textsplit)-1): #counter to itterate
        key = textsplit[i] + ',' + textsplit[i+1] # produces corresponding keys
        try:
                freq_dic[key]+=1 #if
        except:
                freq_dic[key]=1 #if not

for word in freq_dic: 

        print [word], freq_dic[word]


pickle.dump(freq_dic,open(sys.argv[1].split('.')[0]+".pickle","w")) #makepickle

