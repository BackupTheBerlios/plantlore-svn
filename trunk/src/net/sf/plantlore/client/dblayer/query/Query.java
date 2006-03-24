package net.sf.plantlore.client.dblayer.query;

import java.io.Serializable;

import net.sf.plantlore.server.DBLayerException;

public interface Query extends Serializable {
	
	void addData(String field, String value);
	
	void addWhere(String field, String operator, String value);
	
	void addOrderby(String field, String direction);
	
	void setType(int type);
	
	int getType();
	
	String toSQL() throws DBLayerException;
}
