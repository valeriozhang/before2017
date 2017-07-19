
# -----------------   findRoot  ----------------------------- 
#Valerio Zhang 260568861
#  arguments:
#  a       in $f12
#  c       in $f13
#  epsilon in $f14

#  returns result in $f0 or root

findRoot:
	addi 	$sp, $sp, -20
	sw 	$ra, 0($sp) 
	swc1 	$f12, 4($sp) 
	swc1 	$f13, 8($sp) 
	swc1 	$f14, 12($sp) 
while:
	li 	$t1, 0 # n = 0 conditional
	mtc1 	$t1, $f8 #int to float
	cvt.s.w $f8, $f8 #$f8 = 0
	
	li 	$t0, 2  #set divisor
	add.s 	$f4, $f12, $f13 #a+c
	mtc1 	$t0, $f5  #set int to float
	cvt.s.w $f5, $f5  #$f5 = 2
	div.s 	$f6, $f4, $f5 # $f6 = (a + c)/2
	
	mov.s 	$f21, $f12 #$f21 = a
	mov.s	$f12, $f6
	jal	evaluate 
	mov.s	$f23, $f0 #$f23 = p(b) 
	mov.s	$f12, $f21 # a = $f21
	
	c.eq.s	$f23, $f8 #p(b)==0
	bc1t	returnb
	
	sub.s 	$f10, $f12, $f13 #a-c
	abs.s	$f10, $f10 #$f10 = abs(a-c)
	
	c.lt.s	$f10, $f14 #abs(a-c) < epsilon
	bc1t	returnb
	
	mov.s	$f21, $f12 #a = $f21
	mov.s	$f12, $f12 #set $f12 to a
	jal	evaluate
	mov.s	$f20, $f0 #$f20 = p(a)
	mov.s	$f12, $f21 #$f21 = a
	
	mul.s	$f24, $f20, $f23 #$f24 = p(a)p(b)
	
	c.le.s	$f8, $f24 	#If ((0 < p(a)p(b)) Then
	bc1t	aequalsb
	j	cequalsb
	
	j 	while
aequalsb:
	mov.s 	$f12, $f6 # $a = b
	j	while		
cequalsb:
	mov.s	$f13, $f6 #c = b
	j	while
returnb:
	lw 	$ra, 0($sp) 
	mov.s 	$f0, $f6 # return midpoint $f6
	add 	$sp, $sp, 16
	jr 	$ra 

#    ADD YOUR CODE HERE.   ONLY SUBMIT THIS CODE (INCLUDING findRoot LABEL above).   
#    WE WILL TEST YOUR CODE WITH DIFFERENT STARTER CODE. 
