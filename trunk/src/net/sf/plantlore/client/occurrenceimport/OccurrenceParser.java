package net.sf.plantlore.client.occurrenceimport;

import java.io.IOException;

import org.xml.sax.SAXException;

public interface OccurrenceParser {
	
	void setRecordProcessor(RecordProcessor processor);
	
	void startParsing() throws IOException, SAXException ;

}
