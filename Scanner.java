package pickle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Scanner 
{

	public Token nextToken, currentToken;
	public SymbolTable symbolTable;
	private static File f;  // input source file
	private static ArrayList<String> lines, blanks; // all lines of input file, and list of blank lines
	private static char[] currentLine;
	private static int linesIndex, lineIndex, colNmbr; // linesIndex is index into lines, i.e. line number, and lineIndex is index into the current line
												// colNmbr is used to save column position in token
	private static String currentString; // current string of token
	private static boolean delimited, eof, eol, end, comment, printLine, blankPrint; // flags for when a delimiter or end of file or end of line or 
											// comment is encountered or line needs to be printed
	private static String linePrint;
	private String fileName;
	private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n"; // terminate a token
	private final static String operators = "+-*/<>!=#^"; // list of operators
	private final static String separators = "():;[],"; // list of separators

	public Scanner(String sourceFileNm, SymbolTable symbolTable) throws Exception
	{
		this.symbolTable = symbolTable;
		fileName = sourceFileNm;
		f = new File(sourceFileNm);
		nextToken = new Token();
		readSource();
		currentLine = lines.get(0).toCharArray();
		linesIndex = 0;
		lineIndex = 0;
		delimited = false;
		eof = false;
		end = false;
		printLine = false;
		blankPrint = false;
		comment = false;
		blanks = new ArrayList<String>();
		linePrint = "";
		currentString = getToken(); // have to make first call to getToken to set currentString
		
		// make sure currentString is not empty
		while(currentString.equals(""))
			currentString = getToken();
		getNext();
	}

	/**
	 * Reads each line of the input file into
	 * an ArrayList of Strings.
	 * <p>
	 * Uses java.util.Scanner to read the input file.
	 */
	private static void readSource()
	{
		try 
		{
			java.util.Scanner s = new java.util.Scanner(f);
			lines = new ArrayList<String>();
			// while input file has lines; add lines to ArrayList
			while(s.hasNextLine())
			{
				lines.add(s.nextLine());
			}
			s.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * If a " or a ' are encountered in the getToken method, then this method 
	 * is invoked because it means a String has been encountered. The purpose of
	 * this method is to collect that String and return it.
	 * <p>
	 * The returned value is the string. However, in the language Pickle, strings
	 * are assumed to start and end on the same line. So, if the string is not closed
	 * by the end of the line this method will return null (indicating to the invoker
	 * that it should throw an exception).
	 * 
	 * @param c this will tell if the string is opened with " or '
	 * @return the string; if it is properly closed, or null if it is not
	 */
	private String getString(char c)
	{
		String s = "";
		lineIndex++;
		s += c;		// getSubclassif will handle removing " or '
		
		// makes sure lineIndex does not go out of bounds. If it does, then after
		// the while loop we know the string was not closed and null will be returned.
		while(lineIndex < currentLine.length)
		{
			// checks to see if string has been closed
			if(currentLine[lineIndex] == c && currentLine[lineIndex - 1] != '\\')
			{
				s += currentLine[lineIndex];
				lineIndex++;
				return s;
			}
			s += currentLine[lineIndex];
			lineIndex++;
		}
		// if this point is reached then string was not closed, return null
		// and save string for error message
		currentString = s;
		return null;
	}
	
	/**
	 * This is responsible for replacing character escapes with their
	 * appropriate values.
	 * <p>
	 * This method assumes the string passed in is a valid Pickle String, i.e. it must be closed
	 * (to avoid out of bounds)
	 * 
	 * @param s the string to be "corrected"
	 * @return the "corrected" string
	 * @throws Exception if there is an escape character that is not defined in the language Pickle
	 */
	private String correctString(String s) throws ScannerException
	{
		char[] c = s.toCharArray(); // convert string to char array
		String res = "";
		int i = 0;
		
		// iterate through the char array
		while(i < c.length)
		{
			
			// a backslash is found, look at subsequent character to determine escape value
			if(c[i] == '\\')
			{
				
				// look at next character
				switch(c[i + 1])
				{
					case '"':
						res += '"';
						break;
					case '\'':
						res += "'";
						break;
					case '\\':
						res += '\\';
						break;
					case 'n':
						res += (char) Integer.parseInt("0A", 16);
						break;
					case 't':
						res += (char) Integer.parseInt("09", 16);
						break;
					case 'a':
						res += (char) Integer.parseInt("07", 16);
						break;
					default:
						throw new ScannerException(ScannerException.errorMessage("Invalid escape character"
													, linesIndex
													, "" + c[i + 1]
													, fileName));
				}
				i += 2;
			}
			
			// not a backslash, treat it normally
			else
			{
				res += c[i];
				i++;
			}
		}
		return res;
	}
	
	/**
	 * This method is responsible for getting the string for the token. 
	 * <p>
	 * This method will iterate through the input source code looking for delimiters
	 * (meanwhile, saving each character into a string),
	 * and when one is found it will return the string of the token.
	 * It will also save each line of the input source code for printing.
	 * @return string of the token
	 * @throws Exception if a string literal is not closed
	 */
	private String getToken() throws ScannerException
	{
		char c;
		int lineNumber;
		String s = "";
		eol = false;
		colNmbr = lineIndex;
		
		// go to the next line
		if(lineIndex >= currentLine.length)
		{
			lineIndex = 0;
			linesIndex++;
			
			// reached end of file
			if(linesIndex >= lines.size())
			{
				eof = true;
				return currentString;
			}
			currentLine = lines.get(linesIndex).toCharArray();
		}
		
		// start of line; print it on next call to getNext
		if(lineIndex == 0)
		{
			lineNumber = linesIndex + 1;
			eol = true;
			printLine = true;
			linePrint = "  " + lineNumber + " " + lines.get(linesIndex);
		}
		
		// iterate through current line until line is over, or a delimiter is encountered
		while(lineIndex < currentLine.length)
		{
			c = currentLine[lineIndex];
			
			// from previous invocation of getToken; meaning a delimiter was encountered
			if(delimited)
			{
				
				// " or ' indicates a string; invoke getString
				if(c == '"' || c == '\'')
				{
					s = getString(c);
					
					// getString returning null means that the string was not closed, so 
					// an exception needs to be thrown.
					if(s == null)
						throw new ScannerException(ScannerException.errorMessage("Unclosed String Literal"
													, linesIndex
													, currentString
													, fileName));
					return correctString(s);
				}
				lineIndex++;
				delimited = false;
				// if a '/' is encountered it could potentially be a comment, so call skipComment (note: if it is not a comment
				// skipComment will simply call doubleOperator
				return c == '/' ? skipComment(c) : doubleOperator(c);
			}
			
			// whitespace encountered; just return s
			if(Character.isWhitespace(c))
			{
				lineIndex++;
				return s;
			}
			
			// delimiter encountered; set delimited to true for next invocation of
			// getToken and return s
			if(delimiters.indexOf(c) > 0)
			{
				delimited = true;
				return s;
			}
			s += c;
			lineIndex++;
		}
		return s;
	}
	
	/**
	 * Responsible for skipping comments, is called from getToken when a '/' is encountered.
	 * <p>
	 * if it is not a comment, this method will invoke doubleOperator.
	 * 
	 * @param c used to pass to potentially pass to doubleOperator
	 * @return empty string to indicate that this is a comment and should not be a token, or doubleOperator if it is not a comment
	 */
	private String skipComment(char c){
		
		// not a comment
		if(lineIndex >= currentLine.length || currentLine[lineIndex] != '/')
			return doubleOperator(c);
		
		// whole line comment, used for printing information
		if(lineIndex <= 1)
			comment = true;
		lineIndex = currentLine.length;
		return "";
	}
	
	/**
	 * decides if the token is a double operator, i.e, a two character operator
	 * such as +=, !=, etc.
	 * @param c the original character that may be part of the double operator
	 * @return the double operator if it is two characters, otherwise just c (the original)
	 */
	private String doubleOperator(char c){
		
		// check if it is not a double operator
		if(lineIndex >= currentLine.length || currentLine[lineIndex] != '=' || !operators.contains("" + c))
			return "" + c;
		lineIndex++;
		return "" + c + currentLine[lineIndex - 1];
	}
		
	/**
	 * Sets the current token's primary classification.
	 * <p>
	 */
	private void getClassif()
	{
		
		// currentString is empty
		if(currentString.isEmpty())
		{
			nextToken.primClassif = Classif.EMPTY;
		}
		
		// currentString is an operator; primClassif is set to operator
		else if(operators.contains(currentString))
		{
			nextToken.primClassif = Classif.OPERATOR;
		}
		
		// currentString is an separator; primClassif is set to separator
		else if(separators.contains(currentString))
		{
			nextToken.primClassif = Classif.SEPARATOR;
		}
		
		// otherwise, it must be an operand
		else
		{
			nextToken.primClassif = Classif.OPERAND;
		}
	}
	
	/**
	 * Sets the current token's subclassifcation.
	 * <p>
	 * 
	 * If it does not have a subclassifcation then it is set to empty.
	 * @throws Exception if there is an invalid numeric constant
	 */
	private void getSubclassif() throws ScannerException
	{
		char c;
		int n;
		
		// if the token is an operand, then enter the subclassification phase
		if(nextToken.primClassif == Classif.OPERAND)
		{
			c = currentString.charAt(0);
			n = currentString.length() - 1;
			
			// token is a string, so the surrounding " or ' needs to be removed
			if(c == '"' || c == '\'')
			{
				currentString = currentString.substring(1, n);
				nextToken.subClassif = SubClassif.STRING;
			}
			
			// first character of token is a digit, so check if it is a valid float or integer
			else if(Character.isDigit(c))
			{
				
				// check if it is a float
				if(Numeric.isFloat(currentString))
				{
					nextToken.subClassif = SubClassif.FLOAT;
				}
				
				// check if it is an integer
				else if(Numeric.isInteger(currentString))
				{
					nextToken.subClassif = SubClassif.INTEGER;
				}
				
				// if it is neither then it is an invalid numeric constant
				else
				{
					throw new ScannerException(ScannerException.errorMessage("Invalid Numeric Constant"
												, linesIndex
												, currentString
												, fileName));
				}
			}
			
			// a boolean
			else if(currentString.equals("T") || currentString.equals("F"))
				nextToken.subClassif = SubClassif.BOOLEAN;
			
			// at this point it must be an identifier
			else
				nextToken.subClassif = SubClassif.IDENTIFIER;
		}
		
		// token is not an operand; does not have a subclassification
		else
			nextToken.subClassif = SubClassif.EMPTY;
	}
	
	/**
	 * returns the values of the token passed
	 * <p>
	 * 
	 * @param u the token with the values being used to set the new one
	 * @return the new token
	 */
	private Token setToken(Token u){
		Token t = new Token();
		t.primClassif = u.primClassif;
		t.subClassif = u.subClassif;
		t.tokenStr = u.tokenStr;
		t.iColPos = u.iColPos;
		t.iSourceLineNr = u.iSourceLineNr;		
		return t;
	}
	
	/**
	 * This method is responsible for returning the string for the next token,
	 * as well as setting the current token.
	 * <p>
	 * This will set the current token's string to be currentString 
	 * (which is the string representing the current token), and will set the classification
	 * and the subclassifcation of the current token. It will also get the string for the next token
	 * and will end up setting the currentString to that at the end of the method.
	 * 
	 * @return string of the next token
	 * @throws Exception any exceptions thrown by getToken will land here, and subsequently be thrown to the caller
	 */
	public String getNext() throws Exception 
	{
		String next;
		STEntry se;
		currentToken = setToken(nextToken);
		
		// print blank lines
		if(blankPrint)
		{
			blankPrint = false;
			for(String s : blanks)
				System.out.println(s);
			blanks.clear();
		}
		
		// print line
		if(printLine)
		{
			printLine = false;
			System.out.println(linePrint);
		}
		
		// set the currentToken for last time; return "" to let caller know there are no more tokens to be scanned
		if(end)
		{
			currentToken = setToken(nextToken);
			return "";
		}
		
		// end of file has been reached
		if(eof)
		{
			nextToken.primClassif = Classif.EOF;
			nextToken.subClassif = SubClassif.EMPTY;
			nextToken.tokenStr = currentString;
			nextToken.iColPos = colNmbr;
			end = true;
			return currentString;
		}
		
		next = getToken().trim();
		
		// determining current string's classif and subclassif
		se = symbolTable.getSymbol(currentString);
		
		// currentString was found in symbol table, use that information to set the token
		if(se != null)
		{
			nextToken.primClassif = se.primClassif;
			
			// operand, cast STEntry to STIdentifier
			if(se.primClassif == Classif.OPERAND)
				nextToken.subClassif = ((STIdentifier) se).dclType;
			
			// control, cast STEntry to STControl
			else if(se.primClassif == Classif.CONTROL)
				nextToken.subClassif = ((STControl) se).subClassif;
			
			// function, cast STEntry to STFunction
			else if(se.primClassif == Classif.FUNCTION)
				nextToken.subClassif = ((STFunction) se).definedBy;
			
			// operator
			else
				nextToken.subClassif = SubClassif.EMPTY;
		}
		
		// currentString was not found in symbol table
		else
		{
			getClassif();
			getSubclassif();
			
			// if it is an identifier, put it in the symbol table
			if(nextToken.subClassif == SubClassif.IDENTIFIER)
				symbolTable.putSymbol(currentString, new STIdentifier(currentString, nextToken.primClassif, nextToken.subClassif, null, null, 0));
		}
		nextToken.tokenStr = currentString;
		nextToken.iColPos = colNmbr;
		nextToken.iSourceLineNr = eol || eof ? linesIndex : linesIndex + 1;
		
		// skip blank lines and whitespace
		while(next.equals("") && !eof)
		{
			
			// if there is a line to be printed
			if(printLine)
			{
				printLine = false;
				
				// not a comment line
				if(!comment)
				{
					blankPrint = true;
					blanks.add(linePrint);
				}
				
				// a comment line, do not add
				else
					comment = false;
			}
			next = getToken().trim();
		}
		
		currentString = next;
		return next;
	}
	
}
