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
    private Logger logger;
    private File theChosenOne;
    private Selection selection;
    private DBLayer database;
    private JasperReport jasperReport;
    private JasperPrint jasperPrint;
    private boolean reportChanged = false;
    
    /** Creates a new instance of Print */
    public Print() {
            logger = Logger.getLogger(this.getClass().getPackage().getName());        
    }

    public File getTheChosenOne() {
        return theChosenOne;
    }

    public void setTheChosenOne(File theChosenOne) throws JRException {
        if (theChosenOne != null)
            logger.debug("Print report set to "+theChosenOne.getAbsolutePath());
        else
            return;

        jasperReport = JasperCompileManager.compileReport(theChosenOne.getAbsolutePath());
        
        this.theChosenOne = theChosenOne;
        reportChanged = true;
        setChanged();
        notifyObservers("REPORT_CHOSEN");
    }
    
    public JasperPrint createJasperPrint() throws JRException {
        if (jasperPrint == null || reportChanged) {
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
    
    
}
