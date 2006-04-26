/*
 * AuthorTableModel.java
 *
 * Created on 25. duben 2006, 17:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.Pair;

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
        if (btn.getText().equals("Add"))
            aemodel.addAuthorRow();
        else
            aemodel.removeAuthorRow(Integer.parseInt(actionEvent.getActionCommand()));
    }
}


/** Table model for the authors table in AddEditView.
 *
 */
public class AuthorTableModel extends AbstractTableModel {
    ArrayList<Object[]> data = new ArrayList<Object[]>();
    Object[] row;
    AddEdit aemodel;
    
    public AuthorTableModel(AddEdit aemodel) {
        this.aemodel = aemodel;
        row = new Object[3];
        row[0] = new Pair("",0);
        row[1] = "";
        JButton b = new JButton("Add");
        b.addActionListener(new AuthorButtonListener(aemodel));
        b.setActionCommand("0");
        row[2] = b;
        data.add(row);
        loadDataFromModel();
        fireTableDataChanged();
    }
    
    private void loadDataFromModel() {
        for (int i = 0; i < aemodel.getAuthorCount(); i++)
            addRow(aemodel.getAuthor(i), aemodel.getAuthorRole(i));        
    }
    
    public void reset() {
        data.clear();
        row = new Object[3];
        row[0] = new Pair("",0);
        row[1] = "";
        JButton b = new JButton("Add");
        b.addActionListener(new AuthorButtonListener(aemodel));
        b.setActionCommand("0");
        row[2] = b;
        data.add(row);
        loadDataFromModel();
        fireTableDataChanged();        
    }
    
    public void addRow() {
        //create a new row
        row = new Object[3];
        row[0] = new Pair("",0);
        row[1] = "";
        JButton b = new JButton("Add");
        b.addActionListener(new AuthorButtonListener(aemodel));
        b.setActionCommand(""+data.size());
        row[2] = b;
        
        //get the last row and update it's text to Remove
        Object[] rowTmp = data.get(data.size()-1);
        ((JButton)rowTmp[2]).setText("Remove");
        
        data.add(row);

        //we fire that in case someone would like to register a table model listener with us
        //and get some reasonable data
        fireTableRowsInserted(data.size()-1,data.size()-1);
    }
    
    public void addRow(Pair<String, Integer> author, String role) {
        addRow();
        Object[] row = data.get(data.size()-2);
        row[0] = author;
        row[1] = role;
        fireTableDataChanged();
    }
    
    public void removeRow(int i) {
        System.out.println("AuthorTableModel: removing row #"+i);
        Object[] row = data.remove(i);
        JButton b = (JButton)row[2];
        for (int j=0; j < data.size(); j++){
            b = (JButton)data.get(j)[2];
            b.setActionCommand(""+j);
        }

        //we fire that in case someone would like to register a table model listener with us
        //and get some reasonable data
        fireTableRowsDeleted(i,i);

        //unfortunately have to do this so that each cell regets it's updated renderer
        //which is needed for the last row mainly
        fireTableStructureChanged();
    }
    
    public int getRowCount() {
        return data.size();
    }
    
    public int getColumnCount() {
        return row.length;
    }
    
    public Object getValueAt(int i, int i0) {
        return data.get(i)[i0];
    }
    
    /** Has to be overriden so that our data (and table) get updated after editing.
     */
    public void setValueAt(Object o, int r, int c) {
        if (c == 2) //we don't want to lose our nice buttons with listeners, proper text, ...
            return;
        Object[] row = data.get(r);
        row[c] = o;
        if (c == 0)
            aemodel.setAuthor(r, (Pair<String, Integer>) o);
        if (c == 1)
            aemodel.setAuthorRole(r, (String) o);
    }
    
    public Class getColumnClass(int c) {
        //can't do this:
        //return getValueAt(0,c).getClass();
        //otherwise it throws NullPointerException perhaps thanks to some multiple threads running simultaneously
        //so that the table view exists earlier than this model fetched data :-(
        switch (c) {
            case 0:
                return Pair.class;
            case 1:
                return String.class;
            case 2:
                return JButton.class;
            default:
                return null;
        }                                
    }
    
    public boolean isCellEditable(int row, int col) {
        //we don't want the last row to be editable except it's last(=button) column
        if (row == data.size()-1)
            if (col == 2)
                return true;
            else
                return false;
        else
            return true;
    }
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Name";
            case 1:
                return "Role";
            case 2:
                return "";
            default:
                return "";
        }
    }
}
    