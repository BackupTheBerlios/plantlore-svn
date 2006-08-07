/*
 * ButtonEditor.java
 *
 * Created on 24. duben 2006, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import net.sf.plantlore.client.*;
import net.sf.plantlore.l10n.L10n;

/** Button Editor and Renderer used by the author table in AddEdit dialog.
 *
 * Simply returns the buttons stored in the table model.
 * @author reimei
 */
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer  {
    private final static String ADD = L10n.getString("AddEdit.AuthorAdd");
    private final static String REMOVE = L10n.getString("AddEdit.AuthorRemove");
    
    HashMap<Integer,JButton> editors = new HashMap<Integer,JButton>();
    JButton addButton, removeButton;
    AddEdit aemodel;
    
    /** Listener for buttons in the table.
     * Adds or removes rows calling the view according to the button's text.
     */
    class AuthorButtonListener implements ActionListener {
        AddEdit aemodel;

        public AuthorButtonListener(AddEdit aemodel) {
            this.aemodel = aemodel;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            JButton btn = (JButton) actionEvent.getSource();
            if (btn.getText().equals(ADD))
                aemodel.addAuthorRow();
            else
                aemodel.removeAuthorRow(Integer.parseInt(actionEvent.getActionCommand()));     
            fireEditingStopped();
        }
    }

    /** Creates a new instance of ButtonEditor */
    public ButtonEditor(AddEdit aemodel) {
        this.aemodel = aemodel;
        addButton = new JButton(ADD);
        removeButton = new JButton(REMOVE);
    }

    public Object getCellEditorValue() {
        return "";
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        TableModel tm = table.getModel();
        int rowCount = tm.getRowCount();
        
        if (!editors.containsKey(row)) {
            JButton button = new JButton();
            button.addActionListener(new AuthorButtonListener(aemodel));
            editors.put(row, button);
        }
        
        JButton button = editors.get(row);
        if (row == (rowCount-1)) {
            button.setText(ADD);
            button.setActionCommand("");
        } else {
            button.setText(REMOVE);
            button.setActionCommand(""+row);
        }
        
        return button;
    }

    public Component getTableCellRendererComponent(JTable table, Object object, boolean b, boolean b0, int row, int i0) {
        TableModel tm = table.getModel();
        int rowCount = tm.getRowCount();
        
        if (row == (rowCount-1)) {
            return addButton;
        } else {
            return removeButton;
        }
    }
}
