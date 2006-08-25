/*
 * AddEditSettingsCtrl.java
 *
 * Created on 25. srpen 2006, 15:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author fraktalek
 */
public class AddEditSettingsCtrl {
    AddEditSettings model;
    AddEditSettingsView view;
    
    /** Creates a new instance of AddEditSettingsCtrl */
    public AddEditSettingsCtrl(AddEditSettings model, AddEditSettingsView view) {
        assert view != null;
        assert model != null;
        this.view = view;
        this.model = model;
        
        view.enableButton.setAction(new EnableAction());
        view.disableButton.setAction(new DisableAction());
        
        DefaultEscapeKeyPressed dekp = new DefaultEscapeKeyPressed(view);
    }
    
    class EnableAction extends AbstractAction {
        public EnableAction() {
            putValue(NAME, L10n.getString("Common.Enable")); 
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.SettingsEnableTT"));            
        }
        public void actionPerformed(ActionEvent e) {
            DefaultListModel enabled = (DefaultListModel)view.enabledList.getModel();
            DefaultListModel disabled = (DefaultListModel)view.disabledList.getModel();
            
            Object[] selected = view.disabledList.getSelectedValues();
            for (Object item : selected) {
                enabled.addElement(item);
                disabled.removeElement(item);
                model.enable((AddEditSettings.NullableField) item);
            }
        }        
    }//class EnableAction
    
    class DisableAction extends AbstractAction {
        public DisableAction() {
            putValue(NAME, L10n.getString("Common.Disable")); 
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.SettingsDisableTT"));            
        }
        public void actionPerformed(ActionEvent e) {
            DefaultListModel enabled = (DefaultListModel)view.enabledList.getModel();
            DefaultListModel disabled = (DefaultListModel)view.disabledList.getModel();
            
            Object[] selected = view.enabledList.getSelectedValues();
            for (Object item : selected) {
                enabled.removeElement(item);
                disabled.addElement(item);
                model.disable((AddEditSettings.NullableField) item);
            }
        }        
    }//class DisableAction
    
}


