package net.sf.plantlore.common.exception;

import org.hibernate.JDBCException;


/**
 * An exception thrown every time something in the database layer
 * (DBLayer) went wrong.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @author Tomas Kovarik (tkovarik@gmail.com)
 * @since 2006-04-29
 *
 */
public class DBLayerException extends PlantloreException {
	
	private static final long serialVersionUID = 2006060411002L;
	
        /** Error code */
        private int errorCode;
        /** Additional error info */
        private String errorInfo;
    
        // ================= ERROR CODES ===============
        /** It was not possible to detect the reason why the operation failed. */
        public static final int ERROR_UNKNOWN = -1;
        /** Database configuration cannot be loaded properly */
        public static final int ERROR_LOAD_CONFIG = 1;
        /** Connection to the database failed or no connection available */
        public static final int ERROR_CONNECT = 2;
        /** Login and/or password do not match any user record */
        public static final int ERROR_LOGIN = 3;
        /** Saving data into the DB failed */
        public static final int ERROR_SAVE = 4;
        /** Deleteing data from the database failed */
        public static final int ERROR_DELETE = 5;
        /** Updating data in the DB failed */
        public static final int ERROR_UPDATE = 6;
        /** Selecting data from the DB failed */
        public static final int ERROR_SELECT = 7;        
        /** Retrieving data from the DB failed */
        public static final int ERROR_LOAD_DATA = 8;
        /** Cannot close connection to the DB */
        public static final int ERROR_CLOSE = 9;
        /** User doesn't have rights to execute the operation */
        public static final int ERROR_RIGHTS = 10;        
        /** Database not consistent (e.g. tHistoryColumn doesn't contain data) */
        public static final int ERROR_DB = 11;
        /** Transaction rolled back - possible locking conflict */ 
        public static final int ERROR_TRANSACTION = 12;
        /** Insufficient resources error occurred */
        public static final int ERROR_RESOURCES = 13;
        /** Disk full error occurred*/
        public static final int ERROR_DISK_FULL = 14;
        /** Out of memmory error occurred */
        public static final int ERROR_OUT_OF_MEMORY = 15;        
        /** Maximum number of connections achieved */
        public static final int ERROR_MAX_CONNECTIONS = 15;                
        /** Some other error */        
        public static final int ERROR_OTHER = 20;
        // ==============================================
        
        
           
        /** Create new DBLayerException without an error message */
        public DBLayerException() { super(); }
        
        /** Create new DBLayerException with an error message */
        public DBLayerException(String message) { super(message); }
        
        // Better constructor to allow proper exception wrapping.
        public DBLayerException(String message, Throwable originalException) {
        	super(message, originalException);
        	if(originalException instanceof JDBCException)        	
        		setError( translateSQLState( ((JDBCException)originalException).getSQLState()), originalException.getMessage() );
        }
        
        // Better constructor to allow proper exception wrapping.
        public DBLayerException(String message, int error, Throwable originalException) {
        	super(message, originalException);
        	setError( error, originalException.getMessage() );
        }
        
        
        public boolean isReconnectNecessary() {
        	return errorCode == ERROR_CONNECT;
        }
        
        /**
         *  Set error this exception represents.
         *  @param errorCode code of the error. For the list of codes, see constants.
         *  @param errorInfo additional information about the error
         */
        public void setError(int errorCode, String errorInfo) {
            this.errorCode = errorCode;
            this.errorInfo = errorInfo;
        }
        
        /**
         *  Return error code for this exception.
         *  @return error code for this exception
         */
        public int getErrorCode() {
            return this.errorCode;
        }
        
        /**
         *  Return additional information for this exception.
         *  @return additional information for this exception
         */
        public String getErrorInfo() {
            return this.errorInfo;
        }
        
        /**
         *  Translate SQL State to DBLayerException constants. SQL State is obtained from JDBC exceptions
         *  thrown by the JDBC driver. Only several SQL States are translated, for others ERROR_OTHER is
         *  returned.
         *  @param sqlstate String containing SQL State constant
         *  @return DBLayerException constant identifying a problem
         */
        public static int translateSQLState(String sqlstate) {
            String errorClass = sqlstate.substring(0,2);
            String errorDetail = sqlstate.substring(2);
            // Connection exception - Connection does not exist, was interrupted or cannot be established
            if (errorClass.equals("08")) {
                return ERROR_CONNECT;
            }
            // Integrity constraint violation - NOT NULL, FOREIGN KEY, UNIQUE violation
            if (errorClass.equals("23")) {
                return ERROR_DB;
            }
            // Transaction rollback
            if (errorClass.equals("40")) {
                return ERROR_TRANSACTION;
            }
            // Syntax error
            if (errorClass.equals("42")) {
                return ERROR_DB;
            }
            // Insufficient Resources
            if (errorClass.equals("53")) {
                // Generic "Insufficient resources"
                if (errorDetail.equals("000")) {
                    return ERROR_RESOURCES;
                }
                // Disk Full
                if (errorDetail.equals("100")) {
                    return ERROR_DISK_FULL;
                }
                // Out of memory
                if (errorDetail.equals("200")) {
                    return ERROR_OUT_OF_MEMORY;
                }
                // Too many connections
                if (errorDetail.equals("300")) {
                    return ERROR_MAX_CONNECTIONS;
                }                
            }
            // System error - External error (such as IO errors)
            if (errorClass.equals("58")) {
                return ERROR_OTHER;
            }
            return ERROR_OTHER;
        }
}
