package net.sf.plantlore.client.tableimport;

import net.sf.plantlore.common.exception.ParserException;

public interface TableParser {
	
	enum Action { INSERT, UPDATE, DELETE };
	
	Class initialize() throws ParserException;

	boolean hasNext();
	
	DataHolder getNext() throws ParserException;
	
	int getNumberOfRecords();

}
