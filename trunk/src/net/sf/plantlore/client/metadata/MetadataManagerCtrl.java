/*
 * MetadataManagerCtrl.java
 *
 * Created on 23. duben 2006, 11:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;

import net.sf.plantlore.client.history.WholeHistoryCtrl.escapeKeyPressed;
import net.sf.plantlore.common.ProgressBar;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 * Controller for the main MetadataManager dialog (part of the MetadataManager MVC).
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class MetadataManagerCtrl {
    
	/** Instance of a logger */
    private Logger logger;
    /** Model of MetadataManager MVC */
    private MetadataManager model;
    /** View of MetadataManager MVC */
    private MetadataManagerView view;
    
    /** Creates a new instance of MetadataManagerCtrl 
     *  @param model model of the MetadataManager MVC
     *  @param view  view of the MetadataManager MVC 
     */
    public MetadataManagerCtrl(MetadataManager modelPar, MetadataManagerView viewPar) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = modelPar;
        this.view = viewPar;
          
        view.closeButton.addActionListener(new closeButtonListener());
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());            
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());    
        view.detailsButton.addActionListener(new detailsMetadataListener());
        view.addButtons.addActionListener(new addMetadataListener());
        view.editButtons.addActionListener(new editMetadataListener());
        view.deleteButton.addActionListener(new deleteMetadataListener());
        view.searchButton.addActionListener(new searchUserListener());
        view.sortAscendingRadioButton.addFocusListener(new SortDirectionRadioFocusListener());
        view.sortDescendingRadioButton.addFocusListener(new SortDirectionRadioFocusListener());
        view.sortField.addFocusListener(new SortComboFocusListener());
        
        // Add key listeners
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
        view.sortField.addKeyListener(new escapeKeyPressed());
        view.tableMetadataList.addKeyListener(new escapeKeyPressed());
        view.sortAscendingRadioButton.addKeyListener(new escapeKeyPressed());
        view.sortDescendingRadioButton.addKeyListener(new escapeKeyPressed());
        view.sourceInstitutionIdText.addKeyListener(new escapeKeyPressed());           
        view.sourceIdText.addKeyListener(new escapeKeyPressed());
        view.dataSetTitleText.addKeyListener(new escapeKeyPressed());
        view.addKeyListener(new escapeKeyPressed());        
        
        //Search metadata
        Task task = model.searchMetadata(true);
        
        ProgressBar progressBar = new ProgressBar(task, view, true) {		   							 			
			private static final long serialVersionUID = -3944058265290571972L;
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
		        view.tableMetadataList.setModel(new MetadataManagerTableModel(model));
		        int from = model.getCurrentFirstRow();
                int to = from + view.tableMetadataList.getRowCount() - 1;
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
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableMetadataList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);
           }      
           //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableMetadataList.getRowCount() - 1 < model.getResultRows()) {
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
    	   //Check whether an error flag is set 
           if (model.isError()) {
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           // Get next page of result
           if (model.getCurrentFirstRow()+ view.tableMetadataList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableMetadataList.getRowCount());
               if (model.isError()) return;
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tableMetadataList.getRowCount() - 1;
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
           if (model.getCurrentFirstRow()+ view.tableMetadataList.getRowCount() - 1 < model.getResultRows()) {
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
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableMetadataList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
           }
           // Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableMetadataList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }        	   
   }  
 
    /**
    * ActionListener class controlling the <b>Add metadata</b> button on the form.
    */  
    class addMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   //Check whether an error flag is set
    	   if (model.isError()) {
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           //set information abut selected operation ADD
           model.setOperation("ADD");           
           //create and open Add dialog
           AddEditMetadataView addView = new AddEditMetadataView(model, view,true);
           new AddEditMetadataCtrl(addView, model);
           addView.setAddForm();
           addView.setVisible(true); 
           //User press button close
           if (model.usedClose()) return;
           //save new record Metadata into database
           Task task = model.addMetedataRecord();
           
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
   					view.showErrorMessage(MetadataManager.ERROR_REMOTE_TITLE,MetadataManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
   					getTask().stop();
   					return;
   				}
   				view.showErrorMessage(MetadataManager.ERROR_UNKNOWEN_TITLE, MetadataManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
   				logger.error(e);
   			}
   			
   			public void afterStopping() {
    			   //load metadata
    	           model.searchMetadata(false);           
    	           model.processResult(1, model.getDisplayRows());
    	           if (model.isError()) return;
    	           view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                      
    	           view.displayedValueLabel.setText(1 + "-" + view.tableMetadataList.getRowCount());
    	           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());    	           
               } 		   		
   		};
   		progressBar.setTitle(MetadataManager.PROGRESS_ADD);	                   	                   
        task.start();                                       
       }
    }
    
     /**
    * ActionListener class controlling the <b>Edit metadata</b> button on the form.
    */  
    class editMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   //  whether an error flag is set
    	   if (model.isError()) {
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           if (view.tableMetadataList.getSelectedRow() < 0) {
        	   // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(MetadataManager.WARNING_SELECTION_TITLE, MetadataManager.WARNING_SELECTION);
           } else {
               //Set information about selected operation - EDIT
               model.setOperation("EDIT");
               //Set information about selected row
               int resultNumber = view.tableMetadataList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setMetadataRecord(resultNumber);
               //Create edit dialog
               AddEditMetadataView editView = new AddEditMetadataView(model, view,true);
               new AddEditMetadataCtrl(editView, model);
               //Load data and setting of edit dialog
               editView.loadData();                              
               editView.setEditForm();               
               editView.setVisible(true); 
               // User press button close
               if (model.usedClose()) return;
               //Update metadata               
               Task task = model.editMetadataRecord();
               
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
           					view.showErrorMessage(MetadataManager.ERROR_REMOTE_TITLE,MetadataManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
           					getTask().stop();
           					return;
           				}
           				view.showErrorMessage(MetadataManager.ERROR_UNKNOWEN_TITLE, MetadataManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
           				logger.error(e);
           			}

          			public void afterStopping() {
          			   //load metadata          				
                        if (model.isError()) return;
                        view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                         
                     } 		   					
          		};
          		progressBar.setTitle(MetadataManager.PROGRESS_EDIT);	                   	                   
                task.start();                                       
              }
       }
    }
    
    /**
    * ActionListener class controlling the <b>Details</b> button on the form.
    */  
    class detailsMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           if (view.tableMetadataList.getSelectedRow() < 0) {    
               // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(MetadataManager.WARNING_SELECTION_TITLE, MetadataManager.WARNING_SELECTION);
           } else {
               //Set information about selected operation - DETAILS
                model.setOperation("DETAILS");
               //Set information about selected row
               int resultNumber = view.tableMetadataList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setMetadataRecord(resultNumber);
               //Create detail dialog
               AddEditMetadataView detailsView = new AddEditMetadataView(model, view,true);
               new AddEditMetadataCtrl(detailsView, model);
               //Load data and setting of detail dialog
               detailsView.loadData();               
               detailsView.setDetailsForm();
               detailsView.setVisible(true); 
               model.setUsedClose(false);
           }          
       }
    }
    
     /**
    * ActionListener class controlling the <b>Delete metadata</b> button on the form.
    */  
    class deleteMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           if (view.tableMetadataList.getSelectedRow() < 0) {    
               // Display warning message saying that no row of table has been selected.
               view.showWarningMessage(MetadataManager.WARNING_SELECTION_TITLE, MetadataManager.WARNING_SELECTION);
           } else {
               //Set information about selected row
               int resultNumber = view.tableMetadataList.getSelectedRow() + model.getCurrentFirstRow()-1; 
               model.setMetadataRecord(resultNumber);
               //Test if record can be deleted
               if (!model.checkDelete(resultNumber)) {
                   view.showErrorMessage(MetadataManager.ERROR_TITLE, MetadataManager.ERROR_CHECK_DELETE);
               } else {
            	   int okCancle = view.showQuestionMessage(MetadataManager.QUESTION_DELETE_TITLE, MetadataManager.QUESTION_DELETE);               
                   if (okCancle == 0){
                	   //Button OK was press
                	   logger.debug("Button OK was press.");
		               //delete selected record
		               Task task = model.deleteMetadataRecord();
		               
		               ProgressBar progressBar = new ProgressBar(task, view, true) {		   								  		   				
						private static final long serialVersionUID = -6156468821508998437L;
						public void exceptionHandler(Exception e) {
							if (e instanceof DBLayerException) {	   									   							
			   					DBLayerException dbex = (DBLayerException) e;	
			   					view.showErrorMessage(MetadataManager.ERROR_DBLAYER_TITLE,MetadataManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
			   					getTask().stop();
			   					return;
			   				}
			   				if (e instanceof RemoteException) {	 
			   					RemoteException remex = (RemoteException) e;		
			   					view.showErrorMessage(MetadataManager.ERROR_REMOTE_TITLE,MetadataManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
			   					getTask().stop();
			   					return;
			   				}
			   				view.showErrorMessage(MetadataManager.ERROR_UNKNOWEN_TITLE, MetadataManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
			   				logger.error(e);
			   			}
		
                                    public void afterStopping() {
                                       // load metadata
		   	               model.searchMetadata(false);               
		   	               model.processResult(1, model.getDisplayRows());
		   	               if (model.isError()) return;
		   	               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                      
		   	               view.displayedValueLabel.setText(1 + "-" + view.tableMetadataList.getRowCount());
		   	               view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());		   	               
		                } 		   					
		   			};
		   			progressBar.setTitle(MetadataManager.PROGRESS_DELETE);	                   	                   
		            task.start();                                                                  
		          }else {
		        	  logger.debug("Button Cancle was press.");
		          }
	           }
           }
       }
   }       
    
   /**
    * * ActionListener class controlling the <b>Search/Sort</b> button on the form.    
    */
    class searchUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   view.showErrorMessage(MetadataManager.ERROR_TITLE, model.getError());
        	   return;
           }
           model.setSourceInstitutionId(view.sourceInstitutionIdText.getText());           
           model.setSourceId(view.sourceIdText.getText());
           model.setDataSetTitle(view.dataSetTitleText.getText());
            if (!(view.checkNonEmpty("sourceInstitutionId") || view.checkNonEmpty("sourceId") ||
               view.checkNonEmpty("dataSetTitle"))) {
                //Display info message  saying that no search field has been filled in.
               view.showInfoMessage(MetadataManager.INFORMATION_SEARCH_TITLE, MetadataManager.INFORMATION_SEARCH);
               model.setSourceInstitutionId("%");
           }          
           //Load metadata with specific conditions
           Task task = model.searchMetadata(true);   
           
           ProgressBar progressBar = new ProgressBar(task, view, true) {		   								  			
			    private static final long serialVersionUID = -7147752276335991652L; 
				public void exceptionHandler(Exception e) {
					if (e instanceof DBLayerException) {	   									   							
	   					DBLayerException dbex = (DBLayerException) e;	
	   					view.showErrorMessage(MetadataManager.ERROR_DBLAYER_TITLE, MetadataManager.ERROR_DBLAYER+ "\n" + dbex.getMessage());   																				   					
	   					getTask().stop();
	   					return;
	   				}
	   				if (e instanceof RemoteException) {	 
	   					RemoteException remex = (RemoteException) e;		
	   					view.showErrorMessage(MetadataManager.ERROR_REMOTE_TITLE,MetadataManager.ERROR_REMOTE+ "\n" + remex.getMessage());   																				   					
	   					getTask().stop();
	   					return;
	   				}
	   				view.showErrorMessage(MetadataManager.ERROR_UNKNOWEN_TITLE, MetadataManager.ERROR_UNKNOWEN+ "\n" + e.getMessage());   						
	   				logger.error(e);
	   			}

				public void afterStopping() {
					if (model.getDisplayRows() <= 0) {
		               model.setDisplayRows(MetadataManager.DEFAULT_DISPLAY_ROWS);
		           }
		           //No record in result - show message to user
		           if (model.getResultRows() < 1) {
		               view.showInfoMessage(MetadataManager.INFORMATION_RESULT_TITLE,MetadataManager.INFORMATION_RESULT);
		           }
		           model.processResult(1, model.getDisplayRows());
		           if (model.isError()) return;		           
		           view.tableMetadataList.setModel(new MetadataManagerTableModel(model)); 		           
		           view.displayedValueLabel.setText(model.getCurrentDisplayRows());  
		           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString()); 		           
               } 		   					
			};
			progressBar.setTitle(MetadataManager.PROGRESS_SEARCH);	                   	                   
            task.start();                                                                  
       }
    }
    
    /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus 
     *  automaticaly stores value of the field to model.
     */
    class SortComboFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSortField(view.sortField.getSelectedIndex());
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
            logger.debug("Sort asc, dsc: "+ view.getSortDirection());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }    
}
