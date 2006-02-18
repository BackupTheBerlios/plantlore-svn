package net.sf.plantlore.client.dblayer.result;

import java.io.Serializable;

public interface Result extends Serializable {
	
	int getResultID();
	
	int getNumRows();
	
	int getOperationResult();
	
	int getType();

}
