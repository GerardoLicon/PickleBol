package pickle;

public class STEntry {
    public String symbol;
    public Classif primClassif;

    // Set default constructor.
    public STEntry(){}

    // Set constructor.
    public STEntry(String symbol, Classif primClassif){
        this.symbol = symbol;
        this.primClassif = primClassif;
    }
}