# Random R-type instructions
main:
RADD $r1 $r2 $z0
RSUB $t2 $t3 $z0
ROR $t0 $t2 $z0
RNOR $s0 $s3 $ra
RXOR $s0 $gp $ra
RSLT $a0 $gp $ra
SRSRL $a0 $a1 4
SRSLL $fp $g7 4
SRJAL $fp $g7 MYLOOP
SRPC $t3

# Random I-type instructions
MYLOOP:
IADD $r1 $r2  1500
ISUB $t2 $t3  800
IAND $t0 $t2  90
IOR $s0 $s3 180
INOR $s0 $gp 78
IXOR $s0 $gp 900000
ISLT $s3 $gp 900000
SILU $g0 40000
SILOAD $fp $r7 -8
SISTORE $r6 $r7 -8
SIBEQ $r4 $r5 MYLOOP
SIBNEQ $r4 $r5 TOL


MYLOOP:
IADD $r1 $r2  1500

