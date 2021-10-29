
package pickle;

/**
 *
 */
public class ScannerException extends Exception {
    public ScannerException(String message){
        super(message);
    }
    
    /**
	 * Creates an error message including line number and file.
	 * <p>
	 * 
	 * @param s string for extra information based on error
     * @param linesIndex line number
     * @param currentString token str
     * @param fileName filename
	 * @return the error message
	 */
	public static String errorMessage(String s, int linesIndex, String currentString, String fileName)
	{
		int lineNumber = linesIndex + 1;
		return "Line " + lineNumber + " " + s + ": '" + currentString + "', File: " + fileName;
	}
    
}
