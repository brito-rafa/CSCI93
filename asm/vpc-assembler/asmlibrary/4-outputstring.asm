# VPC Assembler Language - CSCI-93 Brito
# Question 2 and 4 PS4
# outputstring is the routine that will display the sequence of char on memory address on $a0 to the IO
# outputchar is the routine that will display the value of $a0 (argument register) to the IO
# loops until $a0 is not cleared
# $r7 is set by the main function
# the memory labels are set on 0-memorylabels.asm


outputstring:
-- ----------------
-- the routine will start to read from the address on $a0 register
-- save the argument $g3 
-- will output until new line (\n) is encountered. \n is "10" or "13"in decimal
-- Register $r7 will have $ra , set my invokation from main
-- ----------------
RAND $g3 $g3 $z0
ROR  $g3 $g3 $a0 -- $g3 got the memory address of $a0
RAND $g0 $g0 $z0
IADD $g0 $g0 10 -- load $g0 with 10 as ascii value of new line
RAND $g4 $g4 $z0
IADD $g4 $g4 13 -- load $g0 with 10 as ascii value of carriage return
LR $g1 $g3 -- Load the char and put on g1 
RAND $a0 $a0 $z0
ROR $a0 $a0 $g1 -- Copy the $g1 to $a0 as argument for the next routine, which display the char 
SRJAL $r5 $r5 outputchar -- Invoke the routine to output char - $a0 will be used to display the char. Register $r5 and $r6 will have the return address 
-------
-- at this time $a0 got zero'd and there is an output. Beginning the loop 
SIBEQ  $g1 $g4 printnewline -- print a new line if carriage return
JEQ  $g1 $g0 $r7 -- Return to main if $g1 is a new line
JEQ  $g1 $g4 $r7 -- Return to main if $g1 is a carriage return 
IADD $g3 $g3 4 -- get the next memory address 
RAND  $a0 $a0 $z0
ROR  $a0 $a0 $g3 
SRJAL $r6 $r6 outputstring

printnewline:
RAND $g2 $g2 $z0
ROR  $g2 $g2 $ra -- saving $ra
RAND $a0 $a0 $z0
IADD $a0 $a0 10 -- print line
SRJAL $r5 $r5 outputchar 
IADD $a0 $a0 13 -- print line
SRJAL $r5 $r5 outputchar 
JR $g2


outputchar:
SIBEQ $z0 $z0 writeonbuffer
-- return to caller only if $a0 got cleared
JEQ $a0 $z0 $r5
-- loop routine if operation has not been done
SRJAL $r4 $r4 outputchar

writeonbuffer:
-- Storing value of $a0 on REG_IOBUFFER_1 0x00FF04
SRSLL $a0 $a0 16 -- char must be on higher order bit
SW $a0 $t0 REG_IOBUFFER_1
SW $a0 $t0 REG_IOBUFFER_2
--SW $a0 $t0 inputmemory -- troubleshooting
-- clear $a0 to indicate operation is done
RAND $a0 $a0 $z0
-- return to the caller
JR $ra
