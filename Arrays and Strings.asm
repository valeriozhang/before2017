# -----------   STARTER CODE -------------------------------

.data	  
	
sizeBuffer:	.word   0	# dummy value:  real value will be entered by user
stringRefArray:	.space 20       # allocate bytes for the array of 5 starting addresses of strings 
strings:	.space 100	# allocate 100 bytes for all the strings		
maxLenStrPrompt:
		.asciiz		"ENTER MAX LENGTH STRING: "	
stringPrompt:
		.asciiz		"ENTER STRING : "	
newline:	.asciiz		"\n"	
numStringsMessage:
		.asciiz		"NUMBER OF STRINGS IS "	
enumerate:	.asciiz		".) "	
MTF:		.asciiz		"MOVE TO FRONT ITEM (ENTER NUMBER) : "
exitMessage:	.asciiz		"BYE FOR NOW !"

.text  
       
main:  	 				


#  print prompt  to enter maximum length of a string
 
	la	$a0, maxLenStrPrompt	
	li	$v0, 4
	syscall 	

#  read maximum length of string buffer,  read value goes into $v0
	li	$v0, 5
	syscall 

#  If user specifies that string is at most N characters, then we need a buffer for 
#  reading the string that is at least size = N+1 bytes.  The reason is that the
#  syscall will append a \0 character.  If the entered string is less than N
#  bytes, then the syscall will append \n\0, that is, a line feed followed by a null terminator.

#   Store the value size = N+1 in Memory.  This is the size of the buffer that syscall needs.

  	add	$v0, $v0, 1  
	la	$t0,  sizeBuffer
	sw 	$v0, 0($t0)

# ---------------  ADD YOUR CODE BELOW HERE  -------------------------------
        add 	$s0, $zero, $zero        #continue if $t0 = 1 counter
        			#set counter
        la 	$s1, stringRefArray	#$s1 has 0 adress of stringRefArray
	
	la 	$t1, strings #t1 has 0 address of strings
	
 	la 	$t2, 0($t0) #$t2 = 0 address of sizeBuffer
loop:
 	#check sizebuffer = sizebuffer enterred
    	slt    	$t0, $t2, $v0 #$t0 = 1 if sizeBuffer < sizeBuffer enterred
    	
        move 	$s2, $v0 #set $s2 to equal sizeBuffer
        
    	bne   	$t0, $zero, loop #if $t0 != 0 goto loop
		
promptString: 
	la 	$a0, stringPrompt	#prompt for string
	li 	$v0, 4	# print
	syscall		
      	
    	add 	$t1, $t1, $t3   #holds address of strings
    	#t3 is offset
    	
    	move   	$a0, $t1 #set $a0 to string address
    
        li 	$v0, 8	#read input 
        lw	$a1, sizeBuffer #limit input to sizeBuffer
        syscall		
        
        subi	$t5, $s2, 1 #$t5 = sizeBuffer - 1 = length of string w/o null
        
        move  	$t4, $a0 #t4 =  string enterred
        
        add	$t2, $zero, $zero #reset to be counter for length
        
stringLength:	
	lb    	$t0,($t4)	#t0 = 0 byte of string enterred
        
	beqz 	$t0, emptyString #if t0 = 0 its an empty string jump to emptystring	
	
	beq 	$t2, $t5, endOfString #if counter reaches end of string go to endOfstring
                    
	add 	$t2, $t2, 1	# string length counter
	
	add 	$t4, $t4, 1 #next byte of string
	
	j 	stringLength	
                    
endOfString:
	lb 	$t8, ($t4) #if end of string relocated pointer
	
	li 	$t7, '\n' #set $t7 = to null terminator
	
	add 	$t2, $t2, 1	    #character +++
	
	sb  	$t7, ($t4) #store nll terminator to $t4 since empty string   
	       	
	beq 	$t8,'\n', emptyString #if 0 byte equals null term jump to emptyString
	
	jal	new_line

emptyString:
        beq 	$t2, 1, numberOfString 	#if char counter = 1 go to numberOfStrings
        add 	$t3, $t2, 1 		# if not move offset
        
        li 	$v0, 4		
        move 	$a0, $t1	#print number of strings
        syscall			
        
        sll 	$t0, $s0, 2     #set t0 to counter offset to 4
        add 	$t0, $t0, $s1    	     #move to next element of array in stringRefARray
        
        sw  	$t1, ($t0)     	   #store t1 to offset($t0) 
        
      add 	$s0, $s0, 1     	 #add 1 to count for strings
      
        j	promptString
            
numberOfString:
	la 	$a0, numStringsMessage	#print number of string message
	li 	$v0, 4 #print str
	syscall	
    
	move 	$a0, $s0	#s0 holds counter for number of strings
	li 	$v0, 1 #print int
	syscall	   
    
	jal	new_line

reset:
    	add 	$t0, $zero, $zero	   #reset t0 
    	
    	la 	$t2, stringRefArray    #set t2 to first element of string array
    
print:
    	beq 	$t0, $s0, moveToFront #go to MTF if counter = number of strings
    
    	move 	$a0, $t0	#print indices
    	li 	$v0, 1 #print int
    	syscall	
    	
        li 	$v0, 4
    	la 	$a0, enumerate #enumerate
    	syscall
    
	lw  	$a0, ($t2)	#print out strings[0]
    	li 	$v0, 4
    	syscall    	
    
    	add 	$t0, $t0, 1	#increase counter for indices
    	
    	add 	$t2, $t2, 4	#next string
    
    	j print
        
moveToFront:   
    	la 	$a0, MTF #print move to front
    	li 	$v0, 4
   	syscall

   	li	$v0, 5 #read int
    	syscall 
    
	slt   	$t0, $v0, $s0 		#set counter to 1 if input value < #of strings
	beq 	$t0, $zero, exit2  #if input > # of strings then BYE for now	

	move 	$t4, $s1   	#set t4 = address of string array
    
	move 	$t0, $v0			#set $t0 to equal input value

	add 	$t2, $zero, $zero #create counter for array elements
		   		         
    	sll 	$t3, $t0, 2     		#input value * 4
    	add 	$t3, $s1, $t3   	#t3 = address of string array + 4
    	lw	$t5, 0 ($t3)     	#t5 = first element in array
    	
    	add 	$t0, $t0, 1 # input value++
    	
restructureArray:
	beq 	$t2, $t0, exit1 #exit if counter = input value
	
	lw 	$t7, ($t4) #load  element
	
	sw  	$t5, ($t4)# overwrite element
	
	move 	$t5, $t7 #set t5 to loaded element
	
	add 	$t4, $t4, 4 #get next string
	
	add 	$t2, $t2, 1 #ct++
	
	j 	restructureArray
	
new_line:
	la 	$a0, newline #new line use jal	new_line
	li 	$v0, 4
	syscall	
	jr	$ra
	
exit1:
    	j	reset	#MTF more than once
    
exit2: #infinite loop without exit2
# ---------------  ADD YOUR CODE ABOVE HERE  ------------------------------
	la	$a0, exitMessage	
	li	$v0, 4
	syscall 

	li	$v0, 10			# exit
	syscall
	nop
