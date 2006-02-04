/*
 * AuthorManagerCtrl.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.authors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.sf.plantlore.common.*;
import javax.swing.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 15, 2006
 */
public class AuthorManagerCtrl {
    /** Instance of a logger */
    private Logger logger;    
    AuthorManager model;
    AuthorManagerView view;
         
    private Timer timerSearch;          // Used for periodic checking of the state of other thread
    private Timer timerDelete;          // Used for periodic checking of the state of other thread    
    private ProgressDialog progress;    // Dialog showing progressbar
    
    private final int TIMER_FREQUENCY = 100;
    /** 
     * Creates a new instance of AuthorManagerCtrl 
     * @param authModel model of AuthorManager MVC
     * @param authView  view of AuthorManager MVC
     */
    public AuthorManagerCtrl(AuthorManager authModel, AuthorManagerView authView) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        // Save model and view
        this.model = authModel;
        this.view = authView;
        // Add action listeners to buttons
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.helpBtnAddActionListener(new HelpButtonListener());
        view.addBtnAddActionListener(new AddAuthorButtonListener());
        view.searchBtnAddActionlistener(new SearchAuthorButtonListener());
        view.deleteBtnAddActionListener(new DeleteAuthorButtonListener());
        view.previousBtnAddActionListener(new PreviousButtonListener());
        view.nextBtnAddActionListener(new NextButtonListener());        
        // Add PropertyChange listeners to fields in search box
        view.nameAddPropertyChangeListener(new NameFieldPropertyChangeListener());
        view.organizationAddPropertyChangeListener(new OrganizationFieldPropertyChangeListener());
        view.roleAddPropertyChangeListener(new RoleFieldPropertyChangeListener());
        view.emailAddPropertyChangeListener(new EmailFieldPropertyChangeListener());                
        // Add PropertyChange listener for the field with number of records to show
        view.rowsAddPropertyChangeListener(new RowsPropertyChangeListener());
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
                        // Display first n rows (n = model.getDisplayRows())
                        model.processResults(1, model.getDisplayRows());
                        model.setCurrentFirstRow(1);
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
                        // Update table with authors - remove deleted author
                        model.removeAuthorRecord();
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
            PlantloreHelp.showHelp("Main.AuthorManager");            
        }
    }
    
    /**
     * ActionListener class controlling the <b>Add author</b> button on the form.
     */    
    class AddAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Display dialog for adding / editing authors. This dialog shares model with
            // the rest of the AuthorManager.
            AddAuthorView addAuthView = new AddAuthorView(model, view.getFrame());
            AddAuthorCtrl addAuthCtrl = new AddAuthorCtrl(model, addAuthView);            
            addAuthView.show();
        }
    }    
    
    /**
     * ActionListener class controlling the <b>Edit author</b> button on the form.
     */    
    class EditAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Not implmeneted yet
        }
    }
    
    /**
     * ActionListener class controlling the <b>Delete Author</b> button on the form.
     */    
    class DeleteAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            logger.debug("Delete butten pressed");
            // Check whether a row is selected
            int index = view.getSelectedAuthor();
            if (index == -1) {
                view.selectRowMsg();
                return;
            }           
            // Confirm deletion
            if (!view.confirmDelete()) {
                return;
            }
            // Call delete
            model.setAuthorIndex(index);
            model.deleteAuthor();
            // Disable current view and run timer
            view.setDialogEnabled(false);                                
            timerDelete.start();                
            // Display dialog with progress bar
            progress = new ProgressDialog(view.getDialog(), true);
            progress.show();                                                
        }
    }    

    /**
     * ActionListener class controlling the <b>Search Authors</b> button on the form.
     */    
    class SearchAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Check whether at least one search field is non-empty
            if (view.checkNonEmpty("name") || view.checkNonEmpty("organization") ||
                view.checkNonEmpty("role") || view.checkNonEmpty("email")) {
                // Run DB search
                model.searchAuthor();
                // Disable current view and run timer
                view.setDialogEnabled(false);                                
                timerSearch.start();                
                // Display dialog with progress bar
                progress = new ProgressDialog(view.getDialog(), true);
                progress.show();                                                
            } else {
                // Show error message - no search criteria
                view.showSearchErrorMessage();
            }
        }
    }        
    
    class PreviousButtonListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            // Call processResults only if we don't see the first page (should not happen, button should be disabled)
            if (model.getCurrentFirstRow() > 1) {
                int firstRow = Math.max(model.getCurrentFirstRow()-view.getDisplayRows(), 1);
                model.processResults(firstRow, view.getDisplayRows());                
            }
        }
    }
    
    class NextButtonListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            // Call processResults only if we don't see the last page (should not happen, button should be desabled)
            if (model.getCurrentFirstRow()+view.getDisplayRows()<model.getResult().getNumRows()) {
                model.processResults(model.getCurrentFirstRow()+view.getDisplayRows(), view.getDisplayRows());                                
            }
        }
    }    
    
    /**
     *  PropertyChange listener for the <strong>name field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */
    class NameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setFirstName(view.getName());
        }        
    }

    /**
     *  PropertyChange listener for the <strong>organization field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */    
    class OrganizationFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setOrganization(view.getOrganization());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>role field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */    
    class RoleFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setRole(view.getRole());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>email field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */        
    class EmailFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setEmail(view.getEmail());
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
            if ((oldValue != view.getDisplayRows()) && (model.getDisplayRows() <= model.getResult().getNumRows())) {
                model.processResults(model.getCurrentFirstRow(), view.getDisplayRows());
            }
        }        
    }            
}
