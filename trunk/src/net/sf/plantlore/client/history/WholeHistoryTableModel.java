/*
 * WholeHistoryTableModel.java
 *
 * Created on 14. duben 2006, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.history;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Implements a table model for the WholeHistory.
 *
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class WholeHistoryTableModel extends AbstractTableModel {
    
	private static final long serialVersionUID = 5203388116532684569L;
	/** Instance of a logger */
    private Logger logger;
    /** Model of the History MVC */
    private History model;
    /** Results of a search query for displaying */
    private ArrayList<HistoryRecord> editHistoryDataList;    
    /** Names of the columns */
    private String[] columnNames;
    /** Size of the columns */
    private int[] columnSizes;
    /** Data values displayed in the table*/
    private Object[][] data;

    /** Constants used for identification of columns of table */
    public final static int DATE = 0;
    public final static int OPERATION = 1; 
    public final static int USER = 2;
    public final static int ITEM = 3;
    public final static int OLD_VALUE = 4;
    public final static int NEW_VALUE = 5;
    
    /** 
     *  Creates a new instance of WholeHistoryTableModel 
     *  @param model model of the WholeHisotry MVC
     */
    public WholeHistoryTableModel(History model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();     	
    }  
   
    /**
     * Init names of columns.
     */
    private void initColumns() {
        columnNames = new String[6];                      
        columnNames[0] = L10n.getString("History.ColumnDate"); 
        columnNames[1] = L10n.getString("History.ColumnOperation");  
        columnNames[2] = L10n.getString("History.ColumnUser");        
        columnNames[3] = L10n.getString("History.ColumnItem");        
        columnNames[4] = L10n.getString("History.ColumnOldValue");       
        columnNames[5] = L10n.getString("History.ColumnNewValue");            
    }   
    
    /**
     * Init size of columns.    
     */
    private void initColumnSize() {
        columnSizes = new int[6];
        columnSizes[0] = 30;
        columnSizes[1] = 100;
        columnSizes[2] = 100;
        columnSizes[3] = 100;
        columnSizes[4] = 100;
        columnSizes[5] = 100;
    }
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	logger.debug("WholeHistoryTableModel: Init data for displaying.");
    	// init size of column
    	initColumnSize();    	  	
    	editHistoryDataList = model.getHistoryDataList();
    	if (editHistoryDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(editHistoryDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;
    	//load data for view
        Object[][] editHistoryData = new Object[countRow][6];   
    	for (int i=firstRow-1; i < countResult; i++) {         		
            String columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
            String tableName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getTableName();
            String item = L10n.getString(tableName+"."+columnName);             
            Date when = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWhen();
            
    	    editHistoryData[ii][0] = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(when);     	    
            editHistoryData[ii][1] = L10n.getString( "History.Operation"+((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOperation());
    	    editHistoryData[ii][2] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWho().getWholeName();    	   
    	    editHistoryData[ii][3] = item;
    	    logger.debug("WWWWWWWWWW: "+ item);
    	    editHistoryData[ii][4] = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
    	    editHistoryData[ii][5] = ((HistoryRecord)editHistoryDataList.get(i)).getNewValue();
    	    ii++;
    	}      	    	
    	this.data = editHistoryData;    	
    }    
    
    /**
     * Gets the value of the given cell.
     * @param row index of row
     * @param column index of column
     */
    public Object getValueAt(int row, int column)
    {
        return data[row][column];
    }    
    
    /** 
     * Gets number of rows in the actual HistoryTableModel
     * @return the number of rows in the HistoryTableModel.
     */
    public int getRowCount()
    {
        return data.length;
    }        

   /**   
     * Gets number of columns in the actual HistoryTableModel
     * @return the number of columns in the HistoryTableModel.
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }
    
      /**
     * Gets the name of the specified column
     * @param column index of column
     * @return the name of the specified column.
     */
    public String getColumnName(int column){
        return columnNames[column];
    } 
    
    /**
     * Gets right Class Integer Object for OPERATION column, Date Object in the DATE column and String Object in other columns. 
     * @param column index of column
     * @return the Class for Object instances in the specified column.
     */   
	public Class getColumnClass(int column) {
    	switch (column) {
            case 0: return DateFormat.class;
            case 1: return Integer.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return String.class;
            default: return String.class;
        }
    }
}
