package e93.assembler.test;

import e93.assembler.Assembler;
import e93.assembler.Instruction;
import e93.emulator.MemorySubsystem;

public class MemoryFixture implements MemorySubsystem {

    private final byte[] bytes;

    public MemoryFixture() {
        this.bytes = new byte[1024];
    }

    public MemoryFixture(byte[] memory) {
        this.bytes = memory;
    }

    @Override
    public void writeInt(final int address, final int value) {
        byte hiBits = (byte) ((value >> 8) & 0xff);
        byte lowBits = (byte) (value & 0xff);
        bytes[address] = hiBits;
        bytes[address+1] = lowBits;
    }

    @Override
    public int readInt(final int address) {
        int hiBits = bytes[address] & 0xff;
        int lowBits = bytes[address +1] & 0xff;
        return (hiBits << 8) | lowBits;
    }

    @Override
    public MemorySubsystem copy() {
        return new MemoryFixture(bytes.clone());
    }

    @Override
    public byte[] toBytes() {
        return bytes.clone();
    }

    static void writeInstructions(MemorySubsystem memory, Instruction...instructions) {
        int address = 0;
        for(Instruction instruction : instructions) {
            int encoded = Assembler.encode(instruction);
            memory.writeInt(address, encoded);
            address += 2;
        }
    }


}
