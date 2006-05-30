/*
 * PublicationManagerCtrl.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.publications;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.sf.plantlore.common.*;
import javax.swing.Timer;
import org.apache.log4j.Logger;

/**
 * Controller for the main PublicationManager dialog (part of the PublicationManager MVC).
 * 
 * @author Tomas Kovarik
 * @version 1.0 BETA, May 1, 2006
 */
public class PublicationManagerCtrl {
    /** Instance of a logger */
    private Logger logger; 
    /** Model of the PublicationManager MVC */
    PublicationManager model;
    /** View of the PublicationManager MVC  */
    PublicationManagerView view;
         
    private Timer timerSearch;          // Used for periodic checking of the state of other thread
    private Timer timerDelete;          // Used for periodic checking of the state of other thread    
    private ProgressDialog progress;    // Dialog showing progressbar
    /** Frequency of the timer used for periodic checking of the state of other threads */
    private final int TIMER_FREQUENCY = 100;
    
    /**
     * Creates a new instance of PublicationManagerCtrl 
     * 
     * @param publModel model of PublicationManager MVC
     * @param publView  view of PublicationManager MVC
     */
    public PublicationManagerCtrl(PublicationManager publModel, PublicationManagerView publView) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        // Save model and view
        this.model = publModel;
        this.view = publView;
        // Add action listeners to buttons
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.helpBtnAddActionListener(new HelpButtonListener());
        view.addBtnAddActionListener(new AddPublicationButtonListener());
        view.searchBtnAddActionlistener(new SearchPublicationButtonListener());
        view.deleteBtnAddActionListener(new DeletePublicationButtonListener());
        view.editBtnAddActionListener(new EditPublicationButtonListener());
        view.previousBtnAddActionListener(new PreviousButtonListener());
        view.nextBtnAddActionListener(new NextButtonListener());        
        // Add PropertyChange listeners to fields in search box
        view.collectionNameAddPropertyChangeListener(new CollectionNameFieldPropertyChangeListener());
        view.journalNameAddPropertyChangeListener(new JournalNameFieldPropertyChangeListener());
        view.referenceCitationAddPropertyChangeListener(new ReferenceCitationFieldPropertyChangeListener());
        view.referenceDetailAddPropertyChangeListener(new ReferenceDetailFieldPropertyChangeListener());                
        // Add PropertyChange listener for the field with number of records to show
        view.rowsAddPropertyChangeListener(new RowsPropertyChangeListener());
        view.sortAddFocusListener(new SortComboFocusListener());
        view.sortDirectionAddFocusListener(new SortDirectionRadioFocusListener());
        // Create a timer for search operation
        timerSearch = new Timer(TIMER_FREQUENCY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Check whether the other thread is still running
                if (model.isOperationDone() == true) {
                    timerSearch.stop();                    
                    progress.close();               // Close dialog with progress bar
                    view.setDialogEnabled(true);    // Enable view dialog                
                    // Check for errors which might have occured. If none occured, tell model to process the result
                    if (model.processErrors() == false) {
                        if (model.getResultRows() == 0) {
                            view.showSearchInfoMessage();
                        }
                        model.setCurrentFirstRow(1);                                                    
                        // Display first n rows (n = model.getDisplayRows())                        
                        model.processResults(1, model.getDisplayRows());                        
                    }
                }
            }
        });                
        // Create a timer for delete operation
        timerDelete = new Timer(TIMER_FREQUENCY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Check whether the other thread is still running
                if (model.isOperationDone() == true) {
                    timerDelete.stop();                    
                    progress.close();               // Close dialog with progress bar
                    view.setDialogEnabled(true);    // Enable view dialog                
                    // Check for errors which might have occured. If none occured, tell model to process the result
                    if (model.processErrors() == false) {
                        // Update curent first row so that it is not greater than number of rows in the result
                        // (this happens in case the last record in the list has been deleted and it was set as 
                        // the current first row)
                        if (model.getCurrentFirstRow() > model.getResultRows()) {                           
                            int row = model.getCurrentFirstRow()-model.getDisplayRows();
                            if (row < 1) {
                                model.setCurrentFirstRow(1);                                
                            } else {
                                model.setCurrentFirstRow(row);                                                                
                            }
                        }
                        // Update table with publications - remove deleted author                        
                        model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                    }
                }
            }
        });   
        // Display all publications when Publication manager is opened
        model.searchPublication();
        // Disable current view and run timer
        view.setDialogEnabled(false);                                
        timerSearch.start();                
        // Display dialog with progress bar
        progress = new ProgressDialog(view.getDialog(), true);
        progress.show();        
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
            logger.info("Help inovked from the AuthorManager window");
            PlantloreHelp.showHelp(PlantloreHelp.ADD_AUTHOR);            
        }
    }
    
    /**
     * ActionListener class controlling the <b>Add publication</b> button on the form.
     */    
    class AddPublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Display dialog for adding / editing publications. This dialog shares model with
            // the rest of the PublicationManager.
            AddPublicationView addPublView = new AddPublicationView(model, view.getFrame(), false);
            AddPublicationCtrl addPublCtrl = new AddPublicationCtrl(model, addPublView);            
            // We are going to add publication, no editing
            model.setEditPublication(null);
            // addPublView.setSize(400,450);        
            addPublView.setLocationRelativeTo(null);
            logger.info("Add Publication dialog opened for adding new author");            
            addPublView.setVisible(true);
        }
    }    
    
    /**
     * ActionListener class controlling the <b>Edit publication</b> button on the form.
     */    
    class EditPublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = view.getSelectedPublication();
            if (index == -1) {
                view.selectRowMsg();
                return;
            }          
            AddPublicationView addPublView = new AddPublicationView(model, view.getFrame(), false);
            AddPublicationCtrl addPublCtrl = new AddPublicationCtrl(model, addPublView);            
            // Save author we are going to edit
            model.setEditPublication(model.getSelectedPublication(index));            
            model.setPublicationIndex(index);
            model.loadPublication();
            // addPublView.setSize(400,450);        
            addPublView.setLocationRelativeTo(null);
            logger.info("Add Publication dialog opened for editing author");
            addPublView.setVisible(true);            
        }
    }
    
    /**
     * ActionListener class controlling the <b>Delete Publication</b> button on the form.
     */    
    class DeletePublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            logger.info("Delete Publication button pressed");
            // Check whether a row is selected
            int index = view.getSelectedPublication();
            if (index == -1) {
                view.selectRowMsg();
                return;
            }           
            // Confirm deletion
            if (!view.confirmDelete()) {
                return;
            }
            // Call delete
            model.setPublicationIndex(index);
            model.deletePublication();
            // Disable current view and run timer
            view.setDialogEnabled(false);                    
            timerDelete.start();                
            // Display dialog with progress bar
            progress = new ProgressDialog(view.getDialog(), true);
            progress.show();                                                
        }
    }    

    /**
     * ActionListener class controlling the <b>Search Publications</b> button on the form.
     */    
    class SearchPublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Check whether at least one search field is non-empty
            // Run DB search
            model.searchPublication();
            // Disable current view and run timer
            view.setDialogEnabled(false);                                
            timerSearch.start();                
            // Display dialog with progress bar
            progress = new ProgressDialog(view.getDialog(), true);
            progress.show();                                   
        }
    }        
    
    /**
     *  ActionListener class for the Previous button (browsing the search results).
     */            
    class PreviousButtonListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            // Call processResults only if we don't see the first page (should not happen, button should be disabled)
            if (model.getCurrentFirstRow() > 1) {
                int firstRow = Math.max(model.getCurrentFirstRow()-view.getDisplayRows(), 1);
                model.processResults(firstRow, view.getDisplayRows());                
            }
        }
    }
    
    /**
     *  ActionListener class for the Next button (browsing the search results).
     */
    class NextButtonListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            // Call processResults only if we don't see the last page (should not happen, button should be disabled)
            if (model.getCurrentFirstRow()+view.getDisplayRows()<=model.getResultRows()) {
                model.processResults(model.getCurrentFirstRow()+view.getDisplayRows(), view.getDisplayRows());
            }
        }
    }    
    
    /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class SortComboFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSortField(view.getSortField());
        }        

        public void focusGained(FocusEvent e) {
            // Empty, no action when focus gained
        }
    }    
    
    /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class SortDirectionRadioFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSortDirection(view.getSortDirection());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }                
    
    /**
     *  PropertyChange listener for the <strong>collection name field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */
    class CollectionNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchCollectionName(view.getCollectionName());
        }        
    }

    /**
     *  PropertyChange listener for the <strong>journal name field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */    
    class JournalNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchJournalName(view.getJournalName());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>reference citation field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */    
    class ReferenceCitationFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchReferenceCitation(view.getReferenceCitation());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>reference detail field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */        
    class ReferenceDetailFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchReferenceDetail(view.getReferenceDetail());
        }        
    }        
    
    /**
     *  PropertyChange listener for the <strong>rows field</strong> with the number of records to display. After losing focus 
     *  automaticaly stores value of the field to model object.
     */        
    class RowsPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            // Save old value
            int oldValue = model.getDisplayRows();
            // Check whether new value > 0
            if (view.getDisplayRows() < 1) {
                view.setDisplayRows(oldValue);
                return;
            }
            // Set new value in the model
            model.setDisplayRows(view.getDisplayRows());
            // If neccessary reload search results
            if ((oldValue != view.getDisplayRows()) && (model.getDisplayRows() <= model.getResultRows())) {
                model.processResults(model.getCurrentFirstRow(), view.getDisplayRows());
                logger.debug("Search results reloaded. First row: "+model.getCurrentFirstRow()+"; Display rows: "+view.getDisplayRows());
            }
        }        
    }            
}
