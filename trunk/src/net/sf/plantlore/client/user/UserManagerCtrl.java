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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;

import net.sf.plantlore.client.metadata.AddEditMetadataCtrl;
import net.sf.plantlore.client.metadata.AddEditMetadataView;
import net.sf.plantlore.client.metadata.MetadataManager;
import net.sf.plantlore.client.metadata.MetadataManagerTableModel;
import net.sf.plantlore.client.metadata.MetadataManagerCtrl.escapeKeyPressed;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.ProgressBar;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.User;
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
    /** View of add dialog*/
    private AddEditUserView addView;
    /** Controler of add dialog*/
    private AddEditUserCtrl addCtrl;
    /** View of edit dialog*/
    private AddEditUserView editView;
    /** Controler of edit dialog*/
    private AddEditUserCtrl editCtrl;
    /** View of details dialog*/
    private AddEditUserView detailView;
    /** Controler of details dialog*/
    private AddEditUserCtrl detailCtrl;
    
    /**
     * Creates a new instance of UserManagerCtrl
     *  @param model model of the MetadataManager MVC
     *  @param view  view of the MetadataManager MVC 
     */
    public UserManagerCtrl(UserManager modelUser, UserManagerView viewUser) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = modelUser;
        this.view = viewUser;
          
        view.closeButton.addActionListener(new closeButtonListener());
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
        
        //Add key listeners
        view.closeButton.addKeyListener(new escapeKeyPressed());
        view.previousButton.addKeyListener(new escapeKeyPressed());
        view.nextButton.addKeyListener(new escapeKeyPressed());
        view.addButtons.addKeyListener(new escapeKeyPressed());
        view.toDisplayValueTextField.addKeyListener(new escapeKeyPressed());
        view.helpButton.addKeyListener(new escapeKeyPressed());
        view.editButtons.addKeyListener(new escapeKeyPressed());
        view.deleteButton.addKeyListener(new escapeKeyPressed());
        view.detailsButton.addKeyListener(new escapeKeyPressed());
        view.searchButton.addKeyListener(new escapeKeyPressed());
        view.sortComboBox.addKeyListener(new escapeKeyPressed());
        view.tableUserList.addKeyListener(new escapeKeyPressed());
        view.sortAscendingRadioButton.addKeyListener(new escapeKeyPressed());
        view.sortDescendingRadioButton.addKeyListener(new escapeKeyPressed());
        view.addressSearchText.addKeyListener(new escapeKeyPressed());           
        view.emailSearchText.addKeyListener(new escapeKeyPressed());
        view.loginSearchText.addKeyListener(new escapeKeyPressed());
        view.wholeNameSearchText.addKeyListener(new escapeKeyPressed());
        view.addKeyListener(new escapeKeyPressed());
        
        //Search user and Load data
        Task task = model.searchUser(true);        
        
        ProgressBar progressBar = new ProgressBar(task, view, true) {		   							 						
        	public void exceptionHandler(Exception e) {
				if (e instanceof DBLayerException) {	   									   							
   					DBLayerException dbex = (DBLayerException) e;	
   					view.showErrorMessage(MetadataManager.ERROR_DBLAYER_TITLE,MetadataManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
   					getTask().stop();
   					return;
   				}
   				if (e instanceof RemoteException) {	 
   					RemoteException remex = (RemoteException) e;		
   					view.showErrorMessage(MetadataManager.ERROR_REMOTE_TITLE, MetadataManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
   					getTask().stop();
   					return;
   				}
   				view.showErrorMessage(MetadataManager.ERROR_UNKNOWEN_TITLE, MetadataManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
   				logger.error(e);
   			}

			public void afterStopping() {
				//Process result
		        model.processResult(1, model.getDisplayRows());
		        //Update view dialog
		        view.tableUserList.setModel(new UserManagerTableModel(model));
		        int from = model.getCurrentFirstRow();
                int to = from + view.tableUserList.getRowCount() - 1;
                view.displayedValueLabel.setText(from + "-" + to);
                view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
           } 		   					
		};
		progressBar.setTitle(MetadataManager.PROGRESS_SEARCH);	                   	                   
        task.start();                                       
    }                        
   
    /**     
     * KeyListener class controlling the pressing key ESCAPE.
     * On key ESCAPE hides the view.     
     */
    public class escapeKeyPressed implements KeyListener {
    	 	 public void keyPressed(KeyEvent evt){
    	 		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
    	 			logger.debug("ESCAPE: " + view.getFocusOwner());
    	 			view.close();
    	 		}    	 		     	 		 
    	 	 }
    	      public void keyReleased(KeyEvent evt) {}    	     
    	      public void keyTyped(KeyEvent evt) {}    	         
    }
 
    
    /**
     * ActionListener class controlling the <b>CLOSE</b> button on the form.
     * On Close hides the view.
     */
   class closeButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   view.close();
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
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           // Get previous page of results
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.isError()) return;
               if (model.getCurrentFirstRow() > 1){
               }
               view.tableUserList.setModel(new UserManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableUserList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);
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
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           // Get next page of result
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableUserList.getRowCount());
               if (model.isError()) return;
               view.tableUserList.setModel(new UserManagerTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tableUserList.getRowCount() - 1;
               if (to <= 0){
            	   view.displayedValueLabel.setText("0-0");
               }else {
            	   view.displayedValueLabel.setText(from + "-" + to);
               }               
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
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
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
               model.processResult(model.getCurrentFirstRow(), view.getDisplayRows());
               if (model.isError()) return;
               view.tableUserList.setModel(new UserManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableUserList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
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
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           //set information abut selected operation ADD
           model.setOperation("ADD");           
           //create add dialog if dialog not exist and open Add dialog
           if (addView == null) {
       	   	    addView = new AddEditUserView(model, view,true);
          		addCtrl = new AddEditUserCtrl(addView, model);
          }
          addView.setAddForm();
          addView.setVisible(true);          
           //User press button close
           if (model.usedClose()) return;
           //save new record Metadata into database
           Task task = model.addUserRecord();
           
           ProgressBar progressBar = new ProgressBar(task, view, true) {		   							 
   			public void exceptionHandler(Exception e) {
   				if (e instanceof DBLayerException) {	   									   							
   					DBLayerException dbex = (DBLayerException) e;	
   					view.showErrorMessage(UserManager.ERROR_DBLAYER_TITLE, UserManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
   					getTask().stop();
   					return;
   				}
   				if (e instanceof RemoteException) {	 
   					RemoteException remex = (RemoteException) e;		
   					view.showErrorMessage(UserManager.ERROR_REMOTE_TITLE,UserManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
   					getTask().stop();
   					return;
   				}
   				view.showErrorMessage(UserManager.ERROR_UNKNOWEN_TITLE, UserManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
   				logger.error(e);
   			}
   			
   			public void afterStopping() {
    			   //load data
    	           model.searchUser(false);           
    	           model.processResult(1, model.getDisplayRows());
    	           if (model.isError()) return;
    	           view.tableUserList.setModel(new UserManagerTableModel(model));                      
    	           view.displayedValueLabel.setText(1 + "-" + view.tableUserList.getRowCount());
    	           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());    	           
               } 		   		
   		};
   		progressBar.setTitle(UserManager.PROGRESS_ADD);	                   	                   
        task.start();                                       
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
        	   view.showErrorMessage(UserManager.ERROR_TITLE, model.getError());
        	   return;
           }
           if (view.tableUserList.getSelectedRow() < 0) {
        	   // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(UserManager.WARNING_SELECTION_TITLE, UserManager.WARNING_SELECTION);
           }else {
               //Set information about selected operation - EDIT
               model.setOperation("EDIT");
               //Set information about selected row
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setUserRecord(resultNumber);
               //Create edit dialog
               User user = model.getUserRecord(); 
               if (editView == null) {
            	   	editView = new AddEditUserView(model,view,true);
        	   		editCtrl = new AddEditUserCtrl(editView, model);
               }               
               //Load data and setting of edit dialog
               editView.loadData();                              
               editView.setEditForm();               
               editView.setVisible(true);               
               // User press button close
               if (model.usedClose()) return;
               //Update User               
               Task task = model.editUserRecord();
               
               ProgressBar progressBar = new ProgressBar(task, view, true) {		   							 
          			public void exceptionHandler(Exception e) {
          				if (e instanceof DBLayerException) {	   									   							
           					DBLayerException dbex = (DBLayerException) e;	
           					view.showErrorMessage(UserManager.ERROR_DBLAYER_TITLE,UserManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
           					getTask().stop();
           					return;
           				}
           				if (e instanceof RemoteException) {	 
           					RemoteException remex = (RemoteException) e;		
           					view.showErrorMessage(UserManager.ERROR_REMOTE_TITLE,UserManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
           					getTask().stop();
           					return;
           				}
           				view.showErrorMessage(UserManager.ERROR_UNKNOWEN_TITLE, UserManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
           				logger.error(e);
           			}

          			public void afterStopping() {
          			   //load User          				
                        if (model.isError()) return;
                        view.tableUserList.setModel(new UserManagerTableModel(model));                         
                     } 		   					
          		};
          		progressBar.setTitle(UserManager.PROGRESS_EDIT);	                   	                   
                task.start();                                       
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
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           if (view.tableUserList.getSelectedRow() < 0) {    
               // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(UserManager.WARNING_SELECTION_TITLE, UserManager.WARNING_SELECTION);
           } else {
               //Set information about selected operation - DETAILS
                model.setOperation("DETAILS");
               //Set information about selected row
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setUserRecord(resultNumber);
               //Create detail dialog
               User user = model.getUserRecord();               
               if (detailView == null) {
               		detailView = new AddEditUserView(model, view,true);
           			detailCtrl = new AddEditUserCtrl(detailView, model);
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
        	   view.showErrorMessage(UserManager.ERROR_TITLE, model.getError());
        	   return;
           }
           if (view.tableUserList.getSelectedRow() < 0) {    
               // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(UserManager.WARNING_SELECTION_TITLE, UserManager.WARNING_SELECTION);          
           } else {
               //Set information about selected row
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1; 
               model.setUserRecord(resultNumber);
               //Test if record was been deleted
               if (model.getUserRecord().getDropWhen() != null) {
            	   view.showInfoMessage(UserManager.INFORMATION_DELETE_TITLE, UserManager.INFORMATION_DELETE);
            	   return;
               }
        	   int okCancle = view.showQuestionMessage(UserManager.QUESTION_DELETE_TITLE, UserManager.QUESTION_DELETE);               
               if (okCancle == 0){
            	   //Button OK was press
            	   logger.debug("Button OK was press.");
	               //Delete selected record
	               Task task = model.deleteUserRecord();
	               
	               ProgressBar progressBar = new ProgressBar(task, view, true) {		   								  		   									
					public void exceptionHandler(Exception e) {
						if (e instanceof DBLayerException) {	   									   							
		   					DBLayerException dbex = (DBLayerException) e;	
		   					view.showErrorMessage(UserManager.ERROR_DBLAYER_TITLE,UserManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
		   					getTask().stop();
		   					return;
		   				}
		   				if (e instanceof RemoteException) {	 
		   					RemoteException remex = (RemoteException) e;		
		   					view.showErrorMessage(UserManager.ERROR_REMOTE_TITLE,UserManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
		   					getTask().stop();
		   					return;
		   				}
		   				view.showErrorMessage(UserManager.ERROR_UNKNOWEN_TITLE, UserManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
		   				logger.error(e);
		   			}
	
                    public void afterStopping() {
                       // load User
	   	               model.searchUser(false);               
	   	               model.processResult(1, model.getDisplayRows());
	   	               if (model.isError()) return;
	   	               view.tableUserList.setModel(new UserManagerTableModel(model));                      
	   	               view.displayedValueLabel.setText(1 + "-" + view.tableUserList.getRowCount());
	   	               view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());		   	               
                                }
		   			};
		   			progressBar.setTitle(UserManager.PROGRESS_DELETE);	                   	                   
		            task.start();                                                                  
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
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
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
           
           ProgressBar progressBar = new ProgressBar(task, view, true) {		   								  						    
				public void exceptionHandler(Exception e) {
					if (e instanceof DBLayerException) {	   									   							
	   					DBLayerException dbex = (DBLayerException) e;	
	   					view.showErrorMessage(UserManager.ERROR_DBLAYER_TITLE, UserManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
	   					getTask().stop();
	   					return;
	   				}
	   				if (e instanceof RemoteException) {	 
	   					RemoteException remex = (RemoteException) e;		
	   					view.showErrorMessage(UserManager.ERROR_REMOTE_TITLE,UserManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
	   					getTask().stop();
	   					return;
	   				}
	   				view.showErrorMessage(UserManager.ERROR_UNKNOWEN_TITLE, UserManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
	   				logger.error(e);
	   			}

				public void afterStopping() {
					if (model.getDisplayRows() <= 0) {
		               model.setDisplayRows(UserManager.DEFAULT_DISPLAY_ROWS);
		           }
		           //No record in result - show message to user
		           if (model.getResultRows() < 1) {
		               view.showInfoMessage(UserManager.INFORMATION_RESULT_TITLE,UserManager.INFORMATION_RESULT);
		           }
		           model.processResult(1, model.getDisplayRows());
		           if (model.isError()) return;		           
		           view.tableUserList.setModel(new UserManagerTableModel(model)); 		           
		           view.displayedValueLabel.setText(model.getCurrentDisplayRows());  
		           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString()); 		           
               } 		   					
			};
			progressBar.setTitle(UserManager.PROGRESS_SEARCH);	                   	                   
            task.start();                                                                  
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
