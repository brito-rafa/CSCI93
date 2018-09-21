VPC assembler - from bare bones E93 assembler

Build as follows:

```
mvn clean install
```

Running assembler without asm libraries

- build as shown above
- `cd target` (where the output files from the build go)

```
java -jar vpc-assembler-1.0-SNAPSHOT.jar name-of-your-file.asm 
```

Running assembler in verbose/debug mode (-v parameter)
```
java -jar vpc-assembler-1.0-SNAPSHOT.jar name-of-your-file.asm -v
```

Running assembler with libraries (at this time the assembler does not have include statements, so we are relying on a wrapper script to build a temp file):

```
run_vpc.sh <file>
```

it will assume location of the libraries the following: `target/asmlibrary` and `target` for the jar file
- the wrapper will assemble a monolitic temp file with all files from asmlibrary
- and it will create the mif file with the code on /tmp/vpctemp.mif

Example of the output:
```
Rafaels-MacBook-Air:vpc-assembler rbrito$ ./run_vpc.sh 9-program.asm 
INFO: Vulture Platform Compute (VPC) - Wrapper script
INFO: Builds the assembler file with library directory
INFO: VPC_JAR_DIR /Users/rbrito/Documents/Harvard/CA/csci-e-93-project/asm/vpc-assembler/target
INFO: VPC_ASMLIB_DIR /Users/rbrito/Documents/Harvard/CA/csci-e-93-project/asm/vpc-assembler/asmlibrary/
INFO: VPC_JAR /Users/rbrito/Documents/Harvard/CA/csci-e-93-project/asm/vpc-assembler/target/vpc-assembler-1.0-SNAPSHOT.jar
INFO: Assembling file passed as argument with library files
INFO: Compiling program
DEPTH = 32768;
WIDTH = 16;
ADDRESS_RADIX = HEX;
DATA_RADIX = BIN;
CONTENT
BEGIN
0000 : 01001100000000000000000000000100
0004 : 00000000001000000000100000000011
0008 : 00000100001000010000010000000000
000C : 00101010100101000000000010101100
0010 : 00000000001000000000100000000011
0014 : 00000100001000010000010001000000
0018 : 00101010100101000000000100001100
001C : 00000000001000000000100000000011
0020 : 00000100001000010000010001000000
0024 : 00101010100101000000001001001100
0028 : 00000001101000000110100000000011
(...)
03F0 : 00000000000000000000000000000000
03F4 : 00000000000000000000000000000000
03F8 : 00000000000000000000000000000000
03FC : 00000000000000000000000000000000
END
INFO: Completed. Please check mif file /tmp/vpctemp.mif and debug file /tmp/vpctemp.mif.debug
```
