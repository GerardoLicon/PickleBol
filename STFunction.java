package pickle;

import java.util.ArrayList;

public class STFunction extends STEntry{
    public SubClassif returnType;
    public SubClassif definedBy;
    public int numArgs;
    public ArrayList<STEntry> parmList;
    public SymbolTable symbolTable;
    public final static int VAR_ARGS = 0;

    // Implementation for initGlobal()
    public STFunction(String symbol, Classif primClassif, SubClassif type, int numArgs){
        // call super because this is a subclass
        super(symbol, primClassif);
        this.returnType = type;
        this.definedBy = SubClassif.BUILTIN;
        this.numArgs = numArgs;
    }
    // implementation of what defined it
    public STFunction(String symbol, Classif primClassif, SubClassif type, int numArgs,
                      ArrayList<STEntry> parmList, SymbolTable symbolTable){
        super(symbol, primClassif);
        this.returnType = type;
        this.definedBy = SubClassif.USER;
        this.numArgs = numArgs;
        this.parmList = parmList;
        this.symbolTable = symbolTable;
    }
}