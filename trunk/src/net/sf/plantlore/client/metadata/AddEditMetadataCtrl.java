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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import net.sf.plantlore.client.history.WholeHistoryCtrl.escapeKeyPressed;
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
        
        // Add action listener
        view.closeButton.addActionListener(new closeButtonListener());
        view.operationButton.addActionListener(new operationButtonListener());
        // Add key listener
        view.closeButton.addKeyListener(new escapeKeyPressed());
        view.operationButton.addKeyListener(new escapeKeyPressed());
        view.technicalContactNameText.addKeyListener(new escapeKeyPressed());
        view.technicalContactEmailText.addKeyListener(new escapeKeyPressed());
        view.technicalContactAddressText.addKeyListener(new escapeKeyPressed());
        view.contentContactNameText.addKeyListener(new escapeKeyPressed());
        view.contentContactEmailText.addKeyListener(new escapeKeyPressed());
        view.contectContactAddressText.addKeyListener(new escapeKeyPressed());
        view.dataSetTitleText.addKeyListener(new escapeKeyPressed());
        view.dataSetDetailsText.addKeyListener(new escapeKeyPressed());
        view.sourceInstirutionIdText.addKeyListener(new escapeKeyPressed());
        view.sourceIdText.addKeyListener(new escapeKeyPressed());
        view.abbrevText.addKeyListener(new escapeKeyPressed());
        view.recordbasisText.addKeyListener(new escapeKeyPressed());
        view.biotopetextText.addKeyListener(new escapeKeyPressed());
        view.createDateChooser.addKeyListener(new escapeKeyPressed());
        view.helpButton.addKeyListener(new escapeKeyPressed());
      }
    
    /**     
     * KeyListener class controlling the pressing key ESCAPE.
     * On key ESCAPE hides the view.     
     */
    public class escapeKeyPressed implements KeyListener {
    	 	 public void keyPressed(KeyEvent evt){
    	 		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
    	 			view.close();
    	 		}    	 		     	 		 
    	 	 }
    	      public void keyReleased(KeyEvent evt) {}    	     
    	      public void keyTyped(KeyEvent evt) {}    	         
    }
    
   /**
    * ActionListener class controlling the <b>CLOSE</b> button on the form.
    * On Close hides the view.
    */
   class closeButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   model.setUsedClose(true);
    	   view.close();
       }
   }
     
   /**
    * ActionListener class controlling the <b>ADD</b>, <b>EDIT</b> and <b>OK</b> buttons on the form.   
    */
   class operationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // Get information about operation - ADD, EDIT, DETAILS
           logger.debug("Operation " + model.getOperation() + "was called");
           if (model.getOperation().equals("ADD")) {
               logger.debug("Add of Metadata.");
               //check wether all obligatory fields were filled 
                if (view.checkNotNull()) {
                        //Check if new name of project (dataSetTitle) already exist
                	if (!model.uniqueDatasetTitle(view.dataSetTitleText.getText())){                		
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
                    metadata.setBiotopeText(view.biotopetextText.getText());
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
                    model.getMetadataRecord().setBiotopeText(view.biotopetextText.getText());
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
