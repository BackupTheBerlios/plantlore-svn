/*
 * OverviewTableModel.java
 *
 * Created on 26. leden 2006, 23:38
 *
 */

package net.sf.plantlore.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;
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
    
    private int resultid = 0;
    private int resultsCount = 0;
    private int pageSize = 30;
    private int currentPage = 1;
    private ArrayList<Record> recordsArray = new ArrayList<Record>();
    private HashMap<Integer, Record> resultsMap = new HashMap<Integer, Record>();
    
    class Record {
        public Record(int id, boolean selected, int number) {
            this.id = id;
            this.selected = selected;
            this.number = number;
        }
        public int id;
        public boolean selected;
        public int number;
        
        public boolean equals(Object o) {
            if (!(o instanceof Record))
                return false;
            Record r = (Record)o;
            return r.id == this.id;
        }
    }
    
    private Object[][] data = {
        {true, "Pampeliska", "neznamy", "Praha", new Integer(1995), "phy", "cechy", new Boolean(false), new Integer(10), new Integer(12), new Integer(-5), "","","","","","","","","","","",""},
        {false,"Hermanek", "Jakub", "Zelezny Brod", new Integer(1990), "phy", "cechy", new Boolean(true), new Integer(10), new Integer(12), new Integer(-5), "","","","","","","","","","","",""}
    };
    
    /** Simple mode if true - only first three columns are displayed
     * Extended mode if false - all columns are displayed
     *
     */
    private boolean simple = true;
    
    private int from = 1;
    private int to = 1;
    
    /** Creates a new instance of OverviewTableModel */
    public OverviewTableModel(DBLayer db, int pageSize) throws RemoteException, DBLayerException {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        init();
        
        this.pageSize = pageSize;
        resultsCount = 0;
        this.db = db;
        SelectQuery sq = db.createQuery(AuthorOccurrence.class);
        //FIXME:
        try {
            setResultid(db.executeQuery(sq));
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        loadData();
    }
    
    private void init() {
        columnNames = new String[COLUMN_COUNT];
        columnSizes = new int[COLUMN_COUNT];
        columnNames[0] = L10n.getString("overviewColX");
        columnSizes[0] = 30;
        columnNames[1] = L10n.getString("overviewColResultNumber");
        columnSizes[1] = 50;
        columnNames[2] = L10n.getString("overviewColName");
        columnSizes[2] = 100;
        columnNames[3] = L10n.getString("overviewColAuthor");
        columnSizes[3] = 100;
        columnNames[4] = L10n.getString("overviewColVillage");
        columnSizes[4] = 100;
        columnNames[5] = L10n.getString("overviewColPlace");
        columnSizes[5] = 150;
        columnNames[6] = L10n.getString("overviewColYear");
        columnSizes[6] = 50;
        columnNames[7] = L10n.getString("overviewColTerritory");
        columnSizes[7] = 100;
        columnNames[8] = L10n.getString("overviewColPhyt");
        columnSizes[8] = 100;
        columnNames[9] = L10n.getString("overviewColPhytCode");
        columnSizes[9] = 50;
        columnNames[10] = L10n.getString("overviewColCountry");
        columnSizes[10] = 100;
        columnNames[11] = L10n.getString("overviewColQuadrant");
        columnSizes[11] = 50;
        columnNames[12] = L10n.getString("overviewColOccNote");
        columnSizes[12] = 150;
        columnNames[13] = L10n.getString("overviewColLocNote");
        columnSizes[13] = 150;
        columnNames[14] = L10n.getString("overviewColAltitude");
        columnSizes[14] = 50;
        columnNames[15] = L10n.getString("overviewColLongitude");
        columnSizes[15] = 50;
        columnNames[16] = L10n.getString("overviewColLatitude");
        columnSizes[16] = 50;
        columnNames[17] = L10n.getString("overviewColSource");
        columnSizes[17] = 100;
        columnNames[18] = L10n.getString("overviewColPublication");
        columnSizes[18] = 100;
        columnNames[19] = L10n.getString("overviewColHerbarium");
        columnSizes[19] = 80;
        columnNames[20] = L10n.getString("overviewColMetadata");
        columnSizes[20] = 100;
        columnNames[21] = L10n.getString("overviewColMonth");
        columnSizes[21] = 50;
        columnNames[22] = L10n.getString("overviewColDay");
        columnSizes[22] = 50;
        columnNames[23] = L10n.getString("overviewColTime");
        columnSizes[23] = 100;
    }
    
    /**
     * Expects from, pageSize, currentPage and resultid variables to be set appropriately.
     */
    private void loadData() throws DBLayerException, RemoteException
    {
        Object[] row;
        AuthorOccurrence result;
        Plant plant;
        Object[] resultObj, records;
        resultsCount = db.getNumRows(getResultid());
        logger.debug("resultsCount = "+resultsCount);
        to = Math.min(resultsCount, from + pageSize - 1);
        logger.debug("to = "+to+" from="+from+" currentPage="+currentPage);
        data = new Object[to - from + 1][];
        logger.debug("data.length = "+data.length);
        records = db.more(getResultid(), from, to);
        logger.debug("records.length = " + records.length);

        for (int i = 1; i <= to - from + 1 ; i++) {
            resultObj = (Object[])records[i-1];
            result = (AuthorOccurrence)resultObj[0];
            Record r = new Record(result.getId(), false, from + i - 1);
            if (!recordsArray.contains(r))
                recordsArray.add(r);
            else 
                r = recordsArray.get(from+i-2);//array starts from 0 whereas records are numbered from 1
                
            row = new Object[COLUMN_COUNT];
            row[0] = r.selected;
            row[1] = r.number;
            row[2] = result.getOccurrence().getPlant().getTaxon();
            row[3] = result.getAuthor().getWholeName();
            row[4] = result.getOccurrence().getHabitat().getNearestVillage().getName();
            row[5] = result.getOccurrence().getHabitat().getDescription();
            row[6] = result.getOccurrence().getYearCollected();
            row[7] = result.getOccurrence().getHabitat().getTerritory().getName();
            row[8] = result.getOccurrence().getHabitat().getPhytochorion().getName();
            row[9] = result.getOccurrence().getHabitat().getPhytochorion().getCode();
            row[10] = result.getOccurrence().getHabitat().getCountry();
            row[11] = result.getOccurrence().getHabitat().getQuadrant();
            row[12] = result.getOccurrence().getNote();
            row[13] = result.getOccurrence().getHabitat().getNote();
            row[14] = result.getOccurrence().getHabitat().getAltitude();
            row[15] = result.getOccurrence().getHabitat().getLongitude();
            row[16] = result.getOccurrence().getHabitat().getLatitude();
            row[17] = result.getOccurrence().getDataSource();
            row[18] = result.getOccurrence().getPublication().getCollectionName();
            row[19] = result.getOccurrence().getHerbarium();
            row[20] = result.getOccurrence().getMetadata().getDataSetTitle();
            row[21] = result.getOccurrence().getMonthCollected();
            row[22] = result.getOccurrence().getDayCollected();
            row[23] = result.getOccurrence().getTimeCollected();
            data[i-1] = row;
        }//i        
    }
    
    public AuthorOccurrence getRecord(int row) {
        AuthorOccurrence result = null;
        Object[] resultObj, records;
        //FIXME:
        try {
            records = db.more(getResultid(), from, from + row);
            result = (AuthorOccurrence)((Object[])records[0])[0];
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
    public int getRowCount() {
        return data.length;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Object getValueAt(int i, int i0) {
        return data[i][i0];
    }
    
    public Class getColumnClass(int c) {
        return getValueAt(0,c).getClass();
    }
    
    public String getColumnName(int c){
        return columnNames[c];
    }
    
    public void setValueAt(Object value, int row, int column) {
        data[row][column] = value;
        if (column == 0)
            //displayed number of record starts from 1 --> we have to subtract 1 coz ArrayList is indexed from 0
            recordsArray.get((Integer)data[row][1]-1).selected = (Boolean)value;
        //repaint view - with new value
        this.fireTableCellUpdated(row, column);
    }
    
    public boolean isCellEditable(int row, int column) {
        if (column == 0 ) {
            return true;
        }
        return false;
    }
    
    public int getColumnSize(int col) {
        return columnSizes[col];
    }
    
    public boolean isSimple() {
        return simple;
    }
    
    public void setSimple(boolean simple) {
        this.simple = simple;
    }
    
    public void selectAll() {
        for (int i = 0; i < data.length; i++) {
            setValueAt(true,i,0);
        }
    }
    
    public void selectNone() {
        for (int i = 0; i < data.length; i++) {
            setValueAt(false, i, 0);
        }
    }
    
    public void invertSelected() {
        for (int i = 0; i < data.length; i++) {
            if ((Boolean)data[i][0] == true)
                setValueAt(false, i, 0);
            else
                setValueAt(true, i, 0);
        }
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (from + pageSize > resultsCount)
            from = resultsCount - pageSize + 1;
        if (from < 1) //pageSize was bigger than the number of results
            from = 1;
        
        currentPage = from / pageSize + 1;
        //FIXME: - taky musi umet prepocitat from a to
        try {
            loadData();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
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
    
    public int getResultid() {
        return resultid;
    }

    public void setResultid(int resultid) {
        this.resultid = resultid;
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
}
