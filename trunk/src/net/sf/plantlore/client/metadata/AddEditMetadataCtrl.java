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

import net.sf.plantlore.client.history.History;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.DefaultReconnectDialog;
import net.sf.plantlore.common.record.Metadata;
import org.apache.log4j.Logger;

/**
 * Controller for the Add/Edit metadata dialog in the MetadataManager MVC.
 *
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class AddEditMetadataCtrl {
        
	/** Instance of a logger */
    private Logger logger;
    /** Model of MetadataManager MVC */
    private MetadataManager model;
    /** View of AddEditMetadata */ 
    private AddEditMetadataView view;
    
    /** 
     * Creates a new instance of AddEditMetadataCtrl
     * @param view View of AddEditMetadata
     * @param model Model of MetadataManager MVC
     */ 
    public AddEditMetadataCtrl(AddEditMetadataView view, MetadataManager model) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);
        
        // Add action listene
        view.closeButton.setAction(new DefaultCancelAction(view)); 
        view.operationButton.addActionListener(new operationButtonListener());       
      }
   
   /**
    * ActionListener class controlling the <b>ADD</b>, <b>EDIT</b> and <b>OK</b> buttons on the form.   
    */
   class operationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // Get information about operation - ADD, EDIT, DETAILS
           logger.debug("Operation " + model.getOperation() + "was called");
           model.setUsedClose(false);
           if (model.getOperation().equals("ADD")) {
               logger.debug("Add of Metadata.");
               //check wether all obligatory fields were filled 
                if (view.checkNotNull()) {
                        //Check if new name of project (dataSetTitle) already exist
                	if (!model.uniqueDatasetTitle(view.dataSetTitleText.getText())){       
                		if (model.isError()) {
                			if (model.getError().equals(History.ERROR_REMOTE_EXCEPTION)) {
                		 		   DefaultReconnectDialog.show(view, model.getRemoteEx());
                		 	   } else {
                		 		   view.showErrorMessage(model.getError());
                		 	   }
                			   //TODO nastavit zde null
                		 	   model.setError(null); 
                		 	   return;
                		}
                		view.showErrorMessage(MetadataManager.ERROR_TITLE, MetadataManager.ERROR_DATASETTITLE);
                		return;
                	}
                    //create new instance of Metadata and save filed values
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
                    metadata.setDateCreate(view.createDateChooser.getDate());                                        
                    metadata.setDateModified(new Date());                                               
                    metadata.setRecordBasis(view.recordbasisText.getText());
                    metadata.setBiotopeText(view.noteTextArea.getText());
                    metadata.setDeleted(0);                   
                    //Save new Metadata into model
                    model.setNewMetadataRecord(metadata);
                    view.close();    
                }
           } else if (model.getOperation().equals("EDIT")) {  
               logger.debug("Edit of Metadata.");
                //check wether all obligatory fields were filled 
                if (view.checkNotNull()) {
                    //load data
                    logger.debug("metadata createWhen: " + model.getMetadataRecord().getDateCreate());
                    model.getMetadataRecord().setTechnicalContactName(view.technicalContactNameText.getText());
                    model.getMetadataRecord().setTechnicalContactEmail(view.technicalContactEmailText.getText());
                    model.getMetadataRecord().setTechnicalContactAddress(view.technicalContactAddressText.getText());
                    model.getMetadataRecord().setContentContactName(view.contentContactNameText.getText());
                    model.getMetadataRecord().setContentContactEmail(view.contentContactEmailText.getText());
                    model.getMetadataRecord().setContentContactAddress(view.contectContactAddressText.getText());
                    model.getMetadataRecord().setDataSetTitle(view.dataSetTitleText.getText());
                    model.getMetadataRecord().setDataSetDetails(view.dataSetDetailsText.getText());
                    model.getMetadataRecord().setSourceInstitutionId(view.sourceInstirutionIdText.getText());
                    model.getMetadataRecord().setSourceId(view.sourceIdText.getText());
                    model.getMetadataRecord().setOwnerOrganizationAbbrev(view.abbrevText.getText());
                    model.getMetadataRecord().setRecordBasis(view.recordbasisText.getText());
                    model.getMetadataRecord().setBiotopeText(view.noteTextArea.getText());                    
                    model.getMetadataRecord().setDateModified(new Date());
                    model.getMetadataRecord().setDeleted(0);                 
                    view.close(); 
                }
           } else if (model.getOperation().equals("DETAILS")) {
                logger.debug("Details of Metadata.");
                view.close();
           } else {
               logger.error("MetadataManager - Incorect operation. Some operation ADD, EDIT, DETAILS is excepted.");
           }           
        }
   }
    
}
