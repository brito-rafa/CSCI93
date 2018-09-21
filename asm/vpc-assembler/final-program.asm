# Main program for ps4 and final term

welcomemessage0: .ascii "WELCOME TO THE DEMO PROGRAM OF VULTURE PLATFORM COMPUTER (VPC) "
welcomemessage1: .ascii "Harvard Extension School - CSCI E-93 "
message1: .ascii "Enter First Number.  "
message2: .ascii "Enter Second Number. "
message3: .ascii "Result of Multiplication is ... "
message4: .ascii "The first number entered was "
message5: .ascii "The second number entered was "
inputmemory1: .address "0x0A00"
inputmemory2: .address "0x0B00"
inputmemory3: .address "0x0C00"

main:
-- Resetting registers
SRJAL $r7 $r7 zeroregisters -- zero'ng registers

SIBEQ $z0 $z0 printnewline -- printing a new line
LW $t0 $a0 welcomemessage0 -- $a0 will get the address of the message1 label
SRJAL $r7 $r7 outputstring
SIBEQ $z0 $z0 printnewline -- printing a new line
LW $t0 $a0 welcomemessage1 -- $a0 will get the address of the message1 label
SRJAL $r7 $r7 outputstring
SIBEQ $z0 $z0 printnewline -- printing a new line

-- Display message on screen 
LW $t0 $a0 message1 -- $a0 will get the address of the message1 label
SRJAL $r7 $r7 outputstring
SIBEQ $z0 $z0 printnewline -- printing a new line
-- Read the input and put on inputmemory1
LW $t0 $a0 inputmemory1
SRJAL $r7 $r7 inputstring
SIBEQ $z0 $z0 printnewline -- printing a new line
LW $t0 $a0 message4 
SRJAL $r7 $r7 outputstring -- Confirming input1 on screen 
SIBEQ $z0 $z0 printnewline -- printing a new line
LW $t0 $a0 inputmemory1 
SRJAL $r7 $r7 outputstring

SIBEQ  $z0 $z0 printnewline

-- Display message on screen 
LW $t0 $a0 message2 -- $a0 will get the address of the message2 label
SRJAL $r7 $r7 outputstring
SIBEQ $z0 $z0 printnewline -- printing a new line
-- Read the input and put on inputmemory2
LW $t0 $a0 inputmemory2
SRJAL $r7 $r7 inputstring
-- Display message on screen 
LW $t0 $a0 message5 
SRJAL $r7 $r7 outputstring
SIBEQ  $z0 $z0 printnewline
LW $t0 $a0 inputmemory2 
SRJAL $r7 $r7 outputstring -- Confirming input2 on the screen

SIBEQ  $z0 $z0 printnewline

-- Convert input in integer
RAND $s0 $s0 $z0
RAND $s1 $s1 $z0
LW $t0 $a0 inputmemory1
SRJAL $r7 $r7 convertstringtointeger -- $s0 returns with the number
RAND $r0 $r0 $z0
ROR $r0 $s0 $r0 -- saving on $r0
-- input2 on $r1
RAND $s0 $s0 $z0
RAND $s1 $s1 $z0
LW $t0 $a0 inputmemory2
SRJAL $r7 $r7 convertstringtointeger -- $s0 returns with the number
RAND $r1 $r1 $z0
ROR $r1 $s0 $r1  -- saving on $r1

-- starting the multiplication: setting $a0 and $a1 argument registers
RAND  $a0 $a0 $z0
ROR  $a0 $a0 $r0
RAND  $a1 $a1 $z0
ROR  $a1 $a1 $r1

-- invoking multiplier - $s0 will have the result
RAND $s0 $s0 $z0
RAND $s1 $s1 $z0
IAND $s1 $s1 1
RSLT $s2 $a0 $z0 -- checking the signals of $a0
RSLT $s3 $a1 $z0 -- checking the signals of $a1
SRJAL $r7 $r7 multiply
RAND $r2 $r2 $z0
ROR $r2 $s0 $r2  -- saving multiplier result on $r2

-- manage signal of multiply return
RXOR $s4 $s2 $s3
-- if $s4 is 1, will need to convert signal
RAND $a0 $a0 $z0
RADD $a0 $a0 $r2
SIBNEQ $s4 $z0 convertnegativetopositive -- it takes $a0 as a parameter and returns $s0
RAND $r3 $r3 $z0
ROR $r3 $r3 $s0 -- saving on $r3


SIBEQ  $z0 $z0 printnewline
-- Display message on screen 
LW $t0 $a0 message3 -- $a0 will get the address of the message3 label
SRJAL $r7 $r7 outputstring
SIBEQ  $z0 $z0 printnewline

-- displayint
-- the number is on $r3
-- will convert the number to string and display chars
-- $s0 is flag if the number is negative
RAND  $a0 $a0 $z0
RADD  $a0 $a0 $r3
SRJAL $r7 $r7 displayint
SIBEQ  $z0 $z0 printnewline
SIBNEQ $z0 $z0 main -- main looping
