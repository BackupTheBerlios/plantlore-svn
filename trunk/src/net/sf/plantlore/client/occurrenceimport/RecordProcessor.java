package net.sf.plantlore.client.occurrenceimport;

import net.sf.plantlore.common.record.*;

/**
 * The record processor is an interface that allows the Occurrence parser
 * to pass the reconstructed record for further processing.
 * It is not the Parser that should decide, what will happen with the record.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-14
  */
public interface RecordProcessor {
	
	/**
	 * Process the whole occurrence record.
	 * 
	 * @param authorOccurrences	The list of all author-occurrences
	 * refering to the same occurrence.
	 */
	void processRecord(AuthorOccurrence...authorOccurrences) throws Exception;

}
