.data  # start data segment with bitmapDisplay so that it is at 0x10010000
.globl bitmapDisplay # force it to show at the top of the symbol table
bitmapDisplay:  .space 0x80000 # Reserve space for memory mapped bitmap display
bitmapBuffer:   .space 0x80000 # Reserve space for an "offscreen" buffer
width:          .word 512 # Screen Width in Pixels
height:         .word 256 # Screen Height in Pixels

spaceString: .asciiz " "
newlineString: .asciiz "\n"

vector: .space 32
testResult: .space 32
testResult2: .space 32

M: .float
331.3682, 156.83034, -163.18181, 1700.7253
-39.86386, -48.649902, -328.51334, 1119.5535
0.13962941, 1.028447, -0.64546686, 0.48553467
0.11424224, 0.84145665, -0.52810925, 6.3950152


R: .float
 0.9994 0.0349 0 0
-0.0349 0.9994 0 0
0 0 1 0
0 0 0 1

.text

main:
	addi $sp, $sp, 20
	sw $ra, ($sp)

	la $s7, LineData
	la $a1, ($s7)

loopForever:
	jal drawTeapot
	jal copyBuffer
	jal clearBuffer
	jal rotateTeapot
	jal loopForever


rotateTeapot:
	addi $sp, $sp, 20
	sw $ra, ($sp)

	la $s7, LineData
	la $a1, ($s7)

	li $t7, 0
	sw $t7, 16($sp)
	li $t2, 18432

rotateLoop:
	lw $t7, 16($sp)
	li $t2, 18432
	beq $t7, $t2, endRotate
	la $a0, R

	sw $a1, 8($sp)
	la $a2, testResult
	jal mulMatrixVec
	#jal printFloatVector

	l.s $f0, ($a2)
	l.s $f2, 4($a2)
	l.s $f4, 8($a2)
	l.s $f6, 12($a2)

	s.s $f0, ($a1)
	s.s $f2, 4($a1)
	s.s $f4, 8($a1)
	s.s $f6, 12($a1)



	lw $a1, 8($sp)
	addi $a1, $a1, 16 #increment
	sw $a1, 8($sp)

	la $a0, R

	lw $t7, 16($sp)
	addi $t7, $t7, 16
	sw $t7, 16($sp)
	la $a2, testResult
	jal mulMatrixVec


	l.s $f8, ($a2)
	l.s $f10, 4($a2)
	l.s $f12, 8($a2)
	l.s $f14, 12($a2)

	s.s $f8, ($a1)
	s.s $f10, 4($a1)
	s.s $f12, 8($a1)
	s.s $f14, 12($a1)


	lw $a1, 8($sp)
	addi $a1, $a1, 16
	sw $a1, 8($sp)

	lw $t7, 16($sp)
	addi $t7, $t7, 16
	sw $t7, 16($sp)

	jal rotateLoop


endRotate:
	li $v0, 10
	lw $ra, ($sp)
	addi $sp, $sp, -20
	jr $ra
	syscall

drawTeapot:
	addi $sp, $sp, 20
	sw $ra, ($sp)

	la $s7, LineData
	la $a1, ($s7)

	li $t7, 0
	sw $t7, 16($sp)
	li $t2, 18432

loopDraw:
	lw $t7, 16($sp)
	li $t2, 18432
	beq $t7, $t2, endDraw
	la $a0, M

	sw $a1, 8($sp)
	la $a2, testResult
	jal mulMatrixVec


	l.s $f0, ($a2)
	l.s $f2, 4($a2)
	l.s $f4, 8($a2)
	l.s $f6, 12($a2)

	div.s $f16, $f0, $f6 #divide x by w
	div.s $f18, $f2, $f6 #divide y by w
	cvt.w.s $f16, $f16
	cvt.w.s $f18, $f18



	#do same with second line of matrix



	lw $a1, 8($sp)
	addi $a1, $a1, 16
	sw $a1, 8($sp)

	lw $t7, 16($sp)
	addi $t7, $t7, 16
	sw $t7, 16($sp)
	#la $a1, 16($s7)
	la $a0, M
	la $a2, testResult
	jal mulMatrixVec

	l.s $f8, ($a2)
	l.s $f10, 4($a2)
	l.s $f12, 8($a2)
	l.s $f14, 12($a2)


	div.s $f20, $f8, $f14 #divide x by w
	div.s $f22, $f10, $f14 #divide y by w

	cvt.w.s $f20, $f20
	cvt.w.s $f22, $f22

	mfc1 $s1, $f16 #put x1 and y1 into s0, s1
	mfc1 $s2, $f18
	mfc1 $s3, $f20 #put x1 and y1 into s0, s1
	mfc1 $s4, $f22

	jal drawLine

	lw $a1, 8($sp)
	addi $a1, $a1, 16
	sw $a1, 8($sp)

	lw $t7, 16($sp)
	addi $t7, $t7, 16
	sw $t7, 16($sp)


	jal loopDraw

endDraw:

	li $v0, 10
	lw $ra, ($sp)
	addi $sp, $sp, -20
	jr $ra
	syscall

clearBuffer:
	#move $a0, $s1 # load color into a0 from t7
	addi $sp, $sp, 8

	li $a1, 0x10090000 #load  fist   pixel into temp registar / base address
	li $a2, 0x00000100 #branching conditions, rows
	li $a3, 0x00000200 #branching conditions, columns
	li $s0, 0x00000000

	li $t1, 0 # x = 0 //columnscounter (0-511)
	li $t2, 0 # y = 0 // rowscounter (0-255)

initialization:
	sll $t3, $t2, 9 #multiply y by 512
	addu $t3, $t3, $t1 #add x + (512 * y)
	sll $t3, $t3, 2 #multiply (x+wy) by 4
	addu $t0, $a1, $t3 #add 4(x+wy) to base address to find pixel we're changing
	j loop

loop:
	beq $t1, $a3, doneRow #branch if x = 511
	beq $t2, $a2, end

	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t0, $t0, 4 #add 4(x+wy) to base address to find pixel we're changing
	sw $s0, ($t0) #load color into this pixel

	addi $t1, $t1, 16 #add 1 to x
	j initialization #jump to loop

doneRow:
	addi $t2, $t2, 1 #add one to y when the row is done
	li $t1, 0 #set x back to 0
	j loop #continue loop

end:
	jr $ra
	syscall


copyBuffer:
	li $t3, 0x10010000 #first place to copy to
	li $a1, 0x10090000

	li $a2, 0x00000100 #branching conditions, rows
	li $a3, 0x00000200 #branching conditions, columns

	li $t1, 0 # x = 0 //columnscounter (0-511)
	li $t2, 0 # y = 0 // rowscounter (0-255)

loop2:
	beq $t1, $a3, doneRow2 #branch if x = 511
	beq $t2, $a2, end2

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	sw  $t4, ($t3) #load t3 into t4
	addi $t3, $t3, 4 #move to next pixel
	addi $a1, $a1, 4 #move to next pixel
	lw $t4, ($a1)

	add $t1, $t1, 16 #add 1 to x
	j   loop2 #jump to loop

doneRow2:
	addi $t2, $t2, 1 #add one to y when the row is done
	li   $t1, 0 #set x back to 0
	j    loop2 #continue loop

end2:
	jr $ra
	syscall




#x0 in t5, y0 in t6, x1 in t7, t1 in t8
drawLine:
	addi $sp, $sp, 20
	sw $ra, ($sp)
	li $t5, 1 #offset x
	li $t6, 1 #offset y
	li $s0, 0

	move $s0, $s1 #x0
	move $s1, $s2 #y0
	move $s2, $s3 #x1
	move $s3, $s4 #y1


	sw $s0, 8($sp)
	sw $s1, 12($sp)
	sw $s2, 16($sp)
	sw $s3, 20($sp)

	sub $t7, $s2, $s0 #int dx = x1 - x0
	sub $t8, $s3, $s1 #int dy = y1-y0

	blt $t7, $zero, FirstIfX
	blt $t8, $zero, SecondIfY

	addu $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	addu $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint

	lw $s0, 8($sp)
	lw $s1, 12($sp)
	lw $s2, 16($sp)
	lw $s3, 20($sp)
	lw $ra, 4($sp)

	bgt $t7, $t8, ThirdIfdxgtdy
	add $t9, $zero, $t8 #set error to dy
	bne $s1, $s3, ifdygtdx
	j end4

FirstIfX:
	sub $t7, $zero, $t7 #set dx to negative dx
	li $t5, -1  #set offset to -1
	blt $t8, $zero, SecondIfY

	addu $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	addu $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint

	lw $s0, 8($sp)
	lw $s1, 12($sp)
	lw $s2, 16($sp)
	lw $s3, 20($sp)
	lw $ra, ($sp)

	bgt $t7, $t8, ThirdIfdxgtdy
	add $t9, $zero, $t8 #set error to dy
	bne $s1, $s3, ifdygtdx
	j end4


SecondIfY:
	sub $t8, $zero, $t8 #set dy to negative dy
	li $t6, -1  #set offset to -1

	addu $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	addu $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint

	lw $s0, 8($sp)
	lw $s1, 12($sp)
	lw $s2, 16($sp)
	lw $s3, 20($sp)
	lw $ra, ($sp)

	bgt $t7, $t8, ThirdIfdxgtdy
	add $t9, $zero, $t8 #set error to dy
	bne $s1, $s3, ifdygtdx
	j end4



ThirdIfdxgtdy:
	add $t9, $zero, $t7 #load error into t9
	bne $s0, $s2, loop1
	j end4

loop1:
	sll $s6, $t8, 1
	sub $t9, $t9, $s6
	bltz  $t9, restOfLoop
	add $s0, $s0, $t5 #x = x + offsetx
	sw $s0, 8($sp)
	sw $s1, 12($sp)
	addu $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	addu $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint
	bne $s0, $s2, loop1
	j end4

restOfLoop:
	add $s1, $s1, $t6 #y = y + offsety
	sll $s6, $t7, 1
	add $t9, $t9, $s6
	add $s0, $s0, $t5 #x = x + offsetx
	sw $s0, 8($sp)
	sw $s1, 12($sp)
	add $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	add $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint
	bne $s0, $s2, loop1
	j end4

ifdygtdx:
	sll $s6, $t7, 1
	subu $t9, $t9, $s6
	bltz  $t9, restOfLoop2
	add $s1, $s1, $t6 #y = y + offsety
	sw $s0, 8($sp)
	sw $s1, 12($sp)
	add $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	add $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint
	bne $s1, $s3, ifdygtdx
	j end4

restOfLoop2:
	add $s0, $s0, $t5 #x = x + offset x
	sll $s6, $t8, 1
	add $t9, $t9, $s6
	add $s1, $s1, $t6 #y = y + offsety
	sw $s0, 8($sp)
	sw $s1, 12($sp)
	add $s4, $zero, $s0 #set s4 to x0 so that drawpoint can work
	add $s5, $zero, $s1 #set s5 to x1 so that drawpoint can work
	jal drawPoint
	bne $s0, $s2, ifdygtdx
	j end4

end4:
	lw $ra, ($sp)
	addi $sp, $sp, -20
	jr $ra
	syscall


mulMatrixVec:

	l.s $f0, ($a1) #load first testVec1 value
	l.s $f1, 4($a1) #load second testVec
	l.s $f2, 8($a1)	#load third
	l.s $f3, 12($a1) #load fourth


	l.s $f4, ($a0) #load first testmatrix value
	mul.s $f4, $f4, $f0 #f4 = position 1 * position 1


	l.s $f5, 4($a0) #load second testmatrix value
	mul.s $f5, $f5, $f1 #multiply second
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f6, 8($a0) #load third testmatrix value
	mul.s $f5, $f6, $f2 #multiply third
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f7, 12($a0) #load fourth testmatrix value
	mul.s $f5, $f7, $f3 #multiply fourth
	add.s $f4, $f4, $f5 #add to what we had

	s.s $f4, ($a2) #store first result into testVector at position 1
	mtc1 $zero, $f4 #bring f4 back to 0

	l.s $f4, 16($a0) #load first testmatrix value
	mul.s $f4, $f4, $f0 #f4 = position 1 * position 1

	l.s $f5, 20($a0) #load second testmatrix value
	mul.s $f5, $f5, $f1 #multiply second
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f6, 24($a0) #load third testmatrix value
	mul.s $f5, $f6, $f2 #multiply third
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f7, 28($a0) #load fourth testmatrix value
	mul.s $f5, $f7, $f3 #multiply fourth
	add.s $f4, $f4, $f5 #add to what we had

	s.s $f4, 4($a2) #store first result into testVector at position 2
	mtc1 $zero, $f4

	l.s $f4, 32($a0) #load first testmatrix value
	mul.s $f4, $f4, $f0 #f4 = position 1 * position 1

	l.s $f5, 36($a0) #load second testmatrix value
	mul.s $f5, $f5, $f1 #multiply second
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f6, 40($a0) #load third testmatrix value
	mul.s $f5, $f6, $f2 #multiply third
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f7, 44($a0) #load fourth testmatrix value
	mul.s $f5, $f7, $f3 #multiply fourth
	add.s $f4, $f4, $f5 #add to what we had

	s.s $f4, 8($a2) #store first result into testVector at position 3
	mtc1 $zero, $f4 #bring f4 bcak to 0

	l.s $f4, 48($a0) #load first testmatrix value
	mul.s $f4, $f4, $f0 #f4 = position 1 * position 1

	l.s $f5, 52($a0) #load second testmatrix value
	mul.s $f5, $f5, $f1 #multiply second
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f6, 56($a0) #load third testmatrix value
	mul.s $f5, $f6, $f2 #multiply third
	add.s $f4, $f4, $f5 #add to what we had

	l.s $f7, 60($a0) #load fourth testmatrix value
	mul.s $f5, $f7, $f3 #multiply fourth
	add.s $f4, $f4, $f5 #add to what we had

	s.s $f4, 12($a2) #store first result into testVector at position 3
	mtc1 $zero, $f4 #bring f4 bcak to 0

	jr $ra
	syscall



#$s4 = x pixel, $s5 = y pixel
drawPoint:
	li $a0, 0xE7B2E7 #load green into a0
	#li $s4, 220 #x pixel
	#li $s5, 25 #y pixel

	li $a2, 0x00000100 #registers max y value
	li $a3, 0x00000200 #register max x value

	sltu $t1, $s4, $a3
	sltu $t2, $s5, $a2

	beq $t1, $zero, end3
	beq $t2, $zero, end3

	li $t3, 0x10090000 #base address

	sll $t4, $s5, 9 #multiply y by 512
	addu $t4, $t4, $s4 #add x + (512 * y)
	sll $t4, $t4, 2 #multiply (x+wy) by 4
	addu $t4, $t3, $t4 #add 4(x+wy) to base address to find pixel we're changing

	sw $a0, ($t4) #load color into this pixel
	jr $ra

end3:
	jr $ra
