package net.sf.plantlore.client.occurrenceimport;

import net.sf.plantlore.common.record.*;

public interface RecordProcessor {
	
	void processRecord(AuthorOccurrence...authorOccurrences) throws Exception;

}
