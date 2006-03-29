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
    	   logger.debug("current first row: "+model.getCurrentFirstRow());
           logger.debug("num rows in the result: "+ model.getResultRows());            
           logger.debug("display rows: "+ view.getTable().getRowCount());
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processEditResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.getTable().setModel(new HistoryTableModel(model.getData()));
               //view.repaint();
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
    	   //Call processResults only if we don't see the last page (should not happen, button should be disabled)
           logger.debug("current first row: "+model.getCurrentFirstRow());
           logger.debug("num rows in the result: "+ model.getResultRows());            
           logger.debug("display rows: "+ model.getDisplayRows());
           if (model.getCurrentFirstRow()+ view.getTable().getRowCount()<=model.getResultRows()) {
               model.processEditResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.getTable().getRowCount());
               view.getTable().setModel(new HistoryTableModel(model.getData()));
               //view.repaint();
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
           //view.repaint(); ... neni potreba zaridi to funkce modelu 
           //setValueAt volanim funkce fireTableCellUpdated(row, column) 
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
           //view.repaint();
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
           for (int row=countRow-1; row > 0; row--)
           {           	  
         	  if (view.getTable().getValueAt(row, 0).equals(true)) {
         		 System.out.println("undo");    
         		 // provedeni zmeny pro danou polozku 
         		 //- pokud neni joz ITEM v listu!!! - OSETRIT
         		 model.updateOlderChanges(model.getCurrentFirstRow() + row);  
         		 //model.setUpdateListItem(row, 3); ... list zmenenych ITEM
         	  }
         	  // projit resultID pro firstRow to 0 a overit pokud je tu ITEM z menenych polozek, 
         	  // tak ji tez smazat z historie
           }
       }
   }
    

   /**
    * 
    */
   class rowSetDisplayChangeListener implements PropertyChangeListener {
	   public void propertyChange(PropertyChangeEvent e) {
		   if (view.getDisplayRows() > 0) {
			   System.out.println(view.getDisplayRows());			  
			   //zatim nefunguje jak by melo !!!
		   }
	   }
   }
}
