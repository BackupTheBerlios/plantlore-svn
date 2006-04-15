/*
 * WholeHistoryTableModel.java
 *
 * Created on 14. duben 2006, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.history;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class WholeHistoryTableModel extends AbstractTableModel {
    
    //Logger
    private Logger logger;
    // History model
    private WholeHistory model; 
    private ArrayList<HistoryRecord> editHistoryDataList;
    
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    public final static int DATE = 0;
    public final static int OPERATION = 1; 
    public final static int USER = 2;
    public final static int ITEM = 3;
    public final static int OLD_VALUE = 4;
    public final static int NEW_VALUE = 5;
    
    /** Creates a new instance of WholeHistoryTableModel */
    public WholeHistoryTableModel(WholeHistory model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    private void initColumns() {
        columnNames = new String[6];                      
        columnNames[0] = L10n.getString("historyColDate"); 
        columnNames[1] = L10n.getString("historyColOperation");  
        columnNames[2] = L10n.getString("historyColUser");        
        columnNames[3] = L10n.getString("historyColItem");        
        columnNames[4] = L10n.getString("historyColOldValue");       
        columnNames[5] = L10n.getString("historyColNewValue");        
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("WholeHistory - Init data.");
    	
    	editHistoryDataList = model.getEditHistoryDataList();
    	if (editHistoryDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(editHistoryDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;
    	//loud data for view
        Object[][] editHistoryData = new Object[countRow][6];   
    	for (int i=firstRow-1; i < countResult; i++) {      	            
    	    editHistoryData[ii][0] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWhen();
            editHistoryData[ii][1] = L10n.getString( "operation"+((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOperation());
    	    editHistoryData[ii][2] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWho().getWholeName();    	   
    	    editHistoryData[ii][3] = L10n.getString(((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName());
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
    
    
   
}
