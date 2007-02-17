package net.sf.plantlore.client.occurrenceimport;

/**
 * The Occurrence parser performs the parsing of the input
 * and every time an Occurrence record is read
 * (with all its Author-occurrences), it is passed to the Record processor.
 * <br/> 
 * The format of the input is implementation specific.
 * 
 * @author kaimu
 * @since 2006-08-14
 *
 */
public interface OccurrenceParser {
	
	/**
	 * Set the Record processor. 
	 * 
	 * @param processor	The record processor that is used to process the whole Occurrence record.
	 */
	void setRecordProcessor(RecordProcessor processor);
	
	/**
	 *	Start parsing the input. 
	 */
	void startParsing() throws Exception ;

}
