package net.sf.plantlore.client.tableimport;

import net.sf.plantlore.common.exception.ParserException;

/**
 * The Table Parser is an interface for a parser capable of reading 
 * an input containing records from an immutable table. 
 * Those records can be added, updated, or removed.
 * <br/>
 * The use of a parser
 * <pre>
 * parser.initialize();
 * while( parser.hasNext() ) {
 *   DataHolder h = parser.getNext();
 *   // Process the data stored in the holder object 
 * }
 * </pre>
 * <br/>
 * The Parser acts as a Builder in the Builder design pattern.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-10
 *
 */
public interface TableParser {
	
	/**
	 * The operation that can be performed with a record.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-08-10
	 *
	 */
	enum Action { INSERT, UPDATE, DELETE };
	
	/**
	 * Initialize the parser. The parser should be able to recognize
	 * the immutable table to which the records belong.
	 * 
	 * @return	The table into which the processed records belong.
	 */
	Class initialize() throws ParserException;

	/**
	 * 
	 * @return	True if there are some records left.
	 */
	boolean hasNext();
	
	/**
	 * Obtain the next record(s) from the input.
	 * 
	 * @return	The holder object storing the record(s) and the action that should be performed with it.
	 */
	DataHolder getNext() throws ParserException;

	/**
	 * Optional.
	 * 
	 * @return	The total number of records on the input, or -1 if the number could not be determined. 
	 */
	int getNumberOfRecords();
	
	/**
	 * Made some final cleanup.
	 * 
	 */
	void cleanup();
}
