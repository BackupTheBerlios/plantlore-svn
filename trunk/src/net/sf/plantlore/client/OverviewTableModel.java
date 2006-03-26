/*
 * OverviewTableModel.java
 *
 * Created on 26. leden 2006, 23:38
 *
 */

package net.sf.plantlore.client;

import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.middleware.SelectQuery;

/** Implements a table model for the main data overview.
 *
 * @author Jakub
 */
public class OverviewTableModel extends AbstractTableModel
{
    private DBLayer db;
    private String[] columnNames = { "X","Name", "Author", "Nearest bigger seat", "Place description", "Year", 
    "Territory", "Territory code","Phytochorion","Phytochorion code","Country","Quadrant",
    "Ocurrence note", "Location note", "Altitude", "Longitude", "Latitude", "Source", "Publication",
    "Herbarium", "Metadata", "Month", "Day", "Time"};
    
    private Object[][] data = {
        {true, "Pampeliska", "neznamy", "Praha", new Integer(1995), "none", "phy", "cechy", new Boolean(false), new Integer(10), new Integer(12), new Integer(-5), "","","","","","","","","","","",""},
        {false,"Hermanek", "Jakub", "Zelezny Brod", new Integer(1990), "none", "phy", "cechy", new Boolean(true), new Integer(10), new Integer(12), new Integer(-5), "","","","","","","","","","","",""}        
    };
    
    /** Simple mode if true - only first three columns are displayed
     * Extended mode if false - all columns are displayed
     *
     */
    private boolean simple = true;
    
    /** Creates a new instance of OverviewTableModel */
    public OverviewTableModel(DBLayer db) {
       /* int resultid = 0;
        this.db = db;
        SelectQuery sq = db.createQuery(Occurrence.class);
        try {
            resultid = db.executeQuery(sq);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        System.out.println("OverviewTableModel: "+db.getNumRows(resultid)+" retrieved.");
        */
    }

    public int getRowCount()
    {
        return data.length;
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public Object getValueAt(int i, int i0)
    {
        return data[i][i0];
    }
    
    public Class getColumnClass(int c) {
        return getValueAt(0,c).getClass();
    }
    
    public String getColumnName(int c){
        return columnNames[c];
    }

    public void setValueAt(Object value, int row, int column)
    {
        data[row][column] = value;
    }
    
    public boolean isCellEditable(int row, int column) 
    {
    	if (column == 0 )
    	{
    		return true;
    	}
    	return false;
    }
    
    public boolean isSimple()
    {
        return simple;
    }
    
    public void setSimple(boolean simple)
    {
        this.simple = simple;
    }
    
    public void selectAll() 
    {
        for (int i = 0; i < data.length; i++) {
            data[i][0] = true;
        }
    }
    
    public void selectNone() 
    {
        for (int i = 0; i < data.length; i++) {
            data[i][0] = false;
        }
    }
    
    public void invertSelected()
    {
        for (int i = 0; i < data.length; i++) {
            if ((Boolean)data[i][0] == true)
                data[i][0] = false;
            else
                data[i][0] = true;
        }        
    }
}
