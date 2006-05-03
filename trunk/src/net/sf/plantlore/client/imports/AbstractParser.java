package net.sf.plantlore.client.imports;

import java.io.Reader;

//import net.sf.plantlore.common.record.*;

public abstract class AbstractParser implements Parser {
	
	protected Reader reader;
	
	public AbstractParser(Reader reader) {
		this.reader = reader;
	}


	public Action intentedFor() {
		return Parser.Action.UNKNOWN;
	}


}
