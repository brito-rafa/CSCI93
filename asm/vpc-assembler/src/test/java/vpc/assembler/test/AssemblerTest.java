package vpc.assembler.test;

import vpc.assembler.Assembler;
import vpc.assembler.Instruction;
import vpc.assembler.OpCode;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author rab405 largely using markford e93 sample code
 */
public class AssemblerTest {

    private Assembler assembler = new Assembler();

    @Test
    public void parseThreeRegisterType() throws Exception {
        Instruction actual = assembler.parse("RADD $g0 $g1 $z0");
        assertEquals(new Instruction()
                .setOpcode(OpCode.ALU)
                .setR1(3)
                .setR2(4)
                .setR3(0)
                .setFunc(1),
        actual);
    }

    @Test
    public void parseTwoRegisterImmediateType() throws Exception {
        Instruction instruction = assembler.parse("ISUB $g0 $g1 123");
        assertEquals(new Instruction()
		.setOpcode(OpCode.ISUB)
		.setR1(3)
		.setR2(4)
		.setImmediate(123), instruction);
    }

    @Test
    public void parseTwoRegisterImmediateOffsetType() throws Exception {
        Instruction instruction = assembler.parse("SILOAD $s0 $s1 123");
        assertEquals(new Instruction()
		.setOpcode(OpCode.SILOAD)
		.setR1(25)
		.setR2(26)
		.setImmediate(123), instruction);
    }

    @Test
    public void parseTwoRegisterImmediateLabelType() throws Exception {
        Instruction instruction = assembler.parse("SIBEQ $s0 $s2 TOL");
        assertEquals(new Instruction()
		.setOpcode(OpCode.SIBEQ)
		.setR1(25)
		.setR2(27)
		.setLabel("TOL"), instruction);
    }


    @Test
    public void parseTwoRegisterRegisterType() throws Exception {
        Instruction instruction = assembler.parse("SRSRL $g0 $g1 4");
        assertEquals(new Instruction()
		.setOpcode(OpCode.ALU)
		.setR1(3)
		.setR2(4)
		.setImmediate(4)
                .setFunc(8),
		instruction);
    }

    @Test
    public void parseOneRegisterImmType() throws Exception {
        Instruction instruction = assembler.parse("SILU $g0 4904");
        assertEquals(new Instruction()
		.setOpcode(OpCode.SILU)
		.setR1(3)
		.setImmediate(4904),
		instruction);

    }

    @Test
    public void parseTwoRegisterJumpandLinkType() throws Exception {
        Instruction instruction = assembler.parse("SRJAL $g0 $g1 TOL");
        assertEquals(new Instruction()
		.setOpcode(OpCode.SRJAL)
		.setR1(3)
		.setR2(4)
		.setLabel("TOL")
                .setFunc(10),
		instruction);
    }

    @Test
    public void invalidFormat() throws Exception {
        Instruction instruction = assembler.parse("AND, $r1, $r2");
        assertFalse(instruction.isValid());
    }

    @Test
    public void invalidRegisterFormat() throws Exception {
        Instruction instruction = assembler.parse("AND r1 r2");
        assertFalse(instruction.isValid());
    }

//    @Test
//    public void encodeTwoRegisterType() throws Exception {
//        Instruction instruction = assembler.parse("AND $r1 $r2");
//        int encoded = assembler.encode(instruction);

        // instruction in binary == 0001000100100001
        // opc   r1   r2  func
        // 0001 0001 0010 0001
        // convert to hex
        //   0    1    2    1
//        assertEquals(0x1121, encoded);
//    }

//    @Test
//    public void decodeTwoRegisterType() throws Exception {
//        Instruction expected = assembler.parse("AND $r1 $r2");
//        int encoded = assembler.encode(expected);
//        assertEquals(expected, assembler.decode(encoded));
//    }

    @Test
    public void parse() throws Exception {
        String program = "AND $r1 $r0\n" +
                         "ADDI $r1 123";
        List<Instruction> instructions = assembler.parse(new StringReader(program));
        assertEquals(3, instructions.size());
    }
}
