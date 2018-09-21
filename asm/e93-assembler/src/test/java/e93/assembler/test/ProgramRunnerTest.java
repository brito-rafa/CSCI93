package e93.assembler.test;

import e93.assembler.Assembler;
import e93.assembler.Instruction;
import e93.emulator.ExecutionVisitor;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ProgramRunnerTest {
    private final Assembler assembler = new Assembler();
    private final MemoryFixture memoryFixture = new MemoryFixture();

    @Test
    public void simpleProgram_max10() throws Exception {
        // parse program into instructions
        // load program into memory
        // simple loop to execute instructions
        // stop after max of 10 instructions

        AtomicInteger address = new AtomicInteger(0);

        Stream.of(
                "AND, $r5, $r0",   // 1         word 0
                "AND, $r6, $r0",   // 2         word 1
                "ORI, $r6, 0x64",  // 3  8      word 2
                "ADDI, $r5, 0x1",  // 4  9      word 3
                "SW, $r5, $r6",    // 5 10      word 4
                "LW, $r5, $r6",    // 6         word 5
                "J, 0x06"          // 7         word 6  jumps to (0x06 << 1) or (byte 12) or (word 6)
        )
        .map(assembler::parse)
        .map(Assembler::encode)
        .forEachOrdered(encoded -> memoryFixture.writeInt(address.getAndAdd(2), encoded));

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, new int[16]);
        int count = 0;
        while (count <= 10) {
            int encodedInstruction = memoryFixture.readInt(ev.getPc());
            Instruction decodedInstruction = Assembler.decode(encodedInstruction);
            decodedInstruction.accept(ev);
            count++;
        }

        // after 10 instructions, we should have incremented r5 twice and written to memory twice
        assertEquals(2, ev.getRegisters()[5]);
        assertEquals(2, memoryFixture.readInt(0x64));
    }
}
