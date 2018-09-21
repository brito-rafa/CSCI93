top:
SRJAL $t0 $t0 main # just a test of comments here

main:
RADD $g0 $g0 $g1 -- another test for comment
RSUB $g0 $g0 $g1
RAND $g0 $g0 $z0
ROR $g0 $g1 $g2
RNOR $g0 $g1 $g2
RXOR $g0 $g1 $g2
SRSRL $g0 $g1 4
SRSLL $g0 $g1 4
SRPC  $t0  
IADD $g0 $g0 1500
ISUB $g0 $g0 1500
IAND $g0 $g0 250
IOR $g0 $g0 250
INOR $g0 $g0 250
IXOR $g0 $g0 250
ISLT $g0 $g0 -250
SILU $g0 65530
SILOAD $g0 $sp -8
SISTORE $g0 $sp -4
SIBEQ $z0 $z0 top
JNEQ $z0 $z0 $ra
LR $a0 $s3
SW $a0 0x0404
LW $a0 0x0404

othertop:
SILU $g0 65530
JEQ $g0 $g1 $ra
