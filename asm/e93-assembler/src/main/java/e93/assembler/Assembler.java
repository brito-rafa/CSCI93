package e93.assembler;

import e93.assembler.ast.AddImmediate;
import e93.assembler.ast.And;
import e93.assembler.ast.JumpImmediate;
import e93.assembler.ast.LoadWord;
import e93.assembler.ast.OrImmediate;
import e93.assembler.ast.StoreWord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A starting point for an assembler. Very bare bones.
 *
 * @author markford
 */
public class Assembler {

    /**
     * The maximum number of registers in the system. This is used to verify
     * that the assembly line doesn't refer to an impossible register value.
     */
    private static final int MAX_REGISTERS = 16;

    /**
     * A mask used to extract the register value from an encoded instruction.
     * This should be enough bits to mask all possible values of the register.
     */
    private static final int REGISTER_MASK = 0xf;

    private static final int IMMEDIATE_MASK = 0xff;

    /**
     * Parses a line of assembly and returns an instruction that the system will
     * know how to execute.
     *
     * At this point, the instruction may refer to a label that needs to be
     * resolved.
     *
     * Note: the format of the instruction is the very basic format that was
     *       outlined in section. It uses commas to separate each of the values
     *       for the instruction. This is a little verbose but very easy to parse.
     *       The most common mistake is to forget a comma somewhere.
     *
     * @param line raw line of assembly to parse into an instruction
     * @return an instruction
     * @throws IllegalArgumentException if the line cannot be parsed.
     */
    public Instruction parse(String line) {
        String[] split = line.split(",");
        if (split.length > 3) {
            return Instruction.error("unknown instruction");
        }
        try {
            String type = split[0].trim();
            switch (type) {
                case "AND": {
                    OpCode opcode = OpCode.ALU;
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    int functionCode = 1;
                    return new And()
                            .setOpcode(opcode)
                            .setR1(r1)
                            .setR2(r2)
                            .setFunc(functionCode);
                }
                case "ADDI": {
                    OpCode opcode = OpCode.ADDI;
                    int r1 = toRegister(split[1].trim());
                    int immediate = toImmediate(split[2].trim());
                    return new AddImmediate()
                            .setOpcode(opcode)
                            .setR1(r1)
                            .setImmediate(immediate);
                }
                case "SW": {
                    OpCode opcode = OpCode.SW;
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    return new StoreWord()
                            .setOpcode(opcode)
                            .setR1(r1)
                            .setR2(r2);
                }
                case "LW": {
                    OpCode opcode = OpCode.LW;
                    int r1 = toRegister(split[1].trim());
                    int r2 = toRegister(split[2].trim());
                    return new LoadWord()
                            .setOpcode(opcode)
                            .setR1(r1)
                            .setR2(r2);
                }
                case "J": {
                    OpCode opcode = OpCode.J;
                    int immediate = toImmediate(split[1].trim());
                    return new JumpImmediate()
                            .setOpcode(opcode)
                            .setImmediate(immediate<<1);
                }
                case "ORI": {
                    OpCode opcode = OpCode.ORI;
                    int r1 = toRegister(split[1].trim());
                    int immediate = toImmediate(split[2].trim());
                    return new OrImmediate()
                            .setOpcode(opcode)
                            .setR1(r1)
                            .setImmediate(immediate);
                }
                default:
                    return Instruction.error("unknown instruction. Are you missing a comma?");
            }
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
    public static int encode(Instruction instruction) {

        assert instruction.getLabel() == null;

        switch(instruction.getOpcode()) {
            case ALU:
                switch(instruction.getFunc()) {
                    /*
                        check your work here: http://www.binaryhexconverter.com/decimal-to-binary-converter
                     */
                    case ALUFunctionCodes.AND:
                        return
                                // opcode is bits 15..12
                                instruction.getOpcode().getValue()<<12 |
                                // r1 is bits 11..8
                                instruction.getR1() << 8 |
                                // r2 is bits 7..4
                                instruction.getR2() << 4 |
                                // func is bits 3..0
                                instruction.getFunc()
                                ;
                }
                break;
            case SW:
            case LW:
                return instruction.getOpcode().getValue()<<12 |
                        instruction.getR1() << 8 |
                        instruction.getR2() << 4;
            case ORI:
            case ADDI:
                return instruction.getOpcode().getValue()<<12 |
                            instruction.getR1() << 8 |
                            instruction.getImmediate();
            case J:
                return instruction.getOpcode().getValue()<<12 |
                        instruction.getImmediate();
        }
        throw new IllegalArgumentException("unhandled instruction:" + instruction);
    }

    /**
     * Decodes an instruction from its encoded form. You'll need something like
     * this for when you write the emulator.
     *
     * @param encoded numeric form of the instruction that we'll decode
     * @return Instruction
     * @throws IllegalArgumentException if we don't know how to decode it
     */
    public static Instruction decode(int encoded) {
        // the opcode is always in 15..12 but the rest of the instruction is
        // unknown until we know what the opcode is so get that first!
        int value = encoded >> 12;
        OpCode opCode = OpCode.fromEncoded(value);
        switch (opCode) {
            case ALU:
                // get the function code to figure out what type of ALU operation
                // it is. The function code is the lower two bits which I can get
                // by AND'ing the number 3 (which is 11 in binary)
                int functionCode = encoded & 0x3;
                switch(functionCode) {
                    case ALUFunctionCodes.AND:
                        // r1 is always in 11..8
                        // shift right to drop the low order bits and then mask with
                        // REGISTER_MASK in order to get all of the
                        // bits for the register number
                        int r1 = (encoded >> 8) & REGISTER_MASK;
                        // r2 is always in 7..4
                        // shift right to drop the low order bits and then mask with
                        // REGISTER_MASK in order to get all of the
                        // bits for the register number
                        int r2 = (encoded >> 4) & REGISTER_MASK;
                        return new And()
                                .setOpcode(opCode)
                                .setFunc(ALUFunctionCodes.AND)
                                .setR1(r1)
                                .setR2(r2);
                }

            case SW:
                return new StoreWord().setOpcode(opCode)
                        .setR1((encoded >> 8) & REGISTER_MASK)
                        .setR2((encoded >> 4) & REGISTER_MASK);
            case LW:
                return new LoadWord().setOpcode(opCode)
                        .setR1((encoded >> 8) & REGISTER_MASK)
                        .setR2((encoded >> 4) & REGISTER_MASK);
            case ORI:
                return new OrImmediate().setOpcode(opCode)
                        .setR1((encoded >> 8) & REGISTER_MASK)
                        .setImmediate(encoded & IMMEDIATE_MASK);
            case ADDI:
                return new AddImmediate().setOpcode(opCode)
                        .setR1((encoded >> 8) & REGISTER_MASK)
                        .setImmediate(encoded & IMMEDIATE_MASK);
            case J:
                return new JumpImmediate().setOpcode(opCode)
                        .setImmediate((encoded & 0xfff)>>1);

        }
        throw new IllegalArgumentException("unhandled encoded instruction:" + encoded);
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
        while ((line = br.readLine()) != null) {
            lineNumber++;
            if (line.startsWith("#")) {
                // it's a comment, ignore it
                continue;
            }

            // todo need to handle labels here

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
     * @return number of the register which is within range
     * @throws IllegalArgumentException if we can't parse it or it's out of range
     */
    private int toRegister(String s) throws InvalidRegisterException {
        if (!s.startsWith("$r")) {
            throw new InvalidRegisterException(s);
        }
        int register = Integer.parseInt(s.substring(2));
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
        // todo - you may want to support hex
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
            return Integer.parseInt(s.substring(2), 16);
        } else {
            return Integer.parseInt(s);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            System.err.println("must pass name of input file");
            return;
        }

        File file = new File(args[0]);
        if (!file.isFile()) {
            System.err.println("file not found or not readable:" + args[0]);
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            Assembler assembler = new Assembler();

            List<Instruction> instructions = assembler.parse(fileReader);

            // this isn't a MIF file format but rather a simple toString() on each of the instructions
            for(Instruction instruction : instructions) {
                System.out.println(instruction);
            }
        }
    }
}
