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
import net.sf.plantlore.l10n.L10n;
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
        view.okButton.addActionListener(new ButtonListener());
        view.cancelButton.addActionListener(new ButtonListener());
        view.helpButton.addActionListener(new ButtonListener());
        view.englishRadioButton.addActionListener(new LanguagesListener());
        view.czechRadioButton.addActionListener(new LanguagesListener());
        view.defaultRadioButton.addActionListener(new LanguagesListener());
    }
    
    /** Handles clicks on languages radio buttons.
     *
     * Stores the currently selected language into Settings model.
     * Reacts only to itemEvents with SECELTED state change set.
     */
    class LanguagesListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("ENGLISH")) {
                model.setLanguage(L10n.ENGLISH);
            }
            if (cmd.equals("CZECH")) 
                model.setLanguage(L10n.CZECH);
            if (cmd.equals("DEFAULT"))
                model.setLanguage(L10n.DEFAULT_LANGUAGE);
        }
    }
    
    /** Handles the main Dialog buttons - Ok, Cancel and Help.
     *
     * On Ok makes the model store() the preferences and hides the view.
     * On Cancel just hides the view.
     * On Help should call help.
     */
    class ButtonListener implements ActionListener {
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
    
    
}
