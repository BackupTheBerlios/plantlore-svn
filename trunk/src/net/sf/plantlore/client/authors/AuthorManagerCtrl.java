
package net.sf.plantlore.client.authors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import net.sf.plantlore.common.*;
import javax.swing.Timer;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Controller for the main AuthorManager dialog (part of the AutorManager MVC).
 *
 * @author Tomas Kovarik
 * @version 1.0
 */
public class AuthorManagerCtrl {
    /** Instance of a logger */
    private Logger logger; 
    /** Model of the AuthorManager MVC */
    AuthorManager model;
    /** View of the AuthorManager MVC */
    AuthorManagerView view;
    
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
        // Add escape key event - close dialog
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);                
        // Add action listeners to buttons
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.addBtnAddActionListener(new AddAuthorButtonListener());
        view.searchBtnAddActionlistener(new SearchAuthorButtonListener());
        view.deleteBtnAddActionListener(new DeleteAuthorButtonListener());
        view.editBtnAddActionListener(new EditAuthorButtonListener());
        view.previousBtnAddActionListener(new PreviousButtonListener());
        view.nextBtnAddActionListener(new NextButtonListener());        
        // Add PropertyChange listeners to fields in search box
        view.nameAddPropertyChangeListener(new NameFieldPropertyChangeListener());
        view.organizationAddPropertyChangeListener(new OrganizationFieldPropertyChangeListener());
        view.roleAddPropertyChangeListener(new RoleFieldPropertyChangeListener());
        view.emailAddPropertyChangeListener(new EmailFieldPropertyChangeListener());                
        // Add PropertyChange listener for the field with number of records to show
        view.rowsAddPropertyChangeListener(new RowsPropertyChangeListener());
        view.sortAddFocusListener(new SortComboFocusListener());
        view.sortDirectionAddFocusListener(new SortDirectionRadioFocusListener());

        Task task = model.searchAuthor(true);
        task.setPostTaskAction(new PostTaskAction() {
            public void afterStopped(Object value) {
                model.setCurrentFirstRow(1);
                try {
                    model.processResults(1, model.getDisplayRows());
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
            }            
        });
        Dispatcher.getDispatcher().dispatch(task, view, false);        
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
     * ActionListener class controlling the <b>Add author</b> button on the form.
     */    
    class AddAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Check whether we have rights for this operation
            try {
                if (!model.hasRights(model.ADD)) {
                    view.showErrorMessage(L10n.getString("Authors.Add.InsufficientRights"));
                    return;
                }
            } catch (RemoteException ex) {
                logger.error("RemoteException caught while checking user's rights. Details: "+ex.getMessage());
                ex.printStackTrace();
                DefaultExceptionHandler.handle(view, ex);
                return;
            }
            
            // Display dialog for adding / editing authors. This dialog shares model with
            // the rest of the AuthorManager.
            AddAuthorView addAuthView = new AddAuthorView(model, view.getFrame(), true);
            AddAuthorCtrl addAuthCtrl = new AddAuthorCtrl(model, addAuthView);            
            // We are going to add author, no editing
            model.setEditAuthor(null);
//            addAuthView.setSize(400,450);        
            addAuthView.setLocationRelativeTo(null);
            logger.info("Add Author dialog opened for adding new author");            
            addAuthView.setVisible(true);
        }
    }    
    
    /**
     * ActionListener class controlling the <b>Edit author</b> button on the form.
     */    
    class EditAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = view.getSelectedAuthor();
            if (index == -1) {
                view.selectRowMsg();
                return;
            }          
            try {
                // Check whether we have rights for this operation
                if (!model.hasRights(model.EDIT)) {
                    view.showErrorMessage(L10n.getString("Authors.Edit.InsufficientRights"));
                    return;
                }                        
            } catch (RemoteException ex) {
                logger.error("RemoteException caught while checking user's rights. Details: "+ex.getMessage());
                ex.printStackTrace();
                DefaultExceptionHandler.handle(view, ex);
                return;
            }                        
            AddAuthorView addAuthView = new AddAuthorView(model, view.getFrame(), false);
            AddAuthorCtrl addAuthCtrl = new AddAuthorCtrl(model, addAuthView);            
            // Save author we are going to edit
            model.setEditAuthor(model.getSelectedAuthor(index));            
            model.setAuthorIndex(index);
            model.loadAuthor();
//            addAuthView.setSize(400,450);        
            addAuthView.setLocationRelativeTo(null);
            logger.info("Add Author dialog opened for editing author");
            addAuthView.setVisible(true);            
        }
    }
    
    /**
     * ActionListener class controlling the <b>Delete Author</b> button on the form.
     */    
    class DeleteAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            logger.info("Delete Author button pressed");
            // Check whether a row is selected
            int index = view.getSelectedAuthor();
            if (index == -1) {
                view.selectRowMsg();
                return;
            }        
            try {
                // Check whether we have rights for this operation
                if (!model.hasRights(model.DELETE)) {
                    view.showErrorMessage(L10n.getString("Authors.Delete.InsufficientRights"));
                    return;
                }           
            } catch (RemoteException ex) {
                logger.error("RemoteException caught while checking user's rights. Details: "+ex.getMessage());
                ex.printStackTrace();
                DefaultExceptionHandler.handle(view, ex);
                return;                
            }
            // Confirm deletion
            if (!view.confirmDelete()) {
                return;
            }
            // Call delete
            model.setAuthorIndex(index);            
            // Delete is executed in a separate thread using Task
            Task task = model.deleteAuthor();
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
    }    

    /**
     * ActionListener class controlling the <b>Search Authors</b> button on the form.
     */    
    class SearchAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Task task = model.searchAuthor(true);
            task.setPostTaskAction(new PostTaskAction() {
                public void afterStopped(Object value) {
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
                }                
            });
            Dispatcher.getDispatcher().dispatch(task, view, false);            
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
                try {
                    model.processResults(firstRow, view.getDisplayRows());                
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
            }
        }
    }
    
    /**
     *  ActionListener class for the Next button (browsing the search results).
     */
    class NextButtonListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            // Call processResults only if we don't see the last page (should not happen, button should be disabled)
            try {
                if (model.getCurrentFirstRow()+view.getDisplayRows()<=model.getResultRows()) {
                    model.processResults(model.getCurrentFirstRow()+view.getDisplayRows(), view.getDisplayRows());
                }
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
     *  PropertyChange listener for the <strong>name field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */
    class NameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchName(view.getName());
        }        
    }

    /**
     *  PropertyChange listener for the <strong>organization field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */    
    class OrganizationFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchOrganization(view.getOrganization());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>role field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */    
    class RoleFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchRole(view.getRole());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>email field</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model object.
     */        
    class EmailFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchEmail(view.getEmail());
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
            logger.debug("New number of rows to display: "+view.getDisplayRows());
            // If neccessary reload search results
            try {                        
                if ((oldValue != view.getDisplayRows()) && (model.getDisplayRows() <= model.getResultRows())) {
                    model.processResults(model.getCurrentFirstRow(), view.getDisplayRows());
                    logger.debug("Search results reloaded. First row: "+model.getCurrentFirstRow()+"; Display rows: "+view.getDisplayRows());
                }
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
        }        
    }            
}
