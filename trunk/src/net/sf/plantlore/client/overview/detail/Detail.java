/*
 * Detail.java
 *
 * Created on 2. červen 2006, 12:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview.detail;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Observable;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.client.*;
import net.sf.plantlore.client.AppCore;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class Detail extends Observable {
    private Logger logger =  Logger.getLogger(Detail.class.getPackage().getName());
    private AppCore appCore;
    private DBLayer database;
    private int currentResult;
    String taxon;
    Integer year;
    Integer day;
    Integer month;
    Date time;
    String project;
    String placeDescription;
    String occNote;
    String habNote;
    String territory;
    String phytName;
    String phytCode;
    String village;
    String country;
    String quadrant;
    Double altitude;
    Double latitude;
    Double longitude;
    String herbarium;
    String source;
    String createdWho;
    Date createdWhen;
    String updatedWho;
    Date updatedWhen;
    String publication;
    DetailTableModel detailTableModel;
    
    
    /** Creates a new instance of Detail */
    public Detail(AppCore appCore) {
        this.appCore = appCore;
    }
    
    
    public void load(int result) throws DBLayerException, RemoteException {
        this.currentResult = result;
        Integer occurrenceId = appCore.getOccurrence(result);
        logger.debug("Detail model: loading data for occurrence id "+occurrenceId);
        SelectQuery sq = null;
        
        sq = database.createQuery(Occurrence.class);
        sq.createAlias(Occurrence.HABITAT,"habitat");
        sq.createAlias(Occurrence.PLANT,"plant");
        // Add publications using LEFT OUTER JOIN - so that occurrences without a publication are displayed as well
        sq.createAlias(Occurrence.PUBLICATION,"publication", PlantloreConstants.LEFT_OUTER_JOIN);
        sq.createAlias(Occurrence.METADATA,"metadata");
        sq.createAlias("habitat."+Habitat.PHYTOCHORION,"phyt");
        sq.createAlias("habitat."+Habitat.NEARESTVILLAGE,"vill");
        sq.createAlias("habitat."+Habitat.TERRITORY,"territory");
        // Add subquery to the query. Compare authoroccurrence.authorid with the result of a subquery (LEALL: <= all(...))
        sq.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, occurrenceId, null);
        
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.DATASOURCE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.HERBARIUM);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.TIMECOLLECTED);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.DAYCOLLECTED);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.MONTHCOLLECTED);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.YEARCOLLECTED);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.NOTE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.CREATEDWHO);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.CREATEDWHEN);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.UPDATEDWHO);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.UPDATEDWHEN);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.ALTITUDE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.LATITUDE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.LONGITUDE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.COUNTRY);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.DESCRIPTION);                    
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.NOTE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.QUADRANT);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"vill."+Village.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"metadata."+Metadata.DATASETTITLE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"phyt."+Phytochorion.CODE);                    
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"phyt."+Phytochorion.NAME);                    
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"plant."+Plant.TAXON);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"publication."+Publication.REFERENCECITATION);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"territory."+Territory.NAME);
        
        int resultId = database.executeQuery(sq);
        int resultsCount = database.getNumRows(resultId);
        if (resultsCount == 0) {
            logger.error("Detail: occurrence "+occurrenceId+" has no record in the database.");
            throw new DBLayerException("The occurrence you selected is no longer in the database.");
        }
        assert resultsCount == 1;
        
        Object[] records = database.more(resultId, 0, resultsCount - 1);
        database.closeQuery(sq);
        Object[] pa = (Object[]) records[0];
        source = (String)pa[0];
        herbarium = (String)pa[1];
        time = (Date)pa[2];
        day = (Integer)pa[3];
        month = (Integer)pa[4];
        year = (Integer)pa[5];
        occNote = (String)pa[6];
        createdWho = ((User)pa[7]).getWholeName();
        createdWhen= (Date)pa[8];
        updatedWho = ((User)pa[9]).getWholeName();
        updatedWhen = (Date)pa[10];
        altitude= (Double)pa[11];
        latitude = (Double)pa[12];
        longitude = (Double)pa[13];
        country = (String)pa[14];
        placeDescription = (String)pa[15];
        quadrant = (String)pa[16];
        habNote = (String)pa[17];
        village = (String)pa[18];
        project = (String)pa[19];
        phytCode = (String)pa[20];
        phytName = (String)pa[21];
        taxon = (String)pa[22];
        publication = (String)pa[23];
        territory = (String)pa[24];
        
        detailTableModel = new DetailTableModel(occurrenceId);
        setChanged();
        notifyObservers("NEW_DETAIL_LOADED");
    }
        
    
    public void setDatabase(DBLayer db) {
        this.database = db;
        logger.debug("Detail model - database set.");
    }
    
    public void next() throws DBLayerException, RemoteException {
        if (appCore.getResultsCount() <= currentResult+1)
            return;
        currentResult++;
        load(currentResult);
        appCore.selectAndShow(currentResult);
    }
    
    public void prev() throws DBLayerException, RemoteException {
        if (currentResult <=0)
            return;
        currentResult--;
        load(currentResult);
        appCore.selectAndShow(currentResult);
    }
    
    class DetailTableModel extends AbstractTableModel {
        Object[][] data;
        
        public DetailTableModel(int occurrenceId) throws DBLayerException, RemoteException {
            SelectQuery sq;
            //-- LOAD AUTHORS of the occurrence
            sq = database.createQuery(AuthorOccurrence.class);
            sq.createAlias(AuthorOccurrence.AUTHOR,"author");
            sq.createAlias(AuthorOccurrence.OCCURRENCE,"occ");
    //        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.ID);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.ID,null,occurrenceId,null);
            sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.DELETED,null,0,null);//only live authors
            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"author."+Author.WHOLENAME);
            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,AuthorOccurrence.ROLE);
            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,AuthorOccurrence.NOTE);

            int resultId = database.executeQuery(sq);
            int resultsCount = database.getNumRows(resultId);
            if (resultsCount == 0) {
                logger.error("Detail: occurrence "+occurrenceId+" has no live authors in the database.");
                throw new DBLayerException("The occurrence has no authors in the database.");
            }        
            assert resultsCount > 0;
            data = new Object[resultsCount][];
            Object[] records = database.more(resultId, 0, resultsCount - 1);
            database.closeQuery(sq);
            for (int i=0; i < resultsCount ; i++) {
                Object[] pa = (Object[]) records[i];
                String[] row = new String[3];
                row[0] = (String) pa[0];
                row[1] = (String) pa[1];
                row[2] = (String) pa[2];
                data[i] = row;
            }                        
        }
        public int getRowCount() {
            if (data != null)
                return data.length;
            else
                return 0;
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }        
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return L10n.getString("AddEdit.NameColumn");
                case 1:
                    return L10n.getString("AddEdit.RoleColumn");
                case 2:
                    return "";
                case 3:
                    return L10n.getString("AddEdit.RevisionColumn");
                default:
                    return "";
            }
        }        
    }
}

