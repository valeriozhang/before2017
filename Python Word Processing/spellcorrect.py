#!/usr/bin/env python
from sys import argv
import pickle 
import sys
import difflib

script, arg1 = argv #takes two arguments

try:
        sys.argv[1]
except IndexError:
        print('doesnt work insert 2 arguments\n')
        exit()

dic = pickle.load(open(sys.argv[1], 'r'))
 
wordz = []
for key in dic:
        key = key.replace(',', ' ')
        word1 = key.split()[0]
        word2 = key.split()[1]
        wordz.append(word1)
        wordz.append(word2)

wordz = list(set(wordz))
print dic
while (True):
	spellcheck = raw_input('Please Enter Two Words with a space in between : [word0] [word1]' )
	if (len(spellcheck.split()) != 2):
		print "Please enter two arguments"
		continue
	else:
		word0 = spellcheck.split()[0]
		word1 = spellcheck.split()[1]
		if ((word0 + ',' + word1) in dic) :
			print "spelling is ok"
		else:					
			checkwith = difflib.get_close_matches(word0, wordz, 10)
			print checkwith
#			fix = [0, '']
			bestfit = ''
			bestoccur = 0
			for checkagainst in checkwith:
	                        key = "'"
				key = key +  checkagainst + "," + word1 + "'"
			#	print checkagainst #this works
				if dic[key] > bestoccur:
					bestoccur = dic[key]
					bestfit = checkagainst + " " + word1
					print bestfit + '\n'
			print "Fix:" , bestfit
