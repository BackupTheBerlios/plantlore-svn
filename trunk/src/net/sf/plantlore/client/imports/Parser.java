package net.sf.plantlore.client.imports;

import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;


/**
 * The parser is responsible for reading the input (from a file for instance)
 * and re-creating the records stored in the file.
 * 
 * <br/>
 * The intented usage (Import of Occurrence data).
 * <pre>
 * Parser parser = new ParserImpl();
 * parser.initialize();
 * 
 * while( parser.hasNextRecord() ) {
 *   Action intention = parser.fetchNextRecord();
 *   Occurrence occ = parser.nextPart(Occurrence.class);
 *   dealWithOccurrenceRecord( occ, intention );
 *   
 *   while( parser.hasNextPart(AuthorOccurrence.class) ) {
 *     AuthorOccurrence ao = parser.nextPart(AuthorOccurrence.class);
 *     intention = parser.intendedFor();
 *     dealWithAssociatedAuthorOccurrence( ao, intention );
 *   } 
 * }
 * parser.cleanup();
 * </pre>
 * Another example (Import of Plants).
 * <pre>
 * Parser parser = new ParserImpl();
 * parser.initialize();
 * 
 * while( parser.hasNextRecord() ) {
 *   Action intention = parser.fetchNextRecord();
 *   Plant plant = (Plant)parsers.nextPart(Plant.class);
 *   dealWithPlant( plant, intention );
 * }
 * parser.cleanup();
 * </pre>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-08
 * @version 1.0
 */
public interface Parser {
	
	/**
	 * Some record may have been intended for a certain action.
	 * These can be:
	 * <ul>
	 * <li><b>UNKNOWN</b> - usually means the default action,</li>
	 * <li><b>INSERT</b> - the record should be inserted, or updated if it is already in the database,</li>
	 * <li><b>UPDATE</b> - the record should be updated, or inserted if it is not in the database,</li>
	 * <li><b>DELETE</b> - the record should be deleted.</li>
	 * </ul>
	 * The Default Director may use this information to perform a certain action
	 * more cleanly.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-05-08
	 */
	public enum Action { UNKNOWN, INSERT, DELETE, UPDATE };

	/**
	 * Initialize the parser - 
	 * verify headers, prepare for the fetching of records. 
	 * 
	 * @throws ParserException	If the format is not valid or suspicious at least.
	 * The exception should contain a brief explanation.
	 */
	void initialize() throws ParserException;
	
	/**
	 * Perform the final cleanup. 
	 */
	void cleanup();
	
	/**
	 * The file may contain several records.
	 * 
	 * @return	True if there are other records.
	 */
	boolean hasNextRecord();
	
	/**
	 * Instruct the parser, that another "whole" record should be fetched.
	 * The whole record may comprise several parts - these should be
	 * obtained via <code>nextPart()</code>.
	 * 
	 * @return	The action the whole record is supposed to undergo.
	 * @see #getNextPart(Class)
	 */
	Action fetchNextRecord() throws ParserException;
	
	/**
	 * Get a part of the whole record. 
	 * The part must contain all foreign keys as well.
	 * <br/>
	 * For example: nextPart(Occurrence.class) must return
	 * the Occurrence and its Plant, Publication, Metadata,
	 * Habitat and also Habitat's Territory, Village, and Phytochorion. 
	 * 
	 * @param table	The table from which the record comes.
	 * @return	The next part of the whole record, or null if there is none.
	 * @throw ParserException if the record is not valid.
	 */
	Record getNextPart(Class table) throws ParserException;
	
	/**
	 * Are there any other subrecords of this type (from this table)?
	 * <br/>
	 * For example: <code>hasNextPart(AuthorOccurrence.class)</code>
	 * will return true if the whole record (the occurrence in this case)
	 * contains other AuthorOccurrences that are associated with it.
	 * @param table	The table from which the record comes.
	 * @return	True if there are more subrecords belonging to the specified table.
	 */
	boolean hasNextPart(Class table);
	
	/**
	 * What is the this part of the record intended for.
	 * 
	 * @return	The operation the last subrecord should undergo. 
	 */
	Action intentedFor();
}
