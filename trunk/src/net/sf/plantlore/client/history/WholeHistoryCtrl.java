/*
 * WholeHistoryCtrl.java
 *
 * Created on 14. duben 2006, 15:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

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
 * Controller for the main WholeHistory dialog (part of the WholeHistory MVC).
 *
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class WholeHistoryCtrl {
   
	/** Instance of a logger */
    private Logger logger;
    /** Model of the WholeHistory MVC */
    private History model;
    /** View of the WholeHistory MVC */
    private WholeHistoryView view;
    
    /** 
     *  Creates a new instance of WholeHistoryCtrl 
     *  @param model model of the WholeHistory MVC
     *  @param view  view of the WholeHistory MVC
     */
    public WholeHistoryCtrl(History model, WholeHistoryView view) {
      
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);
          
        // Add action listeners to buttons        
        view.closeButton.setAction(new DefaultCancelAction(view)); 
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());     
        view.undoToDateButton.addActionListener(new undoToDateButtonListener());
        view.detailsButton.addActionListener(new detailsHistoryListener());
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener()); 
        view.clearHistoryButton.addActionListener(new clearHistoryListener());       
    }
 
    /**
     * Reload new data for displaying in view dialog.
     * @param fromRow number of the first row to show in table. Number of the first row to retraieve is 1.
     * @param countRow number of rows to retrieve 
     */
    public void reloadData(int fromRow, int countRow) {
    	try {
    		model.processResult(fromRow, countRow);                   
            view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
            int from = model.getCurrentFirstRow();
            int to = from + view.tableHistoryList.getRowCount() - 1;
            if (to <= 0){
         	   view.displayedValueLabel.setText("0-0");
            }else {
         	   view.displayedValueLabel.setText(from + "-" + to);
            }    
            //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
    	 } catch (Exception ex) {
               logger.error("Reload data failed.");               
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
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount() - 1 < model.getResultRows()) {
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
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount()<=model.getResultRows()) {
               reloadData(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableHistoryList.getRowCount());                       
           }  
           //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }
   }
   
   /**
    * ActionListener class controlling the text field on the form for set number of rows to displayed.  
    */
    class rowSetDisplayChangeListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent) {
    	   // Check whether an error flag is set
    	   if (model.isError() && view.tableHistoryList.getRowCount() > 0) {
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
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }        	   
   }
   
    /**
     * ActionListener class controlling the <b>UndoToSelected</b> button on the form.
     * On UndoToSelected All changes from now to selected date will be restored.
     */  
    class undoToDateButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError() && view.tableHistoryList.getRowCount() > 0) {
        	  Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
           if (view.tableHistoryList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               logger.debug("Undo to date - id of selected row is: "+ view.tableHistoryList.getSelectedRow());
               int selectedRow = view.tableHistoryList.getSelectedRow();
               int toResult = selectedRow + model.getCurrentFirstRow();
               Object toDate = view.tableHistoryList.getValueAt(selectedRow, 0);                   	       
               model.clearEditObjectList();
               model.undoToDate(toResult);
               // Check whether an error flag after processing records is set
               if (! model.isError()) {
	               int okCancle = view.messageUndo(toDate.toString());     
	               if (okCancle == 0){
	                   //Button OK was press
	                   logger.debug("Button OK was press.");    
	                   Task task = model.commitUpdate(toResult, false);
	                   task.setPostTaskAction(new PostTaskAction() {
                                    public void afterStopped(Object value) {
                                        logger.debug("Load Data");	   
                                        try {
                                           if (! model.isFinishedTask()) return;
                                           model.setInfoFinishedTask(false);	
                                           model.searchWholeHistoryData();
                                           reloadData(1,model.getDisplayRows());
                                           view.setCountResult(model.getResultRows());
                                        } catch (Exception ex) {                                                                   
                                           ex.printStackTrace();
                                           DefaultExceptionHandler.handle(view, ex);                                                                   
                                           return;
                                        } 
                                    }                               
                           });
                           Dispatcher.getDispatcher().dispatch(task, view, false);
                           /*
	                   new DefaultProgressBar(task, view, true) {		   										
                                @Override
                                    public void afterStopping() {
                                        logger.debug("Load Data");	   
                                        try {
                                           if (! model.isFinishedTask()) return;
                                           model.setInfoFinishedTask(false);	
                                           model.searchWholeHistoryData();
                                           reloadData(1,model.getDisplayRows());
                                           view.setCountResult(model.getResultRows());
                                        } catch (Exception ex) {                                                                   
                                           ex.printStackTrace();
                                           DefaultExceptionHandler.handle(view, ex);                                                                   
                                           return;
                                        } 
                                    }
                            };		   					                   	                  
	                    task.start(); */
	                   
	               } else {	                       	                     
	                       logger.debug("Button Cancle was press.");
	               } 
                } else {
                    Exception ex = model.getException();  
                    ex.printStackTrace();
                    DefaultExceptionHandler.handle(view, ex);                                   
                    model.setError(null);                        
                }
            }
         }
     }
    
    /**
     * ActionListener class controlling the <b>Details</b> button on the form.
     * Display dialog with details of record.
     */  
    class detailsHistoryListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError() && view.tableHistoryList.getRowCount() > 0) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }        
           if (view.tableHistoryList.getSelectedRow() < 0) {
        	   // No row is selected
               view.messageSelection();
           } else {
               // Generate and display details of record
               int resultNumber = view.tableHistoryList.getSelectedRow() + model.getCurrentFirstRow()-1;             
               String detailsMessage = model.getDetailsMessage(resultNumber);
               DetailsHistoryView detailsView = new DetailsHistoryView(view, true);
               new DetailsHistoryCtrl(detailsView);
               detailsView.setDetailsMessage(detailsMessage);
               detailsView.setVisible(true);               
           } 
           if (model.isError()) {
	           Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   model.setError(null); 
                   model.setException(null);
           }
       }
    }
    
    /**
    *   ActionListener class controlling the <b>CleareDatabase</b> button on the form.
    *   Delete data from history table and delete records, which have attribute cDelete set on one or greater.
    */  
    class clearHistoryListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   // Check whether an error flag is set
    	   if (model.isError() && view.tableHistoryList.getRowCount() > 0) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }     
    	   if (view.tableHistoryList.getRowCount() == 0) {
    		   view.messageUndo("clearHistory");
    		   return;
    	   }
           int okCancle = view.messageUndo("clearHistory");
           if (okCancle == 0){
           //Button OK was press
           logger.debug("Button OK was press.");  
           // delete records whit contition cdelete > 0
           Task task = model.clearDatabase();
           task.setPostTaskAction(new PostTaskAction() {
                public void afterStopped(Object value) {
                //load data
                try {
                        model.searchWholeHistoryData();
                        reloadData(1,model.getDisplayRows());
                        view.totalResultValueLabel.setText("0");
                        } catch (Exception ex) {                                                                                    
                            ex.printStackTrace();
                            DefaultExceptionHandler.handle(view, ex);                                                                                  		   	               
                        }
                }               
           });
           Dispatcher.getDispatcher().dispatch(task, view, false);
           /*
           new DefaultProgressBar(task, view, true) {
                @Override
                public void afterStopping() {
                //load data
                try {
                        model.searchWholeHistoryData();
                        reloadData(1,model.getDisplayRows());
                        view.totalResultValueLabel.setText("0");
                        } catch (Exception ex) {                                                                                    
                            ex.printStackTrace();
                            DefaultExceptionHandler.handle(view, ex);                                                                                  		   	               
                        }
                }
           };		   					                   	                   
           task.start();	      */                                                   
           }
       }
    }
}
