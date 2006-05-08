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
 * @see net.sf.plantlore.client.export.DefaultDirector
 */
public interface Builder {
		
	/** 
	 * Write a header. This method must be called just once before the iteration starts. 
	 */
	void header() throws IOException;
	
	/** 
	 * Write a footer. This method must be called just once after the iteration ends. 
	 */
	void footer() throws IOException;
	
	/** 
	 * Make adjustments needed before
	 * the output of another record starts. 
	 * This method must be called right before any call of <code>part()</code>. 
	 */
	void startRecord() throws IOException;
	
	/**
	 * Write a part of the record.
	 * 
	 * @param arg A part of a record to be written. The record may span across several tables.
	 * The correct implementation should traverse the given record table by table and 
	 * build the ouptut appropriately.
	 * @throws IOException If the writer encounters an error.
	 */
	void part(Record arg) throws IOException;
	
	/**
	 * Convenient passing of more records. 
	 * A typical implementation should call <code>part(Record)</code> repeatedly.  
	 * 
	 * @param args An unempty list of records.
	 * @throws IOException If the writer encounters an error.
	 * @see Builder#part(Record)
	 */
	void part(Record... args) throws IOException;
	
	/** 
	 * Make adjustments needed after the output of the current record is completed.
	 * This method must be called right after the last call of <code>part()</code>. 
	 */
	void finishRecord() throws IOException;
	
}
