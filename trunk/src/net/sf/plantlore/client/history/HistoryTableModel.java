package net.sf.plantlore.client.history;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.l10n.L10n;

/** 
 * Implements a table model for the History.
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */

public class HistoryTableModel extends AbstractTableModel
{    
	private static final long serialVersionUID = 2027550749272023845L;
	/** Instance of a logger */
    private Logger logger;
    /** Model of the History MVC */
    private History model; 
    /** Results of a search query for displaying */
    private ArrayList<HistoryRecord> editHistoryDataList;
    /** List of identifier of selected item */
    private HashSet markListId;
    /** List of pairs (Item, identifier of the oldest change of this Item) */
    private ArrayList<Object[]> markItem;	
    /** Names of the columns */
    private String[] columnNames;
    /** Size of the columns */
    private int[] columnSizes;
    /** Data values displayed in the table*/
    private Object[][] data;

    /** Constants used for identification of columns of table */
    public final static int MARK = 0;
    public final static int DATE = 1; 
    public final static int USER = 2;
    public final static int ITEM = 3;
    public final static int OLD_VALUE = 4;
    public final static int NEW_VALUE = 5;
    

    /** 
     *  Creates a new instance of HistoryTableModel  
     *  @param model model of the Hisotry MVC
     */
    public HistoryTableModel(History model)
    {
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
        columnNames[0] = L10n.getString("History.ColumnX");        
        columnNames[1] = L10n.getString("History.ColumnDate");        
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
     * Load data for dislaying. 
     */
    public void initData() {    	
    	logger.debug("HistoryTableModel: Init data for displaying.");
    	// init size of column
    	initColumnSize();
    	// load data
    	editHistoryDataList = model.getHistoryDataList();
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
    	boolean selectAll = model.getSelectAll();
      	if (selectAll) {
      		//If was pressed button SellectAll we must update list of mark item
    		initMarkAllItem(firstRow-1, countResult, true);
    		mark = true;    		
    	}else if (model.getUnselectedAll()) {
    		// If was pressed button UnsellectAll we must update list of mark item
    		initMarkAllItem(firstRow-1, countResult, false);
    	}        
    	//load data for view
        Object[][] editHistoryData = new Object[countRow][6];   
    	for (int i=firstRow-1; i < countResult; i++) { 
                String columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
                String tableName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getTableName();
    		String item = L10n.getString(tableName+"."+columnName);    		
    		if (! selectAll)	
    			mark = isMark(item, i);    		
            editHistoryData[ii][0] = new Boolean(mark);  
            Date when = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWhen();
    	    editHistoryData[ii][1] = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(when); 
    	    editHistoryData[ii][2] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWho().getWholeName();    	   
    	    editHistoryData[ii][3] = item;    	    
    	    editHistoryData[ii][4] = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
    	    editHistoryData[ii][5] = ((HistoryRecord)editHistoryDataList.get(i)).getNewValue();
    	    ii++;
    	}      	
    	model.setSelectAll(false);
    	model.setUnselectedAll(false);
    	this.data = editHistoryData;    	
    }    
    
    /**
     * Check marking rows.
     * @param item string containing description of changed attribute
     * @param itemId identifier of slelected record 
     * @return true if record is selected, in other way false      
     */
    public boolean isMark(String item, int itemId) {    	
    	int count = markItem.size();       	
    	for( int i=0; i < count; i++){
    		Object[] itemList = (Object[])(markItem.get(i));
    		String itemFromList = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];    		
    		if (item.equals(itemFromList)) {
    			if (itemId <= maxId) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
 
    /**
     * Update MarkList containing information about selected ITEMs and identifiers of their oldest change.
     * @param item string containing description of changed attribute 
     * @param row index of row in table of dialog
     * @param value true if record is selected
     */
    public void updateMarkList(String item, int row, boolean value) {    	    	    	
    	int itemId = row + model.getCurrentFirstRow() - 1;
    	boolean contains = false;    	
    	int count = markItem.size();    	  
    	for( int i=0; i < count; i++){
    		Object[] itemList = (Object[])(markItem.get(i));
    		String itemFromList = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];        		
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
			    			//logger.debug("Unmark - new itemId is "+ itemList[1].toString());
		    			} else {
		    				markItem.remove(i);
			    			//logger.debug("Unmark - remote record has id: "+ itemId);
			    			return;
		    			}		    	    
		    	    } else {		    			
		    			//logger.debug("This situation is possible only for unselectedAll.");
		    			return;
		    		}
		    	}				 
			}      	
    	}
    	if (! contains && value) {
    		Object [] itemList = new Object[2];    		
			itemList[0] = item;
			itemList[1] = itemId;
			markItem.add(itemList);
		}       	
    }
 
    /**
     *  Update list of selected item. Call after press SelectAll or UnselectAll buttons.
     *  @param from indentifier of first record in table 
     *  @param to identifier of last record in table
     *  @param isSelect true if SelecteAll button has been pressed, false if UnselectAll button has been pressed
     */
    public void initMarkAllItem(int from, int to, boolean isSelect) {    	    	    	    	    	
    	for (int i=to-1; i >= from; i--) {    
            String columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
            String tableName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getTableName();
    		String item = L10n.getString(tableName+"."+columnName);        		
    		updateMarkList(item, i - from, isSelect);
    	} 
    	model.setMarkItem(markItem);
    	updateMarkListId();    	   	
    }
        
    /**
     * Update list of identifiers of selected record.      
     */    
	public void updateMarkListId() {
    	markListId = new HashSet();
    	editHistoryDataList = model.getHistoryDataList();
    	markItem = model.getMarkItem();
    	int countResult = editHistoryDataList.size();    	
    	for (int i=0; i < countResult; i++) {  
    		String columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
            String tableName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getTableName();
    		String item = L10n.getString(tableName+"."+columnName);    
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
        	 fireTableDataChanged();
        }        
    }

    /**
     * Search younger change of ITEM.
     * @param item string containing description of changed attribute
     * @param itemId identifier of slelected record
     * @return identifier of record containing younger change of ITEM or -1 if there isn1t younger change 
     */
    public int searchSmaller(String item, int itemId) {    	    	    	
    	for( int i=itemId - 1; i >=0 ; i--){
    		String columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
            String tableName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getTableName();
    		String itemData = L10n.getString(tableName+"."+columnName); 
    		logger.debug("itemData: " + itemData + " item:" + item + " itemId: " + itemId );
    		if (itemData.equals(item)){
    			logger.debug("return " + i);
    			return i;    			
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
     * Set size of columns
     * @param columnSizes size of columns
     */ 
    public void setColumnSizes(int[] columnSizes) {
          this.columnSizes=columnSizes;
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
    	switch (column) {
            case 0: return Boolean.class;
            case 1: return DateFormat.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return String.class;
            default: return String.class;
        }
    }
    
}
