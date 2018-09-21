package vpc.emulator.ast;

public interface AssemblyVisitor {

    //ALU
    void visit(Radd Radd);
    void visit(Rsub Rsub);
    void visit(Rand Rand);
    void visit(Ror Ror);
    void visit(Rnor Rnor);
    void visit(Rxor Rxor);
    void visit(Rxnor Rxnor);
    void visit(Rslt Rslt);
    void visit(Srsrl Srsrl);
    void visit(Srsll Srsll);
    void visit(Srpc Srpc);

    //Non-ALU
    void visit(Lr Lr);
    void visit(Wsr Wsr);
    void visit(Iadd Iadd);
    void visit(Isub Isub);
    void visit(Iand Iand);
    void visit(Ior Ior);
    void visit(Inor Inor);
    void visit(Ixor Ixor);
    void visit(Islt Islt);
    void visit(Silu Silu);
    void visit(Siload Siload);
    void visit(Sistore Sistore);
    void visit(Lw Lw);
    void visit(Sw Sw);

    // jump
    void visit(Sibeq Sibeq);
    void visit(Sibneq Sibneq);
    void visit(Srjal Srjal);
    void visit(Jr Jr);
    void visit(Jeq Jeq);
    void visit(Jneq Jneq);
}
