/*
 * TransformationChangeCtrl.java
 *
 * Created on 27. srpen 2006, 10:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class TransformationChangeCtrl {
    
    /** Instance of a logger */
    private Logger logger;
    /** Model of AddEdit MVC */
    private AddEdit model;
    /** View of TransformationChangeView MVC */
    private TransformationChangeView view;  
    /** Selected coordinate system */
    private int selectedCoordinateSystem;
    
    /** Creates a new instance of TransformationChangeCtrl */
    public TransformationChangeCtrl(AddEdit modelAddEdit, TransformationChangeView viewTransf) {
        this.model = modelAddEdit;
        this.view = viewTransf;
        selectedCoordinateSystem = model.getCoordinateSystem();
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);  
        selectedCoordinateSystem = model.getCoordinateSystem();
        
        view.closeButton.setAction(new DefaultCancelAction(view)); 
        view.changeButton.addActionListener(new ChangeSystemButtonListener());
        view.selectedSystem.addFocusListener(new ChooseSystemComboFocusListener());
        
        //Add key listeners
        view.closeButton.addKeyListener(escapeKeyPressed);
        view.helpButton.addKeyListener(escapeKeyPressed);
        view.changeButton.addKeyListener(escapeKeyPressed);
        view.selectedSystem.addKeyListener(escapeKeyPressed);       
    }
        
    
    /**
    *  ActionListener class controlling the <b>Change</b> button on the form.
    *  The button CHANGE is used for changing coordinate system, transformation data to select coordinate system.
    */
   class ChangeSystemButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
           //test whether user set transformation from one coordinate system to the same coordinate system 
           if (model.getCoordinateSystem() == getSelectedCoordinateSystem()) {
               JOptionPane.showMessageDialog(view, L10n.getString("Overview.TransformationInfo"), L10n.getString("Overview.TrensformationInfoTitle"), JOptionPane.INFORMATION_MESSAGE);       
           } else {
               //prevest a zavrit dialog - v hlavnim dialogu se zmeni hodnoty a info o aktualnim nastavenem systemu
               model.transformationCoordinateSystem(getSelectedCoordinateSystem()); 
               model.setIsCancle(false);
               view.setVisible(false);
           }
       }
   }
    
     /**
     *  Focus listener for the <strong>choose system combobox</strong>. After losing focus automaticaly 
     *  stores value of the field to model.
     */
    class ChooseSystemComboFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {            
           setSelectedCoordinateSystem(view.selectedSystem.getSelectedIndex());
           logger.debug("Use coordinate system: "+view.selectedSystem.getSelectedIndex());
        }        

        public void focusGained(FocusEvent e) {
            // Empty
        }
    } 
       
    /**
     *  Set choice coordinate system (WGS-84, S-JTSK, S-42)
     *  @param choiceSystem choice coordinate system (0 == WGS-84, 1 == S-JTSK, 2 == S-42)
     */
    public void setSelectedCoordinateSystem(int choiceSystem) {
        this.selectedCoordinateSystem = choiceSystem;
    }
    
    /**
     *  Get choice coordinate system (WGS-84, S-JTSK, S-42)
     *  @return identificator of choice coordinate system (0 == WGS-84, 1 == S-JTSK, 2 == S-42)
     */
    public int getSelectedCoordinateSystem() {
        return this.selectedCoordinateSystem;
    }
}
