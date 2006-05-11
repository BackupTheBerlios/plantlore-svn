/*
 * ButtonEditor.java
 *
 * Created on 24. duben 2006, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

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

/** Button Editor and Renderer used by the author table in AddEdit dialog.
 *
 * Simply returns the buttons stored in the table model.
 * @author reimei
 */
public class ButtonEditorSearch extends AbstractCellEditor implements TableCellEditor, TableCellRenderer  {
    HashMap<Integer,JButton> editors = new HashMap<Integer,JButton>();
    JButton addButton, removeButton;
    Search aemodel;
    
    /** Listener for buttons in the table.
     * Adds or removes rows calling the view according to the button's text.
     */
    class AuthorButtonListener implements ActionListener {
        Search aemodel;

        public AuthorButtonListener(Search aemodel) {
            this.aemodel = aemodel;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            JButton btn = (JButton) actionEvent.getSource();
            if (btn.getText().equals("Add"))
                aemodel.addAuthorRow();
            else
                aemodel.removeAuthorRow(Integer.parseInt(actionEvent.getActionCommand()));     
            fireEditingStopped();
        }
    }

    /** Creates a new instance of ButtonEditor */
    public ButtonEditorSearch(Search aemodel) {
        this.aemodel = aemodel;
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
    }

    public Object getCellEditorValue() {
        System.out.println("ButtonEditor.java: returning cell editor value");
        return "Kdo vi co a jak";
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
            button.setText("Add");
            button.setActionCommand("");
        } else {
            button.setText("Remove");
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
