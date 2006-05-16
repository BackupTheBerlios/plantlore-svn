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
import net.sf.plantlore.common.record.Village;
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
    
//    private ArrayList<Record999> recordsArray = new ArrayList<Record999>();
    private ArrayList<Column> columns;
    
    private Selection selection = new Selection();
    
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
    
    private Object[][] data;
    
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
        sq.addOrder(PlantloreConstants.DIRECT_ASC, "occ."+Occurrence.ID); //setridit podle roku
        sq.addRestriction(PlantloreConstants.RESTR_NE, "occ."+Occurrence.DELETED, null, 1, null);
        sq.addProjection(PlantloreConstants.PROJ_DISTINCT,"occ."+Occurrence.ID);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"plant."+Plant.TAXON);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"author."+Author.WHOLENAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"vill."+Village.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.YEARCOLLECTED);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"phyt."+Phytochorion.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.DESCRIPTION);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"territory."+Territory.NAME);
        
        //FIXME:
        try {
            setResultId(db.executeQuery(sq));
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }
    
    private void init() {
        columns = new ArrayList<Column>(10);
        columns.add(new Column(Column.Type.OCCURRENCE_ID));
        columns.add(new Column(Column.Type.SELECTION));
        columns.add(new Column(Column.Type.NUMBER));
        columns.add(new Column(Column.Type.PLANT_TAXON));        
        columns.add(new Column(Column.Type.AUTHOR));
        columns.add(new Column(Column.Type.HABITAT_NEAREST_VILLAGE_NAME));
        columns.add(new Column(Column.Type.OCCURRENCE_YEARCOLLECTED));
        columns.add(new Column(Column.Type.PHYTOCHORION_NAME));
        columns.add(new Column(Column.Type.HABITAT_DESCRIPTION));
        columns.add(new Column(Column.Type.TERRITORY_NAME));
        selectionColumnIndex = columns.indexOf(new Column(Column.Type.SELECTION)) - 1; // we don't display the first column which is always Occurrence.ID
                                                                                       // so the index as JTable sees it is -1
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
    
    private void loadData() throws DBLayerException, RemoteException 
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

                row = new Object[columns.size() + 1]; //we'll store the record id in the last column
                int proj = 0;
                Object occId = projArray[0] == null ? new Column(Column.Type.OCCURRENCE_ID).getDefaultNullValue() : projArray[0];
                for (int j = 0; j < columns.size(); j++) {
                    Object value = projArray[proj] == null ? columns.get(j).getDefaultNullValue() : projArray[proj];
                    
                    if (columns.get(j).type.equals(Column.Type.SELECTION)) {
                        row[j] = selection.contains((Integer) occId);
                    } else 
                    if (columns.get(j).type.equals((Column.Type.NUMBER))) {
                        row[j] = from + i + 1;
                    } else {
                        if (columns.get(j).type.equals(Column.Type.OCCURRENCE_ID))
                            row[row.length-1] = value;                        
                        row[j] = value;
                        proj++;
                    }
                }// for j                
                data[i] = row;
            }//for i
        } else
            data = null;
        
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
        return columns.size()-1; //the first column is Occurrence.Id, just for our internal purposes
    }
    
    public Object getValueAt(int row, int col) {
        return data[row][col+1]; //col+1 because we need to skip the occurrence.id
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
            loadData();
            //fireTableDataChanged(); //let the table compoment know it should redraw itself
            fireTableStructureChanged();
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

    public ArrayList<Column> getColumns() {
        return (ArrayList<Column>) columns.clone();
    }

    public void setColumns(ArrayList<Column> columns) {
        logger.debug("Setting new overview columns.");
        selectionColumnIndex = columns.indexOf(new Column(Column.Type.SELECTION)) - 1; // we don't display the first column which is always Occurrence.ID
                                                                                       // so the index as JTable sees it is -1
        
        this.columns = columns;
    }
    
    public Selection getSelection() {
        return selection.clone();
    }
    
    public Integer getOccurrenceId(int row) {
        return (Integer)data[row][data[row].length-1];
    }
}

