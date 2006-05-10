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
import java.text.DateFormat;
import java.util.Date;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;


/**
 *
 * @author Lada
 */
public class WholeHistoryCtrl {
   
    private Logger logger;
    private History model;
    private WholeHistoryView view;
    
    /** Creates a new instance of WholeHistoryCtrl */
    public WholeHistoryCtrl(History model, WholeHistoryView view) {
      
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
                  
        view.okButton.addActionListener(new okButtonListener());
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());     
        view.undoToDateButton.addActionListener(new undoToDateButtonListener());
        view.detailsButton.addActionListener(new detailsHistoryListener());
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener()); 
        view.clearHistoryButton.addActionListener(new clearHistoryListener());
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
           logger.debug("display rows: "+ view.tableHistoryList.getRowCount());      
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableHistoryList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);
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
           logger.debug("num rows in table (view) "+ view.tableHistoryList.getRowCount());              
           if (model.getCurrentFirstRow()+ view.tableHistoryList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableHistoryList.getRowCount());
               view.tableHistoryList.setModel(new WholeHistoryTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tableHistoryList.getRowCount() - 1;
               if (to <= 0){
            	   view.displayedValueLabel.setText("0-0");
               }else {
            	   view.displayedValueLabel.setText(from + "-" + to);
               }               
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
               view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableHistoryList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
           }
       }        	   
   }
   
   /**
    *
    */  
    class undoToDateButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableHistoryList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               logger.debug("Undo to date - id of selected row: "+ view.tableHistoryList.getSelectedRow());
               int selectedRow = view.tableHistoryList.getSelectedRow();
               int toResult = selectedRow + model.getCurrentFirstRow();
               Object toDate = view.tableHistoryList.getValueAt(selectedRow, 0);                   	       
               model.clearEditObjectList();
               model.undoToDate(toResult);
               int okCancle = view.messageUndo(toDate.toString());     
               if (okCancle == 0){
                   //Button OK was press
                   logger.debug("Button OK was press.");    
                   model.commitUpdate();
                   model.deleteHistory(toResult, false);
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
               } else {
                       //Button Cancle was press
                       //neco jako rollback - bude se volat nebo to bude zarizeno tim, ze se nezavola executeUpdate??
                       logger.debug("Button Cancle was press.");
               } 
           }
       }
    }
    
    /**
    *
    */  
    class detailsHistoryListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableHistoryList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //zobrazi se detailni informace o vybranem zaznamu
               int resultNumber = view.tableHistoryList.getSelectedRow() + model.getCurrentFirstRow()-1;             
               String detailsMessage = model.getDetailsMessage(resultNumber);
               DetailsHistoryView detailsView = new DetailsHistoryView(view, true);
               DetailsHistoryCtrl detailsCtrl = new DetailsHistoryCtrl(detailsView);
               detailsView.setDetailsMessage(detailsMessage);
               detailsView.setVisible(true);               
           }          
       }
    }
    
    /**
    *
    */  
    class clearHistoryListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           int okCancle = view.messageClearHistory();
           if (okCancle == 0){
                   //Button OK was press
                   logger.debug("Button OK was press.");  
                   //smazani dat z tabulek tHistoryChange a tHistory
                   model.clearHistory();
                   //smaznamu, ktere maji deleted = 1
                   //model.clearDatabase
                   //aktualizovat zobrazeni dat
                   model.searchWholeHistoryData();        	
                   model.processResult(1,model.getDisplayRows());
                   view.tableHistoryList.setModel(new WholeHistoryTableModel(model));
                   view.displayedValueLabel.setText("0-0");
                   view.displayedValueLabel.setText("0-0"); 
                   view.totalResultValueLabel.setText("0");
                   
           }
       }
    }
}
