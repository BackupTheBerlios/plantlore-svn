package net.sf.plantlore.client.export;

import net.sf.plantlore.common.record.*;

/**
 * Interface for building the output.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 0.99
 */
public interface Builder {
		
	void makeHeader();
	
	void makeFooter();
	
	void startNewRecord();
	
	void finishRecord();
	
	void writePartialRecord(Author arg);
	
	void writePartialRecord(AuthorOccurrence arg);
	
	void writePartialRecord(Habitat arg);
	
	void writePartialRecord(Metadata arg);
	
	void writePartialRecord(Occurrence arg);
	
	void writePartialRecord(Phytochorion arg);
	
	void writePartialRecord(Plant arg);
	
	void writePartialRecord(Publication arg);
	
	void writePartialRecord(Territory arg);

	void writePartialRecord(Village arg);
}
