/**
 * 
 */
package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import net.sf.plantlore.client.Settings;
import net.sf.plantlore.client.SettingsView;
import net.sf.plantlore.common.*;


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
                     
        view.addOkButtonListener(new okButtonListener());
        view.addCancelButtonListener(new cancelButtonListener());
        view.addHelpButtonListener(new helpButtonListener());
        view.addPreviousButtonListener(new previousButtonListener());
        view.addNextButtonListener(new nextButtonListener());
        view.addSelectAllButtonListener(new selectAllButtonListener());
        view.addUnselectAllButtonListener(new unselectAllButtonListener());
        view.addUndoSelectedButtonListener(new undoSelectedButtonListener());
        view.rowSetPropertyChangeListener(new rowSetDisplayChangeListener());
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
   class cancelButtonListener implements ActionListener {
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
               model.processEditResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.getTable().setModel(new HistoryTableModel(model.getData()));
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
               model.processEditResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.getTable().getRowCount());
               view.getTable().setModel(new HistoryTableModel(model.getData()));  
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
   class selectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   logger.debug("selectAll");
    	   int countRow = view.getTable().getRowCount();    	  
           for (int row=0; row < countRow; row++)
           {         	     	
         	  view.getTable().setValueAt(true, row, 0);            	  
           }       
       }
   }
   
   /**
    * 
    *
    */
   class unselectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   logger.debug("unselectAll");
    	   int countRow = view.getTable().getRowCount();    	   
           for (int row=0; row < countRow; row++)
           {        	        	  
         	  view.getTable().setValueAt(false, row, 0);          	  
           }           
       }
   }
   
   /**
    * 
    *
    */
   class undoSelectedButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   int countRow = view.getTable().getRowCount(); 
    	   //list with number of selected rows
    	   ArrayList markRows = new ArrayList();
           for (int row=0; row < countRow; row++)
           {           	  
         	  if (view.getTable().getValueAt(row, 0).equals(true)) {
         		 System.out.println("undo "+ row); 
         		 markRows.add(row);         		          		          		
         	  }     
           }
           model.updateOlderChanges(markRows);  
           view.getTable().setModel(new HistoryTableModel(model.getData()));
           int from = model.getCurrentFirstRow();
           int to = from + view.getTable().getRowCount() - 1;
           view.setCurrentRowsInfo(from + "-" + to);
       }
   }
    

   /**
    * 
    */
   class rowSetDisplayChangeListener implements PropertyChangeListener {
	   public void propertyChange(PropertyChangeEvent e) {
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
               model.processEditResult(model.getCurrentFirstRow(), view.getDisplayRows());
               view.getTable().setModel(new HistoryTableModel(model.getData()));
               int from = model.getCurrentFirstRow();
               int to = from + view.getTable().getRowCount() - 1;
               view.setCurrentRowsInfo(from + "-" + to);               
           }
       }        	   
   }
}
