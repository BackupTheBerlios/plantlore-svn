/*
 * AddEditUserCtrl.java
 *
 * Created on 23. duben 2006, 15:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Right;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada Oberreiterova
 */
public class AddEditUserCtrl {
        
    private Logger logger;
    private UserManager model;
    private AddEditUserView view;
    
    /**
     * Creates a new instance of AddEditUserCtrl
     */
    public AddEditUserCtrl(AddEditUserView view, UserManager model) {
        
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
               logger.debug("Add of User.");
               //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //vytvorime novy rekord a ulozime do nej nactene hodnoty
                    User user = new User();                    
                     //nacteni hodnot                                       
                   user.setLogin(view.loginText.getText());
                   user.setPassword(view.passwordtext.getText());
                   user.setFirstName(view.firstNameText.getText());
                   user.setSurname(view.surnameText.getText());
                   user.setWholeName(view.firstNameText.getText()+" "+view.surnameText.getText());
                   user.setEmail(view.emailText.getText());
                   user.setAddress(view.addressText.getText());
                   //user.setCreateWhen(view.createWhenText.getText());
                   //user.setDropWhen(view.dropWhenText.getText());
                   user.setNote(view.noteText.getText());
                   //Right
                   Right right = new Right();
                   user.setRight(right);
                   right.setEditGroup(view.editGroupText.getText());
                   right.setSeeColumns(view.seeColumnText.getText());
                   if (view.administratorCheckBox.isSelected()) {
                       right.setAdministrator(1);
                   } else {
                       right.setAdministrator(0);
                   }
                   if (view.editAllCheckBox.isSelected()) {
                       right.setEditAll(1);
                   } else {
                       right.setEditAll(0);
                   }
                   //FIXME: rozmyslet nejake chytre oznacovani aneb pokud mohu editovat vse, tak je jasne, ze mohu editovat i svoje, atd.
                   //BUDE TO CHTIT CHYTRE OZNACOVANI
                   if (view.editOwenCheckBox.isSelected()) {
                       right.setEditOwn(1);
                   } else {
                       right.setEditOwn(0);
                   }
                   if (view.addRightCheckBox.isSelected()) {
                       right.setAdd(1);
                   } else {
                       right.setAdd(0);
                   }
                    
                    //mela by se tu vypsat nejaka informace pro uzivatele
                    //pridani zaznamu do tabulky User
                   
                   //FIXME: PRIDANI PADA na vlozeni do tabulky tUser
                    //model.addUserRecord(user, right);                                                           
                    view.close(); 
                }
           } else if (model.getOperation().equals("EDIT")) {  
               logger.debug("Edit of User.");
                //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //nacteni hodnot                                       
                   model.getSelectedRecord().setLogin(view.loginText.getText());
                   model.getSelectedRecord().setPassword(view.passwordtext.getText());
                   model.getSelectedRecord().setFirstName(view.firstNameText.getText());
                   model.getSelectedRecord().setSurname(view.surnameText.getText());
                   model.getSelectedRecord().setWholeName(view.firstNameText.getText()+" "+view.surnameText.getText());
                   model.getSelectedRecord().setEmail(view.emailText.getText());
                   model.getSelectedRecord().setAddress(view.addressText.getText());
                   //model.getSelectedRecord().setCreateWhen(view.createWhenText.getText());
                   //model.getSelectedRecord().setDropWhen(view.dropWhenText.getText());
                   model.getSelectedRecord().setNote(view.noteText.getText());
                   //Right
                   Right right = model.getSelectedRecord().getRight();
                   right.setEditGroup(view.editGroupText.getText());
                   right.setSeeColumns(view.seeColumnText.getText());
                   if (view.administratorCheckBox.isSelected()) {
                       right.setAdministrator(1);
                   } else {
                       right.setAdministrator(0);
                   }
                   if (view.editAllCheckBox.isSelected()) {
                       right.setEditAll(1);
                   } else {
                       right.setEditAll(0);
                   }
                   //FIXME: rozmyslet nejake chytre oznacovani aneb pokud mohu editovat vse, tak je jasne, ze mohu editovat i svoje, atd.
                   //BUDE TO CHTIT CHYTRE OZNACOVANI
                   if (view.editOwenCheckBox.isSelected()) {
                       right.setEditOwn(1);
                   } else {
                       right.setEditOwn(0);
                   }
                   if (view.addRightCheckBox.isSelected()) {
                       right.setAdd(1);
                   } else {
                       right.setAdd(0);
                   }
                   //FIXME: OTESTOVAT
                   //model.editUserRecord();
                   view.close(); 
                }
           } else if (model.getOperation().equals("DETAILS")) {
                logger.debug("Details of User.");
                view.close();
           } else {
               logger.error("UserManager - Incorect operation. Some from ADD, EDIT, DETAILS is excepted.");
           }           
        }
   }
    
}
