package net.sf.plantlore.common.record;

import java.io.Serializable;

public abstract class Record implements Serializable {

	public abstract Integer getId();
	
	public String[] getForeignKeys() { return null; }
	
	public String[] getColumns() { return null; }
	
}
