package vpc.emulator.ast;

import vpc.emulator.Instruction;

public class Ior extends Instruction {
    @Override
    public void accept(final AssemblyVisitor assemblyVisitor) {
        assemblyVisitor.visit(this);
    }
}
