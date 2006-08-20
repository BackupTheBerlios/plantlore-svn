/*
 * OccurrenceTableModel.java
 *
 * Created on 16. srpen 2006, 18:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import org.apache.log4j.Logger;

/** Data model for the table of occurrences for a given habitat in AddEdit dialog.
 *
 * @author fraktalek
 */
public class OccurrenceTableModel extends AbstractTableModel {
    public final int MAX_TABLE_SIZE = 500;
    private final int COLUMN_COUNT = 3;
    private final int COLUMN_TAXON = 0;
    private final int COLUMN_AUTHOR = 1;
    private final int COLUMN_DATE = 2;
    private final String[] COLUMN_NAMES = { L10n.getString("Overview.AddEdit.TaxonColumn"),
                                           L10n.getString("Overview.AddEdit.AuthorColumn"),
                                           L10n.getString("Overview.AddEdit.DateColumn") };
    private DBLayer dblayer;
    private Logger logger = Logger.getLogger(OccurrenceTableModel.class.getPackage().getName());
    private Object[] data;
    private DBLayerUtils dlu;
    
    /** Creates a new instance of OccurrenceTableModel */
    public OccurrenceTableModel() {
    }

    public int getRowCount() {
        return data == null ? 0 : data.length;
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public String getColumnName(int c) {
        if (c < COLUMN_COUNT)
            return COLUMN_NAMES[c];
        else
            return "";
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data == null || columnIndex >= COLUMN_COUNT || rowIndex >= data.length)
            return null;
        
        return ((Row)data[rowIndex]).get(columnIndex);
    }
    
    /** Clears the table model so that it contains no data.
     *
     */
    public void clear() {
        data = null;
        fireTableDataChanged();
    }
    
    /** Loads data for given habitat.
     *
     * Occurrences are loaded in descending order ordered by date.
     *
     * @param habitatId the habitat for that we should load occurrences
     * @throws DBLayerException
     * @throws RemoteException
     *
     * @return positive number or zero ... the number of results if everything was Ok
     * @return negative number ... the habitat contained more than <code>MAX_TABLE_SIZE</code> occurrences. Loaded only the newest MAX_TABLE_SIZE occurrences.
     */
    public int load(Integer habitatId) throws DBLayerException, RemoteException {
        boolean occurrenceCountOverflow = false;
        logger.info("OccurenceTableModel: loading occurrences for habitat "+habitatId);
        SelectQuery sq = dblayer.createQuery(AuthorOccurrence.class);
        sq.createAlias(AuthorOccurrence.AUTHOR,"author");
        sq.createAlias(AuthorOccurrence.OCCURRENCE,"occ");                
        sq.createAlias("occ."+Occurrence.PLANT,"plant");
        sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.HABITAT,null,dlu.getObjectFor(habitatId, Habitat.class), null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.DELETED,null,0,null);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"plant."+Plant.TAXON);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"author."+Author.WHOLENAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.ISODATETIMEBEGIN);   
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.ID);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, "occ."+Occurrence.ID);
        int resultid = dblayer.executeQuery(sq);
        int resultCount = dblayer.getNumRows(resultid);
        
        if (resultCount <= 0) {
            data = null;
            return 0;
        }            
        
        if (resultCount > MAX_TABLE_SIZE) {
            logger.warn("OccurrenceTableModel: habitat id "+habitatId+" contains more than "+MAX_TABLE_SIZE+" occurrences. Showing only the last "+MAX_TABLE_SIZE);
            resultCount = MAX_TABLE_SIZE;
            occurrenceCountOverflow = true;
        }
        
        ArrayList<Row> dataTmp = new ArrayList(); //we can't simply use the data ArrayList, because the data structure must be consistent all the time we're loading
                                             // in case that other threads want to read the data.
                
        Object[] records = dblayer.more(resultid, 0, resultCount - 1);
        Object[] projArray;
        Integer id = -1;
        Row row = null;
        for (Object record : records) {
            projArray = (Object[])record;
            if (!id.equals(projArray[3])) {
                row = new Row(projArray[0], projArray[1], projArray[2]);
                dataTmp.add(row);
                id = (Integer) projArray[3];
            } else {
                row.author = row.author + ", " + (String)projArray[1];
            }
        }
        
        Object[] tmp = (Object[]) dataTmp.toArray();
        Arrays.sort(tmp);
                
        data = tmp;
        
        fireTableDataChanged();
        
        if (occurrenceCountOverflow)
            return -1;
        else
            return resultCount;
    }
    
    public void setDBLayer(DBLayer dblayer) {
        this.dblayer = dblayer;
        dlu = new DBLayerUtils(dblayer);
    }
    
    
    class Row implements Comparable {
        String taxon;
        String author;
        Date date;
        
        public Row(String taxon, String author, Date date) {
            this.taxon = taxon;
            this.author = author;
            this.date = date;
        }
        
        public Row(Object taxon, Object author, Object date) {
            this.taxon = (String) taxon;
            this.author = (String) author;
            this.date = (Date) date;
        }
        
        public Object get(int index) {
            switch (index) {
                case COLUMN_TAXON:
                    return taxon;
                case COLUMN_AUTHOR:
                    return author;
                case COLUMN_DATE:
                    return date;
                default:
                    return null;
            }
        }

        public int compareTo(Object o) {
            if (!(o instanceof Row))
                throw new ClassCastException("Instance of Row can only be compared to an instance of Row.");     
            return date.compareTo(((Row)o).date);
        }
    }
}
