package vpc.assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Map; 
import java.util.HashMap; 
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

// this is a functional code without AssemblyVisitor reference

/**
 *
 * @author rab405 largely using markford e93 sample code
 */
public class Assembler {

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

    /**
     * A mask used to extract the register value from an encoded instruction.
     * This should be enough bits to mask all possible values of the register.
     */
    private static final int REGISTER_MASK = 0xf;

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

     /* Parses a line of assembly and returns an instruction that the system will
     * know how to execute.
     *
     * At this point, the instruction may refer to a label that needs to be
     * resolved.
     *
     * @param line raw line of assembly to parse into an instruction
     * @return an instruction
     * @throws IllegalArgumentException if the line cannot be parsed.
     */
    public Instruction parse(String line) {

	// removing comments from the instruction code line
        String[] splittemp = line.split("#");
        splittemp = splittemp[0].split("--");

	// the actual instructions
        String[] split = splittemp[0].split("\\s+");
        if (split.length > 4) {
            return Instruction.error("unknown instruction");
        }
        try {
	    int functionCode = 99;
	    OpCode opcode;
            String type = split[0].trim();

	    /* All instructions that start with R (r-types) */
	    if (type.matches("^R.*")) {
                    opcode = OpCode.ALU;
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    int r3 = toRegister(split[3].trim());
		    switch (type) {
			case "RADD": functionCode = 1; break;
			case "RSUB": functionCode = 2; break;
			case "RAND": functionCode = 3; break;
			case "ROR":  functionCode = 4; break;
			case "RNOR": functionCode = 5; break;
			case "RXOR": functionCode = 6; break;
			case "RXNOR": functionCode = 10; break;
			case "RSLT": functionCode = 7; break;
			default: return Instruction.error("unknown instruction.");
		     }
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setR3(r3)
			    .setFunc(functionCode);

	    /* All instructions that start with I (i-types) */
            } else if (type.matches("^I.*")) {
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    int immediate = toImmediate(split[3].trim());
		    switch (type) {
			case "IADD": opcode = OpCode.IADD; break;
			case "ISUB": opcode = OpCode.ISUB; break;
			case "IAND": opcode = OpCode.IAND; break;
			case "IOR": opcode = OpCode.IOR; break;
			case "INOR": opcode = OpCode.INOR; break;
			case "IXOR": opcode = OpCode.IXOR; break;
			case "ISLT": opcode = OpCode.ISLT; break;
			default: return Instruction.error("unknown instruction.");
		     }
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setImmediate(immediate);

	    /* Shift instructions - R-type */
	    } else if (type.matches("^SRS.*")) {
                    opcode = OpCode.ALU;
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    int immediate = toImmediate(split[3].trim());
		    switch (type) {
			case "SRSRL": functionCode = 8; break;
			case "SRSLL": functionCode = 9; break;
			default: return Instruction.error("unknown instruction.");
		    }
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setImmediate(immediate)
			    .setFunc(functionCode);

	    /* Jump and register - R-type
		we will handle labels at this one
	    */
	    } else if (type.matches("^SRJAL")) {
                    opcode = OpCode.SRJAL;
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    String label = split[3].trim();
		    functionCode = 10;
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setLabel(label)
			    .setFunc(functionCode);

	    /* Jump register unconditionally */
	    } else if (type.matches("^JR")) {
                    opcode = OpCode.JR;
                    int r1 = toRegister(split[1].trim());
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1);

	    /* Jump register conditionally without link */
	    } else if (type.matches("^JEQ|^JNEQ")) {
		    switch (type) {
			case "JEQ": opcode = OpCode.JEQ; break;
			case "JNEQ": opcode = OpCode.JNEQ; break;
			default: return Instruction.error("unknown instruction.");
		    }
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    int r3 = toRegister(split[3].trim());
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setR3(r3);

	    /* get RPC value onto register - instructions for debug purposes
	    */
	    } else if (type.matches("^SRPC")) {
                    opcode = OpCode.ALU;
                    int r1 = toRegister(split[1].trim());
		    functionCode = 11;
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setFunc(functionCode);

	    /* Load an upper number
	    */
	    } else if (type.matches("^SILU")) {
                    opcode = OpCode.SILU;
                    int r1 = toRegister(split[1].trim());
                    int immediate = toImmediate(split[2].trim());
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setImmediate(immediate);

	    /* Load or Store a word based on an offset and a base register
	    */
	    } else if (type.matches("^SILOAD|^SISTORE")) {
		    switch (type) {
			case "SILOAD": opcode = OpCode.SILOAD; break;
			case "SISTORE": opcode = OpCode.SISTORE; break;
			default: return Instruction.error("unknown instruction.");
		    }
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    int immediate = toImmediate(split[3].trim());
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setImmediate(immediate);

	    /* Branch and link - I-type
		we will handle labels at this one
	    */
	    } else if (type.matches("^SIBEQ|^SIBNEQ")) {
		    switch (type) {
			case "SIBEQ": opcode = OpCode.SIBEQ; break;
			case "SIBNEQ": opcode = OpCode.SIBNEQ; break;
			default: return Instruction.error("unknown instruction.");
		    }
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    String label = split[3].trim();
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setLabel(label);

	    /* Load or Store a word based on memory address or an immediate value (only for load)
	    */
	    } else if (type.matches("^LW|^SW")) {
		    switch (type) {
			case "LW": opcode = OpCode.LW; break;
			case "SW": opcode = OpCode.SW; break;
			default: return Instruction.error("unknown instruction.");
		    }
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    String label = split[3].trim();
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2)
			    .setLabel(label);

	    } else if (type.matches("^LR|^WSR")) {
		    switch (type) {
			case "LR": opcode = OpCode.LR; break;
			case "WSR": opcode = OpCode.WSR; break;
			default: return Instruction.error("unknown instruction.");
		    }
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
			return new Instruction()
			    .setOpcode(opcode)
			    .setR1(r1)
			    .setR2(r2);

	    }
		
	    return Instruction.error("unknown instruction.");
        } catch (InvalidRegisterException e) {
            return Instruction.error("Invalid register " + e.getValue());
        }
    }

    /**
     * Encodes an instruction in order to write it to a MIF file or similar.
     *
     * At this point, your assembler should have resolved any labels to numeric
     * values in order to encode the instruction as all numbers.
     *
     * @param instruction an instruction that's had its label resolved (assuming it had one)
     * @return integer version of the instruction
     */
    public int encode(Instruction instruction) {

        assert instruction.getLabel() == null;

        switch(instruction.getOpcode()) {
            case ALU:
                switch(instruction.getFunc()) {
                    /*
                        check your work here: http://www.binaryhexconverter.com/decimal-to-binary-converter
                     */
                    case ALUFunctionCodes.RADD:
                    case ALUFunctionCodes.RSUB:
                    case ALUFunctionCodes.RAND:
                    case ALUFunctionCodes.ROR:
                    case ALUFunctionCodes.RNOR:
                    case ALUFunctionCodes.RXOR:
                    case ALUFunctionCodes.RXNOR:
                    case ALUFunctionCodes.RSLT:
                        return
                                // opcode is bits 31..26
                                instruction.getOpcode().getValue()<<26 |
                                // r2 is bits 25..21
                                instruction.getR2() << 21 |
                                // r3 is bits 20..16
                                instruction.getR3() << 16 |
                                // r1 is bits 15..11
                                instruction.getR1() << 11 |
				// bits 10..6 sbz
                                // func is bits 5..0
                                instruction.getFunc()
                                ;
                    case ALUFunctionCodes.SRSRL:
                    case ALUFunctionCodes.SRSLL:
                        return
                                // opcode is bits 31..26
                                instruction.getOpcode().getValue()<<26 |
                                // r2 is bits 25..21
                                instruction.getR2() << 21 |
				// bits 20..16 sbz
                                // r1 is bits 15..11.
                                instruction.getR1() << 11 |
                                // immediate is bits 10..6
                                instruction.getImmediate() << 6 |
                                // func is bits 5..0
                                instruction.getFunc()
				;
                    case ALUFunctionCodes.SRPC:
                        return
                                // opcode is bits 31..26
                                instruction.getOpcode().getValue()<<26 |
				// bits 20..16 sbz
                                // r1 is bits 10..6
                                instruction.getR1() << 6 |
                                // func is bits 5..0
                                instruction.getFunc()
				;
		    
                }
                break;
	     case IADD: 
	     case ISUB: 
	     case IAND: 
	     case IOR: 
	     case INOR: 
	     case IXOR: 
	     case ISLT: 
	     case SILOAD: 
	     case SISTORE: 
	     case SIBEQ: 
	     case SIBNEQ: 
		return
			// opcode is bits 31..26
			instruction.getOpcode().getValue()<<26 |
			// r2 is bits 25..21
			instruction.getR2() << 21 |
			// r1 is bits 20..16
			instruction.getR1() << 16 |
			// immediate is bits 15..0
			//(instruction.getImmediate() & 0xFFFF)
			(instruction.getImmediate())
			;
	     case SILU: 
	     case LW: 
	     case SW: 
		return
			// opcode is bits 31..26
			instruction.getOpcode().getValue()<<26 |
			// r2 is bits 25..21
			instruction.getR2() << 21 |
			// r1 is bits 20..16
			instruction.getR1() << 16 |
			// immediate is bits 15..0
			//(instruction.getImmediate() & 0xFFFF)
			(instruction.getImmediate())
			; 
	     case LR: 
	     case WSR: 
		return
			// opcode is bits 31..26
			instruction.getOpcode().getValue()<<26 |
			// r2 is bits 25..21
			instruction.getR2() << 21 |
			// r1 is bits 20..16
			instruction.getR1() << 16
			; 
	     case SRJAL: 
		return
			// opcode is bits 31..26
			instruction.getOpcode().getValue()<<26 |
			// r2 is bits 25..21
			instruction.getR2() << 21 |
			// r1 is bits 20..16
			instruction.getR1() << 16 |
			// immediate is bits 15..0
			(instruction.getImmediate() & 0xFFFF)
			;
	     case JR: 
		return
			// opcode is bits 31..26
			instruction.getOpcode().getValue()<<26 |
			// r1 is bits 20..16
			instruction.getR1() << 16
			;
	     case JEQ: 
	     case JNEQ: 
		return
			// opcode is bits 31..26
			instruction.getOpcode().getValue()<<26 |
			// r1 is bits 25..21
			instruction.getR1() << 21 |
			// r2 is bits 20..16
			instruction.getR2() << 16 |
			// r3 is bits 15..11
			instruction.getR3() << 11
			;
        }
        throw new IllegalArgumentException("unhandled instruction:" + instruction);
    }

    /**
     * Converts an integer to a 32-bit binary string
     * @param number
     *      The number to convert
     * @param groupSize
     *      The number of bits in a group
     * @return
     *      The 32-bit long bit string
     */
    public static String intToString(int number, int groupSize) {
        StringBuilder result = new StringBuilder();

        for(int i = 31; i >= 0 ; i--) {
            int mask = 1 << i;
            result.append((number & mask) != 0 ? "1" : "0");
    
            if (i % groupSize == 0)
                result.append(" ");
        }
        result.replace(result.length() - 1, result.length(), "");
    
        return result.toString();
    }

    /**
     * Parses the source into a list of Instructions.
     * @param reader Source for the program
     * @return list of instructions that are ready to encode
     * @throws IOException when there's an error reading a line
     */
    public List<Instruction> parse(Reader reader) throws IOException {
        List<Instruction> instructions = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        String line;
        int lineNumber = 0;

	/* Adding the line number 0 instruction called bootstrapInstruction
	which is an instruction with address 0000 to go to main label
	SIBEQ $z0 $z0 main
	*/

	Instruction bootstrapInstruction = new Instruction();
	OpCode bootstrapOpcode = OpCode.SIBEQ;
	bootstrapInstruction.setOpcode(bootstrapOpcode);
	bootstrapInstruction.setLineNumber(lineNumber);
        bootstrapInstruction.setR1(0);
        bootstrapInstruction.setR2(0);
        bootstrapInstruction.setLabel("main");
	bootstrapInstruction.setSourceLine("SIBEQ $z0 $z0 main");
	instructions.add(bootstrapInstruction);

        while ((line = br.readLine()) != null) {
            lineNumber++;
            if (line.startsWith("#") || line.startsWith("--") || line.matches("^$")) {
                // it's a comment or blank line, ignore it
                continue;
            }


            if (line.matches("^[^\\s]+:.*")) {
                // it's a label, set OpCode to LABEL
		OpCode opcode = OpCode.LABEL;
		String[] split = line.split(":");
		Instruction instruction = new Instruction();
		instruction.setOpcode(opcode);
		instruction.setLabel(split[0].trim());
	        instruction.setLineNumber(lineNumber);
	        instruction.setSourceLine(line);

		// If this label makes a reference to ascii, set ascii field
		// MYDATA: .ascii "My text"
		if (line.matches(".*ascii.*\".*\"")) {
			String[] splittext = split[1].split("\"");
			//instruction.setAscii(splittext[1].trim());
			instruction.setAscii(splittext[1]);
		}
		// If this label makes a reference to memory address, set memaddress field
		// IOADDRESS: .address "0x00FF00"
		if (line.matches(".*address.*\".*\"")) {
			String[] splittext = split[1].split("\"");
			instruction.setMemAddress(splittext[1].trim());
		}
	        instructions.add(instruction);
                continue;
	    }


            Instruction instruction = parse(line);
            instruction.setLineNumber(lineNumber);
            instruction.setSourceLine(line);
            instructions.add(instruction);
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

   public static String printMifLine(String HexIntInStr, int assemblerline, String comment) {
	// remember: mif line is not the PC address 
	String hexlineFirstHalfInstruction = decToHex((assemblerline*2),4,4);
	String hexlineSecondHalfInstruction = decToHex(((assemblerline*2)+1),4,4);
	//int decimalFirstHalf = Integer.parseInt(binaryIntInStr.substring(31,16),2);
	//int decimalSecondHalf = Integer.parseInt(binaryIntInStr.substring(15,0),2);
	//String hexFirstHalfInstruction = decToHex(decimalFirstHalf,4,4);
	//String hexSecondHalfInstruction = decToHex(decimalSecondHalf,4,4);
	String hexFirstHalfInstruction = HexIntInStr.substring(0,4);
	String hexSecondHalfInstruction = HexIntInStr.substring(4,8);
	System.out.println(hexlineFirstHalfInstruction + " : " + hexFirstHalfInstruction + " ; " + comment);
	System.out.println(hexlineSecondHalfInstruction + " : " + hexSecondHalfInstruction + " ; ");
	return hexFirstHalfInstruction;
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



    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            System.err.println("ERROR: must pass name of input file");
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

        try (FileReader fileReader = new FileReader(file)) {
            Assembler assembler = new Assembler();

            List<Instruction> instructions = assembler.parse(fileReader);

	    if (vpcdebug) {
		    for(Instruction instruction : instructions) {
			System.out.println(instruction);
		    }
	    }

	    /* Checking code: invalid lines and invalid labels */

	    int returnerrors = 0;

	    // Checking first for invalid lines, if so, abort
            for(Instruction instruction : instructions) {
		String error = instruction.getErrorMessage();
		if (error != null) {
			System.err.println("ERROR: VPC Compiler could not parse the line: " + error + " line " + instruction.getLineNumber());
			returnerrors++;
		}
	    }
            if (returnerrors > 0) {
     		System.err.println("ERROR: VPC Compiler found " + returnerrors + " parse errors. Aborting");
		return;
	    }

	    // At this point, there is no invalid lines
	    // Now checking labels references
	
	    // Will use lists for the reference
	    List<String> definedlabels = new ArrayList<String>();
	    List<String> referredlabels = new ArrayList<String>();

	    // I will build a hash for labels and fileline number - not sure if i will need it at this point
	    ArrayList<Map.Entry<String, Integer>> LabelToFileLine = new ArrayList<Map.Entry<String, Integer>>();

            for(Instruction instruction : instructions) {

		OpCode code = instruction.getOpcode();
		int codevalue = code.getValue();

		String linelabel = instruction.getLabel();
		int linenumber = instruction.getLineNumber();

		// Labels
		// Filling the array with declared labels - minimally "main" must be asserted
		// There is a pseudo OpCode called "LABEL" just to parse labels
		if (codevalue == OpCode_LABEL) {
			if (definedlabels.contains(linelabel)) {
				System.err.println("ERROR: VPC Compiler found duplicated label: " + linelabel);
				returnerrors++;
			} else {
				definedlabels.add(linelabel);
				LabelToFileLine.add(new AbstractMap.SimpleEntry(linelabel, linenumber));
				
			}
		}

		// Instructions that use label: SRJAL, SIBEQ, SIBNEQ, LW and SW
		if ((codevalue == OpCode_SRJAL) || (codevalue == OpCode_SIBEQ) || (codevalue == OpCode_SIBNEQ) || (codevalue == OpCode_LW) || (codevalue == OpCode_SW)) {
			referredlabels.add(linelabel);

		}
		
            }

	   // Checking if main exists as label
	   if (!definedlabels.contains("main")) {
		System.err.println("ERROR: VPC Compiler did not find main label specified.");
		returnerrors++;
	   } 

	    // Correlating the referredlabels with declared labels
            for (String referred : referredlabels) {
		   if (!definedlabels.contains(referred)) {
			System.err.println("ERROR: VPC Compiler found invalid label references: " + referred);
			returnerrors++;
		   } 
	    }
           if (returnerrors > 0) {
		System.err.println("ERROR: VPC Compiler found " + returnerrors + " label errors. Aborting");
		return;
	   }

	   /*
	     Now starting to encode the file
	     Will need a table to convert the file line number and the assembler line number
	     and fill a hash table to map the label to the assembler line number
 	  */
	  if (vpcdebug) {
		  System.out.println("DEBUG: Parsing syntax completed. Building the program and data stack.");
	  }

	// After this loop I know the address of the datastack: it will be assemblerline, skipping all LABEL lines plus padding buch of lines of "0"
	int assemblerline = 0;
        for(Instruction instruction : instructions) {
		OpCode code = instruction.getOpcode();
		int codevalue = code.getValue();
		if (codevalue == 100) {
			continue;
		}
		assemblerline++;
	}
	int endprogramstack = (assemblerline + TEXT_SEGMENT)*4;
	int datastackaddress = endprogramstack+4;
	if (vpcdebug) {
		  System.out.println("DEBUG: The program stack (in decimal) goes to 0 to " + endprogramstack + ". Data Stack commences at " + datastackaddress + ".");
	}

	/* Looping the main instruction data structure and creating three ancilliary data structures:
		1-) LabeltoCompileLine: a hash for label pointing to pcaddressline (PC) or datastackaddress 
		2-) CompilerLine: an array with a sequential line number as index and the actual line of the asm file. This will be the used for the program stack
		3-) DataStackOffset: a string array with all chars for the datastack (separated by \n")
	*/

	/* simple hash table - will correlate assembler line times 4 with label! */
	  HashMap<String, Integer> LabeltoCompileLine = new HashMap<String, Integer>();

	/* simple array - will correlate an increment of assembler line with the line of the file! */
	  ArrayList<Integer> CompilerLine = new ArrayList<Integer>();

	/* String array for the datastack - the index will be the offset address */
	String strDataStackOffset = "";

	assemblerline = 0;
	int int_datastackoffset = 0;
	
        for(Instruction instruction : instructions) {
		OpCode code = instruction.getOpcode();
		int codevalue = code.getValue();
		int pcaddress = assemblerline*4;  // "PC address" - this is the memory location of the instruction
		String linelabel = instruction.getLabel();
		// linenumber will be the actual line number of the asm source code
		int linenumber = instruction.getLineNumber();
		if (codevalue == 100) {
			// checking if the label is ascii or anything else
			// if ascii, will put the offset address for datastack
			// if not, label will get the pcaddress (PC)
			String ascii = instruction.getAscii();
			if (ascii != null) {
				int int_datalabeladdress = (int_datastackoffset*4)+datastackaddress;
				LabeltoCompileLine.put(linelabel,(int_datalabeladdress));
				instruction.setAddress((int_datalabeladdress));
				if (vpcdebug) {
					String hex = decToHex((int_datalabeladdress),4,4);
					System.out.println("DEBUG: ascii label " + linelabel + " will be assigned data address " + hex );		 
				}
				for (char s: ascii.toCharArray()) {
					strDataStackOffset += s;
					int_datastackoffset++;
				}
				strDataStackOffset += "\n";
				int_datastackoffset++;
			} else {
				String memaddress = instruction.getMemAddress();
				if (memaddress != null) {
					 if (memaddress.startsWith("0x")) {
						// it is hex value
						String stemp = memaddress.substring(2);
						int int_memaddress = Integer.parseInt(stemp,16);
						LabeltoCompileLine.put(linelabel,int_memaddress);
						instruction.setAddress(int_memaddress);
						if (vpcdebug) {
							System.out.println("DEBUG: memory address label " + linelabel + " will be assigned " + memaddress);		 
						}
					} else {
						System.err.println("ERROR: VPC Compiler cannot convert " + memaddress + " address from hex. Expecting 0x address on label " + linelabel);
						return;
					}

				} else {
					// just a regular label, non ascii, non address
					LabeltoCompileLine.put(linelabel,(pcaddress));
					instruction.setAddress(pcaddress);
					if (vpcdebug) {
						Integer value = LabeltoCompileLine.get(linelabel);
						String hex = decToHex((pcaddress),4,4);
						System.out.println("DEBUG: label " + linelabel + " will be assigned byte number " + value + " at line " + hex);		 
					}
				}

			}
			
			continue;
			
		}
		assemblerline++;
		CompilerLine.add(linenumber);
	}

	  int endinputdatastack = datastackaddress + (int_datastackoffset*4);
	  if (vpcdebug) {
		  System.out.println("DEBUG: Data Stack (decimal) commences at " + datastackaddress + " until to " + endinputdatastack);
	  }

	  // Reading instructions data instructions element by element using compilerline
	  // http://sites.fas.harvard.edu/~cscie287/fall2017/def_mif.htm

	  // DE2-115
	  System.out.println("DEPTH = 32768;\nWIDTH = 16;\nADDRESS_RADIX = HEX;\nDATA_RADIX = HEX;\nCONTENT\nBEGIN");
	  assemblerline = 0;

	  // For each element of the CompileLine array (a sequential for each instruction)
	  for (Integer compileline : CompilerLine) {
		  for(Instruction instruction : instructions) {
			int linenumber = instruction.getLineNumber();
			if (linenumber == compileline) {
				OpCode code = instruction.getOpcode();
				int codevalue = code.getValue();
				String comment = "";
				// replacing LABELs by addresses
				if ((codevalue == OpCode_SRJAL) || (codevalue == OpCode_SIBEQ) || (codevalue == OpCode_SIBNEQ) || (codevalue == OpCode_LW) || (codevalue == OpCode_SW)) {
					String linelabel = instruction.getLabel();
					String  myOpCode = "";
					Integer value = LabeltoCompileLine.get(linelabel);
					instruction.setImmediate(value);
				        switch (codevalue) {
				  	  case OpCode_SRJAL:  myOpCode = "SRJAL"; break;
				  	  case OpCode_SIBEQ:  myOpCode = "SIBEQ"; break;
				  	  case OpCode_SIBNEQ: myOpCode = "SIBNEQ"; break;
				  	  case OpCode_LW:     myOpCode = "LW"; break;
				  	  case OpCode_SW:     myOpCode = "SW"; break;
					}
					
					comment = "-- " + myOpCode + " points to label " + linelabel + ", decimal value of " + value; 
				}
				int myencode = assembler.encode(instruction);
				String HexIntInStr = decToHex(myencode,8,4); 
				// Now breaking down the word in two mif lines
				printMifLine(HexIntInStr,assemblerline,comment);
				assemblerline++;
				break;
			}
		  }
	  }

	  Integer totalcodelines = assemblerline+TEXT_SEGMENT;
	  String ZeroIntInStr = "00000000";

	  // Padding extra lines of program stack with 0's
	  while (assemblerline <= (totalcodelines)) {
		String comment = "--End of Program Stack";
		printMifLine(ZeroIntInStr, assemblerline, comment);
		assemblerline++;
	  }

	  // adding the datasegment now
          for (char b: strDataStackOffset.toCharArray()) {
		int myencode = (int) b;
		String HexIntInStr = decToHex(myencode,8,4); 
		String comment = "";
		if (b == '\n') {
			comment = "--data end of label";
		} else {
			comment = "--data " + b;
		}
		printMifLine(HexIntInStr, assemblerline, comment);
		assemblerline++;
       	  }
	  
	  // Padding remaining lines of data stack with 0's
	  int finalline = (endinputdatastack/4)+(TEXT_SEGMENT*2);
	  while (assemblerline <= finalline) {
		String comment = "--Free";
		printMifLine(ZeroIntInStr, assemblerline, comment);
		assemblerline++;
	  }
	  System.out.println("END"); 
        }
    }
}
