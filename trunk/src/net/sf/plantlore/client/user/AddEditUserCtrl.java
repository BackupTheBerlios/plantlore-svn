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
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Right;
import org.apache.log4j.Logger;

/**
 * Controller for the Add/Edit user dialog in the UserManager MVC.
 *
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class AddEditUserCtrl {
     
	/** Instance of a logger */
    private Logger logger;
    /** Model of UserManager MVC */
    private UserManager model;
    /** View of UserManager MVC */
    private AddEditUserView view;
    
    /**
     * Creates a new instance of AddEditUserCtrl
     * @param view View of AddEditUser
     * @param model Model of UserManager MVC
     */
    public AddEditUserCtrl(AddEditUserView view, UserManager model) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);
        
        //Add action listene
        view.closeButton.setAction(new DefaultCancelAction(view)); 
        view.operationButton.addActionListener(new OperationButtonListener());
        view.editGroupTextArea.addFocusListener(new UserAreaListener());      
    }
    
 
   /**
    * ActionListener class controlling the <b>ADD</b>, <b>EDIT</b> and <b>OK</b> buttons on the form.
    */
   class OperationButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
       {    	
        	// Get information about operation - ADD, EDIT, DETAILS
            logger.debug("Operation " + model.getOperation() + "was called");
            model.setUsedClose(false);
            if (model.getOperation().equals(UserManager.ADD)) {
                logger.debug("Add of User.");
                //check wether all obligatory fields were filled 
                 if (view.checkNotNull()) {
                     //Check if new name of project (dataSetTitle) already exist
                 	if (model.uniqueLogin(view.loginText.getText())){                		
                 		view.showErrorMessage(UserManager.ERROR_TITLE, UserManager.ERROR_LOGIN);
                 		return;
                 	}
                    //create new instance of User and save filed values    
                   User user = new User();                                                                            
                   user.setLogin(view.loginText.getText());
                   model.setPassword(view.passwordtext.getText());
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
                   if (view.addRightCheckBox.isSelected()) {
                       right.setAdd(1);
                   } else {
                       right.setAdd(0);
                   }                                       
                    // Save new User into model
                    model.setNewUserRecord(user);
                    model.setRight(right);  
                    view.close();
                    }                                                                   
           } else if (model.getOperation().equals(UserManager.EDIT)) {  
               logger.debug("Edit of User.");
                //check wether all obligatory fields were filled 
                if (view.checkNotNull()) {
                    //Load data                                                          
                   model.setPassword(view.passwordtext.getText());
                   model.getUserRecord().setFirstName(view.firstNameText.getText());
                   model.getUserRecord().setSurname(view.surnameText.getText());
                   model.getUserRecord().setWholeName(view.firstNameText.getText()+" "+view.surnameText.getText());
                   model.getUserRecord().setEmail(view.emailText.getText());
                   model.getUserRecord().setAddress(view.addressText.getText());                  
                   model.getUserRecord().setNote(view.noteText.getText());
                   //Right
                   Right right = model.getUserRecord().getRight();                   
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
                                
                   if (view.addRightCheckBox.isSelected()) {
                       right.setAdd(1);
                   } else {
                       right.setAdd(0);
                   }    
                   model.setRight(right);  
                   view.close(); 
                }
           } else if (model.getOperation().equals(UserManager.DETAIL)) {
                logger.debug("Details of User.");
                view.close();
           } else {
               logger.error("UserManager - Incorect operation. Some from ADD, EDIT, DETAILS is excepted.");
           }           
        }
   }
   
   /**    
     *  Focus listener for the editGroupTextArea - adding user from user list.        
    */
    class UserAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            ArrayList<String> userList = new ArrayList<String>();
            AutoTextArea ta = (AutoTextArea) e.getSource();
            int lineCount = ta.getLineCount();
            for (int i=0; i < lineCount; i++) {
                String tmp = ta.getLine(i);
                if (tmp.length() > 1) 
                    userList.add(tmp);
            }            
        }
    }
    
}
