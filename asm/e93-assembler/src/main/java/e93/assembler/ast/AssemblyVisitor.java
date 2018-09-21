package e93.assembler.ast;

public interface AssemblyVisitor {
    void visit(And and);
    void visit(AddImmediate andi);
    void visit(JumpImmediate jumpImmediate);
    void visit(LoadWord loadWord);
    void visit(OrImmediate orImmediate);
    void visit(StoreWord storeWord);
}
