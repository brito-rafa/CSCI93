package vpc.emulator.ast;

import vpc.emulator.Instruction;

public class Rnor extends Instruction {
    @Override
    public void accept(final AssemblyVisitor assemblyVisitor) {
        assemblyVisitor.visit(this);
    }
}
