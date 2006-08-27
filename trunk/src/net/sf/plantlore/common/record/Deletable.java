package net.sf.plantlore.common.record;

import java.io.Serializable;

public interface Deletable extends Serializable {
	
	static final String DELETED = "deleted";
	
	void setDeleted(Integer arg);
	
	Integer getDeleted();

}
