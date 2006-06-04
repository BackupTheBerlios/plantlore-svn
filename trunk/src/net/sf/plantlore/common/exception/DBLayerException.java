package net.sf.plantlore.common.exception;

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
        /** Some other error */
        public static final int ERROR_OTHER = 20;
        // ==============================================
        /** Create new DBLayerException without an error message */
        public DBLayerException() { super(); }
	/** Create new DBLayerException with an error message */
	public DBLayerException(String message) { super(message); }
        
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
}
