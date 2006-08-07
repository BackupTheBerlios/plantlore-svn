/*
 * SettingsCtrl.java
 *
 * Created on 17. leden 2006, 17:27
 *
 */

package net.sf.plantlore.client.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import net.sf.plantlore.client.*;
import net.sf.plantlore.client.overview.Column;
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
        
        view.addButton.addActionListener(new ButtonListener());
        view.removeButton.addActionListener(new ButtonListener());
        view.upButton.addActionListener(new ButtonListener());
        view.downButton.addActionListener(new ButtonListener());
        
        view.headerOneField.addFocusListener(new SchedaFieldListener());
        view.headerTwoField.addFocusListener(new SchedaFieldListener());
        
        view.dynamicPageCheckBox.addActionListener(new ButtonListener());
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
                DefaultListModel selectedModel = (DefaultListModel)view.selectedList.getModel();
                ArrayList<Column> columns = new ArrayList<Column>(selectedModel.size());
                
                //!! Overview and Search expect that the first column is always Occurrence.ID!!
                columns.add(new Column(Column.Type.OCCURRENCE_ID));
                
                for (int i = 0; i < selectedModel.size(); i++) {
                    columns.add((Column) selectedModel.get(i));
                }
                model.setSelectedColumns(columns);                
                model.store();
                view.setVisible(false);
                return;
            }
            if (s.equals("HELP")) {
                System.out.println("Tady se bude volat Help!");
                return;
            }
            if (s.equals("CANCEL")) {
                view.setVisible(false);
                return;
            }
            
            if (s.equals("ADD")) {
                DefaultListModel availableModel = (DefaultListModel)view.availableList.getModel();
                DefaultListModel selectedModel = (DefaultListModel)view.selectedList.getModel();
                
                Object[] columns = view.availableList.getSelectedValues();
                for (Object c :  columns) {                    
                    selectedModel.addElement(c);
                    availableModel.removeElement(c);
                }
                return;
            }
            if (s.equals("REMOVE")) {
                DefaultListModel availableModel = (DefaultListModel)view.availableList.getModel();
                DefaultListModel selectedModel = (DefaultListModel)view.selectedList.getModel();
                
                Object[] columns = view.selectedList.getSelectedValues();
                for (Object c :  columns) {
                    Column col = (Column)c;
                    switch (col.type) {
                        case SELECTION:
                            JOptionPane.showMessageDialog(view,"You can't remove the selection column.");
                            continue;
                    }
                    availableModel.addElement(c);
                    selectedModel.removeElement(c);
                }
                
                return;
            }
            if (s.equals("UP")) {
                DefaultListModel selectedModel = (DefaultListModel)view.selectedList.getModel();
                int min = view.selectedList.getMinSelectionIndex();
                int max = view.selectedList.getMaxSelectionIndex();
                
                if (min > 0) {
                    Object o = selectedModel.remove(min-1);
                    selectedModel.add(max,o);
                }
                
                return;
            }
            if (s.equals("DOWN")) {
                DefaultListModel selectedModel = (DefaultListModel)view.selectedList.getModel();
                int min = view.selectedList.getMinSelectionIndex();
                int max = view.selectedList.getMaxSelectionIndex();
                
                if (max < selectedModel.getSize() - 1 && max != -1) {
                    Object o = selectedModel.getElementAt(max+1);
                    
                    for (int i = max; i >= min; i--) {
                        selectedModel.set(i+1,selectedModel.getElementAt(i));
                    }
                    selectedModel.set(min,o);
                    view.selectedList.setSelectionInterval(min+1,max+1);
                }
                
                return;
            }
            
            if (s.equals("DYNAMIC_PAGE_LOADING")) {
                boolean dynamic = view.dynamicPageCheckBox.isSelected();
                model.setDynamicPageLoading(dynamic);
            }
        }
    }//ButtonListener
    
    class SchedaFieldListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField jtf = (JTextField)e.getSource();
            String s = jtf.getName();
            if (s.equals("HEADER_ONE")) {
                model.setHeaderOne(jtf.getText());
                return;
            }
            if (s.equals("HEADER_TWO")) {
                model.setHeaderTwo(jtf.getText());
                return;
            }
        }
    }
    
}
