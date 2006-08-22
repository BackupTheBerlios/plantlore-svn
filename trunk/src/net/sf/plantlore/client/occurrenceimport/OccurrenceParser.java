package net.sf.plantlore.client.occurrenceimport;

public interface OccurrenceParser {
	
	void setRecordProcessor(RecordProcessor processor);
	
	void startParsing() throws Exception ;

}
