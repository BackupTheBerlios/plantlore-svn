/*
 * AddAuthorCtrl.java
 *
 * Created on 21. leden 2006, 0:58
 *
 */

package net.sf.plantlore.client.authors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.sf.plantlore.common.PlantloreHelp;
import javax.swing.Timer;
import net.sf.plantlore.client.authors.AuthorManagerCtrl.RoleFieldPropertyChangeListener;
import net.sf.plantlore.common.ProgressDialog;

/**
 *
 * @author Tomas Kovarik
 */
public class AddAuthorCtrl {
    /** Model of the Author manager MVC */
    private AuthorManager model;
    /** View for adding authors in Autho mManager */
    private AddAuthorView view;
    /** Timer used to check for the end of long running tasks */
    private Timer timer;
    /** Instance of progress dialog */
    private ProgressDialog progress;
    
    /** 
     *  Creates a new instance of AddAuthorCtrl 
     *
     *  @param addModel Model of the Author manager MVC
     *  @param addView View for adding authors in Author manager
     */
    public AddAuthorCtrl(AuthorManager addModel, AddAuthorView addView) {
        // Save instance of view and model
        this.model = addModel;
        this.view = addView;
        // Add listeners for buttons and fields
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.helpBtnAddActionListener(new HelpButtonListener());
        view.saveBtnAddActionListener(new SaveAuthorButtonListener());        
        view.firstNameAddPropertyChangeListener(new FirstNameFieldPropertyChangeListener());
        view.surnameAddPropertyChangeListener(new SurnameFieldPropertyChangeListener());
        view.organizationAddPropertyChangeListener(new OrganizationFieldPropertyChangeListener());
        view.roleAddPropertyChangeListener(new RoleFieldPropertyChangeListener());
        view.addressAddFocusListener(new AddressAreaFocusListener());
        view.phoneNumberAddPropertyChangeListener(new PhoneFieldPropertyChangeListener());
        view.emailAddPropertyChangeListener(new EmailFieldPropertyChangeListener());
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
                            System.out.println("current first row: "+model.getCurrentFirstRow());
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
            PlantloreHelp.showHelp(PlantloreHelp.AUTHOR_MANAGER_GENERAL);            
        }
    }
    
    /**
     * ActionListener class controlling the <b>Save author</b> button on the form. Checks whether all the required fields 
     * have been set and calls model to save the data when the button is clicked.
     */    
    class SaveAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {            
            // Check whether all the required fields are present
            if (view.checkNonEmpty("name") && view.checkNonEmpty("surname") &&
                view.checkNonEmpty("organization") && view.checkNonEmpty("role") &&
                view.checkNonEmpty("address") && view.checkNonEmpty("phone") &&
                view.checkNonEmpty("email") && view.checkNonEmpty("url")) {
                // Save the author data
                model.saveAuthor();                
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
     *  PropertyChangeListener class for updating <b>first name</b> field in the model with data from the form.
     */
    class FirstNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setFirstName(view.getFirstName());
        }        
    }
    
    /**
     *  PropertyChangeListener class for updating <b>surname</b> field in the model with data from the form.
     */
    class SurnameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSurname(view.getSurname());
        }        
    }
    
    /**
     *  PropertyChangeListener class for updating <b>organization</b> field in the model with data from the form.
     */
    class OrganizationFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setOrganization(view.getOrganization());
        }        
    }    
    
    /**
     *  PropertyChangeListener class for updating <b>role</b> field in the model with data from the form.
     */
    class RoleFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setRole(view.getRole());
        }        
    }    

    /**
     *  FocusListener class for updating <b>address</b> field in the model with data from the form.
     */    
    class AddressAreaFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setAddress(view.getAddress());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }        
    
    /**
     *  PropertyChangeListener class for updating <b>phone number</b> field in the model with data from the form.
     */
    class PhoneFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setPhoneNumber(view.getPhoneNumber());
        }        
    }        
    
    /**
     *  PropertyChangeListener class for updating <b>email</b> field in the model with data from the form.
     */
    class EmailFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setEmail(view.getEmail());
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