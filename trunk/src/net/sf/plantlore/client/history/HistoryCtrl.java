
package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.DefaultReconnectDialog;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;

import org.apache.log4j.Logger;

/**
 * Controller for the main History dialog (part of the History MVC).
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 *
 */
public class HistoryCtrl {

	/** Instance of a logger */
	private Logger logger;
	/** Model of the History MVC */
    private History model;
    /** View of the History MVC */
    private HistoryView view;
    
    /** 
     *  Creates a new instance of HistoryCtrl 
     *  @param model model of the History MVC
     *  @param view  view of the History MVC
     *  @param occId identifier of selected occurrence
     * 
     */
    public HistoryCtrl(History modelH, HistoryView viewH)
    {    	
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = modelH;        
        this.view = viewH;
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);      
        
        //Add action listeners to buttons        
        view.closeButton.setAction(new DefaultCancelAction(view));        
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());
        view.selectAllButton.addActionListener(new selectAllButtonListener());
        view.unselectAllButton.addActionListener(new unselectAllButtonListener());
        view.undoButton.addActionListener(new undoSelectedButtonListener());
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());

    }
    
    /**
     * Reload new data for displaying in view dialog.
     * @param fromRow number of the first row to show in table
     * @param countRow number of rows to retrieve 
     */
    public void reloadData(int fromRow, int countRow) {
    	try {
    	    model.processResult(fromRow, countRow);        
            view.getTable().setModel(new HistoryTableModel(model));             
            int from = model.getCurrentFirstRow();
            int to = from + view.getTable().getRowCount() - 1;
            if (to <= 0){
         	   view.setCurrentRowsInfo("0-0");
            }else {
         	   view.setCurrentRowsInfo(from + "-" + to);
            }    
            // Set button next inactive if we see the last page, in other way set it active.
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount() - 1 < model.getResultRows()) {
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
    	}catch (Exception ex) {
           logger.error("Reload data failed. Details:" + ex.getMessage());           
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
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount() - 1 < model.getResultRows()) {
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
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount()<=model.getResultRows()) {
        	   reloadData(model.getCurrentFirstRow()+ model.getDisplayRows(), view.getTable().getRowCount());               
           }  
           //Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }
   }
   
   /**
    *  ActionListener class controlling the <b>SelectALL</b> button on the form.
    *  On selectSll All record from active page will be selected. 
    *  Each younger records with the same ITEM as seleced record will be selected too. 
    */
   class selectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {   
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	  Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }    	   
    	   model.setSelectAll(true);    	  	   
    	   view.getTable().setModel(new HistoryTableModel(model));  
       }
   }
   
   /**
    *  ActionListener class controlling the <b>UnselectAll</b> button on the form.
    *  On UnselecAll All record from active page will be unselected. 
    *  Each older records with the same ITEM as unseleced record will be unselected too.
    */
   class unselectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {   
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }
    	   model.setUnselectedAll(true); 
    	   view.getTable().setModel(new HistoryTableModel(model));
       }
   }
   
   /**
    * ActionListener class controlling the <b>UndoSelected</b> button on the form.
    * On UndoSelected All selected changes will be restored.
    */
   class undoSelectedButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {   
    	   // Check whether an error flag is set
    	   if (model.isError()) {
        	   Exception ex = model.getException();
                   ex.printStackTrace();
                   DefaultExceptionHandler.handle(view, ex);  
        	   return;
           }    	   
    	   // process selected record
           model.undoSelected();  
           // Check whether an error flag after processing selected records is set
           if (!model.isError()) {
        	   int okCancle = view.messageUndo(model.getMessageUndo());               
               if (okCancle == 0){
            	   //Button OK was press
            	   logger.debug("Button OK was press.");
            	   Task task = model.commitUpdate(model.getResultRows(), true);  
            	   
            	   new DefaultProgressBar(task, view, true) {                       
                        @Override
                        public void afterStopping() {
                                try {
                                   if (! model.isFinishedTask()) return;
                                   model.setInfoFinishedTask(false);	
                                   model.searchEditHistory(model.getData());
                                   reloadData(1,model.getDisplayRows());			   	            	          
                                   view.setCountResutl(model.getResultRows());
                                }catch (Exception ex) {                                   
                                   ex.printStackTrace();
                                   DefaultExceptionHandler.handle(view, ex);                                   
                                   return;
                                } 
                        } 		   					
                  };		   					                   	                   
	          task.start();     	                   
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
    

   /**
    * ActionListener class controlling the text field on the form for set number of rows to displayed.  
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
               logger.debug("OK"+ model.getCurrentFirstRow() + " " + view.getDisplayRows());
        	   reloadData(model.getCurrentFirstRow(), view.getDisplayRows());                      
           }
           // Set button prev active if we see the first page, in other way set it inactive
           if (model.getCurrentFirstRow() > 1) {
        	   view.previousButton.setEnabled(true);
           } else {
        	   view.previousButton.setEnabled(false);
           }
           //Set button next inactive if we see the last page, in other way set it active
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount() - 1 < model.getResultRows()) {
        	   view.nextButton.setEnabled(true);
           } else {
        	   view.nextButton.setEnabled(false);
           }
       }        	   
   }
  
}
