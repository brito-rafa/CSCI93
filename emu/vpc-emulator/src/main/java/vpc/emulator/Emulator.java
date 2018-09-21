package vpc.emulator;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Map; 
import java.util.Scanner; 
import java.util.HashMap; 
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import vpc.emulator.ast.*;

/**
 *
 * @author rab405 largely using markford e93 sample code
 */
public class Emulator {

    /**
     * The maximum number of registers in the system. This is used to verify
     * to setup the static array size with all VPC registers
     */
    private static final int MAX_REGISTERS = 32;

    /**
     * The maximum number of lines for the program code
     * this is to delimit an area for data stack
     */
    private static final int TEXT_SEGMENT = 5;

    private static final int REG_IOBUFFER_1 = 0x0000FF04;
    private static final int REG_IOBUFFER_2 = 0x0000FF08;

    /**
     * The static array with all VPC registers
     * the array index is the actual decimal value of register
    */

    private static final String[] VPC_REGISTERS = {
						"$z0", /* zero reg */
						"$a0", /* arg reg 0 */
						"$a1", /* arg reg 1 */ 
						"$g0", /* gpr reg 0 */ 
						"$g1", /* gpr reg 1 */ 
						"$g2", /* gpr reg 2 */ 
						"$g3", /* gpr reg 3 */ 
						"$g4", /* gpr reg 4 */ 
						"$g5", /* gpr reg 5 */ 
						"$g6", /* gpr reg 6 */ 
						"$g7", /* gpr reg 7 */ 
						"$sp", /* stack pointer */ 
						"$fp", /* frame pointer */ 
						"$r0", /* memory address reg 0 */ 
						"$r1", /* memory address reg 1 */ 
						"$r2", /* memory address reg 2 */ 
						"$r3", /* memory address reg 3 */ 
						"$r4", /* memory address reg 4 */ 
						"$r5", /* memory address reg 5 */ 
						"$r6", /* memory address reg 6 */ 
						"$r7", /* memory address reg 7 */ 
						"$t0", /* temp reg 0 */ 
						"$t1", /* temp reg 1 */ 
						"$t2", /* temp reg 2 */ 
						"$t3", /* temp reg 3 */ 
						"$s0", /* saved reg 0 */ 
						"$s1", /* saved reg 1 */ 
						"$s2", /* saved reg 2 */ 
						"$s3", /* saved reg 3 */ 
						"$s4", /* saved reg 4 */ 
						"$gp", /* global pointer */ 
						"$ra"  /* return address */ 
						};


    private static final String[] FUNCT_CODES = {
						"invalid",
						"RADD",
						"RSUB",
						"RAND",
						"ROR",
						"RNOR",
						"RXOR",
						"RSLT",
						"SRSRL",
						"SRSLL",
						"RXNOR",
						"SRPC"
						};

    private static final String[] OPCODES = {
						"invalid",
						"IADD",
						"ISUB",
						"IAND",
						"IOR",
						"INOR",
						"IXOR",
						"ISLT",
						"ALU",
						"invalid",
						"SRJAL",
						"JR",
						"JEQ",
						"JNEQ",
						"invalid",
						"invalid",
						"SILU",
						"SILOAD",
						"SISTORE",
						"SIBEQ",
						"SIBNEQ",
						"LW",
						"SW",
						"LR",
						"WSR"
						};

    /**
     * A mask used to extract the register value from an encoded instruction.
     * This should be enough bits to mask all possible values of the register.
     */
    private static final int REGISTER_MASK = 0x1f;

    /**
     * constants for the assembler line in hex
    */
    private static final int halfByte = 0x0F;
    private static final char[] hexDigits = { 
      '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static final int OpCode_LABEL  = 100;
    private static final int OpCode_SRJAL  = 10;
    private static final int OpCode_SIBEQ  = 19;
    private static final int OpCode_SIBNEQ = 20;
    private static final int OpCode_LW     = 21;
    private static final int OpCode_SW     = 22;


    /**
     * Decodes an instruction from its encoded form. You'll need something like
     * this for when you write the emulator.
     *
     * @param encoded numeric form of the instruction that we'll decode
     * @return Instruction
     * @throws IllegalArgumentException if we don't know how to decode it
     */
    public Instruction decode(int encoded) {
        // the opcode is always in 31..26 but the rest of the instruction is
        // unknown until we know what the opcode is so get that first!
        int value = encoded >> 26;
        OpCode opCode = OpCode.fromEncoded(value);
        int r1;
        int r2;
        int r3;
        int immediate;
	String ascii;
        switch (opCode) {
            case ALU:
                // get the function code to figure out what type of ALU operation
                // it is. The function code is the lower 6 bits which I can get
                // by AND'ing the number 63 (which is 111111 in binary or 0x3F)
                int functionCode = encoded & 0x3F;
                switch(functionCode) {
                    case ALUFunctionCodes.RADD:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RADD] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Radd()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RADD)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.RSUB:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RSUB] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Rsub()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RSUB)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.RAND:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RAND] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Rand()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RAND)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.ROR:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.ROR] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Ror()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.ROR)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.RNOR:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RNOR] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Rnor()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RNOR)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.RXOR:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RXOR] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Rxor()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RXOR)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.RXNOR:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RXNOR] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Rxnor()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RXNOR)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.RSLT:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // r3 is always in 20..16
                        r3 = (encoded >> 16) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.RSLT] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Rslt()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.RSLT)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
                    case ALUFunctionCodes.SRSRL:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // immediate is bits 10..6 - will reuse the register_mask since it is a 5 bit field
			immediate = (encoded >> 6 ) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.SRSRL] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Srsrl()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.SRSRL)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
                    case ALUFunctionCodes.SRSLL:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
                        // r2 is always in 25..21
                        r2 = (encoded >> 21) & REGISTER_MASK;
                        // immediate is bits 10..6 - will reuse the register_mask since it is a 5 bit field
			immediate = (encoded >> 6 ) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.SRSLL] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Srsll()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.SRSLL)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
                    case ALUFunctionCodes.SRPC:
                        // r1 is always in 15..11
                        r1 = (encoded >> 11) & REGISTER_MASK;
			ascii = FUNCT_CODES[ALUFunctionCodes.SRPC] + " " + VPC_REGISTERS[r1];
                        return new Srpc()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.SRPC)
				.setAscii(ascii)
                                .setR1(r1);

		// end of ALU case statement
                }
	    case LR:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2];
                        return new Lr()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2);
	    case WSR:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Wsr()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case IADD:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Iadd()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case ISUB:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Isub()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case IAND:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Iand()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case IOR:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Ior()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case INOR:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Inor()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case IXOR:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Ixor()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case ISLT:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Islt()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case SILU:
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + decToHex(immediate,8,4);
                        return new Silu()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setImmediate(immediate);
	    case SILOAD:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Siload()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case LW:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Lw()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case SW:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Sw()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case SIBEQ:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Sibeq()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case SIBNEQ:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Sibneq()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case SRJAL:
                        // r2 is bits 25..21
                        r2 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // immediate is bits 15..0
			immediate = (encoded  & 0xFFFF);
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + decToHex(immediate,8,4);
                        return new Srjal()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setImmediate(immediate);
	    case JR:
                        // r1 is bits 20..16
                        r1 = ( encoded >> 16 ) & REGISTER_MASK; 
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1];
                        return new Jr()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1);
	    case JEQ:
                        // r1 is bits 25..21
                        r1 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r2 is bits 20..16
                        r2 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // r3 is bits 15..11
                        r3 = ( encoded >> 11 ) & REGISTER_MASK; 
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Jeq()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);
	    case JNEQ:
                        // r1 is bits 25..21
                        r1 = ( encoded >> 21 ) & REGISTER_MASK; 
                        // r2 is bits 20..16
                        r2 = ( encoded >> 16 ) & REGISTER_MASK; 
                        // r3 is bits 15..11
                        r3 = ( encoded >> 11 ) & REGISTER_MASK; 
			ascii = OPCODES[value] + " " + VPC_REGISTERS[r1] + " " + VPC_REGISTERS[r2] + " " + VPC_REGISTERS[r3];
                        return new Jneq()
                                .setOpcode(opCode)
				.setAscii(ascii)
                                .setR1(r1)
                                .setR2(r2)
                                .setR3(r3);

	// end of Opcode statement
        }
        throw new IllegalArgumentException("unhandled encoded instruction:" + encoded);
    }

    /**
     * Parses the source into a list of Instructions.
     * @param reader Source for the program
     * @return list of instructions that are ready to encode
     * @throws IOException when there's an error reading a line
     */
    public List<Instruction> parse(Reader reader, MemoryFixture memoryFixture) throws IOException {

        List<Instruction> instructions = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        String line;
	String FullWord;
	String FirstHalfWord = "";
	String SecondHalfWord = "";
	String Address;
        int lineNumber = 0;
        int zeroWordcounter = 0;

        while ((line = br.readLine()) != null) {

            lineNumber++;

            if (line.startsWith("#")  ||
		line.startsWith("--") ||
		line.matches("^$")   ||
		line.startsWith("DEPTH") ||
		line.startsWith("WIDTH") ||
		line.startsWith("ADDRESS_RADIX") ||
		line.startsWith("DATA") ||
		line.startsWith("CONTENT") ||
		line.startsWith ("BEGIN") ||
		line.startsWith("END")) {
                // it's a comment or blank line or header/footer, ignore it
                continue;
            }
	    

	    // We need to read two lines of the MIF file to get the entire instruction
	    // First Half
            if (line.matches("^[0-9A-F][0-9A-F][0-9A-F][02468ACE]\\s:.*")) {
		// First part of the line
		String[] split = line.split("\\s:\\s");
		Address = split[0];
		FirstHalfWord = split[1];
		// Getting only the hex
		FirstHalfWord = FirstHalfWord.substring(0,4);
		// padding the hex 
		FirstHalfWord = "0x" + FirstHalfWord;
		if (FirstHalfWord == "") {
			System.err.println("ERROR: could not parse mif file, line " + lineNumber);
		}
		continue; 
	    }
            if (line.matches("^[0-9A-F][0-9A-F][0-9A-F][13579BDF]\\s:.*")) {
		String[] split = line.split("\\s:\\s");
		Address = split[0];
		SecondHalfWord = split[1];
		SecondHalfWord = SecondHalfWord.substring(0,4);
		if (FirstHalfWord == "") {
			System.err.println("ERROR: Expecting prior line loaded to assemble word" + lineNumber);
		}
		if (SecondHalfWord == "") {
			System.err.println("ERROR: could not parse mif file, line " + lineNumber);
		}
		FullWord = FirstHalfWord + SecondHalfWord;
		// Some words are showing as all F's
		// changing to \n
		if (FullWord == "0xFFFFFFFF") {
			FullWord = "0x0000000A";
		} 

		// loading word to memory - getting the memory address. Current mif line minus 1 times 2for the width
		int int_Address = Integer.parseInt(Address,16);
		int_Address = (int_Address - 1)*2;
		int int_word = Integer.decode(FullWord);
		//if (vpcdebug) {
			System.out.println(FullWord + " line " + Address);
		//}
		memoryFixture.writeInt(int_Address, int_word);
		//if (vpcdebug) {
	//		System.out.println(FullWord + " line " + lineNumber);
//		}

		// If first six bytes is zero (data), do not load instruction
		int int_firsthalfword = Integer.decode(FirstHalfWord);
		// looking for the first 6 bytes
		if (((int_firsthalfword >> 10) & 0x3f) != 0) {
			// stop load instructions if we are on the datastack
			if (zeroWordcounter > 5) {
				continue;
			}
		// continue to read instructions
			Instruction instruction = decode(int_word);
			instruction.setAddress(int_Address);
			instruction.setSourceLine(FullWord);
			System.out.println(instruction.toString());
			instructions.add(instruction);
		} else {
			// counter fo words with zero
			// after 5 occurrences is because we are arriving at the data stack
			zeroWordcounter++;

		}
		// Resetting the first part of the word
		FirstHalfWord = "";
                continue;
	    }
        }
        return instructions;
    }

    /**
     * Helper method that converts a reference to a register to an int
     * @param s reference to a register in the format we're expecting
     * @return number of the register in the array
     * @throws IllegalArgumentException if we can't parse it or it's out of range
     */
    private int toRegister(String s) throws InvalidRegisterException {

	if (!Arrays.asList(VPC_REGISTERS).contains(s)) {
            throw new InvalidRegisterException(s);
	}

        //int register = Integer.parseInt(s.substring(2));
        int register = Arrays.asList(VPC_REGISTERS).indexOf(s);

        if (register > MAX_REGISTERS-1) {
            throw new InvalidRegisterException(s);
        }

        return register;
    }

    /**
     * Helper method that converts an immediate value to an integer
     * @param s string version of a decimal number to use as an immediate
     * @return converted integer
     * @throws NumberFormatException if an unknown format
     */
    private int toImmediate(String s) {
        // todo - assert the register value can be encoded

        // With respect to the encoding assertion, keep in mind that you may only
        // have 8 bits for an immediate value in your instruction (more for a
        // JUMP). You may allow the programmer to use an immediate value outside
        // of this range but that requires your assembler to handle it. For
        // example, the assembler might see a large immediate value and then
        // generate a couple of instructions to handle it. It might use a
        // temporary register and LUI and similar instructions to put the large
        // immediate into the temporary register and then rewrite the instruction
        // to use this temporary register instead of an immediate.

	
        if (s.startsWith("0x")) {
		// it is hex value
		String stemp = s.substring(2);
		return Integer.parseInt(stemp,16);
	} else if (s.startsWith("-")) {
		// negative number - not sure if I need this - will test to remove
		Integer stemp2 = Integer.parseInt(s);
		short s_short = stemp2.shortValue();
		return s_short;
	}


        return Integer.parseInt(s);
    }

   /*
    * Helper procedure to format the assembler line number in two hex numbers
    */
    public static String decToHex(int dec, int size, int number) {
      int sizeOfIntInHalfBytes = size;
      int numberOfBitsInAHalfByte = number;
      StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
      hexBuilder.setLength(sizeOfIntInHalfBytes);
      for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i)
      {
        int j = dec & halfByte;
        hexBuilder.setCharAt(i, hexDigits[j]);
        dec >>= numberOfBitsInAHalfByte;
      }
      return hexBuilder.toString(); 
    }


    public static void printVisitor (int[] registers, int pc, String str_ir, int ir, String vpcScreen) {
            System.out.println("\n\n");
            System.out.println("+--------------------------------------");
            System.out.println("+ PC Address: " + decToHex(pc,8,4));
            System.out.println("+ IR        : " + str_ir);
            System.out.println("+ IR in Hex : " + decToHex(ir,8,4));
            System.out.println("+--------------------------------------");
            System.out.println("+ Registers : " );
            System.out.println("+--------------------------------------");
            for(int i = 0; i <= 15 ; i++) {
		    int n = i + 16;
		    System.out.println("+ " + VPC_REGISTERS[i] + ": " + decToHex(registers[i],8,4) + "\t" + VPC_REGISTERS[n] + ": " + decToHex(registers[n],8,4));
	    }
            System.out.println("+--------------------------------------");
            //System.out.println("+ Memory diff from previous instruction: " ); //to be implemented

            System.out.println("+ Screen " );
            System.out.println("+--------------------------------------");
	    System.out.print(vpcScreen + "\n");
            System.out.println("+--------------------------------------");
    }

    public static void dumpMemory(MemorySubsystem memoryFixture) {
		System.out.println("VPC Emulator. Enter <1> MemoryDump <2> Continue");
		Scanner scanner = new Scanner(System.in);
		int userinput = scanner.nextInt();
		if (userinput == 1){
			System.out.println("Wait... Dumping memory on /tmp/memdump.txt");
/*			try (PrintStream o = new PrintStream(new File("/tmp/memdump.txt"))) {
				PrintStream console = System.out;
				System.setOut(o);
				String memorydump = memoryFixture.toString();
				System.out.println(memorydump);
				System.setOut(console);
				System.out.println("Completed.");
			}
*/
		}
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            System.err.println("ERROR: must pass name of mif file");
            return;
        }

        File file = new File(args[0]);
        if (!file.isFile()) {
            System.err.println("ERROR: file not found or not readable:" + args[0]);
            return;
        }

	boolean vpcdebug = false;
	if (args.length > 1) {
		if  (args[1].equals("-v")) {
			vpcdebug = true;
		}
	}

	// Initialize memory, default is 64k
	MemoryFixture memoryFixture = new MemoryFixture();

	// Initialize registers
	int[] registers = new int[32];
	String str_ir;

	ExecutionVisitor ev = new ExecutionVisitor (memoryFixture, registers);

        try (FileReader fileReader = new FileReader(file)) {
            Emulator emulator = new Emulator();

	    // emulator.parse add the mif file into the memoryFixture
	    // additionally it decodes all the instructions for troubleshooting
            List<Instruction> instructions = emulator.parse(fileReader, memoryFixture);

	    if (vpcdebug) {
		    for(Instruction instruction : instructions) {
			System.out.println(instruction);
		    }
	    }

	    int pc = 0;
	    int ir = 99999; // whatever number, as long as it is not 0
	    String vpcScreen = "";
	    while (ir != 0) {
		ir = memoryFixture.readInt(pc);
		Instruction instruction = emulator.decode(ir);
		str_ir = instruction.getAscii();
		// IO
		// Assuming all operations on 00FF04 are output
		if (str_ir.matches("^SW.*00FF04.*")) {
			vpcScreen += Character.toString((char) (registers[instruction.getR1()]));
		}

		// Assuming all loading operations on 00FF04 are input
		if (str_ir.matches("^LW.*00FF04.*")) {
			// need read char by char on java and add on 00FF04
			String[] cmd = {"/bin/sh", "-c", "stty raw </dev/tty"};
			Runtime.getRuntime().exec(cmd).waitFor();
			char c = (char) System.in.read();
			String[] cmdafter = {"/bin/sh", "-c", "stty cooked </dev/tty"};
			Runtime.getRuntime().exec(cmdafter).waitFor();
			memoryFixture.writeInt(REG_IOBUFFER_1,c);
/*			if (c == '\n') {
				dumpMemory(memoryFixture);
			}
*/
			Thread.sleep(100);
		}

		instruction.accept(ev);
		printVisitor(registers, pc, str_ir, ir, vpcScreen);
		pc = ev.getPc();

		//dumpMemory(memoryFixture);

		Thread.sleep(10);
	    }

	    // End of execution
//	    if (vpcdebug) {
//		    String memorydump = memoryFixture.toString();
//		    System.out.println(memorydump);
	    //}

        }
    }
}
