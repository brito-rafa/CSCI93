# VPC Assembler Language - CSCI-93 Brito
# Question 3 and 5 PS4
# inputstring is the routine that will display the sequence of char on memory address on $a0 to the IO
# inputchar is the routine that will display the value of $a0 (argument register) to the IO
# loops until \n is entered
# memory labels are set on 0-memorylabels.asm

inputstring:
-- the routine will start to read from IO and store value on memory at address on $a0 register
-- will store the array of numbers until new line (\n) is encountered.
-- \n in ascii is "10" in decimal.Update: doing java raw tty, carrier return is 0xD (13)
-- $r7 is the return address stored by the main caller
RAND $g3 $g3 $z0
ROR  $g3 $g3 $a0 # $g3 got the memory address of $a0
RAND $g0 $g0 $z0
IADD $g0 $g0 13 -- load $g0 with 0xD as ascii value of carriage return
RAND $g1 $g1 $z0
IADD $g1 $g1 10 -- load $g1 with 0xA as ascii value of newline
RAND $a0 $a0 $z0 -- zero a0 for the next routine
SRJAL $r5 $r5 inputchar
-- at this time $a0 got some value and must be stored on memory. 
SRSRL $a0 $a0 16
WSR $a0 $g3
JEQ  $a0 $g0 $r7 -- Return to caller if input is carriare return/ $r7 is set my main caller.
JEQ  $a0 $g1 $r7 -- Return to caller if input is newline/ $r7 is set my main caller.
IADD $g3 $g3 4 -- the word is 4 bytes, we are storing one char per word. Increment by 4.
RAND  $a0 $a0 $z0
ROR  $a0 $a0 $g3 
SRJAL $r6 $r6 inputstring

inputchar:
RAND $g4 $g4 $z0
IADD $g4 $g4 0x2bad
SILU $g4 0x2bad -- now $g4 gets 2BAD2BAD
SIBEQ $z0 $z0 inputonbuffer
-- return to caller only if $a0 got somevalue and it is not "2bad"
JNEQ $a0 $g4 $r5 -- r5 is set on inputstring
-- loop routine if operation has not be done
SRJAL $r4 $r4 inputchar

inputonbuffer:
LW $a0 $t1 REG_IOBUFFER_1 -- $a0 gets the value of REG_IOBUFFER_1
JR $ra -- return to the caller - this is the most elementary routine
