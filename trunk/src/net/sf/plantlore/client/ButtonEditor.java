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
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/** Button Editor and Renderer used by the author table in AddEdit dialog.
 *
 * Simply returns the buttons stored in the table model.
 * @author reimei
 */
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer  {
    JButton button;
    
    /** Creates a new instance of ButtonEditor */
    public ButtonEditor() {
        button = new JButton("Add");
    }

    public Object getCellEditorValue() {
        return button;
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        return (Component)table.getModel().getValueAt(row,2);
    }

    public Component getTableCellRendererComponent(JTable table, Object object, boolean b, boolean b0, int row, int i0) {
        return (Component)table.getModel().getValueAt(row,2);
    }
}
