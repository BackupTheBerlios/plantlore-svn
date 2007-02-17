package net.sf.plantlore.common.exception;

import net.sf.plantlore.l10n.L10n;

import org.hibernate.JDBCException;


/**
 * An exception thrown every time something in the database layer
 * (DBLayer) went wrong.
 * 
 * @author kaimu
 * @author Tomas Kovarik (tkovarik@gmail.com)
 * @since 2006-04-29
 *
 */
public class DBLayerException extends PlantloreException {
	
	private static final long serialVersionUID = 2006060411002L;
	
        /** Error code */
        private int errorCode = ERROR_UNSPECIFIED;
        /** Additional error info */
        private String errorInfo;
    
        // ================= ERROR CODES ===============
        /** It was not possible to detect the precise reason why the operation failed. */
        public static final int ERROR_UNSPECIFIED = 0;
        /** Database configuration cannot be loaded properly */
        public static final int ERROR_LOAD_CONFIG = 1;
        /** Connection to the database failed or no connection available */
        public static final int ERROR_CONNECT = 2;
        /** Login and/or password do not match any user record */
        public static final int ERROR_LOGIN = 3;
        /** The username is not recognized - the Username doesn't exist. */
        public static final int ERROR_USERNAME = 31;
        /** The password is incorrect or contains illegal characters. */
        public static final int ERROR_PASSWORD = 32;
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
        public static final int ERROR_MAX_CONNECTIONS = 16;  
        /** CREATE DATABASE statement failed or unable to create tables/users */
        public static final int ERROR_CREATEDB = 17;
        /** Some other error */        
        public static final int ERROR_OTHER = 20;
        // ==============================================
        
        
           
        /** Create new DBLayerException with an error message */
        public DBLayerException(String message) { super(message); }
        
        
        public DBLayerException(String message, int error) {
        	super(message);
        	setError(error, null);
        }
        
              
        // Better constructor to allow proper exception wrapping.
        public DBLayerException(String message, Throwable originalException) {
        	super(message, originalException);
        	if(originalException instanceof JDBCException) {
                    setError( translateSQLState( ((JDBCException)originalException).getSQLState()), null );
                    // Perform a better error recognition here.
                    // The real reason why the connection was refused is usually wrapped 
                    // in several other exceptions. 
                    if( errorCode == ERROR_CONNECT ) {
                        for(Throwable cause = originalException; cause != null; cause = cause.getCause()) {
                            String info = cause.getMessage();
                            if(info == null)
                                continue;
                            if( info.toLowerCase().contains("password") ) {
                                //logger.info("The particular reason the connection was refused: " + info);
                                errorCode = ERROR_PASSWORD;
                                errorInfo = L10n.getString("DBLayer.Error.Password");
                                return;
                            }
                            else if( info.toLowerCase().contains("username") ) {
                                //logger.info("The particular reason the connection was refused: " + info);
                                errorCode = ERROR_PASSWORD;
                                errorInfo = L10n.getString("DBLayer.Error.Username");
                                return;
                            }
                        }
                    }
                }
                
        }
        
        // Better constructor to allow proper exception wrapping.
        public DBLayerException(String message, int error, Throwable originalException) {
        	super(message, originalException);
        	setError( error, null );
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
            if(errorInfo != null)
            	this.errorInfo = errorInfo;
            else {
            	switch(errorCode) {
            	case ERROR_UNSPECIFIED:
            		this.errorInfo = L10n.getString("DBLayer.Error.Unspecified");
            		break;
            	case ERROR_LOAD_CONFIG:
            		this.errorInfo = L10n.getString("DBLayer.Error.LoadConfig");
            		break;
            	case ERROR_CONNECT:
            		this.errorInfo = L10n.getString("DBLayer.Error.Connect");
            		break;
            	case ERROR_LOGIN:
            		this.errorInfo = L10n.getString("DBLayer.Error.Login");
            		break;
            	case ERROR_USERNAME:
            		this.errorInfo = L10n.getString("DBLayer.Error.Username");
            		break;
            	case ERROR_PASSWORD:
            		this.errorInfo = L10n.getString("DBLayer.Error.Password");
            		break;
            	case ERROR_SAVE:
            		this.errorInfo = L10n.getString("DBLayer.Error.Save");
            		break;
            	case ERROR_DELETE:
            		this.errorInfo = L10n.getString("DBLayer.Error.Delete");
            		break;
            	case ERROR_UPDATE:
            		this.errorInfo = L10n.getString("DBLayer.Error.Update");
            		break;
            	case ERROR_SELECT:
            		this.errorInfo = L10n.getString("DBLayer.Error.Select");
            		break;
            	case ERROR_LOAD_DATA:
            		this.errorInfo = L10n.getString("DBLayer.Error.LoadData");
            		break;
            	case ERROR_CLOSE:
            		this.errorInfo = L10n.getString("DBLayer.Error.Close");
            		break;
            	case ERROR_RIGHTS:
            		this.errorInfo = L10n.getString("DBLayer.Error.Rights");
            		break;
            	case ERROR_DB:
            		this.errorInfo = L10n.getString("DBLayer.Error.DB");
            		break;
            	case ERROR_TRANSACTION:
            		this.errorInfo = L10n.getString("DBLayer.Error.Transaction");
            		break;
            	case ERROR_RESOURCES:
            		this.errorInfo = L10n.getString("DBLayer.Error.Resources");
            		break;
            	case ERROR_DISK_FULL:
            		this.errorInfo = L10n.getString("DBLayer.Error.DiskFull");
            		break;
            	case ERROR_OUT_OF_MEMORY:
            		this.errorInfo = L10n.getString("DBLayer.Error.OutOfMemory");
            		break;
            	case ERROR_MAX_CONNECTIONS:
            		this.errorInfo = L10n.getString("DBLayer.Error.MaxConnections");
            		break;
            	case ERROR_OTHER:
            		this.errorInfo = L10n.getString("DBLayer.Error.Other");
            		break;
                case ERROR_CREATEDB:
                        this.errorInfo = L10n.getString("DBLayer.Error.CreateDB");
                        break;
            	default:
            		throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
            	}
            }
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
        public int translateSQLState(String sqlstate) {
        	if( sqlstate == null )
        		return ERROR_OTHER;
        	
            String errorClass = sqlstate.substring(0,2);
            String errorDetail = sqlstate.substring(2);
            
            // Connection exception - Connection does not exist, was interrupted or cannot be established
            if (errorClass.equals("08")) {
            	/* POINTLESS :( - these numbers are database specific...
            	if("004".equals(errorDetail))
            		return ERROR_USERNAME;
            	if ("005".equals(errorDetail))
            		return ERROR_PASSWORD;
            	if("W21".equals(errorDetail) || "W48".equals(errorDetail))
            		return ERROR_LOGIN;
            	*/
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
