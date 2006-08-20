package net.sf.plantlore.client.imports;

import java.io.Reader;

import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;

@Deprecated
public class AbstractParser implements Parser {
	
	protected Reader reader;
	
	
	public AbstractParser(Reader reader) {
		this.reader = reader;
	}
	
	
	public void initialize() throws ParserException {
		// Unimplemented. A subclass may override it.
	}
	
	public void cleanup() {
		// Unimplemented. A subclass may override it.
	}
	
	public boolean hasNextRecord() {
		return false;
	}
	
	public Intention fetchNextRecord() throws ParserException {
		return Intention.UNKNOWN;
	}

	public boolean hasNextPart(Class table) {
		return false;
	}
	
	public Record getNextPart(Class table) throws ParserException {
		return null;
	}
	
	public Intention intentedFor() {
		return Intention.UNKNOWN;
	}
	
	public int getNumberOfRecords() {
		return -1;
	}


}
