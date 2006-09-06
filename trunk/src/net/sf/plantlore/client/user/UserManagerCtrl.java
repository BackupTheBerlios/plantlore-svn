/*
 * UserManagerCtrl.java
 *
 * Created on 23. duben 2006, 11:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.RemoteException;
import net.sf.plantlore.client.history.History;
import net.sf.plantlore.client.metadata.MetadataManager;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.DefaultReconnectDialog;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.PostTaskAction;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;

import org.apache.log4j.Logger;

/**
* Controller for the main UserManager dialog (part of the UserManager MVC).
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class UserManagerCtrl {
    
    /** Instance of a logger */
    private Logger logger;
    /** Model of UserManager MVC */
    private UserManager model;
    /** View of UserManager MVC */
    private UserManagerView view;  
    /** View of add dialog */
    private AddEditUserView addView;
    /** View of edit dialog */
    private AddEditUserView editView;
    /** View of details dialog */
    private AddEditUserView detailView;
    
    /**
     * Creates a new instance of UserManagerCtrl
     *  @param model model of the MetadataManager MVC
     *  @param view  view of the MetadataManager MVC 
     */
    public UserManagerCtrl(UserManager modelUser, UserManagerView viewUser) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = modelUser;
        this.view = viewUser;
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);
          
        view.closeButton.setAction(new DefaultCancelAction(view)); 
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());            
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());    
        view.detailsButton.addActionListener(new detailsUserListener());
        view.addButtons.addActionListener(new addUserListener());
        view.editButtons.addActionListener(new editUserListener());
        view.deleteButton.addActionListener(new deleteUserListener());
        view.searchButton.addActionListener(new searchUserListener());
        view.sortComboBox.addFocusListener(new SortComboFocusListener());
        view.sortAscendingRadioButton.addFocusListener(new SortDirectionRadioFocusListener());
        view.sortDescendingRadioButton.addFocusListener(new SortDirectionRadioFocusListener());
        view.showAllUserRadioBUtton.addFocusListener(new ShowUserDirectionRadioFocusListener());
        view.showCurrentUserRadioButton.addFocusListener(new ShowUserDirectionRadioFocusListener());                              
    }  
    
    /**
     * Reload new data for displaying in view dialog.
     * @param fromRow number of the first row to show in table
     * @param countRow number of rows to retrieve 
     */
    public void reloadData(int fromRow, int countRow) {
    	try {
            model.processResult(fromRow, countRow);     
            view.tableUserList.setModel(new UserManagerTableModel(model));            
            int from = model.getCurrentFirstRow();
            int to = from + view.tableUserList.getRowCount() - 1;
            if (to <= 0 ) {
            	view.displayedValueLabel.setText("0-0");
            } else {
            	view.displayedValueLabel.setText(from + "-" + to);
            }
            view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());   
           // Set button next inactive if we see the last page, in other way set it active.
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);                             
           }else{
        	   view.nextButton.setEnabled(false); 
           }
            //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
    	} catch (Exception ex) {           
           ex.printStackTrace();
           DefaultExceptionHandler.handle(view, ex);           
           return;
        } 
	           
    }
      
   /**
    *  ActionListener class controlling the <b>PREV</b> button on the form.
    *  The button PREV is used for browsing the search results.
    */
   class previousButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
           if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);                   
        	   return;
           }
           // Get previous page of results
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               reloadData(firstRow, model.getDisplayRows());                
           }      
           //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }
   }
    	   
   /**
    * ActionListener class controlling the <b>NEXT</b> button on the form.
    * The button NEXT is used for browsing the search results.    
    */
   class nextButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set 
           if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           // Get next page of result
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount()<=model.getResultRows()) {
               reloadData(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableUserList.getRowCount());                          
           }  
           //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }
   }
    	   
   /**
    *  ActionListener class controlling the text field on the form for set number of rows to displayed.
    */
     class rowSetDisplayChangeListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent) {
           // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           // Save old value 
           int oldValue = model.getDisplayRows();           
           // Check whether new value > 0
           if (view.getDisplayRows() < 1) {
               view.setDisplayRows(oldValue);
               return;
           }           
           // Set new value in the model
           model.setDisplayRows(view.getDisplayRows());
           logger.debug("New display rows: "+view.getDisplayRows());
           // If neccessary reload search results
           if (oldValue != view.getDisplayRows()) {
               reloadData(model.getCurrentFirstRow(), view.getDisplayRows());                         
           }
           // Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }        	   
   }  
     
    /**
    *  ActionListener class controlling the <b>Add user</b> button on the form.
    */  
    class addUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           //set information abut selected operation ADD
           model.setOperation(UserManager.ADD);           
           //create add dialog if dialog not exist and open Add dialog  
           if (addView == null) {
        	   addView = new AddEditUserView(model, view,true);
        	   new AddEditUserCtrl(addView, model);
           }                    
          addView.setAddForm();
          addView.setVisible(true);          
           //User press button close
           if (model.usedClose()) return;
           model.setUsedClose(true);
           //save new record Metadata into database
           Task task = model.addUserRecord();
           task.setPostTaskAction(new PostTaskAction() {
               public void afterStopped(Object value) {
                   if (! model.isFinishedTask()) return;
                   model.setInfoFinishedTask(false);
                   //load data
                   model.searchUser(false);
                   if (model.isError()) {
                       DefaultExceptionHandler.handle(view, model.getException());
                       model.setError(null);
                       model.setException(null);
                       return;
                   }
                   reloadData(1, model.getDisplayRows());
               }
               
           });
           Dispatcher.getDispatcher().dispatch(task, view, false);
           
/*           
           new DefaultProgressBar(task, view, true) {		   							 
   			   @Override
        	   public void afterStopping() {
   				   if (! model.isFinishedTask()) return;
				   model.setInfoFinishedTask(false);
    			   //load data
    	           model.searchUser(false);  
    	           if (model.isError()) {
    	        	   DefaultExceptionHandler.handle(view, model.getException());
                           model.setError(null);
                           model.setException(null);
    	        	   return;
    	           }    	
    	           reloadData(1, model.getDisplayRows());    	             	           
               } 		   		
   		};   			                   	                   
            task.start();                                       
 */
       }
    }
        	 
     /**
    * ActionListener class controlling the <b>Edit user</b> button on the form.
    */  
    class editUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           if (view.tableUserList.getSelectedRow() < 0) {
        	   // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(UserManager.WARNING_SELECTION_TITLE, UserManager.WARNING_SELECTION);
           }else {
               //Set information about selected operation - EDIT
               model.setOperation(UserManager.EDIT);
               //Set information about selected row
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setUserRecord(resultNumber);    
           if (model.getUserRecord().getDropWhen() != null)    {
               //Display information message - user cannot be edited. User was droped
               view.showInfoMessage(UserManager.INFORMATION_EDIT_TITLE, UserManager.INFORMATION_EDIT);
               return;
           }
               if (editView == null) {
            	   	editView = new AddEditUserView(model,view,true);
        	   	new AddEditUserCtrl(editView, model);
               }               
               //Load data and setting of edit dialog
               editView.loadData();                              
               editView.setEditForm();               
               editView.setVisible(true);               
               // User press button close
               if (model.usedClose()) return;
               model.setUsedClose(true);
               //Update User               
               Task task = model.editUserRecord();
               task.setPostTaskAction(new PostTaskAction() {
                   public void afterStopped(Object value) {
                       if (! model.isFinishedTask()) return;
                       model.setInfoFinishedTask(false);
                       //load User
                       model.searchUser(false);                       
                       if (model.isError()) {
                           DefaultExceptionHandler.handle(view, model.getException());
                           model.setError(null);
                           model.setException(null);
                           return;
                       }                        
                       reloadData(1, model.getDisplayRows());                       
                   }                                   
               });                               
               Dispatcher.getDispatcher().dispatch(task, view, false);

               
/*               
               new DefaultProgressBar(task, view, true) {		   							 
          		   @Override
            	   public void afterStopping() {
          			   if (! model.isFinishedTask()) return;
					   model.setInfoFinishedTask(false);
          			   //load User          				
                        if (model.isError()) return;
                        view.tableUserList.setModel(new UserManagerTableModel(model));                         
                     } 		   					
          		};          			                   	                  
                task.start();                                       
 */
              }
       }
    }

    /**
    *  ActionListener class controlling the <b>Details</b> button on the form.
    */  
    class detailsUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           if (view.tableUserList.getSelectedRow() < 0) {    
               // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(UserManager.WARNING_SELECTION_TITLE, UserManager.WARNING_SELECTION);
           } else {
               //Set information about selected operation - DETAILS
                model.setOperation(UserManager.DETAIL);
               //Set information about selected row
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setUserRecord(resultNumber);                             
               if (detailView == null) {
               		detailView = new AddEditUserView(model, view,true);
                        new AddEditUserCtrl(detailView, model);
               }
               //Load data and setting of detail dialog            
               detailView.loadData();               
               detailView.setDetailsForm();
               detailView.setVisible(true);              
           }          
       }
    }
    	
    /**
    *  ActionListener class controlling the <b>Delete metadata</b> button on the form.
    */  
    class deleteUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           if (view.tableUserList.getSelectedRow() < 0) {    
               // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(UserManager.WARNING_SELECTION_TITLE, UserManager.WARNING_SELECTION);          
           } else {
               //Set information about selected row
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1; 
               model.setUserRecord(resultNumber);
               //Test if record has been deleted
               if (model.getUserRecord().getDropWhen() != null) {
            	   view.showInfoMessage(UserManager.INFORMATION_DELETE_TITLE, UserManager.INFORMATION_DELETE);
            	   return;
               }
               //Test if user delete himself 
               if (model.deleteHimself()) {
                   view.showInfoMessage(UserManager.INFORMATION_DELETE_TITLE, UserManager.INFORMATION_DELETE_HIMSELF);
            	   return;
               }
               int okCancle = view.showQuestionMessage(UserManager.QUESTION_DELETE_TITLE, UserManager.QUESTION_DELETE);               
               if (okCancle == 0){
                   //Button OK was press
                   logger.debug("Button OK was press.");
                   //Delete selected record
                   Task task = model.deleteUserRecord();
                   task.setPostTaskAction(new PostTaskAction() {

                       public void afterStopped(Object value) {
                           if (! model.isFinishedTask()) return;
                           model.setInfoFinishedTask(false);
                           // load User
                           model.searchUser(false);
                           if (model.isError()) {
                               DefaultExceptionHandler.handle(view, model.getException());
                               model.setError(null);
                               model.setException(null);
                               return;
                           }
                           reloadData(1, model.getDisplayRows());
                       }
                   });
                   Dispatcher.getDispatcher().dispatch(task, view, false);
                   
/*
                       new DefaultProgressBar(task, view, true) {
                                           @Override
                           public void afterStopping() {
                                   if (! model.isFinishedTask()) return;
                                        model.setInfoFinishedTask(false);
                                       // load User
                                       model.searchUser(false);
                                       if (model.isError()) {
                                           DefaultExceptionHandler.handle(view, model.getException());
                                           model.setError(null);
                                           model.setException(null);
                                           return;
                                   }
                                       reloadData(1, model.getDisplayRows());
                              }
                                                };
                                    task.start();
 */
               }else {
                   logger.debug("Button Cancle was press.");
               }
           }
       }
   }    	       
    
    /**
     * ActionListener class controlling the <b>Search/Sort</b> button on the form.
     */
    class searchUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
    	   model.setWholeName(view.wholeNameSearchText.getText());           
           model.setLogin(view.loginSearchText.getText());
           model.setEmail(view.emailSearchText.getText());
           model.setAddress(view.addressSearchText.getText());
           if (!(view.checkNonEmpty("login") || view.checkNonEmpty("name") ||
               view.checkNonEmpty("email") || view.checkNonEmpty("address"))) {
        	   // Display info message  saying that no search field has been filled in.
        	   view.showInfoMessage(UserManager.INFORMATION_SEARCH_TITLE, UserManager.INFORMATION_SEARCH);
               model.setLogin("%");
           }            
                     
           //Load User with specific conditions
           Task task = model.searchUser(true);   
           task.setPostTaskAction(new PostTaskAction() {
               public void afterStopped(Object value) {
                   if (! model.isFinishedTask()) return;
                   model.setInfoFinishedTask(false);
                   if (model.getDisplayRows() <= 0) {
                       model.setDisplayRows(UserManager.DEFAULT_DISPLAY_ROWS);
                   }
                   //No record in result - show message to user
                   if (model.getResultRows() < 1) {
                       view.showInfoMessage(UserManager.INFORMATION_RESULT_TITLE,UserManager.INFORMATION_RESULT);
                   }
                   reloadData(1, model.getDisplayRows());
               }
               
           });
           Dispatcher.getDispatcher().dispatch(task, view, false);

/*
           new DefaultProgressBar(task, view, true) {		   								  						    
        	   @Override
        	   public void afterStopping() {
        		   if (! model.isFinishedTask()) return;
				   model.setInfoFinishedTask(false);
					if (model.getDisplayRows() <= 0) {
		               model.setDisplayRows(UserManager.DEFAULT_DISPLAY_ROWS);
		           }
		           //No record in result - show message to user
		           if (model.getResultRows() < 1) {
		               view.showInfoMessage(UserManager.INFORMATION_RESULT_TITLE,UserManager.INFORMATION_RESULT);
		           }
		           reloadData(1, model.getDisplayRows());		            		           
               } 		   					
			};				                   	                   
            task.start();                                                                  
 */
        }
    }

    /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class SortComboFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSortField(view.sortComboBox.getSelectedIndex());
            logger.debug("Sort field: "+view.sortComboBox.getSelectedIndex());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }    
    
    /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class SortDirectionRadioFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSortDirection(view.getSortDirection());
            logger.debug("Sort asc, dsc: "+ view.getSortDirection());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }      
    
    /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class ShowUserDirectionRadioFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setShowUserDirection(view.getShowUserDirection());
            logger.debug("Show all user or only current user: "+ view.getShowUserDirection());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }                
}
