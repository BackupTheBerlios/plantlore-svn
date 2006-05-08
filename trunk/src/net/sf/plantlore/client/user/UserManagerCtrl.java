/*
 * UserManagerCtrl.java
 *
 * Created on 23. duben 2006, 11:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class UserManagerCtrl {
    
    private Logger logger;
    private UserManager model;
    private UserManagerView view;
    
    /**
     * Creates a new instance of UserManagerCtrl
     */
    public UserManagerCtrl(UserManager model, UserManagerView view) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
          
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());        
        view.previousButton.addActionListener(new previousButtonListener());
        view.nextButton.addActionListener(new nextButtonListener());            
        view.toDisplayValueTextField.addActionListener(new rowSetDisplayChangeListener());    
        view.detailsButton.addActionListener(new detailsUserListener());
        view.addButtons.addActionListener(new addUserListener());
        view.editButtons.addActionListener(new editUserListener());
        view.deleteButton.addActionListener(new deleteUserListener());
        view.searchButton.addActionListener(new searchUserListener());
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
           logger.debug("display rows: "+ view.tableUserList.getRowCount());      
           if (model.getCurrentFirstRow() > 1) {
               int firstRow = Math.max(model.getCurrentFirstRow()- model.getDisplayRows(), 1);
               model.processResult(firstRow, model.getDisplayRows()); 
               if (model.getCurrentFirstRow() > 1){
               }
               view.tableUserList.setModel(new UserManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableUserList.getRowCount() - 1;
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
           logger.debug("num rows in table (view) "+ view.tableUserList.getRowCount());              
           if (model.getCurrentFirstRow()+ view.tableUserList.getRowCount()<=model.getResultRows()) {
               model.processResult(model.getCurrentFirstRow()+ model.getDisplayRows(), view.tableUserList.getRowCount());
               view.tableUserList.setModel(new UserManagerTableModel(model));             
               int from = model.getCurrentFirstRow();
               int to = from + view.tableUserList.getRowCount() - 1;
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
               view.tableUserList.setModel(new UserManagerTableModel(model));
               int from = model.getCurrentFirstRow();
               int to = from + view.tableUserList.getRowCount() - 1;
               view.displayedValueLabel.setText(from + "-" + to);               
           }
       }        	   
   }
   
 
    /**
    *
    */  
    class addUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           //v modelu nastavim informaci o tom, ze jde o ADD
           model.setOperation("ADD");           
           //otevre se dialog addEdit s tim, ze mu rekneme, ze jde o ADD
           //pozor: pri add se musi ohlidat, zda byly vyplneny povinne polozky
           AddEditUserView addView = new AddEditUserView(view,true);
           AddEditUserCtrl addCtrl = new AddEditUserCtrl(addView, model);
           addView.setAddForm();
           addView.setVisible(true);
           //pokud bude pridan zaznam,je potreba ho zobrazit v tabulce = provest znovu dotaz nebo pridat do datalistu, ale tam
           //nezarucim spravne setrizeni,takze novy dotaz bude lepsi....
           //bylo by dobre si nekde drzet query (kdyby pouzil uzivatel search)
           //nacteni            
           model.searchUser();
           //opet funkci pro vyzadani si dat postupne
           model.processResult(1, model.getDisplayRows());
           view.tableUserList.setModel(new UserManagerTableModel(model));                      
           view.displayedValueLabel.setText(1 + "-" + view.tableUserList.getRowCount());
           view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
       }
    }
    
     /**
    *
    */  
    class editUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableUserList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //v modelu nastavim informaci o tom, ze jde o EDIT
               model.setOperation("EDIT");
               //poznaceni si do modelu inforamce o vybranem radku pro dalsi praci
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setSelectedRecord(resultNumber);
               //nacteni dat do dialogu
               User user = model.getSelectedRecord();               
               AddEditUserView editView = new AddEditUserView(view,true);
               AddEditUserCtrl editCtrl = new AddEditUserCtrl(editView, model);
               editView.loginText.setText(user.getLogin());
               editView.passwordtext.setText(user.getPassword());
               editView.firstNameText.setText(user.getFirstName());
               editView.surnameText.setText(user.getSurname());
               editView.emailText.setText(user.getEmail());
               editView.addressText.setText(user.getAddress());
               //FIXME: osetrit praci s casem
                //editView.createWhenText.setText(user.getCreateWhen().toString());
               //editView.dropWhenText.setText(user.getDropWhen().toString());               
               editView.noteText.setText(user.getNote());
               //Right
               Right right = user.getRight();
               editView.editGroupText.setText(right.getEditGroup());
               editView.seeColumnText.setText(right.getSeeColumns());
               if (right.getAdministrator() == 1) {
                   editView.administratorCheckBox.setSelected(true);
               } else {
                   editView.administratorCheckBox.setSelected(false);
               }
               if (right.getEditAll() == 1) {
                   editView.editAllCheckBox.setSelected(true);                   
               } else {
                   editView.editAllCheckBox.setSelected(false);                   
               }
               if (right.getEditOwn() == 1) {
                   editView.editOwenCheckBox.setSelected(true);
               } else {
                   editView.editOwenCheckBox.setSelected(false);
               }
               if (right.getAdd() == 1) {
                   editView.addRightCheckBox.setSelected(true);
               } else {
                   editView.addRightCheckBox.setSelected(false);
               }
               //FIXME: vsechny Integery v DB nastavit defautlne na nulu,aby to pri prevodech na string nedelalo neplechu
               //editView.versionPlantsFileText.setText(User.getVersionPlantsFile().toString());
               //vytvoreni dialogu
               editView.setEditForm();               
               editView.setVisible(true);    
               //po editaci zaznamu se musi zobrazit zmena i v tabulce
              //nacteni metadat
               model.searchUser();
               //opet funkci pro vyzadani si dat postupne
               model.processResult(1, model.getDisplayRows());
               view.tableUserList.setModel(new UserManagerTableModel(model));                      
               view.displayedValueLabel.setText(1 + "-" + view.tableUserList.getRowCount());                
           }          
       }
    }
    
    /**
    *
    */  
    class detailsUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableUserList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //v modelu nastavim informaci o tom, ze jde o DETAILS
                model.setOperation("DETAILS");
               //poznaceni si do modelu inforamce o vybranem radku pro dalsi praci
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1;  
               model.setSelectedRecord(resultNumber);
               //nacteni dat do dialogu
               User user = model.getSelectedRecord();               
               AddEditUserView detailsView = new AddEditUserView(view,true);
               AddEditUserCtrl detailsCtrl = new AddEditUserCtrl(detailsView, model);
               //nacteni dat            
               //FIXME - nacteni dat by mohlo byt ve zvlastni funkci, protoze je stejne pro EDIT i DETAIL
               detailsView.loginText.setText(user.getLogin());
               detailsView.passwordtext.setText(user.getPassword());
               detailsView.firstNameText.setText(user.getFirstName());
               detailsView.surnameText.setText(user.getSurname());
               detailsView.emailText.setText(user.getEmail());
               detailsView.addressText.setText(user.getAddress());
               //FIXME osetrit praci s casem
               //detailsView.createWhenText.setText(user.getCreateWhen().toString());
               //detailsView.dropWhenText.setText(user.getDropWhen().toString());               
               detailsView.noteText.setText(user.getNote());
               //Right
               Right right = user.getRight();
               detailsView.editGroupText.setText(right.getEditGroup());
               detailsView.seeColumnText.setText(right.getSeeColumns());
               if (right.getAdministrator() == 1) {
                   detailsView.administratorCheckBox.setSelected(true);
               } else {
                   detailsView.administratorCheckBox.setSelected(false);
               }
               if (right.getEditAll() == 1) {
                   detailsView.editAllCheckBox.setSelected(true);                   
               } else {
                   detailsView.editAllCheckBox.setSelected(false);                   
               }
               if (right.getEditOwn() == 1) {
                   detailsView.editOwenCheckBox.setSelected(true);
               } else {
                   detailsView.editOwenCheckBox.setSelected(false);
               }
               if (right.getAdd() == 1) {
                   detailsView.addRightCheckBox.setSelected(true);
               } else {
                   detailsView.addRightCheckBox.setSelected(false);
               }
               //FIXME: vsechny Integery v DB nastavit defautlne na nulu,aby to pri prevodech na string nedelalo neplechu
               //detailsView.versionPlantsFileText.setText(User.getVersionPlantsFile().toString());
               //vytvoreni dialogu
               detailsView.setDetailsForm();
               detailsView.setVisible(true); 
           }          
       }
    }
    
     /**
    *
    */  
    class deleteUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           if (view.tableUserList.getSelectedRow() < 0) {    
               view.messageSelection();
           } else {
               //smazani zaznamu
               int resultNumber = view.tableUserList.getSelectedRow() + model.getCurrentFirstRow()-1; 
               model.setSelectedRecord(resultNumber);
               model.deleteUserRecord();        
               //nacteni metadat
               model.searchUser();
               //opet funkci pro vyzadani si dat postupne
               model.processResult(1, model.getDisplayRows());
               view.tableUserList.setModel(new UserManagerTableModel(model));                      
               view.displayedValueLabel.setText(1 + "-" + view.tableUserList.getRowCount());  
               view.totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
           }          
       }
    }
    
    class searchUserListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           model.setWholeName(view.wholeNameSearchText.getText());           
           model.setLogin(view.loginSearchText.getText());
           model.setEmail(view.emailSearchText.getText());
           model.setAddress(view.addressSearchText.getText());
           //musi se nastavit podle ceho to mabyt setrizeno a zda vzestupne ci sestupne
           //opet funkci pro vyzadani si dat postupne
           model.searchUser();
           if (model.getDisplayRows() <= 0) {
                    model.setDisplayRows(UserManager.DEFAULT_DISPLAY_ROWS);
           }
           model.processResult(1, model.getDisplayRows());
           view.tableUserList.setModel(new UserManagerTableModel(model));                      
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