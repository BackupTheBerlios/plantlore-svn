/*
 * FirebirdDBLayer.java
 *
 * Created on 14. leden 2006, 22:39
 *
 */

package net.sf.plantlore.client.dblayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 * Implementation of Firebird database connection, sending queries and retrieving results from the
 * database. Connects directly to the FirebirdSQL database server, connection is not encrypted or
 * protected in any way.
 *
 * <b>This is just a temporary solution for the database connection.</b>
 *
 * @author Tomas Kovarik
 * @version 0.1, 14.1. 2006
 */
public class FirebirdDBLayer implements DBLayer {
    /** Instance of a logger */
    private Logger logger;
    /** Address of the database server */
    private String server;
    /** Port of the DB server */
    private String port;
    /** Name of the database (either filename with absolute path or alias)*/
    private String database;
    /** Login name of the user */
    private String login;
    /** Password of the user */
    private String password;
    /** Connection to the database */
    private Connection conn;
    /** Pool of opened ResultSets */
    private Hashtable results;
    
    /**
     * Creates a new instance of DBLayer. Sets parameters of the DB connection and initializes pool of results.
     *
     * @param server    Address of the database server
     * @param port      Port of the DB server
     * @param database  Name of the database (either filename with absolute path or alias)
     * @param login     Login name of the user
     * @param password  Password of the user
     */
    public FirebirdDBLayer(String server, String port, String database, String login, String password) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        // Save connection properties
        this.server = server;
        this.port = port;
        this.database = database;
        this.login = login;
        this.password = password;
        // Initialize ResultSet pool
        this.results = new Hashtable();
    }
    
    /**
     * Opens the database connection using parameters specified when creating DBLayer object.
     *
     * @throws DBLayerException in case that JDBC driver was not found in classpath or connection to the
     *                          database failed
     */
    public void initialize() throws DBLayerException {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            this.conn = DriverManager.getConnection("jdbc:firebirdsql:"+this.server+"/"+this.port+":"+this.database, this.login, this.password);
            logger.info("Database connection to "+this.server+" opened");
        } catch (ClassNotFoundException e) {
            logger.fatal("Failed to load JDBC driver");
            throw new DBLayerException("Failed to load JDBC driver");
        } catch (SQLException e) {
            logger.fatal("Unable to connect to the DB server");
            e.printStackTrace();
            throw new DBLayerException("Unable to connect to the DB server");
        }
    }
    
    /**
     *  Executes SQL SELECT query.
     *
     *  @param query query which should be executed
     *  @return result of the query execution
     *  @throws DBLayerException when execution of the query fails
     */
    public QueryResult executeQuery(SelectQuery query) throws DBLayerException {
        ResultSet rs;
        Statement st;
        int numrows, key;
        
        // Get SQL version of the query
        String sql = query.toSQL();
        try {
            // Execute given query using DB connection
            st = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery(sql);
            // Count number of rows in the result. Position cursor to the beginning afterwards
            rs.last();
            numrows = rs.getRow();
            rs.beforeFirst();
            // Save ResultSet to the result pool
            key = results.size();
            this.results.put(key, rs);
        } catch (SQLException e) {
            logger.fatal("EXCEPTION: Failed to execute SELECT query: "+sql+": "+e.getMessage());
            DBLayerException dbe = new DBLayerException("Failed to execute SELECT query: "+sql);
            throw dbe;
        }
        return new QueryResult(key, numrows, 1, query.getType());
    }
    
    /**
     *  Execute SQL INSERT query.
     *
     *  @param query query which should be executed
     *  @return result of the query execution
     *  @throws DBLayerException when execution of the query fails
     */
    public QueryResult executeQuery(InsertQuery query) throws DBLayerException {
        Statement st;
        int numrows;
        
        String sql = query.toSQL();
        try {
            // Execute query using DB connection
            st = this.conn.createStatement();
            numrows = st.executeUpdate(sql);
        } catch (SQLException e) {
            logger.fatal("Failed to execute INSERT query: "+sql);
            throw new DBLayerException("Failed to execute INSERT query: "+sql);
        }
        // Return query results
        return new QueryResult(0, numrows, 1, query.getType());
    }
    
    /**
     *  Execute SQL UPDATE query.
     *
     *  @param query query which should be executed
     *  @return result of the query execution
     *  @throws DBLayerException when execution of the query fails
     */
    public QueryResult executeQuery(UpdateQuery query) throws DBLayerException {
        Statement st;
        int numrows;
        
        String sql = query.toSQL();
        try {
            // Execute query using DB connection
            st = this.conn.createStatement();
            numrows = st.executeUpdate(sql);
        } catch (SQLException e) {
            logger.fatal("Failed to execute UPDATE query: "+query);
            throw new DBLayerException("Failed to execute UPDATE query: "+query);
        }
        // Return query result
        return new QueryResult(0, numrows, 1, query.getType());
    }
    
    /**
     *  Execute SQL DELETE query.
     *
     *  @param query query which should be executed
     *  @return result of the query execution
     *  @throws DBLayerException when execution of the query fails
     */
    public QueryResult executeQuery(DeleteQuery query) throws DBLayerException {
        Statement st;
        int numrows;
        
        String sql = query.toSQL();
        try {
            // Execute query using DB connection
            st = this.conn.createStatement();
            numrows = st.executeUpdate(sql);
        } catch (SQLException e) {
            logger.fatal("Failed to execute DELETE query: "+sql);
            throw new DBLayerException("Failed to execute DELETE query: "+sql);
        }
        // Return query result
        return new QueryResult(0, numrows, 1, query.getType());
    }
    
    /**
     *  Retrieves selected rows from the given result. Returns data from the ResultSet identified by the given
     *  QueryResult object. Retrieves an interval of rows including rows on positions "from" and "to".
     *  Results are returned as an array of objects (type <code>Object[]</code>). To use the results, you
     *  have to cast it to the correct type of data holder objects.
     *
     *  @param QRes     Object identifying result from which we want to retrieve rows
     *  @param from     Index of the first row to retrieve. Must be smaller or equal than to.
     *  @param to       Index of the last row to retrieve. Must be bigger than from.
     *  @return         Array of objects representing retrieved rows.
     *  @throws         In case illegal or invalid arguments (range from - to) are provided
     *  @see            next()
     */
    public Object[] more(QueryResult QRes, int from, int to) throws DBLayerException {
        // Check validity of arguments
        if (from>to) {
            logger.error("Cannot read rows from "+from+" to "+to+" because from > to");
            throw new DBLayerException("Cannot read rows from "+from+" to "+to+" because from > to");
        }
        // Get the ResultSet object from the result
        ResultSet rs = (ResultSet)results.get(QRes.getResultID());
        int numRows = QRes.getNumRows();
        // Check whether we have enough rows in the result
        if (to >= numRows) {
            logger.error("Result doesn't have enough rows. Number of rows: "+numRows);
            throw new DBLayerException("Result doesn't have enough rows. Number of rows: "+numRows);
        }
        // Move ResultSet to the first row we want to read
        try {
            rs.absolute(from);
        } catch (SQLException e) {
            logger.error("Cannot move ResultSet to the given row: "+from);
            throw new DBLayerException("Cannot move ResultSet to the given row: "+from);
        }
        // Allocate space for data
        Object[] data = new Object[to-from+1];
        // Read all the selected rows
        for (int i=0; i<=(to-from); i++) {
            data[i] = next(QRes);
        }
        return data;
    }
    
    /**
     *  Retrieves next row from the given result. Results are returned as an <code>Object</code>. in order to
     *  use it, you have to cast it to the correct type of data holder objects.
     *
     *  @param QRes     Identifier of the ResultSet we want to read
     *  @return         Object containing next row from the given result or <code>null</code> in case there
     *                  are no more rows in the result.
     *  @throws DBLayerException in case database error occured
     */
    public Object next(QueryResult QRes) throws DBLayerException {
        ResultSet rs = (ResultSet)results.get(QRes.getResultID());
        
        // In case no more rows are available, return null
        try {
            if (!rs.next()) {
                return null;
            }
        } catch (SQLException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }
        
        // Read data from the result according to the type of the result
        String type = QRes.getType();
        if (type.equals("USER")) {
            return getUserRow(rs);
        } else if (type.equals("AUTHOR")) {
            return getAuthorRow(rs);
        } else if (type.equals("PLANT")) {
            return getPlantRow(rs);
        } else if (type.equals("PUBLICATION")) {
            return getPublicationRow(rs);
        } else {
            // TODO: If given type is not defined, raise exception
            return null;
        }
    }
    
    /**
     *  Closes datbase connection.
     *
     *  <b>Currently closes DB connection not the ResultSet. TODO: Rewrite</b>
     *
     *  @param QRes identifier of the result we want to close
     *  @throws DBLayerException in case close operation on the connection failed
     */
    public void close(QueryResult QRes) throws DBLayerException {
        try {
            conn.close();
        } catch (SQLException e) {
            logger.fatal("Unable to close connection to the DB server: "+e);
            throw new DBLayerException("Unable to close connection to the DB server");
        }
    }
    
    /**
     *  Read information about the user from the ResultSet and store them into <code>UserRecord</code> object
     *
     *  @param rs   ResultSet containing the data
     *  @return     Object containing the data. In order to use the data result must be cast to a
     *              <code>UserRecord</code> type
     *  @throws DBLayerException in case database error occured
     *  @see        class UserRecord
     */
    private Object getUserRow(ResultSet rs) throws DBLayerException {
        UserRecord ur = new UserRecord();
        try {
            ur.setID(rs.getInt(0));
            ur.setLogin(rs.getString(1));
            ur.setFirstName(rs.getString(2));
            ur.setSurname(rs.getString(3));
            ur.setEmail(rs.getString(4));
            ur.setAddress(rs.getString(5));
            ur.setWhenCreated(rs.getDate(6));
            ur.setWhenDropped(rs.getDate(7));
            // CRIGHT from table TUSER should be here, but we don't need it
            ur.setNote(rs.getString(9));
            // CID from table TRIGHT should be here, but we don't need it
            ur.setExportRight(rs.getInt(11));
            ur.setImportRight(rs.getInt(12));
            ur.setRole(rs.getString(13));
        } catch (SQLException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }
        return ur;
    }
    
    /**
     *  Read information about the author from the ResultSet and store them into <code>AuthorRecord</code> object
     *
     *  @param rs   ResultSet containing the data
     *  @return     Object containing the data. In order to use the data result must be cast to a
     *              <code>AuthorRecord</code> type
     *  @throws DBLayerException in case database error occured
     *  @see        class AuthorRecord
     */
    private Object getAuthorRow(ResultSet rs) throws DBLayerException {
        AuthorRecord ar = new AuthorRecord();
        
        try {
            ar.setID(rs.getInt(0));
            ar.setFirstName(rs.getString(1));
            ar.setSurname(rs.getString(2));
            // CWHOLENAME from table TAUTHORS should be here, but we don't need it (TODO: Really?)
            ar.setOrganization(rs.getString(4));
            ar.setRole(rs.getString(5));
            ar.setAddress(rs.getString(6));
            ar.setPhoneNumber(rs.getString(7));
            ar.setEmail(rs.getString(8));
            ar.setUrl(rs.getString(9));
            ar.setNote(rs.getString(10));
        } catch (SQLException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }
        
        return ar;
    }
    
    /**
     *  Read information about the plant from the ResultSet and store them into <code>PlantRecord</code> object
     *
     *  @param rs   ResultSet containing the data
     *  @return     Object containing the data. In order to use the data result must be cast to a
     *              <code>PlantRecord</code> type
     *  @throws DBLayerException in case database error occured
     *  @see        class PlantRecord
     */
    private Object getPlantRow(ResultSet rs) throws DBLayerException {
        PlantRecord pr = new PlantRecord();
        try {
            pr.setID(rs.getInt(1));
            pr.setAdoptedName(rs.getString(2));
            pr.setCzechName(rs.getString(3));
            pr.setPublishableName(rs.getString(4));
            pr.setAbbreviation(rs.getString(5));
            pr.setNote(rs.getString(6));
        } catch (SQLException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }
        
        return pr;
    }
    
    /**
     *  Read information about the publication from the ResultSet and store them into
     *  <code>PublicationRecord</code> object
     *
     *  @param rs   ResultSet containing the data
     *  @return     Object containing the data. In order to use the data result must be cast to a
     *              <code>PublicationRecord</code> type
     *  @throws DBLayerException in case database error occured
     *  @see        class PublicationRecord
     */
    private Object getPublicationRow(ResultSet rs) throws DBLayerException {
        PublicationRecord pr = new PublicationRecord();
        try {
            pr.setID(rs.getInt(0));
            pr.setCollectionName(rs.getString(1));
            pr.setPublicationYear(rs.getInt(2));
            pr.setJournalName(rs.getString(3));
            pr.setJournalAuthor(rs.getString(4));
        } catch (SQLException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }
        
        return pr;
    }
}
