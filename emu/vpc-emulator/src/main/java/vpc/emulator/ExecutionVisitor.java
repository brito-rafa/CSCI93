package vpc.emulator;

import vpc.emulator.ast.*;

import javax.annotation.Generated;

public class ExecutionVisitor implements AssemblyVisitor {

    private int pc = 0;
    private int ra = 31;
    private MemorySubsystem memorySubsystem;
    private int[] registers = new int[32];

    public ExecutionVisitor(MemorySubsystem memorySubsystem, int[] registers) {
        this.memorySubsystem = memorySubsystem;
        this.registers = registers;
        // registers[0] is always zero - $z0
        this.registers[0] = 0;
    }

    @Override
    public void visit(final Radd Radd) {
        int op1 = registers[Radd.getR1()];
        int op2 = registers[Radd.getR2()];
        int op3 = registers[Radd.getR3()];
        int result = op2 + op3;
        registers[Radd.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Rsub Rsub) {
        int op1 = registers[Rsub.getR1()];
        int op2 = registers[Rsub.getR2()];
        int op3 = registers[Rsub.getR3()];
        int result = op2 - op3;
        registers[Rsub.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Rand Rand) {
        int op1 = registers[Rand.getR1()];
        int op2 = registers[Rand.getR2()];
        int op3 = registers[Rand.getR3()];
        int result = op2 & op3;
        registers[Rand.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Ror Ror) {
        int op1 = registers[Ror.getR1()];
        int op2 = registers[Ror.getR2()];
        int op3 = registers[Ror.getR3()];
        int result = op2 | op3;
        registers[Ror.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Rnor Rnor) {
        int op1 = registers[Rnor.getR1()];
        int op2 = registers[Rnor.getR2()];
        int op3 = registers[Rnor.getR3()];
        int result = ~(op2 | op3);
        registers[Rnor.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Rxor Rxor) {
        int op1 = registers[Rxor.getR1()];
        int op2 = registers[Rxor.getR2()];
        int op3 = registers[Rxor.getR3()];
        int result = op2 ^ op3;
        registers[Rxor.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Rxnor Rxnor) {
        int op1 = registers[Rxnor.getR1()];
        int op2 = registers[Rxnor.getR2()];
        int op3 = registers[Rxnor.getR3()];
        int result = ~(op2 ^ op3);
        registers[Rxnor.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Rslt Rslt) {
        int op1 = registers[Rslt.getR1()];
        int op2 = registers[Rslt.getR2()];
        int op3 = registers[Rslt.getR3()];
        int result = op2 - op3;
	if (result < 0) {
		registers[Rslt.getR1()] = 1;
	} else {
		registers[Rslt.getR1()] = 0;
	}
        incrementPc();
    }

    @Override
    public void visit(final Srsrl Srsrl) {
        int op1 = registers[Srsrl.getR1()];
        int op2 = registers[Srsrl.getR2()];
        int immediate = Srsrl.getImmediate();
	int result = (op2 >> immediate);
	registers[Srsrl.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Srsll Srsll) {
        int op1 = registers[Srsll.getR1()];
        int op2 = registers[Srsll.getR2()];
        int immediate = Srsll.getImmediate();
	int result = (op2 << immediate);
	registers[Srsll.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Srpc Srpc) {
        int op1 = registers[Srpc.getR1()];
	int result = this.getPc();
	registers[Srpc.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Lr Lr) {
        int op1 = registers[Lr.getR1()];
        int op2 = registers[Lr.getR2()];
	int result = this.memorySubsystem.readInt(op2);
	registers[Lr.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Wsr Wsr) {
        int op1 = registers[Wsr.getR1()];
        int op2 = registers[Wsr.getR2()];
	this.memorySubsystem.writeInt(op2,op1);
        incrementPc();
    }

    @Override
    public void visit(final Iadd Iadd) {
        int op1 = registers[Iadd.getR1()];
        int op2 = registers[Iadd.getR2()];
        int immediate = Iadd.getImmediate();
        int result = op2 + immediate;
        registers[Iadd.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Isub Isub) {
        int op1 = registers[Isub.getR1()];
        int op2 = registers[Isub.getR2()];
        int immediate = Isub.getImmediate();
        int result = op2 - immediate;
        registers[Isub.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Ior Ior) {
        int op1 = registers[Ior.getR1()];
        int op2 = registers[Ior.getR2()];
        int immediate = Ior.getImmediate();
        int result = (op2 | immediate);
        registers[Ior.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Ixor Ixor) {
        int op1 = registers[Ixor.getR1()];
        int op2 = registers[Ixor.getR2()];
        int immediate = Ixor.getImmediate();
        int result = (op2 ^ immediate);
        registers[Ixor.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Inor Inor) {
        int op1 = registers[Inor.getR1()];
        int op2 = registers[Inor.getR2()];
        int immediate = Inor.getImmediate();
        int result = ~(op2 | immediate);
        registers[Inor.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Iand Iand) {
        int op1 = registers[Iand.getR1()];
        int op2 = registers[Iand.getR2()];
        int immediate = Iand.getImmediate();
        int result = (op2 & immediate);
        registers[Iand.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Islt Islt) {
        int op1 = registers[Islt.getR1()];
        int op2 = registers[Islt.getR2()];
        int immediate = Islt.getImmediate();
        int result = op2 - immediate;
	if (result < 0) {
		registers[Islt.getR1()] = 1;
	} else {
		registers[Islt.getR1()] = 0;
	}
        incrementPc();
    }

    @Override
    public void visit(final Silu Silu) {
        int op1 = registers[Silu.getR1()];
        int immediate = Silu.getImmediate();
        int result = (immediate << 16);
        registers[Silu.getR1()] = result;
        incrementPc();
    }

    @Override
    public void visit(final Siload Siload) {
        int op1 = registers[Siload.getR1()];
        int op2 = registers[Siload.getR2()];
        int immediate = Siload.getImmediate();
        int result = this.memorySubsystem.readInt(op2+immediate);
        registers[Siload.getR1()] = result;
        incrementPc();
    }


    @Override
    public void visit(final Sistore Sistore) {
        int op1 = registers[Sistore.getR1()];
        int op2 = registers[Sistore.getR2()];
        int immediate = Sistore.getImmediate();
        this.memorySubsystem.writeInt(op2+immediate,op1);
        incrementPc();
    }

    @Override
    public void visit(final Lw Lw) {
        int op1 = registers[Lw.getR1()];
        int op2 = registers[Lw.getR2()];
        int immediate = Lw.getImmediate();
        int result = this.memorySubsystem.readInt(immediate);
        registers[Lw.getR1()] = result;
        registers[Lw.getR2()] = immediate;
        incrementPc();
    }

    @Override
    public void visit(final Sw Sw) {
        int op1 = registers[Sw.getR1()];
        int op2 = registers[Sw.getR2()];
        int immediate = Sw.getImmediate();
        this.memorySubsystem.writeInt(immediate,op1);
        registers[Sw.getR2()] = immediate;
        incrementPc();
    }

    @Override
    public void visit(final Sibeq Sibeq) {
        int op1 = registers[Sibeq.getR1()];
        int op2 = registers[Sibeq.getR2()];
        int immediate = Sibeq.getImmediate();
        incrementPc();
        if (op1 == op2) {
		registers[ra] = this.pc;
		this.pc = immediate;
	}
    }

    @Override
    public void visit(final Sibneq Sibneq) {
        int op1 = registers[Sibneq.getR1()];
        int op2 = registers[Sibneq.getR2()];
        int immediate = Sibneq.getImmediate();
        incrementPc();
        if (op1 != op2) {
		registers[ra] = this.pc;
		this.pc = immediate;
	}
    }

    @Override
    public void visit(final Srjal Srjal) {
        int op1 = registers[Srjal.getR1()];
        int op2 = registers[Srjal.getR2()];
        int immediate = Srjal.getImmediate();
        incrementPc();
	registers[ra] = this.pc;
        registers[Srjal.getR1()] = this.pc;
        registers[Srjal.getR2()] = this.pc;
	this.pc = immediate;
    }

    @Override
    public void visit(final Jr Jr) {
        int op1 = registers[Jr.getR1()];
	this.pc = op1;
    }

    @Override
    public void visit(final Jeq Jeq) {
        int op1 = registers[Jeq.getR1()];
        int op2 = registers[Jeq.getR2()];
        int op3 = registers[Jeq.getR3()];
        incrementPc();
        if (op1 == op2) {
		this.pc = op3;
	}
    }

    @Override
    public void visit(final Jneq Jneq) {
        int op1 = registers[Jneq.getR1()];
        int op2 = registers[Jneq.getR2()];
        int op3 = registers[Jneq.getR3()];
        incrementPc();
        if (op1 != op2) {
		this.pc = op3;
	}
    }
    

    private void incrementPc() {
        pc += 4;
    }

    @Generated("by IDE")
    public int getPc() {
        return pc;
    }

    @Generated("by IDE")
    public ExecutionVisitor setPc(final int pc) {
        this.pc = pc;
        return this;
    }

    public int[] getRegisters() {
        return registers.clone();
    }

    public ExecutionVisitor zeroRegisters() {
        for(int i = 31; i > 0 ; i--) {
		this.registers[i] = 0;
	}
	return this;
    }
}

