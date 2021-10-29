package pickle;

public class STControl extends STEntry{
    public String symbol;
    public Classif primClassif;
    public SubClassif subClassif;
    public STControl(String symbol, Classif primClassif, SubClassif subClassif){
        // call super because this is a subclass
        super(symbol, primClassif);
        this.subClassif = subClassif;
    }
}