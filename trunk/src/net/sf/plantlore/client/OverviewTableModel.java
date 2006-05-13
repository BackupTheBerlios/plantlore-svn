/*
 * OverviewTableModel.java
 *
 * Created on 26. leden 2006, 23:38
 *
 */

package net.sf.plantlore.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.l10n.L10n;
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
    private ArrayList<Record999> recordsArray = new ArrayList<Record999>();
    private HashMap<Integer, Record999> resultsMap = new HashMap<Integer, Record999>();
    private Column[] columns;
    
    class Record999 {
        public Record999(int id, boolean selected, int number) {
            this.id = id;
            this.selected = selected;
            this.number = number;
        }
        public int id;
        public boolean selected;
        public int number;
        
        public boolean equals(Object o) {
            if (!(o instanceof Record999))
                return false;
            Record999 r = (Record999)o;
            return r.id == this.id;
        }
    }
    
    private Object[][] data;/* = {
        {true, "Pampeliska", "neznamy", "Praha", new Integer(1995), "phy", "cechy", new Boolean(false), new Integer(10), new Integer(12), new Integer(-5), "","","","","","","","","","","",""},
        {false,"Hermanek", "Jakub", "Zelezny Brod", new Integer(1990), "phy", "cechy", new Boolean(true), new Integer(10), new Integer(12), new Integer(-5), "","","","","","","","","","","",""}
    };*/
    
    /** Simple mode if true - only first three columns are displayed
     * Extended mode if false - all columns are displayed
     *
     */
    private boolean simple = true;
    
    private int from = 0;
    private int to = 1;
    
    /** Creates a new instance of OverviewTableModel */
    public OverviewTableModel(DBLayer db, int pageSize) throws RemoteException, DBLayerException {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        init();
        
        this.pageSize = pageSize;
        resultsCount = 0;
        this.db = db;
/*        SelectQuery sq = db.createQuery(Occurrence.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Occurrence.YEARCOLLECTED); //setridit podle roku
        sq.addRestriction(PlantloreConstants.RESTR_NE, Occurrence.DELETED, null, 1, null);*/
        SelectQuery sq = db.createQuery(AuthorOccurrence.class);
        sq.createAlias(AuthorOccurrence.AUTHOR,"author");
        sq.createAlias(AuthorOccurrence.OCCURRENCE,"occ");
        sq.createAlias("occ."+Occurrence.HABITAT,"habitat");
        sq.createAlias("occ."+Occurrence.PLANT,"plant");
        sq.createAlias("occ."+Occurrence.PUBLICATION,"publication");
        sq.createAlias("occ."+Occurrence.METADATA,"metadata");
        sq.createAlias("habitat."+Habitat.PHYTOCHORION,"phyt");
        sq.createAlias("habitat."+Habitat.NEARESTVILLAGE,"vill");
        sq.createAlias("habitat."+Habitat.TERRITORY,"territory");
        sq.addOrder(PlantloreConstants.DIRECT_ASC, "occ."+Occurrence.YEARCOLLECTED); //setridit podle roku
        sq.addRestriction(PlantloreConstants.RESTR_NE, "occ."+Occurrence.DELETED, null, 1, null);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"plant."+Plant.TAXON);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"author."+Author.WHOLENAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"vill."+Village.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.YEARCOLLECTED);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"phyt."+Phytochorion.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.DESCRIPTION);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"territory."+Territory.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.ID);
        
        //FIXME:
        try {
            setResultId(db.executeQuery(sq));
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        loadData2();
    }
    
    private void init() {
        columns = new Column[10];
        columns[0] = new Column(Column.Type.SELECTION);
        columns[1] = new Column(Column.Type.NUMBER);
        columns[2] = new Column(Column.Type.PLANT_TAXON);        
        columns[3] = new Column(Column.Type.AUTHOR);
        columns[4] = new Column(Column.Type.HABITAT_NEAREST_VILLAGE_NAME);
        columns[5] = new Column(Column.Type.OCCURRENCE_YEARCOLLECTED);
        columns[6] = new Column(Column.Type.PHYTOCHORION_NAME);
        columns[7] = new Column(Column.Type.HABITAT_DESCRIPTION);
        columns[8] = new Column(Column.Type.TERRITORY_NAME);
        columns[9] = new Column(Column.Type.OCCURRENCE_ID);
/*        columnNames = new String[COLUMN_COUNT];
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
        columnSizes[23] = 100; */
    }
    
    private Pair<String,Integer>[] getAuthorsOf(Occurrence o) {
        Pair<String,Integer>[] authorResults = null;
        //FIXME:
        try {
            SelectQuery sq = db.createQuery(AuthorOccurrence.class);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
            int resultid = db.executeQuery(sq);
            int resultCount = db.getNumRows(resultid);
            authorResults = new Pair[resultCount];
            Object[] results = db.more(resultid, 0, resultCount-1);
            Object[] tmp;
            Author a;
            for (int i = 0; i < resultCount; i++) {
                tmp = (Object[]) results[i];
                a = (Author)((AuthorOccurrence)tmp[0]).getAuthor();
                authorResults[i] = new Pair<String,Integer>(a.getWholeName(), a.getId());
            }
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return authorResults;
    }
    
    private void loadData2() throws DBLayerException, RemoteException 
    {
        resultsCount = db.getNumRows(getResultId());
        System.out.println("resultsCount="+resultsCount);
        Object[] records;
        Object[] projArray;
        Object[] row;
        if (resultsCount > 0) {
            logger.debug("resultsCount = "+resultsCount);
            to = Math.min(resultsCount-1, from + pageSize - 1);
            logger.debug("to = "+to+" from="+from+" currentPage="+currentPage);
            data = new Object[to - from + 1][];
            logger.debug("data.length = "+data.length);
            records = db.more(getResultId(), from, to);
            logger.debug("records.length = " + records.length);

            for (int i = 0; i < data.length ; i++) {
                projArray = (Object[]) records[i];
                Record999 r = new Record999(from + i + 1, false, from + i + 1);//we want to show the user numbers starting from 1 therefor the +1
                if (from + i + 1 > recordsArray.size()) //most probably much faster than to ask recordsArray.contains(r)
                    recordsArray.add(r);
                else 
                    r = recordsArray.get(from+i);

                row = new Object[columns.length + 1]; //we'll store the record id in the last column
                int proj = 0;
                for (int j = 0; j < columns.length; j++) {
                    if (columns[j].type.equals(Column.Type.SELECTION)) {
                        row[j] = r.selected;
                    System.out.println("row["+j+"]="+r.selected);
                    } else 
                    if (columns[j].type.equals((Column.Type.NUMBER))) {
                        row[j] = r.number;
                    System.out.println("row["+j+"]="+r.number);
                    } else {
                        if (columns[j].type.equals(Column.Type.OCCURRENCE_ID))
                            row[row.length-1] = projArray[proj];
                        row[j] = projArray[proj];
                        System.out.println("row["+j+"]="+projArray[proj]);
                        proj++;
                    }
                }// for j                
                data[i] = row;
            }//for i
        }//if resultsCount>0
        
    }
    
    /**
     * Expects from, pageSize, currentPage and resultid variables to be set appropriately.
     */
    private void loadData() throws DBLayerException, RemoteException
    {
        logger.info("Loading data for overview.");
        Object[] row;
        Occurrence result;
        Plant plant;
        Object[] resultObj, records;
        resultsCount = db.getNumRows(getResultId());
        System.out.println("resultsCount="+resultsCount);
        if (resultsCount > 0) {
            logger.debug("resultsCount = "+resultsCount);
            to = Math.min(resultsCount-1, from + pageSize - 1);
            logger.debug("to = "+to+" from="+from+" currentPage="+currentPage);
            data = new Object[to - from + 1][];
            logger.debug("data.length = "+data.length);
            records = db.more(getResultId(), from, to);
            logger.debug("records.length = " + records.length);

            for (int i = 0; i < data.length ; i++) {
                System.out.println("i="+i);
                resultObj = (Object[])records[i];
                result = (Occurrence)resultObj[0];
                Record999 r = new Record999(result.getId(), false, from + i + 1);//we want to show the user numbers starting from 1 therefor the +1
                if (from + i + 1 > recordsArray.size()) //most probably much faster than to ask recordsArray.contains(r)
                    recordsArray.add(r);
                else 
                    r = recordsArray.get(from+i);

                row = new Object[COLUMN_COUNT + 1]; //we'll store the record id in the last column
                row[0] = r.selected;
                row[1] = r.number;
                row[2] = result.getPlant().getTaxon();
                row[3] = ((Pair<String,Integer>)((Object[])getAuthorsOf(result))[0]).getFirst();//occurrence must have at least one author, we'll choose the first one
                row[4] = result.getHabitat().getNearestVillage().getName();
                row[5] = result.getHabitat().getDescription();
                row[6] = result.getYearCollected();
                row[7] = result.getHabitat().getTerritory().getName();
                row[8] = result.getHabitat().getPhytochorion().getName();
                row[9] = result.getHabitat().getPhytochorion().getCode();
                row[10] = result.getHabitat().getCountry();
                row[11] = result.getHabitat().getQuadrant();
                row[12] = result.getNote();
                row[13] = result.getHabitat().getNote();
                row[14] = result.getHabitat().getAltitude();
                row[15] = result.getHabitat().getLongitude();
                row[16] = result.getHabitat().getLatitude();
                row[17] = result.getDataSource();
                row[18] = result.getPublication().getCollectionName();
                row[19] = result.getHerbarium();
                row[20] = result.getMetadata().getDataSetTitle();
                row[21] = result.getMonthCollected();
                row[22] = result.getDayCollected();
                row[23] = result.getTimeCollected();
                row[24] = result; //won't  be displayed, because in getColumnCount we pretend not to have this column
                data[i] = row;
                for (int k=0; k < 24 ; k++)
                    System.out.println("row "+k+"=#"+row[k]+"#");
            }//i        
        }//if resultsCount > 1
    }
    
    //momentalne nepouzita metoda
    public AuthorOccurrence getRecord(int row) {
        AuthorOccurrence result = null;
        Object[] resultObj, records;
        //FIXME:
        try {
            records = db.more(getResultId(), from, from + row);
            result = (AuthorOccurrence)((Object[])records[0])[0];
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
    public Object[] getRow(int i) {
        return data[i];
    }
    
    public int getRowCount() {
        if (data != null)
            return data.length;
        else
            return 0;
    }
    
    public int getColumnCount() {
        return columns.length;
        //return columnNames.length;
    }
    
    public Object getValueAt(int i, int i0) {
        return data[i][i0];
    }
    
    public Class getColumnClass(int c) {
        return columns[c].getColumnClass();
    }
/*    
    public Class getColumnClass(int c) {
        switch (c) {
            case 0:return Boolean.class;
            case 1:return Integer.class;
            case 2:return String.class;
            case 3:return String.class;
            case 4:return String.class;
            case 5:return String.class;
            case 6:return Integer.class;
            case 7:return String.class;
            case 8:return String.class;
            case 9:return String.class;
            case 10:return String.class;
            case 11:return String.class;
            case 12:return String.class;
            case 13:return String.class;
            case 14:return Double.class;
            case 15:return Double.class;
            case 16:return Double.class;
            case 17:return String.class;
            case 18:return String.class;
            case 19:return String.class;
            case 20:return String.class;
            case 21:return Integer.class;
            case 22:return Integer.class;
            case 23:return Date.class;
            case 24:return Occurrence.class;
            default:
                return Object.class;
        }
        //return getValueAt(0,c).getClass();
    }
*/    
    public String getColumnName(int c){
//        return columnNames[c];
        return columns[c].getL10nName();
    }
    
    /* nepouziva se
     * primo v overview nelze editovat
     */
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
        //return columnSizes[col];
        return columns[col].getPreferredSize();
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
            from = resultsCount - pageSize;
        if (from < 0) //pageSize was bigger than the number of results
            from = 0;
        
        currentPage = from / pageSize + 1;
        //FIXME: 
        try {
            loadData2();
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
            loadData2(); //load data with new parameter currentPage
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
            loadData2(); //load data with new parameter currentPage
            fireTableDataChanged(); //let the table compoment know it should redraw itself
            logger.debug("currentPage = "+ currentPage);
            return true;
        } else {
            return false;
        }
    }
    
    public int getResultId() {
        System.out.println("returning resultid "+resultId);
        return resultId;
    }

    public void setResultId(int resultId) {
        logger.debug("Setting resultid to "+resultId);
        this.resultId = resultId;
        from = 0;
        //FIXME
        try {
            loadData2();
            fireTableDataChanged(); //let the table compoment know it should redraw itself
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
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

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }
}
