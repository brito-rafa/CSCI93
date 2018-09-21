displayint:
-- display signed integer number passed as $a0
-- it depends on routine outputchar
SIBEQ $a0 $z0 displayzeros -- display 0 if $a0 passed as zero
JEQ $a0 $z0 $r7 -- return if $a0 is zero

-- setting variables
-- $g7 is set to 1 if $a0 is negative and will be using accordingly
RSLT $g7 $r3 $z0
-- using $g6 to flag if there has been a number displayed. 
RAND $g6 $g6 $z0
SIBNEQ $g7 $z0 convertnegativetopositive -- $s0 returns the negative of $a0. $a0 is *not* converted
SIBNEQ $g7 $z0 prepforintegerdisplayconversion

-- starting the loop
RAND $a1 $a1 $z0
IADD $a1 $a1 10000
SIBEQ $z0 $z0 processnumber
RAND $a1 $a1 $z0
IADD $a1 $a1 1000
SIBEQ $z0 $z0 processnumber
RAND $a1 $a1 $z0
IADD $a1 $a1 100
SIBEQ $z0 $z0 processnumber
RAND $a1 $a1 $z0
IADD $a1 $a1 10
SIBEQ $z0 $z0 processnumber
RAND $a1 $a1 $z0
IADD $a1 $a1 1
SIBEQ $z0 $z0 processnumber

SIBEQ $z0 $z0 printnewline -- printing a new line

JR $r7 -- returns to the main

processnumber:
-- saving $ra 
RAND $g1 $g1 $z0
RADD $g1 $g1 $ra
-- $a0 will be the mod from previous invokation or initial number
RAND $t2 $t2 $z0
RADD $t2 $t2 $a1
RAND $g5 $g5 $z0
RADD $g5 $g5 $a0
IADD $g5 $g5 1
RSLT $t1 $t2 $g5 -- $t1 will tell if $a0 is bigger of the same as $a1
RSLT $t3 $t2 $g5 -- $t3 will tell if $a0 is bigger of the same as $a1
SIBEQ $t1 $z0 checkforzero  --if $a0 is smaller, let's see if there is a necessary for zero
SIBNEQ $t3 $z0 prepdisplaynumorder -- display then number of the order if required
JR $g1

checkforzero:
-- saving $ra
RAND $s4 $s4 $z0
RADD $s4 $s4 $ra
-- saving $a0
RAND $s3 $s3 $z0
RADD $s3 $s3 $a0
SIBNEQ $g6 $z0 displayzeros -- display the zero if there is a number displayed before
-- returning $a0
RAND $a0 $a0 $z0
RADD $a0 $a0 $s3
JR $s4


prepdisplaynumorder:
-- saving arg registers
-- saving $a0
RAND $g0 $g0 $z0
RADD $g0 $g0 $a0
-- saving $ra 
RAND $g2 $g2 $z0
RADD $g2 $g2 $ra
-- setting args for division
RAND $a1 $a1 $z0
RADD $a1 $a1 $t2
RAND $s0 $s0 $z0
RAND $s1 $s1 $z0
SRJAL $r6 $r6 simpledivision
-- display quotient that comes back as $s0
RAND $a0 $a0 $z0
RADD $a0 $a0 $s0
IADD $a0 $a0 48 -- ASCII are always int + 48 decimal
SRJAL $r5 $r5 outputchar  -- remember, $a0 get cleared when displayed
RADD $a0 $a0 $s1 -- Restoring $a0 with mod
RAND $g6 $g6 $z0 -- zeroing $g6 will tell that one number was displayed
IADD $g6 $g6 1
JR $g2

simpledivision:
-- $a0 dividend and and $a1 divider - I do not care for signals
-- $s0 returns the quotient. $s1 returns the mod
-- routine ends when divider bigger than divident
-- $s0 and $s1 must come as 0
-- if invoked by main, make sure $r6 gets $ra
RSLT $t0 $a0 $a1
JNEQ $t0 $z0 $r6
RSUB $s1 $a0 $a1
RSUB $a0 $a0 $a1
IADD $s0 $s0 1
SRJAL $r5 $r5 simpledivision

prepforintegerdisplayconversion:
-- saving original $a0 to $g1
RAND $g1 $g1 $z0
RADD $g1 $g1 $a0
-- saving $ra - never know
RAND $g2 $g2 $z0
RADD $g2 $g2 $ra
-- display negative symbol
RAND $a0 $a0 $z0
IADD $a0 $a0 45 -- ASCII code for "-"
SRJAL $r5 $r5 outputchar  -- remember, $a0 get cleared when displayed
RAND $a0 $a0 $z0
RADD $a0 $a0 $s0  -- $a0 gets the value of $s0 returned by the convertnegativetopositive
-- Jumping back
JR $g2

displayzeros:
-- saving $ra 
RAND $g2 $g2 $z0
RADD $g2 $g2 $ra
RAND $a0 $a0 $z0 -- ASCII are always int + 48 decimal
IADD $a0 $a0 48 -- ASCII are always int + 48 decimal
SRJAL $r5 $r5 outputchar  -- remember, $a0 get cleared when displayed
JR $g2

displaydash:
-- saving $ra 
RAND $g2 $g2 $z0
RADD $g2 $g2 $ra
RAND $a0 $a0 $z0 
IADD $a0 $a0 45 -- ASCII dash
SRJAL $r5 $r5 outputchar  -- remember, $a0 get cleared when displayed
JR $g2
