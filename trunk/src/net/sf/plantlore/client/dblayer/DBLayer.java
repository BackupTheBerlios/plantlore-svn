/*
 * DBLayer.java
 *
 * Created on 14. leden 2006, 23:25
 *
 */

package net.sf.plantlore.client.dblayer;

import java.sql.SQLException;

/**
 * Interface for database access.
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 14, 2006
 */
public interface DBLayer {
    
    /** Initialize database connection */
    void initialize() throws DBLayerException;
    
    /** Execute SELECT query */
    QueryResult executeQuery(SelectQuery query) throws DBLayerException;
    
    /** Execute INSERT query */
    QueryResult executeQuery(InsertQuery query) throws DBLayerException;
    
    /** Execute UPDATE query */
    QueryResult executeQuery(UpdateQuery query) throws DBLayerException;
    
    /** Execute DELETE query */
    QueryResult executeQuery(DeleteQuery query) throws DBLayerException;
    
    /** Retrieve an interval of rows from the result */
    public Object[] more(QueryResult QRes, int from, int to) throws DBLayerException;
    
    /** Retrieve next row from the result */
    public Object next(QueryResult QRes) throws DBLayerException;
    
    /** Close the result */
    public void close(QueryResult QRes) throws DBLayerException;
    
}
