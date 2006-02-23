package net.sf.plantlore.client.history;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;

/** 
 *
 * @author Lada
 */
public class HistoryTableModel extends AbstractTableModel
{
    private String[] columnNames = {"Mark", "Date", "User", "Item", "Old value", "New value"};
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

    /** Creates a new instance of HistoryTableModel with data*/
    public HistoryTableModel(Object[][] tableData)
    {
    	data = tableData;
    }
    
    //povoleni editace sloupce pro vyber radku
    public boolean isCellEditable(int row, int column) 
    {
    	if (column == MARK )
    	{
    		return true;
    	}
    	return false;
    }
    
    //pro zmenu hodnoty v tabulce
    public void setValueAt(Object value, int row, int column)
    {
        data[row][column] = value;
    }
 
    //vyzadam si hodnotu dane polozky
    public Object getValueAt(int row, int column)
    {
        return data[row][column];
    }    
    
    //vyzadam si pocet zobrazenych radku
    public int getRowCount()
    {
        return data.length;
    }
    
    public int getColumnCount()
    {
        return columnNames.length;
    }

    public String getColumnName(int column){
        return columnNames[column];
    }
    
    //nastaveni datoveho typu pro jednotlive sloupce
    //implicitne je zobrazovan string
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
