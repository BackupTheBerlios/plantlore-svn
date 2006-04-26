/*
 * AddEditPublicationCtrl.java
 *
 * Created on 23. duben 2006, 15:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.publication;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import net.sf.plantlore.common.record.Publication;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class AddEditPublicationCtrl {
        
    private Logger logger;
    private PublicationManager model;
    private AddEditPublicationView view;
    
    /**
     * Creates a new instance of AddEditPublicationCtrl
     */
    public AddEditPublicationCtrl(AddEditPublicationView view, PublicationManager model) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
        
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());
        view.operationButton.addActionListener(new operationButtonListener());
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
   
   /*
    *
    */
   class operationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // zeptame se modelu, co je treba provest za akci DETEIL, ADD, EDIT
            logger.debug(model.getOperation());
           if (model.getOperation().equals("ADD")) {
               logger.debug("Add of Publication.");
               //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //vytvorime novy rekord a ulozime do nej nactene hodnoty
                    Publication publication = new Publication();
                    publication.setCollectionName(view.collectionNameText.getText());
                    //FIXME - osetrit zadani necoho jineho nez int 
                    //publication.setCollectionYearPublication(Integer.parseInt(view.colllectionYearPublictionText.getText()));
                    publication.setCollectionYearPublication(0);
                    publication.setJournalName(view.journalNameText.getText());
                    publication.setJournalAuthorName(view.journalAuthorNameText.getText());
                    publication.setReferenceCitation(view.collectionNameText.getText()+", "+view.colllectionYearPublictionText.getText()
                                                      +", "+view.journalNameText.getText()+", "+ view.journalAuthorNameText.getText());
                    publication.setReferenceDetail(view.referenceDetailText.getText());
                    publication.setUrl(view.urlText.getText());
                    publication.setNote(view.noteText.getText());
                    //mela by se tu vypsat nejaka informace pro uzivatele
                    //pridani zaznamu do tabulky publication
                    model.addPublicationRecord(publication);                                                           
                    view.close(); 
                }
           } else if (model.getOperation().equals("EDIT")) {  
               logger.debug("Edit of Publication.");
                //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //nacteni hodnot                   
                    model.getSelectedRecord().setCollectionName(view.collectionNameText.getText());
                    model.getSelectedRecord().setCollectionYearPublication(Integer.parseInt(view.colllectionYearPublictionText.getText()));
                    model.getSelectedRecord().setJournalName(view.journalNameText.getText());
                    model.getSelectedRecord().setJournalAuthorName(view.journalAuthorNameText.getText());
                    model.getSelectedRecord().setReferenceCitation(view.collectionNameText.getText()+", "+view.colllectionYearPublictionText.getText()
                                                      +", "+view.journalNameText.getText()+", "+ view.journalAuthorNameText.getText());
                    model.getSelectedRecord().setReferenceDetail(view.referenceDetailText.getText());
                    model.getSelectedRecord().setUrl(view.urlText.getText());
                    model.getSelectedRecord().setNote(view.noteText.getText());
                   model.editPublicationRecord(); 
                   view.close(); 
                }
           } else if (model.getOperation().equals("DETAILS")) {
                logger.debug("Details of Publication.");
                view.close();
           } else {
               logger.error("PublicationManager - Incorect operation. Some from ADD, EDIT, DETAILS is excepted.");
           }           
        }
   }
    
}
