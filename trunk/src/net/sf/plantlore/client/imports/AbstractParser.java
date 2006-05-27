package net.sf.plantlore.client.imports;

import java.io.Reader;

import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;

public abstract class AbstractParser implements Parser {
	
	protected Reader reader;
	protected AuthorOccurrence ao;
	
	public AbstractParser(Reader reader) {
		this.reader = reader;
	}
	
	
	public boolean hasNextRecord() {
		return false;
	}
	
	
	public Record nextPart(Class table) 
	throws ParserException {
		return null;
	}
	
	public Action intentedFor() {
		return Parser.Action.UNKNOWN;
	}


}
