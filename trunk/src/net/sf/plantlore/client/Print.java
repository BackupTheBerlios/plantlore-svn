/*
 * Print.java
 *
 * Created on 19. květen 2006, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import java.util.prefs.Preferences;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.middleware.DBLayer;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class Print extends Observable {
    public final static int SCHEDA = 1;
    public final static int A4LIST = 2;
    public final static int OWNREPORT = 3;
    private Logger logger;
    private File theChosenOne;
    private Selection selection;
    private DBLayer database;
    private JasperReport jasperReport;
    private JasperReport schedaReport;
    private JasperReport a4listReport;
    private JasperPrint jasperPrint;
    private boolean reportChanged = false;
    private int reportToUse = A4LIST;
    
    /** Creates a new instance of Print */
    public Print() {
            logger = Logger.getLogger(this.getClass().getPackage().getName());        
            InputStream schedaIs = this.getClass().getClassLoader().getResourceAsStream("net/sf/plantlore/client/resources/SchedaA6.jasper");
            InputStream a4listIs = this.getClass().getClassLoader().getResourceAsStream("net/sf/plantlore/client/resources/OccurrenceListA4.jasper");
            
            try {
                ObjectInputStream schedaOis = new ObjectInputStream(schedaIs);
                ObjectInputStream a4listOis = new ObjectInputStream(a4listIs);
                schedaReport = (JasperReport) schedaOis.readObject();
                a4listReport = (JasperReport) a4listOis.readObject();
            } catch (FileNotFoundException ex) {
                logger.error("Problem loading jasper report resource: "+ex);
                return;                    
            } catch (IOException ex) {                    
                logger.error("Problem loading jasper report resource: "+ex);
                return;
            } catch (ClassNotFoundException ex) {
                logger.error("Problem loading jasper report resource: "+ex);
                return;                    
            }
    }

    public File getTheChosenOne() {
        return theChosenOne;
    }

    public void setTheChosenOne(File theChosenOne) throws JRException, FileNotFoundException, IOException, ClassNotFoundException {
        if (theChosenOne != null)
            logger.debug("Print report set to "+theChosenOne.getAbsolutePath());
        else
            return;

        FileInputStream fis = new FileInputStream(theChosenOne.getAbsolutePath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        try {
            jasperReport = (JasperReport) ois.readObject();
        } catch (ClassCastException ex) { //some insidious user could pass us under a serialized object but of different class
            throw new JRException("The file is not a JasperReport class.");
        }
        
        this.theChosenOne = theChosenOne;
        reportChanged = true;
        setChanged();
        notifyObservers("REPORT_CHOSEN");
    }
    
    public JasperPrint createJasperPrint() throws JRException {
        JasperReport jasperReport;
        switch (reportToUse) {
            case SCHEDA:
                jasperReport = schedaReport;
                break;
            case A4LIST:
                jasperReport = a4listReport;
                break;
            case OWNREPORT:
                jasperReport = this.jasperReport;
                break;
            default:
                jasperReport = a4listReport;
                logger.warn("Inconsistency detected. Unknown report constant: "+reportToUse);
        }
        
        if (jasperPrint == null || reportChanged) { //jasperPrint is nulled in setSource()
            Preferences prefs = Preferences.userNodeForPackage(AppCoreCtrl.class);
            String h1 = prefs.get("HEADER_ONE","Set the first header in settings, please.");
            String h2 = prefs.get("HEADER_TWO","Set the second header in settings, please.");
            HashMap params = new HashMap();
            params.put("HEADER_ONE",h1);
            params.put("HEADER_TWO",h2);
            jasperPrint = JasperFillManager.fillReport(
                  jasperReport, params, new JasperDataSource(database, selection)  );    
            reportChanged = false;
        }
        return jasperPrint;
    }

    public Selection getSelection() {
        return selection;
    }
    public void setSource(DBLayer database, Selection selection) {
        this.database = database;
        this.selection = selection;
        logger.debug("Selection and database set.");
        jasperPrint = null;
        setChanged();
        notifyObservers("NEW_SOURCE");
    }
    
    public DBLayer getDatabase() {
        return database;
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    public int getReportToUse() {
        return reportToUse;
    }

    public void setReportToUse(int reportToUse) {
        if (reportToUse != this.reportToUse) {
            this.reportToUse = reportToUse;
            reportChanged = true;
        }
    }
    
    
}
