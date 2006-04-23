package net.sf.plantlore.client.export;

import java.io.IOException;

import net.sf.plantlore.common.record.*;

/**
 * Interface for building the output.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 0.99
 */
public interface Builder {
		
	void makeHeader() throws IOException;
	
	void makeFooter() throws IOException;
	
	void startNewRecord() throws IOException;
	
	void writeRecord(Record[] args) throws IOException;
	
	void finishRecord() throws IOException;
	
}
