
package net.sf.plantlore.client.authors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.PlantloreHelp;
import javax.swing.Timer;
import net.sf.plantlore.client.authors.AuthorManagerCtrl.RoleFieldPropertyChangeListener;
import net.sf.plantlore.common.PostTaskAction;
import net.sf.plantlore.common.ProgressDialog;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 * Controller for the Add/Edit author dialog in the AuthorManager MVC.
 *
 * @author Tomas Kovarik
 * @version 1.0
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
    /** Instance of a logger */ 
    private Logger logger;
    /** 
     *  Creates a new instance of AddAuthorCtrl 
     *
     *  @param addModel Model of the Author manager MVC
     *  @param addView View for adding authors in Author manager
     */
    public AddAuthorCtrl(AuthorManager addModel, AddAuthorView addView) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        // Save instance of view and model
        this.model = addModel;
        this.view = addView;
        // Add escape key event - close dialog
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);        
        // Add listeners for buttons and fields
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.saveBtnAddActionListener(new SaveAuthorButtonListener());        
        view.nameAddPropertyChangeListener(new NameFieldPropertyChangeListener());
        view.organizationAddPropertyChangeListener(new OrganizationFieldPropertyChangeListener());
        view.roleAddPropertyChangeListener(new RoleFieldPropertyChangeListener());
        view.addressAddFocusListener(new AddressAreaFocusListener());
        view.phoneNumberAddPropertyChangeListener(new PhoneFieldPropertyChangeListener());
        view.emailAddPropertyChangeListener(new EmailFieldPropertyChangeListener());
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
     * ActionListener class controlling the <b>Save author</b> button on the form. Checks whether all the 
     * required fields have been set and calls model to save the data when the button is clicked.
     * This metod is used for saving new author as well as updating the existing one.
     */    
    class SaveAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {                        
            // Check whether all the required fields are present
            if (view.checkNonEmpty("name")) {
                if (model.getEditAuthor() == null) {                    
                    // Save new author
                    Task task = model.saveAuthor();     
                    task.setPostTaskAction(new PostTaskAction() {
                        public void afterStopped(Object value) {
                            model.searchAuthor(false);
                            try {
                                model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                            } catch (RemoteException ex) {
                                logger.error("RemoteException caught while processing search results. Details: "+ex.getMessage());
                                ex.printStackTrace();
                                DefaultExceptionHandler.handle(view, ex);
                                return;                    
                            } catch (DBLayerException ex) {
                                logger.error("RemoteException caught while processing search results. Details: "+ex.getMessage());
                                ex.printStackTrace();
                                DefaultExceptionHandler.handle(view, ex);
                                return;                    
                            }
                            model.reloadCache();
                        }                        
                    });
                    Dispatcher.getDispatcher().dispatch(task, view, false);
                } else {                    
                    // Edit existing author
                    Task task = model.editAuthor();
                    task.setPostTaskAction(new PostTaskAction() {
                        public void afterStopped(Object value) {
                            model.searchAuthor(false);
                            try {
                                model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                            } catch (RemoteException ex) {
                                logger.error("RemoteException caught while processing search results. Details: "+ex.getMessage());
                                ex.printStackTrace();
                                DefaultExceptionHandler.handle(view, ex);
                                return;                    
                            } catch (DBLayerException ex) {
                                logger.error("RemoteException caught while processing search results. Details: "+ex.getMessage());
                                ex.printStackTrace();
                                DefaultExceptionHandler.handle(view, ex);
                                return;                    
                            }
                            model.reloadCache();                            
                        }                        
                    });
                    Dispatcher.getDispatcher().dispatch(task, view, false);
                }
                // Close the add dialog when save finished
                view.close();
            }
        }
    }        

    /**
     *  PropertyChangeListener class for updating <b>Name</b> field in the model with data from the form.
     */
    class NameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setName(view.getName());
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