package pickle;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {

	public HashMap<String, STEntry> ht;

	// Set the constructor and call the initGlobal();
	public SymbolTable(){
		this.ht = new HashMap<String, STEntry>();
		initGlobal();
	}
	/**
	 *  Returns the symbol table entry for the given symbol.
	 * <p>
	 *
	 * @param symbol is a String value e.g.("if", "endif", etc.).
	 * @return STEntry object for the given symbol.
	 */
	public STEntry getSymbol(String symbol){
		try{
			// check if the hashmap contains the key, if so return symbol.
			if(ht.containsKey(symbol)){
				return ht.get(symbol);
			}
			else{
				throw new Exception();
			}
		}
		catch(Exception ex){
			//ex.printStackTrace();
			return null;
		}
	}
	/**
	 *  Stores the symbol and its corresponding entry in the symbol table.
	 * <p>
	 *
	 * @param symbol is a String value e.g.("if", "endif", etc.).
	 * @return STEntry object for the given symbol.
	 * @throws new error exception
	 */
	public void putSymbol(String symbol, STEntry entry){
		try{
			// Check if the key is already in the table
			// if not then put it in there
			// don't want duplicates
			if(!ht.containsKey(entry.symbol)){
				ht.put(symbol, entry);
			}
			else{
				throw new Exception("Cannot put symbol in table");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 *  Inserts reserved symbol entries into the global symbol table.
	 *
	 */
	private void initGlobal(){
		ht.put("def", new STControl("def", Classif.CONTROL, SubClassif.FLOW));
		ht.put("enddef", new STControl("enddef", Classif.CONTROL, SubClassif.END));
		ht.put("if", new STControl("if",Classif.CONTROL, SubClassif.FLOW));
		ht.put("endif", new STControl("endif",Classif.CONTROL,SubClassif.END));
		ht.put("else", new STControl("else", Classif.CONTROL, SubClassif.END));
		ht.put("for", new STControl("for",Classif.CONTROL,SubClassif.FLOW));
		ht.put("endfor", new STControl("for",Classif.CONTROL,SubClassif.END));
		ht.put("while", new STControl("while",Classif.CONTROL,SubClassif.FLOW));
		ht.put("endwhile", new STControl("endwhile",Classif.CONTROL,SubClassif.END));

		ht.put("print", new STFunction("print",Classif.FUNCTION,SubClassif.VOID, STFunction.VAR_ARGS));


		ht.put("Int", new STControl("Int",Classif.CONTROL,SubClassif.DECLARE));
		ht.put("Float", new STControl("Float",Classif.CONTROL,SubClassif.DECLARE));
		ht.put("String", new STControl("String",Classif.CONTROL,SubClassif.DECLARE));
		ht.put("Bool", new STControl("Bool",Classif.CONTROL,SubClassif.DECLARE));
		ht.put("Date", new STControl("Date",Classif.CONTROL,SubClassif.DECLARE));

		ht.put("LENGTH", new STFunction("LENGTH",Classif.FUNCTION,SubClassif.INTEGER,0));
		ht.put("MAXLENGTH", new STFunction("MAXLENGTH",Classif.FUNCTION,SubClassif.VOID, 0));
		ht.put("SPACES", new STFunction("SPACES",Classif.FUNCTION,SubClassif.VOID, 0));
		ht.put("ELEM", new STFunction("ELEM",Classif.FUNCTION,SubClassif.VOID, 0));
		ht.put("MAXELEM", new STFunction("MAXELEM",Classif.FUNCTION,SubClassif.VOID, 0));

		ht.put("and", new STEntry("and", Classif.OPERATOR));
		ht.put("or", new STEntry("or", Classif.OPERATOR));
		ht.put("not", new STEntry("not", Classif.OPERATOR));
		ht.put("in", new STEntry("in", Classif.OPERATOR));
		ht.put("notin", new STEntry("notin", Classif.OPERATOR));

	}
}