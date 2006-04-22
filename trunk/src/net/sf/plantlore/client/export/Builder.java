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
	
	void writeRecord(Record arg);
	
	void finishRecord();
	
}
