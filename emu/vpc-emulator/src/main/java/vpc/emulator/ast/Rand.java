package vpc.emulator.ast;

import vpc.emulator.Instruction;

public class Rand extends Instruction {
    @Override
    public void accept(final AssemblyVisitor assemblyVisitor) {
        assemblyVisitor.visit(this);
    }
}
