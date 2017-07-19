# -----------------   findRoot  ----------------------------- 
#Valerio Zhang 260568861
#  arguments:
#  a       in $f12
#  c       in $f13
#  epsilon in $f14

#  returns result in $f0

findRoot:
	addi 	$sp, $sp, -20 
	sw 	$ra, 0($sp) 
	swc1 	$f12, 4($sp) 
	swc1 	$f13, 8($sp) 
	swc1 	$f14, 12($sp) 

	add.s 	$f4, $f12, $f13 # a + c
	
	li 	$t3, 2 #create divisor
	mtc1 	$t3,$f5  
	cvt.s.w $f5, $f5 # changed to float
	
	div.s 	$f6, $f4, $f5 # (a + c)/2
	
	mov.s 	$f7, $f12 #set $f7 = a to preserve value
	
	mov.s 	$f12, $f6 
	jal 	evaluate #p(midpoint)
	mov.s 	$f10, $f0 #set $f10 to p(midpoint)
	
	addi 	$t4, $zero, 0 #create conditional
	mtc1 	$t4, $f8
	cvt.s.w $f8, $f8  #turn to float

	c.eq.s 	$f10, $f8 #if p(midpoint) == 0
	bc1t 	returnb 

	sub.s 	$f9, $f7, $f13 #a - c
	abs.s	$f9, $f9 #abs(a - c)
	
	c.lt.s 	$f9, $f14 #else if abs(c-a) < epsilon
	bc1t 	returnb 

	mov.s 	$f12, $f7 #move a into argument
	jal 	evaluate 
	mul.s 	$f20, $f0, $f10 #$f20 = p(a)p(b)
	
	c.lt.s 	$f8, $f20 #if 0 < p(a)p(b)
	bc1t 	aequalsb 
	j 	cequalsb 
aequalsb:
	mov.s 	$f12, $f6 
	j 	recursion 
cequalsb:
	mov.s 	$f13, $f6 
recursion: 
	jal 	findRoot 
returnb:
	lw 	$ra, 0($sp) 
	addi 	$sp, $sp, 20 
	mov.s 	$f0, $f6 #return b
	jr 	$ra 

