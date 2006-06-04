package net.sf.plantlore.common.record;

import java.io.Serializable;

public interface Deletable extends Serializable {
	
	void setDeleted(Integer arg);
	
	Integer getDeleted();

}
