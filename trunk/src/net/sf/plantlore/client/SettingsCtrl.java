/*
 * SettingsCtrl.java
 *
 * Created on 17. leden 2006, 17:27
 *
 */

package net.sf.plantlore.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observable;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import org.apache.log4j.Logger;

/** Controller for the Settings MVC
 *
 * @author Jakub
 */
public class SettingsCtrl extends Observable
{
    private Logger logger;
    private Settings model;
    private SettingsView view;
    
    /** Creates a new instance of SettingsCtrl */
    public SettingsCtrl(Settings model, SettingsView view)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
        view.addListener(new SettingsListener());
        view.addIconListener(new IconListener());
        view.addLanguagesListener(new LanguagesListener());
        view.addButtonsListener(new ButtonsListener());
    }
    
    /** Handles clicks on languages radio buttons.
     *
     * Stores the currently selected language into Settings model.
     * Reacts only to itemEvents with SECELTED state change set.
     */
    class LanguagesListener implements ItemListener {
        public void itemStateChanged(ItemEvent itemEvent)
        {
            //we're ineterested only in what button was selected
            if (itemEvent.getStateChange() == itemEvent.DESELECTED)
                return;
            
            if (itemEvent.getStateChange() == itemEvent.SELECTED) {
                String s;
                JRadioButton b;
                b = (JRadioButton) itemEvent.getItem();
                s = b.getActionCommand();
                model.setLanguage(s);
                return;
            }
            
            logger.error("ItemEvent in LanguagesListener was neither of: SELECTED, DESELECTED.");
        }
    }
    
    /** Handles the main Dialog buttons - Ok, Cancel and Help.
     *
     * On Ok makes the model store() the preferences and hides the view.
     * On Cancel just hides the view.
     * On Help should call help.
     */
    class ButtonsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            String s = actionEvent.getActionCommand();
            if (s.equals("OK")) {
                model.store();
                view.setVisible(false);
            }
            if (s.equals("HELP"))
                System.out.println("Tady se bude volat Help!");
            if (s.equals("CANCEL"))
                view.setVisible(false);
        }
    }
    
    /** JToggleButtons listener.
     *
     * Changes the view's mainPane.
     */
    class IconListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            view.setMainPane((JToggleButton)actionEvent.getSource());
        } 
    }
    
    /** Settings dialog listener.
     * Only logs window activation and deactivation now.
     */
    class SettingsListener implements WindowListener {
        public void windowOpened(WindowEvent windowEvent)
        {
        }

        public void windowClosing(WindowEvent windowEvent)
        {
        }

        public void windowClosed(WindowEvent windowEvent)
        {
        }

        public void windowIconified(WindowEvent windowEvent)
        {
        }

        public void windowDeiconified(WindowEvent windowEvent)
        {
        }

        public void windowActivated(WindowEvent windowEvent)
        {
            logger.info("Settings activated");
        }

        public void windowDeactivated(WindowEvent windowEvent)
        {
            logger.info("Settings deactivated");
        }        
    }
}
