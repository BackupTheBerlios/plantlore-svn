/*
 * Phytochorion.java
 *
 * Created on 15. březen 2006, 0:17
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing TPHYTOCHORIA table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a phytochorion
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Phytochorion {
    /** Parameters of phytochorion */
    private int id;
    private String code;
    private String name;
    
    /**
     *   Default constructor to create new class Phytochoria
     */
    public Phytochorion() {
        
    }
    
    /**
     *   Get phytochorion id
     *   @return id of the phytochorion
     *   @see setId
     */
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set phytochorion id
     *   @param id id of the phytochorion
     *   @see getId
     */
    public void setId(int id) {
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