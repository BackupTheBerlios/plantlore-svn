/*
 * JasperDataSource.java
 *
 * Created on 17. květen 2006, 23:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryColumn;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.middleware.DBLayer;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class JasperDataSource implements JRDataSource {
    enum AuthorInfo {ALLAUTHORS,  
        AUTHOR1_NAME, AUTHOR2_NAME, AUTHOR3_NAME,
        AUTHOR1_EMAIL, AUTHOR2_EMAIL, AUTHOR3_EMAIL,
        AUTHOR1_ROLE, AUTHOR2_ROLE, AUTHOR3_ROLE};
    Logger logger;
    Collection<Integer> selected;
    Iterator<Integer> it;
    DBLayerUtils dlu;
    AuthorOccurrence[] aos;
    Occurrence occurrence;
    int aosForIndex = -1;
    int index = -1;
    
    /** Creates a new instance of JasperDataSource */
    public JasperDataSource(DBLayer database, Selection selection) {        
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        this.selected = selection.values();
        it = selected.iterator();
        dlu = new DBLayerUtils(database);
    }

    public boolean next() throws JRException {
        index++;
        if (it.hasNext()) {
            occurrence = (Occurrence) dlu.getObjectFor(it.next(), Occurrence.class);
            return true;
        } else
            return false;
    }

    public Object getFieldValue(JRField jRField) throws JRException {
        String[] id = jRField.getName().split("\\.");
        Object value;
                
        if (id.length != 2) {
            logger.error("The report is broken. \""+jRField.getName()+"\" is not a valid identifier. It should be \"TABLE_NAME.COLUMN_NAME\"");
            //it's not easy to construct a default null value for a given Class so we'll take the safe way and throw an exception
            throw new JRException("The report is broken. Contains wrong field name \""+jRField.getName()+"\"");
        }
        
        String column = id[1];
        PlantloreConstants.Table table;
        
        try {
            table = PlantloreConstants.Table.valueOf(id[0]);
        } catch (IllegalArgumentException e) {
            logger.error("The report is broken. \""+jRField.getName()+"\" is not a valid identifier. The table name is not valid. The identifier must have form \"TABLE_NAME.COLUMN_NAME\"");
            //it's not easy to construct a default null value for a given Class so we'll take the safe way and throw an exception
            throw new JRException("The report is broken. Contains wrong field name \""+jRField.getName()+"\"");            
        }
        
        if (table.equals(PlantloreConstants.Table.AUTHOROCCURRENCE)) {
            logger.error("Fetching data from table "+table+" is not supported!");
            throw new JRException("Fetching data from table "+table+" is not supported!");
        }

        if (table.equals(PlantloreConstants.Table.AUTHOR)) {
            AuthorInfo ai;
            try {
                ai = AuthorInfo.valueOf(column);
            } catch (IllegalArgumentException e) {
                logger.error("The report is broken. \""+jRField.getName()+"\" is not a valid identifier. The table name is not valid. The identifier must have form \"TABLE_NAME.COLUMN_NAME\"");
                //it's not easy to construct a default null value for a given Class so we'll take the safe way and throw an exception
                throw new JRException("The report is broken. Contains wrong field name \""+jRField.getName()+"\"");                            
            }
            
            if (aos == null || aosForIndex != index) {
                aos = dlu.getAuthorsOf(occurrence);
            }
            if (aos.length < 1) {
                logger.error("Given occurrence doesn't have any live authors in the database. Is it a try to work with a deleted occurrence?");
                return ""; //for authors we return only strings so we can afford to do this
            }
            
            switch (ai) {
                case ALLAUTHORS:
                    StringBuffer authors = new StringBuffer();
                    for (int i = 0; i < aos.length; i++) {
                        authors.append(aos[i].getAuthor().getWholeName());
                        if (i < aos.length-1)
                            authors.append(", ");
                    }
                    return authors.toString();
                case AUTHOR1_NAME:
                    return aos[0].getAuthor().getWholeName();
                case AUTHOR1_EMAIL:
                    return aos[0].getAuthor().getEmail();
                case AUTHOR1_ROLE:
                    return aos[0].getRole();
                case AUTHOR2_NAME:
                    if (aos.length < 2)
                        return "";
                    return aos[1].getAuthor().getWholeName();
                case AUTHOR2_EMAIL:
                    if (aos.length < 2)
                        return "";
                    return aos[1].getAuthor().getEmail();
                case AUTHOR2_ROLE:
                    if (aos.length < 2)
                        return "";
                    return aos[1].getRole();
                case AUTHOR3_NAME:
                    if (aos.length < 3)
                        return "";
                    return aos[2].getAuthor().getWholeName();
                case AUTHOR3_EMAIL:
                    if (aos.length < 3)
                        return "";
                    return aos[2].getAuthor().getEmail();
                case AUTHOR3_ROLE:
                    if (aos.length < 3)
                        return "";
                    return aos[2].getRole();
                default:
                    logger.error("AuthorInfo type "+ai+" is not handled properly!");
                    return "";
            }//switch AuthorInfo
        }//if table is Author
        
        if (!getColumns(table).contains(column)) {
            logger.error("The report is broken. \""+jRField.getName()+"\" is not a valid identifier. The column name is not valid. The identifier must have form \"TABLE_NAME.COLUMN_NAME\"");
            //it's not easy to construct a default null value for a given Class so we'll take the safe way and throw an exception
            throw new JRException("The report is broken. Contains wrong field name \""+jRField.getName()+"\"");                        
        }
        
        switch (table) {
            case HABITAT:
                value = occurrence.getValue(Habitat.class,column);
                break;
            case HISTORYCHANGE:
                value = occurrence.getValue(HistoryChange.class,column);
                break;
            case HISTORYCOLUMN:
                value = occurrence.getValue(HistoryColumn.class,column);
                break;
            case HISTORYRECORD:
                value = occurrence.getValue(HistoryRecord.class,column);
                break;
            case METADATA:
                value = occurrence.getValue(Metadata.class,column);
                break;
            case OCCURRENCE:
                value = occurrence.getValue(Occurrence.class,column);
                break;
            case PHYTOCHORION:
                value = occurrence.getValue(Phytochorion.class,column);
                break;
            case PLANT:
                value = occurrence.getValue(Plant.class,column);
                break;
            case PUBLICATION:
                value = occurrence.getValue(Publication.class,column);
                break;
            case RIGHT:
                value = occurrence.getValue(Right.class,column);
                break;
            case TERRITORY:
                value = occurrence.getValue(Territory.class,column);
                break;
            case USER:
                value = occurrence.getValue(User.class,column);
                break;
            case VILLAGE:
                value = occurrence.getValue(Village.class,column);
                break;
            default:
                logger.error("Table type "+table+" is not handled properly in JasperDataSource!");
                throw new JRException("Table type "+table+" is not handled properly in JasperDataSource!");
        }//switch
        
        return value;
    }//getFieldValue()
    
    private List<String> getColumns(PlantloreConstants.Table table) throws JRException {
        switch (table) {
            case HABITAT:
                return new Habitat().getColumns();
            case HISTORYCHANGE:
                return new HistoryChange().getColumns();
            case HISTORYCOLUMN:
                return new HistoryColumn().getColumns();
            case HISTORYRECORD:
                return new HistoryRecord().getColumns();
            case METADATA:
                return new Metadata().getColumns();
            case OCCURRENCE:
                return new Occurrence().getColumns();
            case PHYTOCHORION:
                return new Phytochorion().getColumns();
            case PLANT:
                return new Plant().getColumns();
            case PUBLICATION:
                return new Publication().getColumns();
            case RIGHT:
                return new Right().getColumns();
            case TERRITORY:
                return new Territory().getColumns();
            case USER:
                return new User().getColumns();
            case VILLAGE:
                return new Village().getColumns();
            default:
                logger.error("Table type "+table+" is not handled properly in JasperDataSource!");
                throw new JRException("Table type "+table+" is not handled properly in JasperDataSource!");
        }//switch        
    }
    
}//class JasperDataSource

