package net.sf.plantlore.client.export;

import java.io.IOException;

import net.sf.plantlore.common.record.*;

/**
 * Interface for building the output. 
 * The builder is responsible for "building" the output when given 
 * a record. The Director calls only these few methods - everything
 * else is "particular-implementation specific".
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 1.0
 * @see Director
 */
public interface Builder {
		
	/** 
	 * Write a header. This method is called just once before the iteration starts. 
	 */
	void header() throws IOException;
	
	/** 
	 * Write a footer. This method is called just once after the iteration ends. 
	 */
	void footer() throws IOException;
	
	/** 
	 * Make adjustments needed before
	 * the output of another record starts. 
	 * This method is called right before the <code>writeRecord()</code>. 
	 */
	void startRecord() throws IOException;
	
	/**
	 * 
	 * @param arg
	 * @throws IOException
	 */
	void part(Record arg) throws IOException;
	
	
	void part(Record... args) throws IOException;
	
	/** 
	 * Make adjustments needed after the output of the current record is completed.
	 * This method is called right after the <code>writeRecord()</code>. 
	 */
	void finishRecord() throws IOException;
	
}
