multiply:
-- multiply two numbers passed as $a0 and $a1
-- $a0 is multiplicand and $a1 is the multiplier
-- $s1 is the incremental and $s0 the result
-- prior invocation from main caller, set:
-- $s0 to 0
-- $s1 to 1
-- $s2 to 1 if $a0 is negative
-- $s3 to 1 if $a1 is negative
-- multiplier will always return positive integer. caller must handle signal based on $s2 and $s3


-- return 0 if $a0 or $a1 is zero - remembering that $s0 is zero by default
JEQ $a0 $z0 $r7
JEQ $a1 $z0 $r7

-- $t1 will flag if multiplicand is negative
RSLT $t1 $a0 $z0
SIBNEQ $t1 $z0 prepformultiplicandconversion
SIBNEQ $t1 $z0 convertnegativetopositive
SIBNEQ $t1 $z0 restoreformultiplicandconversion

-- $t2 will flag if multiplier is negative
RSLT $t2 $a1 $z0
SIBNEQ $t2 $z0 prepformultiplierconversion
SIBNEQ $t2 $z0 convertnegativetopositive
SIBNEQ $t2 $z0 restoreformultiplierconversion

-- at this time, both $a0 and $a1 are positive
-- first, check if multiplier is less than incremental. if so, $t0 gets 1 and return to the main caller
RSLT $t0 $a1 $s1
JNEQ $t0 $z0 $r7
-- increment $s0 and $s1
RADD $s0 $s0 $a0
IADD $s1 $s1 1
-- if multiplier is the same as incremental. if so, return to the main caller
JEQ $a1 $s1 $r7
SRJAL $r6 $r6 multiply -- returns to the loop of multiply

convertnegativetopositive:
RSUB $s0 $z0 $a0
JR $ra

prepformultiplicandconversion:
RAND $g0 $g0 $z0
RADD $g0 $g0 $a0
RAND $g1 $g1 $z0
RADD $g1 $g1 $a1
RAND $g2 $g2 $z0
RADD $g2 $g2 $s0
JR $ra

restoreformultiplicandconversion:
RAND $a0 $a0 $z0
-- $a0 will get positive now - remmebering that $s0 is the return from conversion
RADD $a0 $a0 $s0
-- $s0 is back from the total
RAND $s0 $s0 $z0
RADD $s0 $s0 $g2
JR $ra

prepformultiplierconversion:
RAND $g0 $g0 $z0
RADD $g0 $g0 $a0
RAND $g1 $g1 $z0
RADD $g1 $g1 $a1
RAND $g2 $g2 $z0
RADD $g2 $g2 $s0
-- setting the argument for the next routine
RAND $a0 $a0 $z0
RADD $a0 $a1 $z0
JR $ra

restoreformultiplierconversion:
RAND $a1 $a1 $z0
-- $a0 will get positive now
RADD $a1 $a1 $s0
-- $s0 is back from the total
RAND $s0 $s0 $z0
RADD $s0 $s0 $g2
-- normalizing $a0 back to the original argument
RAND $a0 $a0 $z0
RADD $a0 $a0 $g0
JR $ra

