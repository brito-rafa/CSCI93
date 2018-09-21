# Main program for ps4

message1: .ascii "Enter First Number.  "
message2: .ascii "Enter Second Number. "
message3: .ascii "Result of Multiplication is ... "
message4: .ascii "The first number entered was "
message5: .ascii "The second number entered was "
inputmemory1: .address "0x0A00"
inputmemory2: .address "0x0B00"
inputmemory3: .address "0x0C00"

main:
-- Display message on screen 
LW $t0 $a0 message1 -- $a0 will get the address of the message1 label
SRJAL $r7 $r7 outputstring
SIBEQ  $z0 $z0 printnewline
SIBNEQ $z0 $z0 main -- main looping
