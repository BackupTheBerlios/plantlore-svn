package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Collection;

import org.hibernate.Criteria;

public interface SelectQuery extends Serializable, Remote{
	
	Criteria getCriteria();
	
	void addRestriction(int type, String firstPropertyName, String secondPropertyName, Object value, Collection values);
	
	void addProjection(int type, String propertyName);
	
	void setFetchMode(String associationPath, int mode);
	
	void addOrder(int direction, String propertyName);
	
	void addAssociation(String associationPath);
	
	

}
