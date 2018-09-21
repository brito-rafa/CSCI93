inputmemory1: .address "0x070C"
inputmemory2: .address "0x071C"

main:
RAND $a0 $a0 $z0
IADD $a0 $a0 1500
SW $a0 $a1 inputmemory2
LW $g0 $g1 inputmemory2
SW $g0 $g1 inputmemory1
SIBEQ $z0 $z0 main -- main looping
