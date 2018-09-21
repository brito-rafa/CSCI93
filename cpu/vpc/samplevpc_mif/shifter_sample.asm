main:
RAND $a0 $a0 $z0
SILU $a0 0x000F
SRSLL $a0 $a0 1
SRSRL $a0 $a0 1
SRSLL $a0 $a0 2
SRSRL $a0 $a0 2
SRSLL $a0 $a0 3
SRSRL $a0 $a0 3
SRSLL $a0 $a0 4
SRSRL $a0 $a0 4
SRSLL $a0 $a0 8
SRSRL $a0 $a0 8
SRSLL $a0 $a0 12
SRSRL $a0 $a0 12
-- pushing other direction
SRSRL $a0 $a0 16 -- should see "0000 000F"
SRSRL $a0 $a0 3  -- should see "0000 0001"
SRSLL $a0 $a0 31 -- should see "8000 0000"
--SRSRL $a0 $a0 16 -- should see "0001 0000"
--SRSRL $a0 $a0 16 -- should see "0000 0001"
SIBEQ $z0 $z0 main
