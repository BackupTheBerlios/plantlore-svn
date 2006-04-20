package net.sf.plantlore.client.history;

import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.l10n.L10n;

/** 
 * Implements a table model for the history data.
 * @author Lada Oberreiterova
 */
public class HistoryTableModel extends AbstractTableModel
{
    //Logger
    private Logger logger;
    // History model
    private History model; 
    private ArrayList<HistoryRecord> editHistoryDataList;
    private HashSet markListId;
    private ArrayList<Object[]> markItem;
	
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    public final static int MARK = 0;
    public final static int DATE = 1; 
    public final static int USER = 2;
    public final static int ITEM = 3;
    public final static int OLD_VALUE = 4;
    public final static int NEW_VALUE = 5;
    

    /** 
     *  Creates a new instance of HistoryTableModel with the specified data values  
     *  @param model
     */
    public HistoryTableModel(History model)
    {
    	logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    private void initColumns() {
        columnNames = new String[6];        
        columnNames[0] = L10n.getString("historyColX");        
        columnNames[1] = L10n.getString("historyColDate");        
        columnNames[2] = L10n.getString("historyColUser");        
        columnNames[3] = L10n.getString("historyColItem");        
        columnNames[4] = L10n.getString("historyColOldValue");       
        columnNames[5] = L10n.getString("historyColNewValue");        
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("Init data.");
    	
    	editHistoryDataList = model.getEditHistoryDataList();
    	if (editHistoryDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}
    	markItem = model.getMarkItem();    	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(editHistoryDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;
    	boolean mark = false;
    	int ii = 0;  
    	//If was use button "sellect all" we must init list of mark item
    	boolean selectAll = model.getSelectAll();
    	if (selectAll) {
    		initMarkAllItem();
    		mark = true;    		
    	}
    	//loud data for view
        Object[][] editHistoryData = new Object[countRow][6];   
    	for (int i=firstRow-1; i < countResult; i++) {  
    		String item = L10n.getString(((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName());    		
    		if (! selectAll){     			
    			mark = isMark(item, i);
    		}
    		editHistoryData[ii][0] = new Boolean(mark);    		
    	    editHistoryData[ii][1] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWhen();
    	    editHistoryData[ii][2] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWho().getWholeName();    	   
    	    editHistoryData[ii][3] = item;
    	    editHistoryData[ii][4] = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
    	    editHistoryData[ii][5] = ((HistoryRecord)editHistoryDataList.get(i)).getNewValue();
    	    ii++;
    	}      	
    	model.setSelectAll(false);
    	this.data = editHistoryData;    	
    }    
    
    /**
     * Check marking row
     * @param item
     * @return
     */
    public boolean isMark(String item, int itemId) {    	
    	int count = markItem.size();       	
    	for( int i=0; i < count; i++){
    		Object[] itemList = (Object[])(markItem.get(i));
    		String itemFromList = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];
    		logger.debug("IsMark - itemFromList: "+itemFromList + ", item: "+ item + ", maxId: "+ maxId + ", itemId: "+ itemId);
    		if (item.equals(itemFromList)) {
    			if (itemId <= maxId) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
 
    /**
     * 
     * @param row
     * @param value
     */
    public void updateMarkList(String item, int row, boolean value) {    	    	    	
    	int itemId = row + model.getCurrentFirstRow() - 1;
    	boolean contains = false;    	
    	int count = markItem.size();
    	logger.debug("Update markListItem. Count item: "+count);
    	//ArrayList<Object[]> tmpMarkItem = markItem;
    	for( int i=0; i < count; i++){
    		Object[] itemList = (Object[])(markItem.get(i));
    		String itemFromList = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];    
    		logger.debug("MarkItem update - item: "+ itemFromList + ", maxId: " + maxId);
    		if (value) {    		    			    			
    			if (item.equals(itemFromList)) { 
    				contains = true;
					if (itemId > maxId) {
						//Set max id of mark item
						itemList[1] = itemId;
					   	markItem.set(i, itemList);
					}
				}
		    } else {
		    	if (item.equals(itemFromList)) {
		    		contains = true;
		    		if (itemId <= maxId){
		    			//search smaller id of mark item
		    			int newId = searchSmaller(item, itemId);
		    			if (newId != -1) {
		    				itemList[1] = newId;
			    			markItem.set(i,itemList);
			    			logger.debug("Unmark - new itemId is "+ itemList[1].toString());
		    			} else {
		    				markItem.remove(i);
			    			logger.debug("Unmark - remote record has id: "+ itemId);
			    			return;
		    			}
		    	    }else {		    		
		    			markItem.remove(i);
		    			logger.debug("Unmark - remote record has id: "+ itemId);
		    			return;
		    		}
		    	}				 
			}      	
    	}
    	if (! contains) {
    		Object [] itemList = new Object[2];    		
			itemList[0] = item;
			itemList[1] = itemId;
			markItem.add(itemList);
		}       	
    }
 
    /**
     * 
     *
     */
    public void initMarkAllItem() {    	
    	editHistoryDataList = model.getEditHistoryDataList();    	
    	int countResult = editHistoryDataList.size();    	
    	for (int i=0; i < countResult; i++) {      		    		    	
    		String item = L10n.getString(((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName());
    		updateMarkList(item, i, true);
    	} 
    	model.setMarkItem(markItem);
    	updateMarkListId();
    	logger.debug("All records were selected.");    	
    }
    
    /**
     * 
     *
     */
    public void updateMarkListId() {
    	markListId = new HashSet();
    	editHistoryDataList = model.getEditHistoryDataList();
    	markItem = model.getMarkItem();
    	int countResult = editHistoryDataList.size();    	
    	for (int i=0; i < countResult; i++) {  
    		String item = L10n.getString(((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName());    		    		
    		if (isMark(item, i)){
    			markListId.add(i);
    		}
    	}        	
    	model.setMarkListId(markListId);
    	logger.debug("List ID of selected record: "+ markListId.toString());
    }
    
    /** 
     *  Allows to edit of the MARK cell.
     *  @param row index of row
     *  @param column index of column
     *  @return true for MARK cell and false for other cells
     */
    public boolean isCellEditable(int row, int column) 
    {
    	if (column == MARK )
    	{
    		return true;
    	}
    	return false;
    }
       
    /**
     * Sets the value of the given cell.
     * @param value value of cell 
     * @param row index of row
     * @param column index of column
     */
    public void setValueAt(Object value, int row, int column)
    {    	
        data[row][column] = value;
        if (column == 0) {       
        	 String item = (String)getValueAt(row, 3);
        	 updateMarkList(item, row, (Boolean)value);         	 
       	     model.setMarkItem(markItem); 
        	 //update data
        	 initData();
        	 //Update list of selected record
        	 updateMarkListId();
        	 //update view
        	 this.fireTableDataChanged();
        }        
    }

    /**
     * 
     * @param item
     * @param itemId
     * @return
     */
    public int searchSmaller(String item, int itemId) {    	    	
    	int firstRow = model.getCurrentFirstRow();
    	for( int i=itemId-firstRow; i >=0 ; i--){
    		if (getValueAt(i,3).equals(item)){
    			return i+firstRow-1;
    		}    		
    	}
    	return -1;
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
     * Gets right Class for Boolean Object in the MARK column, Date Object in the DATE column and String Object in other columns. 
     * @param column index of column
     * @return the Class for Object instances in the specified column.
     */
    public Class getColumnClass(int column) {
    	Class dataType = super.getColumnClass(column);
    	if (column == MARK){
    		dataType = Boolean.class;
    	}
    	//FIXME: do budoucna by sloupec DATE mel byt typu Date
    	//else if (column == DATE){
    	//	dataType = java.util.Date.class;
    	//}
    	else {
    		dataType = String.class;
    	}    		
        return dataType;
    }
    
}
