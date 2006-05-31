package net.sf.plantlore.client.imports.table;

import net.sf.plantlore.common.exception.ParserException;

public interface TableParser {
	
	enum Action { INSERT, UPDATE, DELETE };

	boolean hasNext();
	
	DataHolder getNext() throws ParserException;
	
	int getNumberOfRecords();

}
