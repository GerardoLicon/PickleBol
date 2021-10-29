package pickle;

public class Parser{
    public Parser(){}

    public Scanner scan;
    public void errorWithCurrent(String message) throws Exception {
        throw new Exception(message);
    }
    public void error(String formatMessage, String tokenStr) throws Exception {
        String formatStr = String.format(formatMessage, tokenStr);
        throw new Exception(formatStr);
    }
    public void stmt(Boolean bExec) throws Exception {
        String tokenStr = scan.currentToken.tokenStr;
        Classif tokenType = scan.currentToken.primClassif;
        switch(tokenType){
            case EMPTY:
                error("Empty Token", tokenStr);
            case EOF:
                error("Reached EOF", tokenStr);
            case OPERAND:
                assignmentStmt(bExec);
            case OPERATOR:
            case SEPARATOR:
                //should also pass the line number
                error("Separator is invalid for start", tokenStr);
            case FUNCTION:
                System.out.println("Function is not supported yet!");
            case CONTROL:
                System.out.println("Control is not supported yet!");
            default:
                error("Unknown error", tokenStr);

        }
    }
    public ResultValue assignmentStmt(Boolean bExec) throws Exception {
        ResultValue res = new ResultValue();
        scan = new Scanner();
        if(!bExec){
            skipTo(";");
        }
        else{
            if(scan.currentToken.subClassif != SubClassif.IDENTIFIER){
                errorWithCurrent("Expected a variable for the target of an assignment");
            }
            String variableStr = scan.currentToken.tokenStr;

            //get the assignment operator and check it
            scan.getNext();
            if(scan.currentToken.primClassif != Classif.OPERATOR){
                errorWithCurrent("Expected assignment operator.");
            }
            String operatorStr = scan.currentToken.tokenStr;
            ResultValue resO2;
            ResultValue resO1;
            Numeric nOp2; //numeric value of second operand
            Numeric nOp1; //numeric value of first operand
            switch(operatorStr) {
                case "=":
                    //call expression class - just put a holder in there for now
                    resO2 = expr();
                    res = assign(variableStr, resO2);
                case "-=":
                    //call expression class - just put a holder in there for now
                    resO2 = expr();
                    //needs to be redefined just a holder for now
                    nOp2 = new Numeric(this, resO2, "-=", "2nd operand");
                    // Not sure if this method will be in the numeric class?
                    resO1 = getVariableValue(variableStr);
                    nOp1 = new Numeric(this, resO1, "-=", "1st operand");
                    res = assign(variableStr, Utility.subtract(this, nOp1, nOp2));
                case "+=":
                    resO2 = expr();
                    nOp2 = new Numeric(this, resO2, "+=", "2nd operand");
                    resO1 = getVariableValue(variableStr);
                    nOp1 = new Numeric(this, resO1, "+=", "1st operand");
                    res = assign(variableStr, Utility.add(this, nOp1, nOp2));
                default:
                    errorWithCurrent("expected assignment operator");
            }
        }
        return res;
    }
    public void skipTo(String skip) throws Exception {
        while(scan.currentToken.tokenStr.equals(skip)){
            scan.getNext();
        }
    }
    public ResultValue assign(String variableStr, ResultValue res1){
        ResultValue res = new ResultValue();
        return res;
    }
    public ResultValue expr(){
        ResultValue res = new ResultValue();
        return res;
    }
    public ResultValue getVariableValue(String variableStr){
        ResultValue res = new ResultValue();
        return res;
    }
}