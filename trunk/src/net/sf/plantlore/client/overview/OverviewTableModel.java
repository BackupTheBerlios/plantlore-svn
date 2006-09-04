/*
 * OverviewTableModel.java
 *
 * Created on 26. leden 2006, 23:38
 *
 */

package net.sf.plantlore.client.overview;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.client.*;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.NearestVillage;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.middleware.SelectQuery;
import org.apache.log4j.Logger;

/** Implements a table model for the main data overview.
 *
 * @author Jakub
 */
public class OverviewTableModel extends AbstractTableModel {
    private Logger logger;
    private static final int COLUMN_COUNT = 24;
    private DBLayer db;
    private String[] columnNames;
    private int[] columnSizes;
    
    private int resultId = 0;
    private int resultsCount = 0;
    private int pageSize = 30;
    private int currentPage = 1;
    private int selectionColumnIndex = -1;
    private int numberColumnIndex = -1;
    
    private ArrayList<Column> columns;
    private Selection selection = new Selection();    
    private Object[][] data;
    
    /** The currently open select query that we need to close
     * when we receive a requirement for loading data from
     * a new select query.
     *
     */
    private SelectQuery oldSelectQuery;
    
    /** Simple mode if true - only first three columns are displayed
     * Extended mode if false - all columns are displayed
     *
     */
    private boolean simple = true;
    
    private int from = 0;
    private int to = 1;
    
    /** Creates a new instance of OverviewTableModel */
    public OverviewTableModel(int pageSize, ArrayList<Column> columns) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        setColumns(columns);
                
        this.pageSize = pageSize;
        resultsCount = 0;
        this.db = db;
    }
        
    private synchronized void loadData() throws DBLayerException, RemoteException {
        if (oldSelectQuery == null) //the query hasn't been created yet
            return;
        
        resultsCount = db.getNumRows(getResultId());
        logger.debug("resultsCount="+resultsCount);
        Object[] records;
        Object[] projArray;
        Object[] row;
        if (resultsCount > 0) {
            to = Math.min(resultsCount-1, from + pageSize - 1);
            logger.debug("to = "+to+" from="+from+" currentPage="+currentPage);
            
            // !!! It is essential to create a local data array, because
            // while we are fetching data from the database someone can still
            // ask the table for it's value at some cell, etc...
            ///!!!
            Object[][] data = new Object[to - from + 1][];
            logger.debug("data.length = "+data.length);
            records = db.more(getResultId(), from, to);            
            logger.debug("records.length = " + records.length);

            for (int i = 0; i < data.length ; i++) {
                projArray = (Object[]) records[i];

                row = new Object[columns.size() + 1]; //we'll store the record id in the last column
                int proj = 0;
                Object occId = projArray[0] == null ? new Column(Column.Type.OCCURRENCE_ID).getDefaultNullValue() : projArray[0];
                Object value = null;
                for (int j = 0; j < columns.size(); j++) {
                    
                    if (columns.get(j).type.equals(Column.Type.SELECTION)) {
                        row[j] = selection.contains((Integer) occId);
                    } else 
                    if (columns.get(j).type.equals((Column.Type.NUMBER))) {
                        row[j] = from + i + 1;
                    } else {
                        value = projArray[proj] == null ? columns.get(j).getDefaultNullValue() : projArray[proj];
                        if (columns.get(j).type.equals(Column.Type.OCCURRENCE_ID))
                            row[row.length-1] = value;    
                        row[j] = value;
                        proj++;
                    }
                }// for j                
                data[i] = row;
            }//for i
            this.data = data;
        } else
            this.data = null;
    }
            
    public Integer getOccurrence(int i) throws DBLayerException, RemoteException {
        Object[] obj = db.more(getResultId(), i, i);
        Object[] pa = (Object[]) obj[0];
        return (Integer) pa[0];
    }
    
    public Object[] getRow(int i) {
        if (data != null)
            return data[i];
        else
            return null;
    }
    
    public int getRowCount() {
        if (data != null)
            return data.length;
        else
            return 0;
    }
    
    public int getColumnCount() {
        return columns.size()-1; //the first column is Occurrence.Id, just for our internal purposes
    }
    
    public Object getValueAt(int row, int col) {
        if (data != null)
            return data[row][col+1]; //col+1 because we need to skip the occurrence.id
        else
            return null;
    }
    
    public Class getColumnClass(int c) {
        return columns.get(c+1).getColumnClass(); //c+1 --> skip the occurrence.id
    }

    public String getColumnName(int c){
        return columns.get(c+1).getL10nName(); //c+1 --> skip the occurrence.id
    }
    
    /* nepouziva se
     * primo v overview nelze editovat
     */
    public void setValueAt(Object value, int row, int column) {
        data[row][column + 1] = value; //column+1 --> skip the occurrence.id
        if (column == selectionColumnIndex)
            //displayed number of record starts from 1 --> we have to subtract 1 coz ArrayList is indexed from 0
            if ((Boolean)value)
                selection.add((Integer) data[row][data[row].length-1]);
            else
                selection.remove((Integer) data[row][data[row].length-1]);
            
        //recordsArray.get((Integer)data[row][1]-1).selected = (Boolean)value;
        //repaint view - with new value
        this.fireTableCellUpdated(row, column);
    }
    
    public boolean isCellEditable(int row, int column) {
        if (column == selectionColumnIndex) {
            return true;
        }
        return false;
    }
    
    public int getColumnSize(int col) {
        //return columnSizes[col];
        return columns.get(col+1).getPreferredSize();//col+1 --> skip the occurrence.id
    }
    
    public boolean isSimple() {
        return simple;
    }
    
    public void setSimple(boolean simple) {
        this.simple = simple;
    }
    
    public void selectAll() {
        for (int i = 0; i < data.length; i++) {
            setValueAt(true,i,selectionColumnIndex);
        }
    }
    
    public void selectNone() {
        for (int i = 0; i < data.length; i++) {
            setValueAt(false, i, selectionColumnIndex);
        }
    }
    
    public void invertSelected() {
        for (int i = 0; i < data.length; i++) {
            if ((Boolean)data[i][selectionColumnIndex+1] == true) //+1 --> skip the occurrence.id, the selectionColumnIndex is from the JTable's point of view
                setValueAt(false, i, selectionColumnIndex);
            else
                setValueAt(true, i, selectionColumnIndex);
        }
    }
    
    public void invertSelected(int row) {
        setValueAt(!(Boolean)getValueAt(row, selectionColumnIndex), row, selectionColumnIndex);
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) throws DBLayerException, RemoteException {
        if (pageSize <= 0) {
            data = null;
            this.pageSize = 0;
            fireTableDataChanged();
            return;
        }
        this.pageSize = pageSize;
        
        from = (from / pageSize) * pageSize;
        if (from + pageSize > resultsCount)
            from = resultsCount - (resultsCount % pageSize);
        if (from < 0) //pageSize is bigger than the number of results
            from = 0;
        
        currentPage = from / pageSize + 1;
        
        loadData();
        fireTableDataChanged();
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean nextPage() throws DBLayerException, RemoteException 
    {
        int lastPage = getPagesCount();
        logger.debug("lastPage = " + lastPage);
        if (currentPage < lastPage)
        {
            currentPage++;  
            from = from + pageSize;
            loadData(); //load data with new parameter currentPage
            fireTableDataChanged(); //let the table compoment know it should redraw itself
            logger.debug("currentPage = "+ currentPage);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean prevPage() throws DBLayerException, RemoteException
    {
        if (currentPage > 1)
        {
            currentPage--;
            from = from - pageSize;
            loadData(); //load data with new parameter currentPage
            fireTableDataChanged(); //let the table compoment know it should redraw itself
            logger.debug("currentPage = "+ currentPage);
            return true;
        } else {
            return false;
        }
    }
    
    public int getResultId() {
        logger.debug("returning resultid "+resultId);
        return resultId;
    }

    public void setResultId(int resultId, SelectQuery sq) throws RemoteException, DBLayerException {
        if (oldSelectQuery != null)
            db.closeQuery(oldSelectQuery);
        
        oldSelectQuery = sq;
        logger.debug("Setting resultid to "+resultId);
        this.resultId = resultId;
        from = 0;
        currentPage = 1;
        loadData();
        fireTableDataChanged(); //let the table compoment know it should redraw itself
        //fireTableStructureChanged();
    }

    public int getResultsCount() {
        return resultsCount;
    }
    
    public void setResultsCount(int resultsCount) {
        this.resultsCount = resultsCount;
    }
    
    public int getPagesCount() 
    {
        return ((Number)Math.ceil(resultsCount / (pageSize*1.0))).intValue();
    }

    public ArrayList<Column> getColumns() {
        return (ArrayList<Column>) columns.clone();
    }

    public void setColumns(ArrayList<Column> columns) {
        logger.debug("Setting new overview columns.");
        selectionColumnIndex = columns.indexOf(new Column(Column.Type.SELECTION)) - 1; // we don't display the first column which is always Occurrence.ID
                                                                                       // so the index as JTable sees it is -1
        
        numberColumnIndex = columns.indexOf(new Column(Column.Type.NUMBER));
        this.columns = columns;
    }
    
    public Selection getSelection() {
        return selection.clone();
    }
    
    public Integer getOccurrenceId(int row) {
        if (data != null)
            return (Integer)data[row][data[row].length-1];
        else
            return -1;
    }
    
    public Integer getResultNumber(int row) {
        if (data != null) 
//            return ((Integer)data[row][numberColumnIndex])-1; //displayed numbers are indexed from 1
            return from + row; //we do not depend on data array which is crucial because the Number column doesn't have to be between the displayed/projected columns
        else
            return -1;        
    }
    
    public void setDatabase(DBLayer database) {
        this.db = database;
        if (database == null) {//the user logged out
            reset();
        } else {
            logger.debug("OverviewTableModel: database set.");
        }
    }

    public void reset() {
        data = null;
        resultsCount = 0;
        oldSelectQuery = null; //otherwise after login we would try to close the query in setResultId() which would cause an exception
        fireTableDataChanged();
        logger.debug("OverviewTableModel: RESET.");        
    }
    
    public int getSelectionColumnIndex() {
        return selectionColumnIndex;
    }

    public void clearSelection() {
        selection.clear();
    }
    
    public void clear() {
        data = null;
        fireTableDataChanged();
    }
}

