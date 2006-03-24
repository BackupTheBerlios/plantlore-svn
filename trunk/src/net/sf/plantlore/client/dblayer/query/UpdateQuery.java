/*
 * UpdateQuery.java
 *
 * Created on 15. leden 2006, 13:56
 *
 */

package net.sf.plantlore.client.dblayer.query;

import java.util.ArrayList;
import net.sf.plantlore.server.DBLayerException;
import org.apache.log4j.Logger;

/**
 * Object holding parameters of delte query
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 15, 2006
 */
public class UpdateQuery implements Query {
    /** Type of data we are working with. For the list of types see DBmapping class */
    private int type;
    /** List of triples for the WHERE part of the query */
    private ArrayList whereList;
    // Array of tuples containing the data we want to insert
    private ArrayList data;
    /** Instance of a logger */
    private Logger logger;
    
    /**
     * Creates a new instance of UpdateQuery. Initializes empty <code>whereList</code> and <code>data</code> array.
     */
    public UpdateQuery() {
        whereList = new ArrayList();
        data = new ArrayList();
        // dbmap = new DBMapping();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        
    }
    
    /**
     *  Create new instance of the DeleteQuery. Initializes <code>whereList</code> and <code>data</code> with the given values
     *
     *  @param type         type of data we are working with
     *  @param whereList    List of triples for the WHERE clause
     *  @param updateData   List of tuples with the updated data
     *  @throws DBLayetException in case the given type was not found in the DBMapping class
     */
    public UpdateQuery(int type, ArrayList where, ArrayList updateData) throws DBLayerException {
        this.whereList = where;
        this.data = updateData;
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
     *  Add an item (tuple) to the list of values which will be updated
     *
     *  @param field    name of the field (column)
     *  @param value    value of the field (column)
     */
    public void addData(String field, String value) {
        String[] tuple = {new String(field), new String(value) };
        data.add(tuple);
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
    public String toSQL() throws DBLayerException {
        StringBuffer sql;
        String table;
        StringBuffer where;
        StringBuffer update;
        String fieldName;
        String[] triple;
        String[] tuple;
        
        // Initialize StringBuffers
        where = new StringBuffer();
        sql = new StringBuffer();
/*        
        update = new StringBuffer();
        // Get the name of the table we want to update
        table = dbmap.getTableName(this.type);
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
        // Construct swction of the query containing updated data
        for (int i=0;i<data.size();i++) {
            if (i>0) {
                update.append(", ");
            }
            tuple = (String[])data.get(i);
            fieldName = dbmap.getFieldName(tuple[0], this.type);
            update.append(fieldName);
            update.append(" = ");
            update.append("'"+tuple[1]+"'");
        }
        // Construct whole SQL query
        sql.append("UPDATE ");
        sql.append(table);
        sql.append(" SET ");
        sql.append(update);
        sql.append(" ");
        sql.append(where);
*/        
        logger.debug(sql.toString());
        return sql.toString();
    }
    
    public void addOrderby(String field, String direction) { }
}
