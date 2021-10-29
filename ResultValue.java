package pickle;

public class ResultValue{
    public ResultValue(){
    }
    public SubClassif type;
    public String value;
    public int structure;
    public String terminatingStr;

    public ResultValue(SubClassif type, String value, int structure, String terminatingStr){
        this.type = type;
        this.value = value;
        this.structure = structure;
        this.terminatingStr = terminatingStr;
    }
}