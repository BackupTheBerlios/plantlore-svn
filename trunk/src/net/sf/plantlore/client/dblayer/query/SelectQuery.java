/*
 * SelectQuery.java
 *
 * Created on 15. leden 2006, 13:55
 *
 */

package net.sf.plantlore.client.dblayer.query;

import java.util.ArrayList;
import net.sf.plantlore.server.DBLayerException;
import org.apache.log4j.Logger;

/**
 * Object holding parameters of a select query.
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 15, 2006
 */
public class SelectQuery implements Query {
    /** Type of data we are working with. For the list of types see DBmapping class */
    private int type;
    /** List of triples for the WHERE part of the query */
    private ArrayList whereList;
    /** List of tuples for the ORDER BY part of the query */
    private ArrayList orderbyList;
    /** Instance of DBMapping object */
    private Logger logger;
    
    /**
     * Create a new instance of SelectQuery. Initializes empty <code>whereList</code> and <code>orderbyList</code>
     */
    public SelectQuery() {
        this.whereList = new ArrayList();
        this.orderbyList = new ArrayList();
        // dbmap = new DBMapping();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /**
     *  Create new instance of the SelectQuery. Initializes <code>whereList</code> and <code>orderbyList</code>
     *  with the given values
     *
     *  @param type     type of data we are working with
     *  @param where    List of triples for the WHERE part of the query
     *  @param orderby  List of tuples for the ORDER BY part of the query
     *  @throws DBLayerException in case the given type was not found in the DBMapping class
     */
    public SelectQuery(int type, ArrayList where, ArrayList orderby) throws DBLayerException {
        this.whereList = where;
        this.orderbyList = orderby;
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
     *  Add an item (tuple) to the list of tuples for the ORDER BY part of the query
     *
     *  @param field    name of the field for sorting
     *  @param value    direction of sorting (<code>ASC</code> or <code>DESC</code>)
     */
    public void addOrderby(String field, String direction) {
        String[] tuple = { new String(field), new String(direction) };
        orderbyList.add(tuple);
    }
    
    /**
     *  Set the type of data we are working with. For the list of types see DBmapping class.
     *
     *  @param type Type of data we are working with
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
    public String toSQL() throws DBLayerException{
        StringBuffer sql;
        String from;
        StringBuffer where;
        StringBuffer orderby;
        String fieldName;
        String[] triple;
        String[] tuple;

        // Initialize StringBuffers
        where = new StringBuffer();
        sql = new StringBuffer();
/*        
        orderby = new StringBuffer();
        // Get the table names we will be working with
        from = dbmap.getTableName(this.type);
        // Construct WHERE clause
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
        // Construct orderby clause
        orderby.append(" ORDER BY ");
        for (int i=0;i<orderbyList.size();i++) {
            if (i>0) {
                orderby.append(", ");
            }
            tuple = (String[])orderbyList.get(i);
            fieldName = dbmap.getFieldName(tuple[0], this.type);
            orderby.append(fieldName);
            orderby.append(" ");
            orderby.append(tuple[1]);
        }
        // Construct SQL query
        sql.append("SELECT * FROM ");
        sql.append(from);
        if (whereList.size() != 0) {
            sql.append(" WHERE ");
            sql.append(where);
        }
        if (orderbyList.size() != 0) {
            sql.append(" ");
            sql.append(orderby);
        }
*/        
        logger.debug(sql.toString());
        return sql.toString();

    }
    
    public void addData(String field, String value) { }
}
