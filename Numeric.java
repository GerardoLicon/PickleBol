package pickle;

public class Numeric {
	public Numeric(Parser parser, ResultValue res, String op1, String op2){

	}
    /**
     * Determines if the currentString is a valid float.
     * <p>
     * Looks for a single ., and the rest have to be digits.
     * Also, since floats in Pickle have to start with a digit,
     * the invoking method (getSubclassif) handles that.
     * 
     * @return true if currentString is a float
     */    
    static public boolean isFloat(String currentString){
        int count = 0;
		int i;
		
		// iterates through currentString
		for(i = 0; i < currentString.length(); i++)
		{
			
			// encountered a .; increment count
			if(currentString.charAt(i) == '.')
				count++;
			
			// otherwise if character is not a digit, it is not a valid float
			else if(!Character.isDigit(currentString.charAt(i)))
					return false;
		}
		return count == 1;
    }

    /**
     * Determines if the currentString is a valid integer.
     * <p>
     * Just returns false if any character other than a digit
     * is encountered.
     * 
     * @return true if currentString is an integer
     */    
    static public boolean isInteger(String currentString){
        int i;
		
		// iterates through currentString
		for(i = 0; i < currentString.length(); i++)
			
			// something other than a digit was encountered, so return false
			if(!Character.isDigit(currentString.charAt(i)))
				return false;
		return true;
    }
    
}
