package net.sf.plantlore.client.history;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;

import net.sf.plantlore.client.dblayer.result.Result;

/** 
 * Implements a table model for the history data.
 * @author Lada
 */
public class HistoryTableModel extends AbstractTableModel
{
	/** Names of the columns */
    private String[] columnNames = {"Mark", "Date", "User", "Item", "Old value", "New value"};
    /** Data values displayed in the table*/
    private Object[][] data;

    public final static int MARK = 0;
    public final static int DATE = 1; 
    public final static int USER = 2;
    public final static int ITEM = 3;
    public final static int OLD_VALUE = 4;
    public final static int NEW_VALUE = 5;
    
    /** Creates a new instance of HistoryTableModel */
    public HistoryTableModel()
    {    	
    }

    /** 
     *  Creates a new instance of HistoryTableModel with the specified data values  
     *  @param tableData data values 
     */
    public HistoryTableModel(Object[][] tableData)
    {
    	data = tableData;
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
        	if ((Boolean)value){        	    
        	    selectYounger(row, column);        	    
        	} else {        		
        		selectOlder(row, column);
        	}
        }
        //repaint view - with new value
        this.fireTableCellUpdated(row, column);
    }

    public void selectYounger(int row, int column){
    	String item = (String)getValueAt(row, 3);
    	for(int i=0; i < row; i++) {
    		if (item.equals(getValueAt(i,3))) {
    			setValueAt(true,i,0);
    			System.out.print("oznaceno " +i + " \n" );
    		}
    	}
    }
    
    public void selectOlder(int row, int column){
    	String item = (String)getValueAt(row, 3);
    	for(int i=row+1; i < data.length; i++) {
    		if (item.equals(getValueAt(i,3))) {
    			setValueAt(false,i,0);
    			System.out.print("odznaceno " +i + " \n" );
    		}
    	}
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
