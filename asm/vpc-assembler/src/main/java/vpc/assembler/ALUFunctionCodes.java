package vpc.assembler;

// this is a functional code without AssemblyVisitor reference

/**
 * Constants to use for FunctionCodes for the ALU
 *
 * @author rab405 largely using markford e93 sample code
 */
public interface ALUFunctionCodes {
    int RADD   = 1;
    int RSUB   = 2;
    int RAND   = 3;
    int ROR    = 4;
    int RNOR   = 5;
    int RXOR   = 6;
    int RSLT   = 7;
    int SRSRL  = 8;
    int SRSLL  = 9;
    int RXNOR  = 10;
    int SRPC   = 11;
}
