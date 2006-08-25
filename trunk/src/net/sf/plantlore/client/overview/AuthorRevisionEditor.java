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
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.l10n.L10n;

/** Handles clicks into the revision column of author table.
 * 
 * 
 * @author fraktalek
 */
public class AuthorRevisionEditor extends AbstractCellEditor implements TableCellEditor {
    HashMap<Integer,JButton> buttonMap = new HashMap<Integer,JButton>();
    HashMap<JButton,Integer> buttonMapInverted = new HashMap<JButton,Integer>();
    AddEdit aemodel;
    String value = "";
    
    /** Handles the actual revision text editing and value passing back to the cell editor and <code>AddEdit</code> model.
     *
     */
    class RevisionAction extends AbstractAction {
        RoleEditDialog red;
        
        /** Sets the button's name and tooltip. Creates a new <code>RoleEditDialog</code> for later use for the text editing.
         *
         * Configures the RoleEditDialog - sets ActionListeners to the buttons, sets their titles, installs a <code>DocumentSizeFilter</code>
         * to the dialog's <code>JTextArea</code> to limit the length of user inputted text.
         */
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

        /** The actual revision button click handler.
         * 
         * Initializes the dialog's text and shows the dialog. Then stores the new value into the <code>AddEdit</code> model.
         */
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            int row = buttonMapInverted.get(button);
//            String s = (String)JOptionPane.showInputDialog(null,L10n.getString("AddEdit.RevisionDialogMessage"),L10n.getString("AddEdit.RevisionDialogTitle"),JOptionPane.QUESTION_MESSAGE,null,null,e.getActionCommand());
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
    public AuthorRevisionEditor(AddEdit aemodel) {
        this.aemodel = aemodel;
    }

    /** Returns the right button with proper action command set.
     * The action command is the value to be edited. It is a kinda hack, but it works...
     * Creates the button if it doesn't exist. Creates and adds to it a new action listener <code>RevisionAction</code>.
     *
     * @return <code>JButton</code> for the given row
     */
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

    /** Returns the current editor value.
     *
     */
    public Object getCellEditorValue() {
        return value;
    }
    
}
