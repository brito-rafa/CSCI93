convertstringtointeger:
-- the routine will start to read from the address on $a0 register
-- prior invocation from main caller, set:
-- $a0 with memory address of the input
-- $s0 to 0
-- $s1 to 0

-- reading first character
LR $g0 $a0
RAND $t0 $t0 $z0 
IADD $t0 $t0 45  -- 45 is dash 
SIBEQ $t0 $g0 isnegative -- adjust a0 address and set $s1 as flag
RAND $g2 $g2 $z0 
IADD $g2 $g2 13  -- carriage return is 13 in decimal
RAND $g3 $g3 $z0 
IADD $g3 $g3 10  -- new line in decimal
SRJAL $r6 $r6 readincrement
SIBNEQ $s1 $z0 flipsignal -- adjust a0 address and set $s1 as flag
JR $r7

readincrement:
LR $g0 $a0
JEQ $g2 $g0 $r6 -- back to main if read char is carriage return
JEQ $g3 $g0 $r6 -- back to main if read char is new line
ISUB $g0 $g0 48 -- converting g0 from ascii into integer
RAND $t2 $t2 $z0
RAND $t3 $t3 $z0
ROR $t2 $t2 $s0
ROR $t3 $t3 $s0
SRSLL $t2 $t2 3 -- multiplying $s0 by 4 
SRSLL $t3 $t3 1 -- multiplying $s0 by 2
RAND $s0 $s0 $z0
RADD $s0 $s0 $t2
RADD $s0 $s0 $t3
RADD $s0 $s0 $g0
IADD $a0 $a0 4
SRJAL $r5 $r5 readincrement

isnegative:
IADD $a0 $a0 4
IADD $s1 $s1 1 -- $s1 is the register that will determine if input is negative
JR $ra

flipsignal:
RSUB $s0 $z0 $s0
JR $ra
