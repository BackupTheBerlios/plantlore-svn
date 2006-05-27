package net.sf.plantlore.client.imports;

import java.io.Reader;

import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;

public class AbstractParser implements Parser {
	
	protected Reader reader;
	
	
	public AbstractParser(Reader reader) {
		this.reader = reader;
	}
	
	
	public void initialize() throws ParserException {}
	
	public void cleanup() {}
	
	public boolean hasNextRecord() {
		return false;
	}
	
	public Action fetchNextRecord() throws ParserException {
		return Action.UNKNOWN;
	}

	public boolean hasNextPart(Class table) {
		return false;
	}
	
	public Record getNextPart(Class table) throws ParserException {
		return null;
	}
	
	public Action intentedFor() {
		return Action.UNKNOWN;
	}


}
