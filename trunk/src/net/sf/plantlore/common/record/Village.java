/*
 * Village.java
 *
 * Created on 15. březen 2006, 0:17
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing TVILLAGES table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a village
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Village extends Record {
    /** Parameters of the Village */    
    private Integer id;   
    private String name;

    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String NAME = "name";    

    //public enum Column {ID, NAME};
    
    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value))
        	value = null;
    	
		if(column.equals(ID)) setId((Integer)value);
		else if(column.equals(NAME)) setName((String)value);
    }
    
    /** Creates a new instance of Village */
    public Village() {
        
    }
    
    public List<String> getColumns() {
    	return asList( NAME );
    }
        
    public List<String> getNN() {
    	return asList(NAME);
    }
    
    /**
     *   Get village id
     *   @return id of the village
     *   @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set village id
     *   @param id id of the village
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get name of the village
     *   @return name of the village
     *   @see setName
     */
    public String getName() {
        return this.name;
    }
    
    /**
     *   Set name of the village
     *   @param name string containing name of the village
     *   @see getName
     */
    public void setName(String name) {
        this.name = name;
    }    
}

