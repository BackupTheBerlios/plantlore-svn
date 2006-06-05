
package net.sf.plantlore.client.publications;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.*;
import javax.swing.Timer;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Controller for the main PublicationManager dialog (part of the PublicationManager MVC).
 * 
 * @author Tomas Kovarik
 * @version 1.0, June 4, 2006
 */
public class PublicationManagerCtrl {
    /** Instance of a logger */
    private Logger logger; 
    /** Model of the PublicationManager MVC */
    PublicationManager model;
    /** View of the PublicationManager MVC  */
    PublicationManagerView view;
    
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
        // Display all publication when Publication manager is opened using the Task
        Task task = model.searchPublication(true);
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
            // After Task is finished, display the results
            public void afterStopped(Object value) {
                model.setCurrentFirstRow(1);
                model.processResults(1, model.getDisplayRows());
            }
        };
        progressBar.setTitle(L10n.getString("Delete.ProgressTitle"));
        task.start();
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
     * ActionListener class controlling the <b>Add publication</b> button on the form.
     */    
    class AddPublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Check whether we have rights for this operation
            if (!model.hasRights(model.ADD)) {
                view.showErrorMessage("You don't have sufficient rights for this operation");
                return;
            }            
            // Display dialog for adding / editing publications. This dialog shares model with
            // the rest of the PublicationManager.
            AddPublicationView addPublView = new AddPublicationView(model, view.getFrame(), true);
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
            // Check whether we have rights for this operation
            if (!model.hasRights(model.EDIT)) {
                view.showErrorMessage("You don't have sufficient rights for this operation");
                return;
            }                        
            AddPublicationView addPublView = new AddPublicationView(model, view.getFrame(), false);
            AddPublicationCtrl addPublCtrl = new AddPublicationCtrl(model, addPublView);            
            // Save publication we are going to edit
            model.setEditPublication(model.getSelectedPublication(index));            
            model.setPublicationIndex(index);
            model.loadPublication();            
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
            // Check whether we have rights for this operation
            if (!model.hasRights(model.DELETE)) {
                view.showErrorMessage("You don't have sufficient rights for this operation");
                return;
            }           
            // Check whether it is OK to delete the publication (it cannot be used in an occurrence)
            if (model.checkDelete(index) == false) {
                view.showErrorMessage("This publication cannot be deleted because it is used in an occurence");
                return;
            }
            System.out.println("********* CHECK DELETE OK");
            // Confirm deletion
            if (!view.confirmDelete()) {
                return;
            }
            // Call delete
            model.setPublicationIndex(index);
            // Delete is executed in a separate thread using Task
            Task task = model.deletePublication();
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
                // Refresh the list of publications after a delete
                public void afterStopped(Object value) {
                    model.searchPublication(false);
                    model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                    model.reloadCache();
                }
            };
            progressBar.setTitle(L10n.getString("Delete.ProgressTitle"));
            task.start();
        }
    }    

    /**
     * ActionListener class controlling the <b>Search Publications</b> button on the form.
     */    
    class SearchPublicationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Run DB search. The operation is executed in a separate thread
            Task task = model.searchPublication(true);
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
                // Display the results of a search
                public void afterStopped(Object value) {
                    model.processResults(model.getCurrentFirstRow(), model.getDisplayRows());
                }
            };
            progressBar.setTitle(L10n.getString("Delete.ProgressTitle"));
            task.start();
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
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus 
     *  automaticaly stores value of the field to model.
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
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus 
     *  automaticaly stores value of the field to model.
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
     *  PropertyChange listener for the <strong>collection name field</strong> at the search panel. After losing focus 
     *  automaticaly stores value of the field to model object.
     */
    class CollectionNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchCollectionName(view.getCollectionName());
        }        
    }

    /**
     *  PropertyChange listener for the <strong>journal name field</strong> at the search panel. After 
     *  losing focus automaticaly stores value of the field to model object.
     */    
    class JournalNameFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchJournalName(view.getJournalName());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>reference citation field</strong> at the search panel. After 
     *  losing focus automaticaly stores value of the field to model object.
     */    
    class ReferenceCitationFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchReferenceCitation(view.getReferenceCitation());
        }        
    }    

    /**
     *  PropertyChange listener for the <strong>reference detail field</strong> at the search panel. 
     *  After losing focus automaticaly stores value of the field to model object.
     */        
    class ReferenceDetailFieldPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            model.setSearchReferenceDetail(view.getReferenceDetail());
        }        
    }        
    
    /**
     *  PropertyChange listener for the <strong>rows field</strong> with the number of records to 
     *  display. After losing focus automaticaly stores value of the field to model object.
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
            }
        }        
    }            
}
