/*
 * DBLayer.java
 *
 * Created on 14. leden 2006, 23:25
 *
 */

package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.server.DBLayerException;

/**
 * Interface for database access.
 * 
 * TODO: Zdokumentovat metody interface!
 *
 * @author Tomáš Kovařík, Erik Kratochvíl
 */
public interface DBLayer extends Remote, Serializable {
    
    /** Initialize database connection */
    void initialize(String dbID, String user, String password) throws DBLayerException, RemoteException;
    
    public int executeInsert(Object data) throws DBLayerException, RemoteException;
    
    public void executeDelete(Object data) throws DBLayerException, RemoteException;
        
    public void executeUpdate(Object data) throws DBLayerException, RemoteException;
    
    public Object[] more(int resultId, int from, int to) throws DBLayerException, RemoteException;
    
    public Object[] next(int resultId) throws DBLayerException, RemoteException;

    public int getNumRows(int resultId) throws RemoteException;
        
    public void close(Result QRes) throws DBLayerException, RemoteException;
    
    public SelectQuery createQuery(Class classname) throws RemoteException;

    public int executeQuery(SelectQuery query) throws DBLayerException, RemoteException;    
        
}
