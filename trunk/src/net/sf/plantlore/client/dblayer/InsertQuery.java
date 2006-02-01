/*
 * InsertQuery.java
 *
 * Created on 15. leden 2006, 13:56
 *
 */

package net.sf.plantlore.client.dblayer;

import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Object hodling parameters of an insert query
 *
 * @author Tomas Kovarik
 * @versionm 0.1, Jan 15, 2006
 */
public class InsertQuery {
    /** Type of data we are working with. For the list of types see DBmapping class */
    private String type;
    // Array of tuples containing the data we want to insert
    private ArrayList data;
    /** Instance of DBMapping object */
    private DBMapping dbmap;
    /** Instance of a logger */
    private Logger logger;
    
    /**
     * Create a new instance of InsertQuery. Initializes empty <code>data</code> array.
     */
    public InsertQuery() {
        data = new ArrayList();
        dbmap = new DBMapping();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /**
     *  Create new instance of the InsertQuery. Initializes <code>data</code> array with the given values
     *
     *  @param type     type of data we are working with
     *  @param data     List of tuples containing values to insert
     *  @throws DBLayetException in case the given type was not found in the DBMapping class
     */
    public InsertQuery(String type, ArrayList insertData) throws DBLayerException {
        this.data = insertData;
        setType(type);
        dbmap = new DBMapping();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /**
     *  Add an item (tuple) to the list of values which will be inserted
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
     *  @throws DBLayerException in case the given type was not found in the DBMapping class
     */
    public void setType(String type) throws DBLayerException {
        if (dbmap.checkType(type)) {
            this.type = type;
        } else {
            logger.error("Type '"+type+"' not found in DB mapping");
            throw new DBLayerException("Type '"+type+"' not found in DB mapping");
        }
    }
    
    /**
     *  Get the type of data we are working with. For the list of types see DBmapping class.
     *
     *  @return Type of data we are working with
     */
    public String getType() {
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
        StringBuffer insertFields;
        StringBuffer insertData;
        String into;
        String fieldName;
        String[] tuple;
        
        // Initialize StringBuffers
        sql = new StringBuffer();
        insertFields = new StringBuffer();
        insertData = new StringBuffer();
        // Get the name of the table into which we will insert data
        into = dbmap.getTableName(this.type);
        // Construct part of the query with the data
        for (int i=0;i<data.size();i++) {
            if (i>0) {
                insertFields.append(", ");
                insertData.append(", ");
            }
            tuple = (String[])data.get(i);
            fieldName = dbmap.getFieldName(tuple[0], this.type);
            insertFields.append(fieldName);
            // TODO: Rewrite adding quotes to the queries
            if (i>0) {
                insertData.append("'"+tuple[1]+"'");
            } else {
                insertData.append(tuple[1]);                
            }
        }
        // Construct SQL query
        sql.append("INSERT INTO ");
        sql.append(into);
        sql.append(" (");
        sql.append(insertFields);
        sql.append(") VALUES (");
        sql.append(insertData);
        sql.append(")");
        
        logger.debug(sql.toString());
        return sql.toString();
    }
}
