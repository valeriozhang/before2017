#!/usr/bin/env python
import csv
import json
import re
import nltk
import sys
from sys import argv
import re
from string import *

script, arg1 = argv #takes two arguments
#conditionals
try:
        sys.argv[1]
except IndexError:
        print('doesnt work insert 2 arguments\n')
        exit()

word_list = re.split('\s+', file(arg1).read().lower()) #creates a list seperated by spaces of file made lowercase

freq_dic = {} #creates empty dictionary
rmvpunctuation = re.compile(r'[.?!\',"():;]') #removes punctuation and compiles making into objects

for word in word_list:
    word = rmvpunctuation.sub("", word) #removes punctuation
    freq_dic[word] = freq_dic.get(word,0) + 1 #gets the value at word

freq_list = freq_dic.items() #prints copy of dictionary lists (key, value) pairs

for word, freq in freq_list: #loops each value
    print word, freq #prints each word and frequency

