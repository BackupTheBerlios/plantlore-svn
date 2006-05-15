/*
 * PublicationManagerCtrl.java
 *
 * Created on 23. duben 2006, 11:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.publication;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.record.Publication;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class PublicationManagerCtrl {
    
    private Logger logger;
    private PublicationManager model;
    private PublicationManagerView view;
    
    /**
     * Creates a new instance of PublicationManagerCtrl
     */
    public PublicationManagerCtrl(PublicationManager model, PublicationManagerView view) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
          
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());        
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());            
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());    
        view.detailsButton.addActionListener(new detailsPublicationListener());
        view.addButtons.addActionListener(new addPublicationListener());
        view.editButtons.addActionListener(new editPublicationListener());
        view.deleteButton.addActionListener(new deletePublicationListener());
        view.searchButton.addActionListener(new searchPublicationListener());
        view.sortComboBox.addFocusListener(new SortComboFocusListener());
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
           PlantloreHelp.showHelp(PlantloreHelp.PUBLICATION_MANAGER); 
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
           logger.debug("display rows: "+ view.tablePublicationList.getRowCount());      
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.tablePublicationList.setModel(new PublicationManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tablePublicationList.getRowCount() - 1;
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
           logger.debug("num rows in table (view) "+ view.tablePublicationList.getRowCount());              
           if (model.getCurrentFirstRow()+ view.tablePublicationList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tablePublicationList.getRowCount());
               view.tablePublicationList.setModel(new PublicationManagerTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tablePublicationList.getRowCount() - 1;
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
               view.tablePublicationList.setModel(new PublicationManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tablePublicationList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
           }
       }        	   
   }
   
 
    /**
    *
    */  
    class addPublicationListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           //v modelu nastavim informaci o tom, ze jde o ADD
           model.setOperation("ADD");           
           //otevre se dialog addEdit s tim, ze mu rekneme, ze jde o ADD
           //pozor: pri add se musi ohlidat, zda byly vyplneny povinne polozky
           AddEditPublicationView addView = new AddEditPublicationView(view,true);
           AddEditPublicationCtrl addCtrl = new AddEditPublicationCtrl(addView, model);
           addView.setAddForm();
           addView.setVisible(true);
           //pokud bude pridan zaznam,je potreba ho zobrazit v tabulce = provest znovu dotaz nebo pridat do datalistu, ale tam
           //nezarucim spravne setrizeni,takze novy dotaz bude lepsi....
           //bylo by dobre si nekde drzet query (kdyby pouzil uzivatel search)
           //nacteni metadat
           model.searchPublication();
           //opet funkci pro vyzadani si dat postupne
           model.processResult(1, model.getDisplayRows());
           view.tablePublicationList.setModel(new PublicationManagerTableModel(model));                      
           view.displayedValueLabel.setText(1 + "-" + view.tablePublicationList.getRowCount());
           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
       }
    }
    
     /**
    *
    */  
    class editPublicationListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tablePublicationList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //v modelu nastavim informaci o tom, ze jde o EDIT
               model.setOperation("EDIT");
               //poznaceni si do modelu inforamce o vybranem radku pro dalsi praci
               int resultNumber = view.tablePublicationList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setSelectedRecord(resultNumber);
               //nacteni dat do dialogu
               Publication publication = model.getSelectedRecord();               
               AddEditPublicationView editView = new AddEditPublicationView(view,true);
               AddEditPublicationCtrl editCtrl = new AddEditPublicationCtrl(editView, model);
               editView.collectionNameText.setText(publication.getCollectionName());
               editView.colllectionYearPublictionText.setText(publication.getCollectionYearPublication().toString());
               editView.journalNameText.setText(publication.getJournalName());
               editView.journalAuthorNameText.setText(publication.getJournalAuthorName());
               editView.referenceDetailText.setText(publication.getReferenceDetail());
               editView.urlText.setText(publication.getUrl());
               editView.noteText.setText(publication.getNote());
               //FIXME: vsechny Integery v DB nastavit defautlne na nulu,aby to pri prevodech na string nedelalo neplechu
               //editView.versionPlantsFileText.setText(publication.getVersionPlantsFile().toString());
               //vytvoreni dialogu
               editView.setEditForm();               
               editView.setVisible(true);    
               //po editaci zaznamu se musi zobrazit zmena i v tabulce
              //nacteni metadat
               model.searchPublication();
               //opet funkci pro vyzadani si dat postupne
               model.processResult(1, model.getDisplayRows());
               view.tablePublicationList.setModel(new PublicationManagerTableModel(model));                      
               view.displayedValueLabel.setText(1 + "-" + view.tablePublicationList.getRowCount());                
           }          
       }
    }
    
    /**
    *
    */  
    class detailsPublicationListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tablePublicationList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //v modelu nastavim informaci o tom, ze jde o DETAILS
                model.setOperation("DETAILS");
               //poznaceni si do modelu inforamce o vybranem radku pro dalsi praci
               int resultNumber = view.tablePublicationList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setSelectedRecord(resultNumber);
               //nacteni dat do dialogu
               Publication publication = model.getSelectedRecord();               
               AddEditPublicationView detailsView = new AddEditPublicationView(view,true);
               AddEditPublicationCtrl detailsCtrl = new AddEditPublicationCtrl(detailsView, model);
               //nacteni dat                              
               detailsView.collectionNameText.setText(publication.getCollectionName());
               detailsView.colllectionYearPublictionText.setText(publication.getCollectionYearPublication().toString());
               detailsView.journalNameText.setText(publication.getJournalName());
               detailsView.journalAuthorNameText.setText(publication.getJournalAuthorName());
               detailsView.referenceDetailText.setText(publication.getReferenceDetail());
               detailsView.urlText.setText(publication.getUrl());
               detailsView.noteText.setText(publication.getNote());
               //FIXME: vsechny Integery v DB nastavit defautlne na nulu,aby to pri prevodech na string nedelalo neplechu
               //editView.versionPlantsFileText.setText(publication.getVersionPlantsFile().toString());
               //vytvoreni dialogu
               detailsView.setDetailsForm();
               detailsView.setVisible(true); 
           }          
       }
    }
    
     /**
    *
    */  
    class deletePublicationListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tablePublicationList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //smazani zaznamu
               int resultNumber = view.tablePublicationList.getSelectedRow() + model.getCurrentFirstRow()-1; 
               model.setSelectedRecord(resultNumber);
               model.deletePublicationRecord();        
               //nacteni metadat
               model.searchPublication();
               //opet funkci pro vyzadani si dat postupne
               model.processResult(1, model.getDisplayRows());
               view.tablePublicationList.setModel(new PublicationManagerTableModel(model));                      
               view.displayedValueLabel.setText(1 + "-" + view.tablePublicationList.getRowCount());  
               view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
           }          
       }
    }
    
    class searchPublicationListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           model.setCollectionName(view.collectionNameSearchText.getText());
           //model.setCollectionYearPublication(view.collectionYearSearchText.getText());
           model.setCollectionYearPublication(0);
           model.setJournalName(view.journalNameSearchText.getText());
           model.setJournalAuthorName(view.journalAuthorNameSearchText.getText());
           //musi se nastavit podle ceho to mabyt setrizeno a zda vzestupne ci sestupne
           //opet funkci pro vyzadani si dat postupne
           model.searchPublication();
           if (model.getDisplayRows() <= 0) {
                    model.setDisplayRows(PublicationManager.DEFAULT_DISPLAY_ROWS);
           }
           model.processResult(1, model.getDisplayRows());
           view.tablePublicationList.setModel(new PublicationManagerTableModel(model));                      
           view.displayedValueLabel.setText(model.getCurrentDisplayRows());  
           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
       }
    }
    
     /**
     *  Focus listener for the <strong>sort combobox</strong> at the search panel. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class SortComboFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSortField(view.sortComboBox.getSelectedIndex());
            logger.debug("Sort field: "+view.sortComboBox.getSelectedIndex());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
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
