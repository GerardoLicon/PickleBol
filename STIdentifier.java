package pickle;

public class STIdentifier extends STEntry{
    public SubClassif dclType;
    public SubClassif structure;
    public SubClassif parm;
    public int nonLocal;

    //Set constructor.
    public STIdentifier(String symbol, Classif primClassif, SubClassif type,
                        SubClassif structure, SubClassif parm, int nonlocal){
        // call super because this is a subclass
        super(symbol, primClassif);
        this.dclType = type;
        this.structure = structure;
        this.parm = parm;
        this.nonLocal = nonlocal;
    }
}