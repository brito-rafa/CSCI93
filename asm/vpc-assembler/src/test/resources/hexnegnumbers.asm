main:
IAND $g0 $g0 0xFA
IOR $g0 $g0 0xFA
INOR $g0 $g0 0xFA
IXOR $g0 $g0 0xFA
ISLT $g0 $g0 -300
SILU $g0 0xFFFA
SILOAD $g0 $sp -8
SISTORE $g0 $sp -4
