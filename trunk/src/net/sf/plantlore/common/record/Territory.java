/*
 * Territory.java
 *
 * Created on 15. březen 2006, 0:18
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing TTERRITORIES table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a territory
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Territory extends Record {
    /** Parameters of the territory */
    private int id;    
    private String name;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String NAME = "name";    
    
    /**
     *   Default constructor to create new class Territory
     */
    public Territory() {
        
    }
    
    public String[] getColumns() {
    	return new String[] { ID, NAME };
    }
    
    /**
     *   Get territory id
     *   @return id of the territory
     *   @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set territory id
     *   @param id   id of the territory
     *   @see getId
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get name of the territory
     *   @return name of the territory
     *   @see setName
     */
    public String getName() {
        return this.name;
    }
    
    /**
     *   Set name of the territory
     *   @param name string containing name of the territory
     *   @see getName
     */
    public void setName(String name) {
        this.name = name;
    }    
}