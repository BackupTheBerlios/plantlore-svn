/*
 * DeleteQuery.java
 *
 * Created on 15. leden 2006, 13:56
 *
 */

package net.sf.plantlore.client.dblayer.query;

import java.util.ArrayList;
import net.sf.plantlore.server.DBLayerException;
import org.apache.log4j.Logger;

/**
 * Object holding parameters of delete query
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 15, 2006
 */
public class DeleteQuery implements Query {
    /** Type of data we are working with. For the list of types see DBmapping class */
    private int type;
    /** List of triples for the WHERE part of the query */
    private ArrayList whereList;
    /** Instance of a logger */
    private Logger logger;
    
    /**
     * Create a new instance of DeleteQuery. Initializes empty <code>whereList</code> array.
     */
    public DeleteQuery() {
        whereList = new ArrayList();
        // dbmap = new DBMapping();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /**
     *  Create new instance of the DeleteQuery. Initializes <code>whereList</code> with the given values
     *
     *  @param type         type of data we are working with
     *  @param whereList    List of triples for the WHERE clause
     *  @throws DBLayetException in case the given type was not found in the DBMapping class
     */
    public DeleteQuery(int type, ArrayList where) throws DBLayerException {
        this.whereList = where;
        setType(type);
        // dbmap = new DBMapping();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /**
     *  Add an item (triplet) to the list of triples for the WHERE part of the query
     *
     *  @param field    name of the field (left operand)
     *  @param operator operator connecting operands
     *  @param value    value of the field (right operand)
     */
    public void addWhere(String field, String operator, String value) {
        String[] triple = {new String(field), new String(operator), new String(value) };
        whereList.add(triple);
    }
    
    /**
     *  Set the type of data we are working with. For the list of types see DBmapping class.
     *
     *  @param type Type of data we are working with
     *  @throws DBLayerException in case the given type was not found in the DBMapping class
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     *  Get the type of data we are working with. For the list of types see DBmapping class.
     *
     *  @return Type of data we are working with
     */
    public int getType() {
        return this.type;
    }
    
    /**
     *  Transform data in this object to the SQL query.
     *
     *  @return String with the SQL query constructed from the data stored in the object
     *  @throws DBLayerException in case some part of the SQL query cannot be constructed
     */
    public String toSQL() throws DBLayerException {
        StringBuffer sql;
        StringBuffer where;
        String from;
        String fieldName;
        String[] triple;
        
        // Initialize StringBuffers
        sql = new StringBuffer();
        where = new StringBuffer();
/*        
        // Get the name of the table from which we want to delete records
        from = dbmap.getTableName(this.type);
        // Construct the WHERE clasue
        for (int i=0;i<whereList.size();i++) {
            if (i>0) {
                where.append(" AND ");
            }
            triple = (String[])whereList.get(i);
            fieldName = dbmap.getFieldName(triple[0], this.type);
            where.append(fieldName);
            where.append(" ");
            where.append(triple[1]);
            where.append(" ");
            where.append("'"+triple[2]+"'");
        }
        // Construct SQL query
        sql.append("DELETE FROM ");
        sql.append(from);
        sql.append(" WHERE ");
        sql.append(where);
*/        
        logger.debug(sql.toString());
        return sql.toString();
    }
    
    public void addOrderby(String field, String direction) { }    

    public void addData(String field, String value) { }
}
