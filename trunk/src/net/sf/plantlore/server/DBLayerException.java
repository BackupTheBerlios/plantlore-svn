/*
 * DBLayerException.java
 *
 * Created on 16. leden 2006, 3:25
 *
 */

package net.sf.plantlore.server;

/**
 *  Exception for the DBLayer package (on the client side)
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Jan 16, 2006
 */
public class DBLayerException extends Exception {
    
    /** Creates a new instance of DBLayerException */
    public DBLayerException() {
        super();
    }
    
    /** Creates a new instance of DBLayerException with the specified message */
    public DBLayerException(String message) {
        super(message);
    }
}
