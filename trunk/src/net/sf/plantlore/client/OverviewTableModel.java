/*
 * OverviewTableModel.java
 *
 * Created on 26. leden 2006, 23:38
 *
 */

package net.sf.plantlore.client;

import javax.swing.table.AbstractTableModel;

/** Implements a table model for the main data overview.
 *
 * @author Jakub
 */
public class OverviewTableModel extends AbstractTableModel
{
    private String[] columnNames = { "Name", "Author", "Location", "Year", "Habitat",
    "Phytochorion", "Region", "Quadrant", "Altitude", "Longitude", "Latitude" };
    
    private Object[][] data = {
        {"Pampeliska", "neznamy", "Praha", new Integer(1995), "none", "phy", "cechy", new Boolean(false), new Integer(10), new Integer(12), new Integer(-5)},
        {"Hermanek", "Jakub", "Zelezny Brod", new Integer(1990), "none", "phy", "cechy", new Boolean(true), new Integer(10), new Integer(12), new Integer(-5)}        
    };
    
    /** Simple mode if true - only first three columns are displayed
     * Extended mode if false - all columns are displayed
     *
     */
    private boolean simple = true;
    
    /** Creates a new instance of OverviewTableModel */
    public OverviewTableModel()
    {
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
    
    
    public boolean isSimple()
    {
        return simple;
    }
    
    public void setSimple(boolean simple)
    {
        this.simple = simple;
    }
}
