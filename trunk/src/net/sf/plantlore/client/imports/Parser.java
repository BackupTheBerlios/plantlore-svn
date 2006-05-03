package net.sf.plantlore.client.imports;

import java.io.Reader;
import net.sf.plantlore.common.record.*;

public interface Parser {
	
	public enum Action { UNKNOWN, INSERT, DELETE, UPDATE };
	
	boolean initialize(Reader reader);
	
	void cleanup();
	
	boolean hasNext();
	
	Record next();
	
	Action intentedFor(); 
	
	Record part(Class table);
}
