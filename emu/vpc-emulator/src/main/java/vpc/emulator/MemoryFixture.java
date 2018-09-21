package vpc.emulator;

import vpc.emulator.MemorySubsystem;

public class MemoryFixture implements MemorySubsystem {

    private final byte[] bytes;

    public MemoryFixture() {
        this.bytes = new byte[65536];
    }

    public MemoryFixture(byte[] memory) {
        this.bytes = memory;
    }

    @Override
    public void writeInt(final int address, final int value) {
        byte hiBits          = (byte) ((value >> 24) & 0xff);
        byte upMiddleBits    = (byte) ((value >> 16) & 0xff);
        byte downMiddleBits  = (byte) ((value >> 8) & 0xff);
        byte lowBits         = (byte) (value & 0xff);
        bytes[address] = hiBits;
        bytes[address+1] = upMiddleBits;
        bytes[address+2] = downMiddleBits;
        bytes[address+3] = lowBits;
    }

    @Override
    public int readInt(final int address) {
        int hiBits = bytes[address] & 0xff;
        int upMiddleBits = bytes[address+1] & 0xff;
        int downMiddleBits = bytes[address+2] & 0xff;
        int lowBits = bytes[address+3] & 0xff;
        return (hiBits << 24) | (upMiddleBits << 16) | (downMiddleBits << 8) | lowBits;
    }

    @Override
    public MemorySubsystem copy() {
        return new MemoryFixture(bytes.clone());
    }

    @Override
    public byte[] toBytes() {
        return bytes.clone();
    }

/*    static void writeInstructions(MemorySubsystem memory, Instruction...instructions) {
        int address = 0;
        for(Instruction instruction : instructions) {
            int encoded = Assembler.encode(instruction);
            memory.writeInt(address, encoded);
            address += 4;
        }
    }

*/

   public String toString() {
	String output = "";
	for (int i = 0; i <= 65535; i++) {
		String address = Integer.toHexString(i);
		String value = Integer.toHexString(bytes[i] & 0xFF);
		output += address + " : " + value + "\n"; 
	}
	return output;
   }

}

