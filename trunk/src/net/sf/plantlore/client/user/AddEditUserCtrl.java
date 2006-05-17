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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Date;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.PlantloreHelp;
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
        
        view.closeButton.addActionListener(new CloseButtonListener());
        view.helpButton.addActionListener(new HelpButtonListener());
        view.operationButton.addActionListener(new OperationButtonListener());
        view.editGroupTextArea.addFocusListener(new UserAreaListener());
    }
    
   /**
    * On Cancel just hides the view.
    *
    */
   class CloseButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   view.close();
       }
   }
   
   /**
    * On Help should call help.
    *
    */
   class HelpButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // Display help viewer            
    	   System.out.println("Tady se bude volat Help!");
           PlantloreHelp.showHelp(PlantloreHelp.USER_RIGHTS); 
       }
   }
   
   /*
    *
    */
   class OperationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // zeptame se modelu, co je treba provest za akci DETEIL, ADD, EDIT
            logger.debug(model.getOperation());
           if (model.getOperation().equals("ADD")) {
               logger.debug("Add of User.");
               //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                   //ovezeni, zda se neopakuje login (musi byt unikatni)
                   if (model.uniqueLogin(view.loginText.getText())) {
                       //message s informaci, ze je dany login jiz existuje 
                       view.checUniqueLoginMessage(view.loginText.getText());
                   } else {
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
                       user.setCreateWhen(new Date());
                       user.setDropWhen(null);
                       user.setNote(view.noteText.getText());
                       //Right
                       Right right = new Right();
                       user.setRight(right);

                       right.setEditGroup(model.getEditGroupID());

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

                       if (view.addRightCheckBox.isSelected()) {
                           right.setAdd(1);
                       } else {
                           right.setAdd(0);
                       }                                       

                        //mela by se tu vypsat nejaka informace pro uzivatele
                        //pridani zaznamu do tabulky User

                       //FIXME: PRIDANI PADA na vlozeni do tabulky tUser
                        model.addUserRecord(user, right);                                                                                  
                        //zavreni pridavaciho dialogu
                        view.close();
                    }                                                           
                }
           } else if (model.getOperation().equals("EDIT")) {  
               logger.debug("Edit of User.");
                //otestovani, zda jsou vyplneny povinne polozky
                if (view.checkNotNull()) {
                    //nacteni hodnot                                                          
                   model.getSelectedRecord().setPassword(view.passwordtext.getText());
                   model.getSelectedRecord().setFirstName(view.firstNameText.getText());
                   model.getSelectedRecord().setSurname(view.surnameText.getText());
                   model.getSelectedRecord().setWholeName(view.firstNameText.getText()+" "+view.surnameText.getText());
                   model.getSelectedRecord().setEmail(view.emailText.getText());
                   model.getSelectedRecord().setAddress(view.addressText.getText());                  
                   model.getSelectedRecord().setNote(view.noteText.getText());
                   //Right
                   Right right = model.getSelectedRecord().getRight();
                   
                   right.setEditGroup(model.getEditGroupID());
                   
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
                   if (view.addRightCheckBox.isSelected()) {
                       right.setAdd(1);
                   } else {
                       right.setAdd(0);
                   }
                   //ulozeni uzivatele - melibychom zobrazit message,ze bude olozen novy uzivatel OK, CANCLE
                   model.editUserRecord();
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
   
    class UserAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            ArrayList<String> userList = new ArrayList<String>();
            AutoTextArea ta = (AutoTextArea) e.getSource();
            int lineCount = ta.getLineCount();
            for (int i=0; i < lineCount; i++) {
                String tmp = ta.getLine(i);
                if (tmp.length() > 1) //omit empty lines
                    userList.add(tmp);
            }
            model.setEditGroup(userList);
        }
    }//taxonAreaListener
    
}
