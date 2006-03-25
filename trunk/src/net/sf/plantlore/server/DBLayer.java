/*
 * DBLayer.java
 *
 * Created on 14. leden 2006, 23:25
 *
 */

package net.sf.plantlore.server;

import java.rmi.Remote;
import java.util.Collection;
import net.sf.plantlore.client.dblayer.query.InsertQuery;
import net.sf.plantlore.client.dblayer.query.Query;
import net.sf.plantlore.client.dblayer.result.Result;

/**
 * Interface for database access.
 *
 * @author Tomas Kovarik
 */
public interface DBLayer extends Remote {
    
    /** Initialize database connection */
    void initialize() throws DBLayerException;
    
    public int executeInsert(Object data) throws DBLayerException;
    
    public void executeDelete(Object data) throws DBLayerException;
        
    public void executeUpdate(Object data) throws DBLayerException;
    
    public Object[] more(int resultId, int from, int to) throws DBLayerException;
    
    public Object[] next(int resultId) throws DBLayerException;

    public int getNumRows(int resultId);
        
    public void close(Result QRes) throws DBLayerException;
    
    public SelectQuery createQuery(Class classname);

    public int executeQuery(SelectQuery query) throws DBLayerException;    
        
}
