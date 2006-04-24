/*
 * AddEditMetadataCtrl.java
 *
 * Created on 23. duben 2006, 15:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import net.sf.plantlore.common.record.Metadata;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class AddEditMetadataCtrl {
        
    private Logger logger;
    private MetadataManager model;
    private AddEditMetadataView view;
    
    /** Creates a new instance of AddEditMetadataCtrl */
    public AddEditMetadataCtrl(AddEditMetadataView view, MetadataManager model) {
        
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
               logger.debug("Add of Metadata.");
               //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //vytvorime novy rekord a ulozime do nej nactene hodnoty
                    Metadata metadata = new Metadata();
                    metadata.setTechnicalContactName(view.technicalContactNameText.getText());
                    metadata.setTechnicalContactEmail(view.technicalContactEmailText.getText());
                    metadata.setTechnicalContactAddress(view.technicalContactAddressText.getText());
                    metadata.setContentContactName(view.contentContactNameText.getText());
                    metadata.setContentContactEmail(view.contentContactEmailText.getText());
                    metadata.setContentContactAddress(view.contectContactAddressText.getText());
                    metadata.setDataSetTitle(view.dataSetTitleText.getText());
                    metadata.setDataSetDetails(view.dataSetDetailsText.getText());
                    metadata.setSourceInstitutionId(view.sourceInstirutionIdText.getText());
                    metadata.setSourceId(view.sourceIdText.getText());
                    metadata.setOwnerOrganizationAbbrev(view.abbrevText.getText());
                    //metadata.setDateCreate(view.dateCreateText.getText());
                    metadata.setDateCreate(new Date());
                    //metadata.setDateModified(view.dateModifiedText.getText());
                    metadata.setDateModified(new Date());                            
                    metadata.setRecordBasis(view.recordbasisText.getText());
                    metadata.setBiotopeText(view.biotopetextText.getText());
                    //metadata.setVersionPlantsFile(Integer.parseInt(view.versionPlantsFileText.getText())); 
                    //mela by se tu vypsat nejaka informace pro uzivatele
                    //pridani metadat
                    model.addMetedataRecord(metadata);                                                           
                    view.close(); 
                }
           } else if (model.getOperation().equals("EDIT")) {  
               logger.debug("Edit of Metadata.");
                //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //nacteni hodnot
                    model.getSelectedRecord().setTechnicalContactName(view.technicalContactNameText.getText());
                    model.getSelectedRecord().setTechnicalContactEmail(view.technicalContactEmailText.getText());
                    model.getSelectedRecord().setTechnicalContactAddress(view.technicalContactAddressText.getText());
                    model.getSelectedRecord().setContentContactName(view.contentContactNameText.getText());
                    model.getSelectedRecord().setContentContactEmail(view.contentContactEmailText.getText());
                    model.getSelectedRecord().setContentContactAddress(view.contectContactAddressText.getText());
                    model.getSelectedRecord().setDataSetTitle(view.dataSetTitleText.getText());
                    model.getSelectedRecord().setDataSetDetails(view.dataSetDetailsText.getText());
                    model.getSelectedRecord().setSourceInstitutionId(view.sourceInstirutionIdText.getText());
                    model.getSelectedRecord().setSourceId(view.sourceIdText.getText());
                    model.getSelectedRecord().setOwnerOrganizationAbbrev(view.abbrevText.getText());
                    //model.getSelectedRecord().setDateCreate(view.dateCreateText.getText());
                    //model.getSelectedRecord().setDateModified(view.dateModifiedText.getText());
                    model.getSelectedRecord().setRecordBasis(view.recordbasisText.getText());
                    model.getSelectedRecord().setBiotopeText(view.biotopetextText.getText());
                   // model.getSelectedRecord().setVersionPlantsFile(Integer.parseInt(view.versionPlantsFileText.getText()));                
                   //mela by se tu vypsat nejaka informace pro uzivatele
                   //editace vybraneho zaznamu
                   model.editMetadataRecord(); 
                   view.close(); 
                }
           } else if (model.getOperation().equals("DETAILS")) {
                logger.debug("Details of Metadata.");
                view.close();
           } else {
               logger.error("MetadataManager - Incorect operation. Some from ADD, EDIT, DETAILS is excepted.");
           }           
        }
   }
    
}
