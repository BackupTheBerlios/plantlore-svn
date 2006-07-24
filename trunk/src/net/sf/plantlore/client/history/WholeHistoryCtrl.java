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

import javax.swing.JOptionPane;

import net.sf.plantlore.common.ProgressBar;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;

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
          
        // Add action listeners to buttons
        view.okButton.addActionListener(new okButtonListener());
        view.closeButton.addActionListener(new closeButtonListener());
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());     
        view.undoToDateButton.addActionListener(new undoToDateButtonListener());
        view.detailsButton.addActionListener(new detailsHistoryListener());
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener()); 
        view.clearHistoryButton.addActionListener(new clearHistoryListener());
    }
    
    /** 
     * ActionListener class controlling the <b>OK</b> button on the form.
     * On Ok makes the model store() the preferences and hides the view.     
     */  
   class okButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {       
           view.close();           
       }
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
        	   view.showErrorMessage(model.getError());
        	   return;
           }
           // Get previous page of results
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.isError()) return;
               if (model.getCurrentFirstRow() > 1){
               }
               view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableHistoryList.getRowCount() - 1;
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
        	   view.showErrorMessage(model.getError());
        	   return;
           }
           // Get next page of result
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableHistoryList.getRowCount());
               if (model.isError()) return;
               view.tableHistoryList.setModel(new HistoryTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tableHistoryList.getRowCount() - 1;
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
        	   view.showErrorMessage(model.getError());
        	   return;
           }
           // Save old value 
           int oldValue = model.getDisplayRows();           
           // Check whether new value > 0
           if (view.getDisplayRows() < 1) {
               view.setDisplayRows(oldValue);
               return;
           }
           if (view.getDisplayRows() > model.getResultRows()){
        	   view.setDisplayRows(model.getResultRows());
           }            
           // Set new value in the model
           model.setDisplayRows(view.getDisplayRows());
           logger.debug("New display rows: "+view.getDisplayRows());
           // If neccessary reload search results
           if ((oldValue != view.getDisplayRows()) && (model.getDisplayRows() <= model.getResultRows())) {
               model.processResult(model.getCurrentFirstRow(), view.getDisplayRows());
               if (model.isError()) return;
               view.tableHistoryList.setModel(new HistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableHistoryList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
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
        	   view.showErrorMessage(model.getError());
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
	                   
	                   ProgressBar progressBar = new ProgressBar(task, view, true) {		   				
						private static final long serialVersionUID = -6065695152319199854L;
							public void exceptionHandler(Exception e) {
		   						if (e instanceof DBLayerException) {	   									   							
		   							DBLayerException dbex = (DBLayerException) e;
									JOptionPane.showMessageDialog(view, L10n.getString("Error.HistoryDBLayerException")+ "\n" + dbex.getErrorInfo(),
		 							   L10n.getString("Error.HistoryDBLayerExceptionTitle"), JOptionPane.WARNING_MESSAGE);																						
									logger.error(dbex + ": " + dbex.getErrorInfo());
		   							getTask().stop();
		   							return;
		   						}
		   						if (e instanceof RemoteException) {	 
		   							RemoteException remex = (RemoteException) e;
		   							//TODO zobrazit vlastni message - nemusi byt vzdy byt poskozene pripojeni k DB, nekdo mohl smazat data, atd..
		   							JOptionPane.showMessageDialog(view, L10n.getString("Error.RemoteException")+ "\n" + remex.getMessage(),
		 							   L10n.getString("Error.RemoteExceptionTitle"), JOptionPane.WARNING_MESSAGE);																						
									logger.error(remex + ": " + remex.getMessage());
		   							getTask().stop();
		   							return;
		   						}
		   						JOptionPane.showMessageDialog(view, L10n.getString("Delete.Message.UnknownException")+ "\n" + e.getMessage(),
			 					    L10n.getString("Delete.Message.UnknownExceptionTitle"), JOptionPane.WARNING_MESSAGE);							
		   						logger.error(e);
		   					}
	
		   					public void afterStopping() {
		   						logger.debug("Load Data");	                   
		 	                    model.searchWholeHistoryData();        	
		 	                    model.processResult(1,model.getDisplayRows());
		 	                    view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
		 	                    Integer resultRows = model.getResultRows();
		 	                    if (resultRows == 0) {
		 	                            view.displayedValueLabel.setText("0-0"); 
		 	                    } else {
		 	                            int from = model.getCurrentFirstRow();
		 	                    int to = from + view.tableHistoryList.getRowCount() - 1;               
		 	                    view.displayedValueLabel.setText(from + "-" + to);    
		 	                    }               
		 	                    view.totalResultValueLabel.setText(resultRows.toString());
		   					}
		   				};
		   				progressBar.setTitle(L10n.getString("History.Undo.ProgressTitle"));	                   	                   
	                    task.start();
	                   
	               } else {	                       	                     
	                       logger.debug("Button Cancle was press.");
	               } 
                } else {
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
        	   view.showErrorMessage(model.getError());
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
               DetailsHistoryCtrl detailsCtrl = new DetailsHistoryCtrl(detailsView);
               detailsView.setDetailsMessage(detailsMessage);
               detailsView.setVisible(true);               
           }    
           model.setError(null);
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
        	   view.showErrorMessage(model.getError());
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
                   
                   ProgressBar progressBar = new ProgressBar(task, view, true) {		   				
						private static final long serialVersionUID = -6065695152319199854L;
							public void exceptionHandler(Exception e) {
		   						if (e instanceof DBLayerException) {	   									   							
		   							DBLayerException dbex = (DBLayerException) e;		   							
									JOptionPane.showMessageDialog(view, L10n.getString("Error.HistoryDBLayerException")+ "\n" + dbex.getErrorInfo(),
		 							   L10n.getString("Error.HistoryDBLayerExceptionTitle"), JOptionPane.WARNING_MESSAGE);																						
									logger.error(dbex + ": " + dbex.getErrorInfo());
		   							getTask().stop();
		   							return;
		   						}
		   						if (e instanceof RemoteException) {	 
		   							RemoteException remex = (RemoteException) e;
		   							//TODO zobrazit vlastni message - nemusi vzdy byt poskozene pripojeni k DB, nekdo mohl smazat data, atd..
		   							JOptionPane.showMessageDialog(view, L10n.getString("Error.RemoteException")+ "\n" + remex.getMessage(),
		 							   L10n.getString("Error.RemoteExceptionTitle"), JOptionPane.WARNING_MESSAGE);																						
									logger.error(remex + ": " + remex.getMessage());
		   							getTask().stop();
		   							return;
		   						}
		   						JOptionPane.showMessageDialog(view, L10n.getString("Delete.Message.UnknownException")+ "\n" + e.getMessage(),
			 					    L10n.getString("Delete.Message.UnknownExceptionTitle"), JOptionPane.WARNING_MESSAGE);							
		   						logger.error(e);
		   					}
	
		   					public void afterStopping() {
		   						//load data
		   	                   model.searchWholeHistoryData();        	
		   	                   model.processResult(1,model.getDisplayRows());
		   	                   view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
		   	                   view.displayedValueLabel.setText("0-0");
		   	                   view.displayedValueLabel.setText("0-0"); 
		   	                   view.totalResultValueLabel.setText("0");
		   					}
		   				};
		   				progressBar.setTitle(L10n.getString("History.Undo.ProgressTitle"));	                   	                   
	                    task.start();	                                                         
           }
       }
    }
}
