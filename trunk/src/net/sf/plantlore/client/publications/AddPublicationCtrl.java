/*
 * AddPublicationCtrl.java
 *
 * Created on 21. leden 2006, 0:58
 *
 */

package net.sf.plantlore.client.publications;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.PlantloreHelp;
import javax.swing.Timer;
import net.sf.plantlore.common.ProgressBar;
import net.sf.plantlore.common.ProgressDialog;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Controller for the Add/Edit publication dialog in the PublicationManager MVC.
 * 
 * @author Tomas Kovarik
 * @version 1.0 BETA, May 1, 2006
 */
public class AddPublicationCtrl {
    /** Model of the Publication manager MVC */
    private PublicationManager model;
    /** View for adding publications in PublicationManager */
    private AddPublicationView view;
    /** Timer used to check for the end of long running tasks */
    private Timer timer;
    /** Instance of progress dialog */
    private ProgressDialog progress;    
    /** Instance of a logger */ 
    private Logger logger;
    
    /**
     *  Creates a new instance of AddPublicationCtrl 
     * 
     * @param addModel Model of the Publication manager MVC
     * @param addView View for adding authors in Publication manager
     */
    public AddPublicationCtrl(PublicationManager addModel, AddPublicationView addView) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        // Save instance of view and model
        this.model = addModel;
        this.view = addView;
        // Add listeners for buttons and fields
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.saveBtnAddActionListener(new SavePublicationButtonListener());        
        view.collectionNameAddPropertyChangeListener(new CollectionNameFieldPropertyChangeListener());
        view.publicationYearAddPropertyChangeListener(new PublicationYearFieldPropertyChangeListener());
        view.journalNameAddPropertyChangeListener(new JournalNameFieldPropertyChangeListener());
        view.journalAuthorAddPropertyChangeListener(new JournalAuthorFieldPropertyChangeListener());
        view.referenceDetailAddPropertyChangeListener(new ReferenceDetailFieldPropertyChangeListener());
        view.urlAddPropertyChangeListener(new UrlFieldPropertyChangeListener());
        view.noteAddFocusListener(new NoteAreaFocusListener());                
    }
    
    /**
     * ActionListener class controlling the <b>close</b> button on the form.
     */
    class CloseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Close the dialog
            view.close();
        }
    }

    /**
     * ActionListener class controlling the <b>Save publication</b> button on the form. Checks whether all 
     * the required fields have been set and calls model to save the data when the button is clicked.
     * This metod is used for saving new publication as well as updating the existing one.
     */    
    class SavePublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {            
            // Check whether all the required fields are present
            if (view.checkCompulsory()) {
                if (model.getEditPublication() == null) {                    
                    // Save new publication
                    Task task = model.savePublication();                
                    ProgressBar progressBar = new ProgressBar(task, view, true) {
                        public void exceptionHandler(Exception ex) {
                            if (ex instanceof DBLayerException) {
                                DBLayerException e = (DBLayerException)ex;
                                JOptionPane.showMessageDialog(view,L10n.getString("Error.DBLayerException")+"\n"+e.getErrorInfo(),L10n.getString("Error.DBLayerExceptionTitle"),JOptionPane.WARNING_MESSAGE);
                                logger.error(e+": "+e.getErrorInfo());
                                getTask().stop();
                                return;
                            }
                            if (ex instanceof RemoteException) {
                                RemoteException e = (RemoteException)ex;
                                JOptionPane.showMessageDialog(view,L10n.getString("Error.RemoteException")+"\n"+e.getMessage(),L10n.getString("Error.RemoteExceptionTitle"),JOptionPane.WARNING_MESSAGE);
                                logger.error(e);
                                getTask().stop();
                                return;
                            }
                            JOptionPane.showMessageDialog(view,L10n.getString("Delete.Message.UnknownException")+"\n"+ex.getMessage(),L10n.getString("Delete.Message.UnkownExceptionTitle"),JOptionPane.WARNING_MESSAGE);
                            logger.error(ex);                            
                        }              
                        
                        public void afterStopped(Object value) {
                            model.searchPublication(false);
                            model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                            model.reloadCache();
                        }
                    };
                    progressBar.setTitle(L10n.getString("Delete.ProgressTitle"));
                    task.start();
                } else {                    
                    // Edit existing publication
                    Task task = model.editPublication();
                    ProgressBar progressBar = new ProgressBar(task, view, true) {
                        public void exceptionHandler(Exception ex) {
                            if (ex instanceof DBLayerException) {
                                DBLayerException e = (DBLayerException)ex;
                                JOptionPane.showMessageDialog(view,L10n.getString("Error.DBLayerException")+"\n"+e.getErrorInfo(),L10n.getString("Error.DBLayerExceptionTitle"),JOptionPane.WARNING_MESSAGE);
                                logger.error(e+": "+e.getErrorInfo());
                                getTask().stop();
                                return;
                            }
                            if (ex instanceof RemoteException) {
                                RemoteException e = (RemoteException)ex;
                                JOptionPane.showMessageDialog(view,L10n.getString("Error.RemoteException")+"\n"+e.getMessage(),L10n.getString("Error.RemoteExceptionTitle"),JOptionPane.WARNING_MESSAGE);
                                logger.error(e);
                                getTask().stop();
                                return;
                            }
                            JOptionPane.showMessageDialog(view,L10n.getString("Delete.Message.UnknownException")+"\n"+ex.getMessage(),L10n.getString("Delete.Message.UnkownExceptionTitle"),JOptionPane.WARNING_MESSAGE);
                            logger.error(ex);                            
                        }                                      
                        public void afterStopped(Object value) {
                            model.searchPublication(false);
                            model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                            model.reloadCache();                            
                        }
                    };
                    progressBar.setTitle(L10n.getString("Delete.ProgressTitle"));
                    task.start();                    
                }
                // Close the add dialog when save finished
                view.close();
            }
        }
    }        

    /**
     *  PropertyChangeListener class for updating <b>Collection name</b> field in the model with data from the form.
     */
    class CollectionNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setCollectionName(view.getCollectionName());
        }        
    }
    
    /**
     *  PropertyChangeListener class for updating <b>publication year</b> field in the model with data from the form.
     */
    class PublicationYearFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (view.getPublicationYear() != null) {
                model.setPublicationYear(view.getPublicationYear());
            }
        }        
    }    
    
    /**
     *  PropertyChangeListener class for updating <b>journal name</b> field in the model with data from the form.
     */
    class JournalNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setJournalName(view.getJournalName());
        }        
    }    

    /**
     *  PropertyChangeListener class for updating <b>journal author</b> field in the model with data from the form.
     */
    class JournalAuthorFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setJournalAuthor(view.getJournalAuthor());
        }        
    }    
    
    /**
     *  PropertyChangeListener class for updating <b>reference detail</b> field in the model with data from the form.
     */
    class ReferenceDetailFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setReferenceDetail(view.getReferenceDetail());
        }        
    }        
    
    /**
     *  PropertyChangeListener class for updating <b>URL</b> field in the model with data from the form.
     */
    class UrlFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setUrl(view.getUrl());
        }        
    }            

    /**
     *  FocusListener class for updating <b>note</b> field in the model with data from the form.
     */
    class NoteAreaFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setNote(view.getNote());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }                
}