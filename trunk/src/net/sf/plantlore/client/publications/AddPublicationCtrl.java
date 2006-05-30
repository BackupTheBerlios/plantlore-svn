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
import net.sf.plantlore.common.PlantloreHelp;
import javax.swing.Timer;
import net.sf.plantlore.common.ProgressDialog;
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
        view.helpBtnAddActionListener(new HelpButtonListener());
        view.saveBtnAddActionListener(new SavePublicationButtonListener());        
        view.collectionNameAddPropertyChangeListener(new CollectionNameFieldPropertyChangeListener());
        view.publicationYearAddPropertyChangeListener(new PublicationYearFieldPropertyChangeListener());
        view.journalNameAddPropertyChangeListener(new JournalNameFieldPropertyChangeListener());
        view.journalAuthorAddPropertyChangeListener(new JournalAuthorFieldPropertyChangeListener());
        view.referenceCitationAddPropertyChangeListener(new ReferenceCitationFieldPropertyChangeListener());
        view.referenceDetailAddPropertyChangeListener(new ReferenceDetailFieldPropertyChangeListener());
        view.urlAddPropertyChangeListener(new UrlFieldPropertyChangeListener());
        view.noteAddFocusListener(new NoteAreaFocusListener());                
        // Create a timer to check for the end of long running task
        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (model.isOperationDone() == true) {
                    timer.stop();
                    // Close progress bar dialog
                    progress.close();
                    view.setDialogEnabled(true);                    
                    if (model.processErrors() == false) {    
                        if (model.isResultAvailable()) {   
                            model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());                        
                        }
                    }
                }
            }
        });        
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
     * ActionListener class controlling the <b>help</b> button on the form.
     */    
    class HelpButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Display help viewer      
            logger.info("Help inovked from the Add/Edit author window");
            PlantloreHelp.showHelp(PlantloreHelp.AUTHOR_MANAGER);
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
            if (view.checkNonEmpty(PublicationManager.FIELD_REFERENCE_CITATION)) {
                if (model.getEditPublication() == null) {
                    // Save new publication
                    model.savePublication();                
                } else {
                    // Edit existing publication
                    model.editPublication();
                }
                // Disable the dialog while saving author
                view.setDialogEnabled(false);                                
                timer.start();                
                // Display dialog with progress bar
                progress = new ProgressDialog(view.getDialog(), true);
                progress.show();
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
            model.setPublicationYear(view.getPublicationYear());
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
     *  PropertyChangeListener class for updating <b>reference citation</b> field in the model with data from the form.
     */
    class ReferenceCitationFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setReferenceCitation(view.getReferenceCitation());
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