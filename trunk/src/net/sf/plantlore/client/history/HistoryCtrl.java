/**
 * 
 */
package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;



import org.apache.log4j.Logger;

/**
 * @author Lada
 *
 */
public class HistoryCtrl {

	private Logger logger;
    private History model;
    private HistoryView view;
    
    /** Creates a new instance of HistoryCtrl */
    public HistoryCtrl(History model, HistoryView view)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
             
        view.okButton.addActionListener(new okButtonListener());
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());
        view.selectAllButton.addActionListener(new selectAllButtonListener());
        view.unselectAllButton.addActionListener(new unselectAllButtonListener());
        view.undoButton.addActionListener(new undoSelectedButtonListener());
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());           
    }
    
        /** 
    * On Ok makes the model store() the preferences and hides the view.
    * 
    */
   class okButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {       
           view.close();           
       }
   }
  
   /**
    * On Cancel just hides the view.
    *
    */
   class closeButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   view.close();
       }
   }
   
   /**
    * On Help should call help.
    *
    */
   class helpButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // Display help viewer            
    	   System.out.println("Tady se bude volat Help!");
       }
   }
   
   /**
    * 
    *
    */
   class previousButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   //   Call processResults only if we don't see the first page (should not happen, button should be disabled)
    	   logger.debug("FIRST");
    	   logger.debug("current first row: "+model.getCurrentFirstRow());
           logger.debug("num rows in the result: "+ model.getResultRows());            
           logger.debug("display rows: "+ view.getTable().getRowCount());      
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.getTable().setModel(new HistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               view.setCurrentRowsInfo(from + "-" + to);
           }                           
       }
   }
   
   /**
    * 
    *
    */
   class nextButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   //Call processResults only if we don't see the last page
    	   logger.debug("NEXT");
           logger.debug("current first row: "+model.getCurrentFirstRow());
           logger.debug("num rows in the result: "+ model.getResultRows());            
           logger.debug("display rows: "+ model.getDisplayRows());
           logger.debug("num rows in table (view) "+ view.getTable().getRowCount());              
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.getTable().getRowCount());
               view.getTable().setModel(new HistoryTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               if (to <= 0){
            	   view.setCurrentRowsInfo("0-0");
               }else {
            	   view.setCurrentRowsInfo(from + "-" + to);
               }               
           }                       
       }
   }
   
   /**
    * 
    *
    */
   class selectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	   
    	   model.setSelectAll(true);
    	   model.processResult(1,model.getResultRows());    	   
    	   view.getTable().setModel(new HistoryTableModel(model));  
       }
   }
   
   /**
    * 
    *
    */
   class unselectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    
    	   ArrayList<Object[]> markItem = new ArrayList();    	   
    	   model.setMarkItem(markItem); 
    	   view.getTable().setModel(new HistoryTableModel(model));
       }
   }
   
   /**
    * 
    *
    */
   class undoSelectedButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	   
           model.undoSelected();            
           int okCancle = view.messageUndo(model.getMessageUndo());
           logger.debug("button "+okCancle);
           if (okCancle == 0){
        	   //Button OK was press
        	   logger.debug("Button OK was press.");
        	   model.commitUpdate();
        	   model.deleteHistory(model.getResultRows(), true);
        	   model.searchEditHistory();
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
        	   //Button Cancle was press
        	   //neco jako rollback - bude se volat nebo to bude zarizeno tim, ze se nezavola executeUpdate??
        	   logger.debug("Button Cancle was press.");
           }           
       }
   }
    

   /**
    * 
    */
    class rowSetDisplayChangeListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent) {
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
               view.getTable().setModel(new HistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               view.setCurrentRowsInfo(from + "-" + to);               
           }
       }        	   
   }
  
}
