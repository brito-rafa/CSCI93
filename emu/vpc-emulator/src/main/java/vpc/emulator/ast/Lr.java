package vpc.emulator.ast;

import vpc.emulator.Instruction;

public class Lr extends Instruction {
    @Override
    public void accept(final AssemblyVisitor assemblyVisitor) {
        assemblyVisitor.visit(this);
    }
}
