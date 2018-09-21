package vpc.emulator;

import vpc.emulator.ast.AssemblyVisitor;
import vpc.emulator.ast.Rand;

import javax.annotation.Generated;
import java.util.Objects;

/**
 * Simple data structure for an Instruction. All of the instructions in the
 * instruction set are modeled with this class.
 *
 * Note that not all instructions will have all of the fields populated.
 *
 * Also note that this assumes a two register instruction format only. If you have
 * a three register format then you should expand this.
 *
 *
 * @author rab405 largely using markford e93 sample code
 */
public abstract class Instruction {
    /**
     * The address (or value of the PC) of the instruction is set by the assembler.
     * This should be done at a point where you're sure that the instruction
     * won't change. For example, if it's a pseudo instruction, it may
     * generate 2 or 3 other instructions that take its place and would therefore
     * shift the addresses of all of the other instructions.
     *
     * The address may be used when encoding an instruction.
     */
    private int address;

    /**
     * The OpCode for the instructions. All instructions have OpCodes.
     */
    private OpCode opcode;

    /**
     * Some instructions have a value for the first register. This may be rd, rs,
     * or rt depending on your instruction set.
     */
    private int r1;

    /**
     * Some instructions have a value for the second register. This may be rd, rs,
     * or rt depending on your instruction set.
     */
    private int r2;

    /**
     * R-type instructions have a value for the third register.
     */
    private int r3;

    /**
     * Some instructions have an immediate value.
     */
    private int immediate;

    /**
     * Some instructions have a function code. This is a good technique to pack
     * more behavior into an instruction set. For example, all of the ALU related
     * instructions could have the same opcode value but then use a few bits
     * in the instruction to differentiate between them.
     */
    private int func;

    /**
     * Some instructions have a label. This is a reference to some other instruction
     * that they're jumping or branching to.
     *
     * In cases where the assembly programmer used a label in their instruction,
     * the Assembler is responsible for replacing this label reference with the
     * correct immediate. The way this is done is outlined in the principles of
     * operation document for your instruction set.
     *
     * For example, when handling a branch instruction, the assembler may take
     * address of the label and compute the difference between it and PC + 2.
     * When handling a jump instruction, it may take the address of the label and
     * compute a value for the lower 12 bits that can be OR'd in with the high
     * order bits of PC + 2.
     */
    private String label;

    private String ascii;

    private String memaddress;

    private int lineNumber;

    private String sourceLine;

    private String errorMessage;

    public static Instruction error(String errorMessage) {
        return new Instruction() {
            @Override
            public void accept(final AssemblyVisitor assemblyVisitor) {

            } 
        }.setErrorMessage(errorMessage);
    }

    public boolean isValid() {
        return opcode != null;
    }

    @Generated("by IDE")
    public int getAddress() {
        return address;
    }

    @Generated("by IDE")
    public Instruction setAddress(final int address) {
        this.address = address;
        return this;
    }

    @Generated("by IDE")
    public OpCode getOpcode() {
        return opcode;
    }

    @Generated("by IDE")
    public Instruction setOpcode(final OpCode opcode) {
        this.opcode = opcode;
        return this;
    }

    @Generated("by IDE")
    public int getR1() {
        return r1;
    }

    @Generated("by IDE")
    public Instruction setR1(final int r1) {
        this.r1 = r1;
        return this;
    }

    @Generated("by IDE")
    public int getR2() {
        return r2;
    }

    @Generated("by IDE")
    public Instruction setR2(final int r2) {
        this.r2 = r2;
        return this;
    }

    @Generated("by IDE")
    public int getR3() {
        return r3;
    }

    @Generated("by IDE")
    public Instruction setR3(final int r3) {
        this.r3 = r3;
        return this;
    }

    @Generated("by IDE")
    public int getImmediate() {
        return immediate;
    }

    @Generated("by IDE")
    public Instruction setImmediate(final int immediate) {
        this.immediate = immediate;
        return this;
    }

    @Generated("by IDE")
    public int getFunc() {
        return func;
    }

    @Generated("by IDE")
    public Instruction setFunc(final int func) {
        this.func = func;
        return this;
    }

    @Generated("by IDE")
    public String getLabel() {
        return label;
    }

    @Generated("by IDE")
    public Instruction setLabel(final String label) {
        this.label = label;
        return this;
    }

    @Generated("by IDE")
    public String getAscii() {
        return ascii;
    }

    @Generated("by IDE")
    public Instruction setAscii(final String ascii) {
        this.ascii = ascii;
        return this;
    }

    @Generated("by IDE")
    public String getMemAddress() {
        return memaddress;
    }

    @Generated("by IDE")
    public Instruction setMemAddress(final String memaddress) {
        this.memaddress = memaddress;
        return this;
    }

    @Generated("by IDE")
    public int getLineNumber() {
        return lineNumber;
    }

    @Generated("by IDE")
    public Instruction setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    @Generated("by IDE")
    public String getSourceLine() {
        return sourceLine;
    }

    @Generated("by IDE")
    public Instruction setSourceLine(final String sourceLine) {
        this.sourceLine = sourceLine;
        return this;
    }

    @Generated("by IDE")
    public String getErrorMessage() {
        return errorMessage;
    }

    @Generated("by IDE")
    public Instruction setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public abstract void accept(AssemblyVisitor assemblyVisitor);

    @Override
    @Generated("by IDE")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Instruction that = (Instruction) o;
        return address == that.address &&
                r1 == that.r1 &&
                r2 == that.r2 &&
                r3 == that.r3 &&
                immediate == that.immediate &&
                func == that.func &&
                lineNumber == that.lineNumber &&
                opcode == that.opcode &&
                Objects.equals(label, that.label) &&
                Objects.equals(ascii, that.ascii) &&
                Objects.equals(memaddress, that.memaddress) &&
                Objects.equals(sourceLine, that.sourceLine) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    @Generated("by IDE")
    public int hashCode() {
        return Objects.hash(address, opcode, r1, r2, r3, immediate, func, label, ascii, memaddress, lineNumber, sourceLine, errorMessage);
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "address=" + address +
                ", opcode=" + opcode +
                ", r1=" + r1 +
                ", r2=" + r2 +
                ", r3=" + r3 +
                ", immediate=" + immediate +
                ", func=" + func +
                ", label='" + label + '\'' +
                ", ascii='" + ascii + '\'' +
                ", memaddress='" + memaddress + '\'' +
                ", lineNumber=" + lineNumber +
                ", sourceLine='" + sourceLine + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
