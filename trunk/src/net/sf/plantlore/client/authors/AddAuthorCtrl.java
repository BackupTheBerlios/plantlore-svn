/*
 * AddAuthorCtrl.java
 *
 * Created on 21. leden 2006, 0:58
 *
 */

package net.sf.plantlore.client.authors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import net.sf.plantlore.common.PlantloreHelp;
import javax.swing.Timer;
import net.sf.plantlore.common.ProgressDialog;

/**
 *
 * @author Tomas Kovarik
 */
public class AddAuthorCtrl {
    
    AuthorManager model;
    AddAuthorView view;
    private Timer timer;
    private ProgressDialog progress;
    
    /** Creates a new instance of AddAuthorCtrl */
    public AddAuthorCtrl(AuthorManager addModel, AddAuthorView addView) {
        this.model = addModel;
        this.view = addView;
        view.closeBtnAddActionListener(new CloseButtonListener());
        view.helpBtnAddActionListener(new HelpButtonListener());
        view.saveBtnAddActionListener(new SaveAuthorButtonListener());        
        view.firstNameAddFocusListener(new FirstNameFieldFocusListener());
        view.surnameAddFocusListener(new SurnameFieldFocusListener());
        view.organizationAddFocusListener(new OrganizationFieldFocusListener());
        view.roleAddFocusListener(new RoleFieldFocusListener());
        view.addressAddFocusListener(new AddressAreaFocusListener());
        view.phoneNumberAddFocusListener(new PhoneFieldFocusListener());
        view.emailAddFocusListener(new EmailFieldFocusListener());
        view.urlAddFocusListener(new UrlFieldFocusListener());
        view.noteAddFocusListener(new NoteAreaFocusListener());        
        
        // Create a timer
        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (model.isOperationDone() == true) {
                    timer.stop();
                    System.out.println("closing progressbar...");
                    progress.close();
                    view.setDialogEnabled(true);                    
                }
            }
        });        
    }
    
    /**
     * ActionListener class controlling the <b>close</b> button on the form.
     */
    class CloseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Close the dialog
            view.close();
        }
    }

    /**
     * ActionListener class controlling the <b>help</b> button on the form.
     */    
    class HelpButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Display help viewer            
            PlantloreHelp.showHelp("Main.AuthorManager");            
        }
    }
    
    /**
     * ActionListener class controlling the <b>Save author</b> button on the form.
     */    
    class SaveAuthorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {            
            // Check whether all the required fields are present
            if (view.checkNonEmpty("name") && view.checkNonEmpty("surname") &&
                view.checkNonEmpty("organization") && view.checkNonEmpty("role") &&
                view.checkNonEmpty("address") && view.checkNonEmpty("phone") &&
                view.checkNonEmpty("email") && view.checkNonEmpty("url")) {
                
                model.saveAuthor();                

                view.setDialogEnabled(false);                                
                timer.start();                
                // Display dialog with progress bar
                progress = new ProgressDialog(view.getDialog(), true);
                progress.show();
                
                view.close();
            }
        }
    }        

    class FirstNameFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setFirstName(view.getFirstName());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }
    
    class SurnameFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setSurname(view.getSurname());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }
    
    class OrganizationFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setOrganization(view.getOrganization());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }    
    
    class RoleFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setRole(view.getRole());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }    
    
    class AddressAreaFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setAddress(view.getAddress());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }        
    
    class PhoneFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setPhoneNumber(view.getPhoneNumber());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }        
    
    class EmailFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setEmail(view.getEmail());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }        
    
    class UrlFieldFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setUrl(view.getUrl());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }            

    class NoteAreaFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            model.setNote(view.getNote());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    }                
}
