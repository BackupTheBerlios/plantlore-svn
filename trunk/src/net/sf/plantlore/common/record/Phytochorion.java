/*
 * Phytochorion.java
 *
 * Created on 15. březen 2006, 0:17
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing TPHYTOCHORIA table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a phytochorion
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Phytochorion extends Record {
	
	private static final long serialVersionUID = 20060604020L;
	
    /** Parameters of phytochorion */
    private Integer id;
    private String code;
    private String name;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String CODE = "code";    
    public static final String NAME = "name";
    
    //public enum Column {ID, CODE, NAME};
    
    /**
     *   Default constructor to create new class Phytochoria
     */
    public Phytochorion() {
        
    }
    
    public List<String> getColumns() {
    	return asList( CODE, NAME );
    }
    
    public List<String> getNN() {
    	return asList(CODE, NAME);
    }
    
    
    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value))
        	value = null;
    	
		if(column.equalsIgnoreCase(ID)) {
			if(value != null && value instanceof String)
				setId(Integer.parseInt((String)value));
			else
				setId((Integer)value);
		}
		else if(column.equalsIgnoreCase(CODE)) setCode((String)value);
		else if(column.equalsIgnoreCase(NAME)) setName((String)value);
    }
    
    /**
     *   Get phytochorion id
     *   @return id of the phytochorion
     *   @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set phytochorion id
     *   @param id id of the phytochorion
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get name of the phytochorion
     *   @return name of the phytochorion
     *   @see setName
     */
    public String getName() {
        return this.name;
    }
    
    /**
     *   Set name of the phytochorion
     *   @param name string containing name of the phytochorion
     *   @see getName
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     *   Get code of the phytochorion
     *   @return code of the phytochorion
     *   @see setCode
     */
    public String getCode() {
        return this.code;
    }
    
    /**
     *   Set code of the phytochorion
     *   @param code string containing code of the phytochorion
     *   @see getCode
     */
    public void setCode(String code) {
        this.code = code;
    }
}
