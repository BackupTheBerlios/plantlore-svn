
package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
     * 
     */
    public HistoryCtrl(History model, HistoryView view)
    {    	
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;        
        this.view = view;
             
        //Add action listeners to buttons
        view.okButton.addActionListener(new okButtonListener());
        view.closeButton.addActionListener(new closeButtonListener());
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());
        view.selectAllButton.addActionListener(new selectAllButtonListener());
        view.unselectAllButton.addActionListener(new unselectAllButtonListener());
        view.undoButton.addActionListener(new undoSelectedButtonListener());
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());           
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
               view.getTable().setModel(new HistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               view.setCurrentRowsInfo(from + "-" + to);
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
        	   view.showErrorMessage(model.getError());
        	   return;
           }
           // Get next page of result
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.getTable().getRowCount());
               if (model.isError()) return;
               view.getTable().setModel(new HistoryTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               if (to <= 0){
            	   view.setCurrentRowsInfo("0-0");
               }else {
            	   view.setCurrentRowsInfo(from + "-" + to);
               }               
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
        	   view.showErrorMessage(model.getError());
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
        	   view.showErrorMessage(model.getError());
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
        	   view.showErrorMessage(model.getError());
        	   return;
           }    	   
    	   // process selected record
           model.undoSelected();  
           // Check whether an error flag after processing selected records is set
           if (!model.isError()) {
        	   int okCancle = view.messageUndo(model.getMessageUndo());
               logger.debug("button "+okCancle);
               if (okCancle == 0){
            	   //Button OK was press
            	   logger.debug("Button OK was press.");
            	   model.commitUpdate();
            	   model.deleteHistory(model.getResultRows(), true);            	   
            	   model.searchEditHistory(model.getData());
            	   model.processResult(1,model.getDisplayRows());
            	   view.getTable().setModel(new HistoryTableModel(model));
            	   int resultRows = model.getResultRows();
            	   if (resultRows == 0) {
            		   view.setCurrentRowsInfo("0-0"); 
            	   } else {
            		   int from = model.getCurrentFirstRow();
                       int to = from + view.getTable().getRowCount() - 1;               
                       view.setCurrentRowsInfo(from + "-" + to);    
            	   }               
                       view.setCountResutl(resultRows);   
               } else {            	  
            	   logger.debug("Button Cancle was press."); 
               }
           } else {
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
               view.getTable().setModel(new HistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               view.setCurrentRowsInfo(from + "-" + to);               
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
