/*
 * QueryResult.java
 *
 * Created on 14. leden 2006, 23:22
 *
 */

package net.sf.plantlore.client.dblayer.result;

/**
 * Storage class for information about the results of a query.
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 14, 2006
 */
public class QueryResult implements Result {
    /** Unique identification of the result */
    private int resultID;
    /** Number of rows in the result / affected by the operation */
    private int numRows;
    /** Result code of the operation */
    private int operationResult;
    /** Type of the operation (entity which is in the result) */
    private int type;
    
    /**
     * Creates a new instance of QueryResult
     *
     * @param resultID          Unique identification of the result
     * @param numRows           Number of rows in the result / affected by the operation
     * @param operationResult   Result code of the operation
     * @param type              Type of the operation
     */
    public QueryResult(int resultID, int numRows, int operationResult, int type) {
        this.resultID = resultID;
        this.numRows = numRows;
        this.operationResult = operationResult;
        this.type = type;
    }
    
    /**
     * Return unique ID of the result represented by this object
     *
     * @return Unique ID of the result
     */
    public int getResultID() {
        return this.resultID;
    }
    
    /**
     * Return number of rows in the result (either number of selected or affected rows)
     *
     * @return Number of rows in the result
     */
    public int getNumRows() {
        return this.numRows;
    }
    
    /**
     * Return result code of the operation (of the executed query)
     *
     * @return Code of the operation result
     */
    public int getOperationResult() {
        return this.operationResult;
    }
    
    /**
     * Return type of the operation (entity returned by thye query)
     *
     * @return String type of the operation
     */
    public int getType() {
        return this.type;
    }
}
