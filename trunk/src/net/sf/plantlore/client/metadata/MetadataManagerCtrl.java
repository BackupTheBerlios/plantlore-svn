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
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.record.Metadata;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class MetadataManagerCtrl {
    
    private Logger logger;
    private MetadataManager model;
    private MetadataManagerView view;
    
    /** Creates a new instance of MetadataManagerCtrl */
    public MetadataManagerCtrl(MetadataManager model, MetadataManagerView view) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
          
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());        
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
           PlantloreHelp.showHelp(PlantloreHelp.METDATA_MANAGER); 
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
           logger.debug("display rows: "+ view.tableMetadataList.getRowCount());      
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableMetadataList.getRowCount() - 1;
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
           logger.debug("num rows in table (view) "+ view.tableMetadataList.getRowCount());              
           if (model.getCurrentFirstRow()+ view.tableMetadataList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableMetadataList.getRowCount());
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tableMetadataList.getRowCount() - 1;
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
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableMetadataList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
           }
       }        	   
   }
   
 
    /**
    *
    */  
    class addMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           //v modelu nastavim informaci o tom, ze jde o ADD
           model.setOperation("ADD");           
           //otevre se dialog addEdit s tim, ze mu rekneme, ze jde o ADD
           //pozor: pri add se musi ohlidat, zda byly vyplneny povinne polozky
           AddEditMetadataView addView = new AddEditMetadataView(model, view,true);
           AddEditMetadataCtrl addCtrl = new AddEditMetadataCtrl(addView, model);
           addView.setAddForm();
           addView.setVisible(true);
           //pokud bude pridan zaznam,je potreba ho zobrazit v tabulce = provest znovu dotaz nebo pridat do datalistu, ale tam
           //nezarucim spravne setrizeni,takze novy dotaz bude lepsi....
           //bylo by dobre si nekde drzet query (kdyby pouzil uzivatel search)
           //nacteni metadat
           model.searchMetadata();
           //opet funkci pro vyzadani si dat postupne
           model.processResult(1, model.getDisplayRows());
           view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                      
           view.displayedValueLabel.setText(1 + "-" + view.tableMetadataList.getRowCount());  
       }
    }
    
     /**
    *
    */  
    class editMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableMetadataList.getSelectedRow() < 0) {    
               view.selectRowMessage();
           } else {
               //v modelu nastavim informaci o tom, ze jde o EDIT
               model.setOperation("EDIT");
               //poznaceni si do modelu inforamce o vybranem radku pro dalsi praci
               int resultNumber = view.tableMetadataList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setSelectedRecord(resultNumber);
               //vytvoreni dialogu
               AddEditMetadataView editView = new AddEditMetadataView(model, view,true);
               AddEditMetadataCtrl editCtrl = new AddEditMetadataCtrl(editView, model);
               //nacteni dat
               editView.loadData();               
               //nastaveni dialogu
               editView.setEditForm();               
               editView.setVisible(true);    
               //po editaci zaznamu se musi zobrazit zmena i v tabulce
              //nacteni metadat
               model.searchMetadata();
               //opet funkci pro vyzadani si dat postupne
               model.processResult(1, model.getDisplayRows());
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                      
               view.displayedValueLabel.setText(1 + "-" + view.tableMetadataList.getRowCount());  
           }          
       }
    }
    
    /**
    *
    */  
    class detailsMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableMetadataList.getSelectedRow() < 0) {    
               view.selectRowMessage();
           } else {
               //v modelu nastavim informaci o tom, ze jde o DETAILS
                model.setOperation("DETAILS");
               //poznaceni si do modelu inforamce o vybranem radku pro dalsi praci
               int resultNumber = view.tableMetadataList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setSelectedRecord(resultNumber);
               //vytvoreni dialogu
               AddEditMetadataView detailsView = new AddEditMetadataView(model, view,true);
               AddEditMetadataCtrl detailsCtrl = new AddEditMetadataCtrl(detailsView, model);
               //nacteni dat
               detailsView.loadData();
               //nastaveni dialogu
               detailsView.setDetailsForm();
               detailsView.setVisible(true); 
           }          
       }
    }
    
     /**
    *
    */  
    class deleteMetadataListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableMetadataList.getSelectedRow() < 0) {    
               view.selectRowMessage();
           } else {
               //smazani zaznamu
               int resultNumber = view.tableMetadataList.getSelectedRow() + model.getCurrentFirstRow()-1; 
               model.setSelectedRecord(resultNumber);
               model.deleteMetadataRecord();        
               //nacteni metadat
               model.searchMetadata();
               //opet funkci pro vyzadani si dat postupne
               model.processResult(1, model.getDisplayRows());
               view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                      
               view.displayedValueLabel.setText(1 + "-" + view.tableMetadataList.getRowCount());  
           }          
       }
    }
    
        class searchUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           model.setSourceInstitutionId(view.sourceInstitutionIdText.getText());           
           model.setSourceId(view.sourceIdText.getText());
           model.setDataSetTitle(view.dataSetTitleText.getText());
            if (!(view.checkNonEmpty("sourceInstitutionId") || view.checkNonEmpty("sourceId") ||
               view.checkNonEmpty("dataSetTitle"))) {
               view.showSearchInfoFillMessage();
               model.setSourceInstitutionId("%");
           }          
           //opet funkci pro vyzadani si dat postupne
           model.searchMetadata();
           //pokud je pocet radku pro zobrazeni roven 0, tak se nastavi defaultni hodnota
           if (model.getDisplayRows() <= 0) {
               model.setDisplayRows(MetadataManager.DEFAULT_DISPLAY_ROWS);
           }
           if (model.getResultRows() < 1) {
               view.showSearchInfoMessage();
           }
           model.processResult(1, model.getDisplayRows());
           view.tableMetadataList.setModel(new MetadataManagerTableModel(model));                      
           view.displayedValueLabel.setText(model.getCurrentDisplayRows());  
           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());                      
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
