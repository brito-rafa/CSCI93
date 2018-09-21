package e93.assembler.test;

import e93.assembler.Assembler;
import e93.assembler.Instruction;
import e93.emulator.ExecutionVisitor;
import e93.emulator.MemorySubsystem;
import org.junit.Test;

import static e93.assembler.test.MemoryFixture.writeInstructions;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExecutionVisitorTest {
    private final Assembler assembler = new Assembler();
    private final MemoryFixture memoryFixture = new MemoryFixture();

    @Test
    public void and() throws Exception {
        // load memory
        // load registers
        // execute
        // assert register results
        // assert memory
        Instruction and = parse("AND, $r1, $r0");
        writeInstructions(memoryFixture, and);

        // set the value of r1 to 5
        int[] registers = new int[16];
        registers[1] = 5;

        MemorySubsystem original = memoryFixture.copy();

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, registers);
        and.accept(ev);

        assertEquals("register r1 should have been set to 0",
                0, registers[1]);

        assertArrayEquals("memory shouldn't have changed",
                original.toBytes(),
                memoryFixture.toBytes());

    }

    @Test
    public void ori() throws Exception {
        // load memory
        // load registers
        // execute
        // assert register results
        // assert memory
        Instruction ori = parse("ORI, $r1, 0x1");
        writeInstructions(memoryFixture, ori);

        // set the value of r1 to 2, this way we can see the effect of ORI
        int[] registers = new int[16];
        registers[1] = 2;

        MemorySubsystem original = memoryFixture.copy();

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, registers);
        ori.accept(ev);

        assertEquals("register r1 should have been set to 3",
                3, registers[1]);

        assertArrayEquals("memory shouldn't have changed",
                original.toBytes(),
                memoryFixture.toBytes());

    }

    @Test
    public void addi() throws Exception {
        // load memory
        // load registers
        // execute
        // assert register results
        // assert memory
        Instruction addi = parse("ADDI, $r1, 0x1");
        writeInstructions(memoryFixture, addi);

        // set the value of r1 to 2
        int[] registers = new int[16];
        registers[1] = 2;

        MemorySubsystem original = memoryFixture.copy();

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, registers);
        addi.accept(ev);

        assertEquals("register r1 should have been set to 3",
                3, registers[1]);

        assertArrayEquals("memory shouldn't have changed",
                original.toBytes(),
                memoryFixture.toBytes());

    }

    @Test
    public void sw() throws Exception {
        // load memory
        // load registers
        // execute
        // assert register results
        // assert memory
        Instruction sw = parse("SW, $r1, $r2");
        writeInstructions(memoryFixture, sw);

        int[] registers = new int[16];
        registers[1] = 2;
        registers[2] = 0x64;

        int[] originalRegisters = registers.clone();

        MemorySubsystem expected = memoryFixture.copy();
        expected.writeInt(0x64, 2);

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, registers);
        sw.accept(ev);

        assertArrayEquals("registers shouldn't have changed",
                originalRegisters,
                registers);

        assertArrayEquals("memory should have changed in one spot",
                expected.toBytes(),
                memoryFixture.toBytes());

    }

    @Test
    public void lw() throws Exception {
        // load memory
        // load registers
        // execute
        // assert register results
        // assert memory
        Instruction sw = parse("LW, $r1, $r2");
        writeInstructions(memoryFixture, sw);

        int[] registers = new int[16];
        registers[1] = 42;
        registers[2] = 0x64;

        memoryFixture.writeInt(0x64, 2);

        int[] expectedRegisters = registers.clone();
        expectedRegisters[1] = 2;

        MemorySubsystem expected = memoryFixture.copy();

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, registers);
        sw.accept(ev);

        assertArrayEquals("registers should have changed",
                expectedRegisters,
                registers);

        assertArrayEquals("memory shouldn't have changed",
                expected.toBytes(),
                memoryFixture.toBytes());

    }

    @Test
    public void jumpImmediate() throws Exception {
        // load memory
        // load registers
        // execute
        // assert register results
        // assert memory
        Instruction jump = parse("J, 0x3");
        writeInstructions(memoryFixture, jump);

        int[] registers = new int[16];
        int[] originalRegisters = registers.clone();

        MemorySubsystem expected = memoryFixture.copy();

        ExecutionVisitor ev = new ExecutionVisitor(memoryFixture, registers);
        jump.accept(ev);

        assertArrayEquals("registers shouldn't have changed",
                originalRegisters,
                registers);

        assertArrayEquals("memory shouldn't have changed",
                expected.toBytes(),
                memoryFixture.toBytes());

        assertEquals(0x6, ev.getPc());
    }

    private Instruction parse(final String line) {
        Instruction and = assembler.parse(line);
        assertTrue("failed to parse instruction", and.isValid());
        return and;
    }


}
