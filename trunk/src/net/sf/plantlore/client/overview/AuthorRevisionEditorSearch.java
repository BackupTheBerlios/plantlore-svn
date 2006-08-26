/*
 * AuthorRevisionEditor.java
 *
 * Created on 5. květen 2006, 14:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.PlainDocument;
import net.sf.plantlore.client.*;
import net.sf.plantlore.client.overview.search.Search;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author fraktalek
 */
public class AuthorRevisionEditorSearch extends AbstractCellEditor implements TableCellEditor {
    HashMap<Integer,JButton> buttonMap = new HashMap<Integer,JButton>();
    HashMap<JButton,Integer> buttonMapInverted = new HashMap<JButton,Integer>();
    Search aemodel;
    String value = "";
    RoleEditDialog red;
    
    class RevisionAction extends AbstractAction {
        public RevisionAction() {
            putValue(NAME, L10n.getString("AddEdit.Revision"));
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.RevisionTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("AddEdit.Revision"));            
            red = new RoleEditDialog(null,true);
            red.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            red.okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    value = red.roleArea.getText();
                    red.setVisible(false);
                }  });
            red.okButton.setText(L10n.getString("Common.Ok"));
            red.cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    value = null;
                    red.setVisible(false);
                }                
             }); 
            red.cancelButton.setText(L10n.getString("Common.Cancel"));
            ((PlainDocument)red.roleArea.getDocument()).setDocumentFilter(new DocumentSizeFilter(AuthorOccurrence.getColumnSize(AuthorOccurrence.NOTE)));                
            red.setTitle(L10n.getString("AddEdit.RevisionDialogTitle"));
        } 

        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            int row = buttonMapInverted.get(button);
            //String s = (String)JOptionPane.showInputDialog(null,L10n.getString("AddEdit.RevisionDialogMessage"),L10n.getString("AddEdit.RevisionDialogTitle"),JOptionPane.QUESTION_MESSAGE,null,null,e.getActionCommand());
            //value = s;
            red.roleArea.setText(e.getActionCommand());
            red.setVisible(true);
            if (value != null) {
                aemodel.setResultRevision(row,value);
            } else { // user pressed cancel
                value = e.getActionCommand();
            }
            fireEditingStopped();
        }
    }
    
    /** Creates a new instance of AuthorRevisionEditor */
    public AuthorRevisionEditorSearch(Search aemodel) {
        this.aemodel = aemodel;
    }


    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (!buttonMap.containsKey(row)) {
            JButton button = new JButton();
            button.addActionListener(new RevisionAction());
            button.setActionCommand("");
            buttonMap.put(row,button);
            buttonMapInverted.put(button,row);
        }
        JButton button = buttonMap.get(row);
        TableModel tm = table.getModel();
        button.setActionCommand((String) value);
        return button;
    }

    public Object getCellEditorValue() {
        return value;
    }
    
}
