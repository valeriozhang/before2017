
# --------------------------------------------------
#    REMOVE CODE ABOVE HERE WHEN YOU PREPARE TO SUBMIT.    YOU MUST ONLY SUBMIT THE 
#    FUNCTION findRankK AND YOU MUST DO SO IN A SEPARATE FILE  findRankK.asm THAT ONLY
#   HAS YOUR CODE BELOW (INCLUDING LABEL findRankK).   THIS FILE WILL BE USED FOR TESTING.
#   THE GRADER WILL CALL YOUR findRankK FUNCTION FROM A MAIN PROGRAM THAT IS DIFFERENT FROM ABOVE.  
findRankK:  
	#   $a0 holds k,  the index of the desired element
	#   $a1 is the base address of list  (list is size L words, buffer of size L words 
	#    - so array is of size 2L words)
	#   $a2 holds the length L of the list
	#
	
	sub 	$t2, $a2, 1 					#$t2 = list.length-1
	sll 	$t2, $t2, 2 					#$t2 = last element of list
	
	add 	$t8, $a1, 4 					#set offest(list)
	lw   	$t6, 0($t8) 					#$t6 = list[1]

	li 	$t0, 1 						#i = 1;
	li 	$t4, 0 						#int low = 0;
	sub 	$t5, $a2, 1 					#int high = len-1;
	lw  	$t3, 0($a1) 					#int pivot  = list[0];
	li 	$t1, 0 						#first element of list
	
								#  Copy elements from list into the buffer, making L1, L2, L3.
								#  Elements smaller than the pivot go in the low part of the buffer.
								#  Elements larger than the pivot go in the high part of the buffer.
								#  Fill space in between with L2 (pivot). 
	add	$sp, $sp, -16					#buffer int[] partitionedList = new int[len];
	sw	$ra, 12($sp) 					#return address
	sw	$a0, 8($sp)					#user enterred k
	sw	$a1, 4($sp) 					#base address of list
	sw	$a2, 0($sp) 					#length of list
	
firstWhileLoop:
	beq 	$t0, $a2, exitfirstWhileLoop 
	
		blt 	$t6, $t3, iLessThanPivot 		#if (list[i] < pivot){
		bgt 	$t6, $t3, iGreaterThanPivot 		#else if (list[i] > pivot){
		
	add 	$t0, $t0, 1 					#i++
	j 	firstWhileLoop 
	
iLessThanPivot:							#if (list[i] < pivot){
	sw 	$t6, partitionedList($t1) 			#partitionedList[low] = list[i]; build list lessthan

	add 	$t8, $t8, 4 					#list[i++] second element
	
	lw   	$t6, 0($t8) 					#$t6 = list[0]
	
	add 	$t0, $t0, 1 					#i++
	add 	$t4, $t4, 1 					#low++;
	add 	$t1, $t1, 4					#low++
	
	j	firstWhileLoop 					

iGreaterThanPivot: 						#else if (list[i] > pivot){
	sw    	$t6, partitionedList($t2)			#build list greater
	sub 	$t2, $t2, 4 					#list is built
	
	add 	$t8, $t8, 4 					# get next element
	lw    	$t6, 0($t8) 					#first element
	
	add 	$t0, $t0, 1 					#i++
	add 	$t5, $t5, -1 					#high++;
	
	j 	firstWhileLoop
			
exitfirstWhileLoop:
	move  	$t0, $t4 					#i = low;

secondWhileLoop:						#while  (i <= high){
	bgt 	$t0, $t5, ifKLessThanLow 			#if  (i >= high){
	
	sw 	$t3, partitionedList($t1) 			#partitionedList[i] = pivot ;
	
	add 	$t1, $t1, 4 					#get next element
	add 	$t0, $t0, 1 					#i++;
	
	j 	secondWhileLoop 				
	
ifKLessThanLow: 						#if (k < low){
	li   	$t0, 0 						#set $t0 to be i now for for loop
	li 	$t1, 0 						#reset $t1 which is offset
	
	bge    	$a0, $t4, ifKLessThanLow2 			#if (k >= low){		
								#The partitionedList array will have L1,L2,L3 in 
								#[0,low-1],[low,high], [high+1,len-1]
								#Now make a new list to be used for the recursion.
								#In the MIPS code, we will use the original array space
								#to hold this list.
firstForLoop: 							#for (i = 0; i < low; i++){
	beq 	$t0, $t4, Recursion 				#if i=low then return findRankK(k, newlist);
	
	lw 	$t8, partitionedList($t1) 			#$t8 takes partitionedlist[i]
	sw  	$t8, list($t1) 					#newlist[i] = partitionedList[i];
	
	add 	$t1, $t1, 4 					#partitionedlist[i++]
	add 	$t0, $t0, 1					#i++
	
	j 	firstForLoop

Recursion:							#return findRankK(k, newlist);
	move 	$a2, $t0 					#copy length for next list					
	jal 	findRankK 					#recursion call
	
	lw 	$ra, 12($sp) 					#load return address
	lw 	$a0, 8($sp) 					#load user enttered k
	lw 	$a1, 4($sp) 					#load base address of list[]
	lw 	$a2, 0($sp) 					#load list.length
	
	add 	$sp, $sp, 16 					#close stack
	jr 	$ra 
	
ifKLessThanLow2:  
	bgt 	$a0, $t5, elseIf 				#k > high ; branch to elseif
	
	move  	$v0, $t3
	
	jr 	$ra 

elseIf:
	add 	$t5, $t5,1 					#high ++

	move  	$t0, $t5 					#$t0 = high
	
	
secondForLoop: 							#for (i = high+1; i < len; i++){
	beq 	$t0, $a2, Recursion2 				#i = l.length branch go to recursion call
	
	move 	$t6, $t0 					#$t6 = i
	sll 	$t6, $t6, 2 					#offset + address
	
	
	sub 	$t2, $t0, $t5 					#$t2 = i - high++
	move 	$t2, $t2 					#Copy the difference into register $t2
			
	sll 	$t2,$t2, 2 					#get offset of i
	
	lw 	$t8, partitionedList($t6) 			#loading partioned list
	sw 	$t8, list($t2) 					#newlist[i-(high+1)] =  partitionedList[i];}
			
	add 	$t0, $t0, 1 					#i++
	
	j 	secondForLoop 
	
Recursion2:							#return findRankK(k - (high+1), newlist);
	sub 	$t7, $a0, $t5  
	move 	$a0, $t7 
	sub 	$a2, $a2, $t5 
	
	jal 	findRankK 					#return findRankK(k - (high+1), newlist);
	
	lw 	$ra, 12($sp) 					#load return
	lw 	$a0, 8($sp) 					#load user k
	lw 	$a1, 4($sp)					#load base of list[]
	lw 	$a2, 0($sp) 					#load list.length
	
	add 	$sp, $sp, 16  					#close stack
	
	jr 	$ra 
			
	
	
	
