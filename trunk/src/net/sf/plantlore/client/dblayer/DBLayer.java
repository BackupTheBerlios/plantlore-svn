/*
 * DBLayer.java
 *
 * Created on 14. leden 2006, 23:25
 *
 */

package net.sf.plantlore.client.dblayer;

import java.rmi.Remote;
import net.sf.plantlore.client.dblayer.query.Query;
import net.sf.plantlore.client.dblayer.result.Result;

/**
 * Interface for database access.
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 14, 2006
 */
public interface DBLayer extends Remote {
    
    /** Initialize database connection */
    void initialize() throws DBLayerException;
    
    /** Execute query */
    Result executeQuery(Query query) throws DBLayerException;
       
    /** Retrieve an interval of rows from the result */
    public Object[] more(Result QRes, int from, int to) throws DBLayerException;
    
    /** Retrieve next row from the result */
    public Object next(Result QRes) throws DBLayerException;
    
    /** Close the result */
    public void close(Result QRes) throws DBLayerException;
    
}
