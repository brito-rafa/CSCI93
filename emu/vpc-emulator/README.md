VPC emulator 

Build as follows:

```
mvn clean install
```

Build as follows:
```
Rafaels-MacBook-Air:vpc-emulator rbrito$ ./build_vpc_emu.sh 
```

Running assembler without asm libraries

- build as shown above
- `cd target` (where the output files from the build go)

```
java -jar vpc-emulator-1.0-SNAPSHOT.jar name-of-your-file.mif 
```

Running with wrapper script run_vpc_emu.sh
```
Rafaels-MacBook-Air:vpc-emulator rbrito$ ./run_vpc_emu.sh sample.mif 
INFO: Vulture Platform Compute (VPC) - Emulator Wrapper script
INFO: Executes Emulator
INFO: VPC_JAR_DIR /Users/rbrito/Documents/Harvard/CA/csci-e-93-project/emu/vpc-emulator/target
INFO: VPC_JAR /Users/rbrito/Documents/Harvard/CA/csci-e-93-project/emu/vpc-emulator/target/vpc-emulator-1.1-SNAPSHOT.jar

```

History
11/19/2017 - version 1.1 - Added IO subsystem and multiply. Pending testing on negative numbers.

11/14/2017 - version 1.0. Initial Release. Pending IO subsystem and better output for changes in memory.

Example of the output:
```
+--------------------------------------
+ PC Address: 000001A4
+ IR        : IAND $t0 $t0 00000002
+ IR in Hex : 0EB50002
+--------------------------------------
+ Registers : 
+--------------------------------------
+ $z0: 00000000	$r2: 00000000
+ $a0: 0000000A	$r3: 00000000
+ $a1: 00000000	$r4: 00000000
+ $g0: 0000000A	$r5: 00000194
+ $g1: 0000000D	$r6: 00000180
+ $g2: 00000054	$r7: 00000050
+ $g3: 0000068C	$t0: 00000002
+ $g4: 0000000D	$t1: 0000FF00
+ $g5: 00000001	$t2: 00000000
+ $g6: 00000000	$t3: 00000002
+ $g7: 00000000	$s0: 00000000
+ $sp: 00000000	$s1: 00000000
+ $fp: 00000000	$s2: 00000000
+ $r0: 00000000	$s3: 00000000
+ $r1: 00000000	$s4: 00000000
+ $r2: 00000000	$gp: 00000000
+ $r3: 00000000	$ra: 00000194
+--------------------------------------
+ Screen 
+--------------------------------------

Enter First Number.  
10

Enter Second Number. 
10

Result of Multiplication is ... 
100


```
