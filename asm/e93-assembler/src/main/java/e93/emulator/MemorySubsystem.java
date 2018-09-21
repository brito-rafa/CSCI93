package e93.emulator;

public interface MemorySubsystem {
    void writeInt(int address, int value);

    int readInt(int address);

    MemorySubsystem copy();

    byte[] toBytes();
}
