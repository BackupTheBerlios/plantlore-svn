package net.sf.plantlore.client.imports;

import java.io.Reader;

import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;

public abstract class AbstractParser implements Parser {
	
	protected Reader reader;
	protected Record torso;
	
	
	public AbstractParser(Reader reader) {
		this.reader = reader;
		torso = new AuthorOccurrence().createTorso();
	}
	
	
	public boolean hasNextRecord() {
		return false;
	}
	
	
	public Record nextPart(Class table) 
	throws ParserException {
		return torso.findSubrecord( table );
		// + nacteni dalsiho zaznamu k teto tabulce - tyka se jenom AO
	}
	


	public Action intentedFor() {
		return Parser.Action.UNKNOWN;
	}


}
