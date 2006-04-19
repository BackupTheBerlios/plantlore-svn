/*
 * LoginView2.java
 *
 * Created on 9. duben 2006, 16:35
 */

package net.sf.plantlore.client.login;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;

import net.sf.plantlore.middleware.DBLayer;

/**
 *
 * @author  yaa
 */
public class LoginView extends javax.swing.JDialog implements Observer {
	
	private Login model;
    
    /** Creates new form LoginView2 */
    public LoginView(Login model) {
		this.model = model;
		model.addObserver(this);
        initComponents();
        setLocationRelativeTo(null); // center of the screen
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setModal(true);
        // See what's new.
        update(null, null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        popup = new javax.swing.JPopupMenu();
        add = new javax.swing.JMenuItem();
        edit = new javax.swing.JMenuItem();
        remove = new javax.swing.JMenuItem();
        next = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        choice = new javax.swing.JList();
        remember = new javax.swing.JCheckBox();

        popup.setName("popup");
        add.setText("Add");
        popup.add(add);

        edit.setText("Edit");
        popup.add(edit);

        remove.setText("Remove");
        popup.add(remove);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        next.setText("Continue");
        next.setName("next");

        choice.setComponentPopupMenu(popup);
        choice.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(choice);

        remember.setText("select automatically");
        remember.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        remember.setMargin(new java.awt.Insets(0, 0, 0, 0));
        remember.setSelected(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(remember)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 157, Short.MAX_VALUE)
                        .add(next)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(next)
                    .add(remember))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
        
    @Override
    public void setVisible(boolean visible) {
    	if(!visible) super.setVisible(false);
    	else {
    		
    	}
    }
    
    
    public void update(Observable source, Object parameter) {
    	// Ignore setSelected() event
    	if(parameter == null) {
    		// Every item of the list will - after being added to the list - 
    		// cause a ListSelectionEvent (valueChange) event!
    		// This is probably because every time an item is inserted 
    		// into the list, it is also selected!
    		choice.setListData(model.getRecords());
    		
    	}
    	else if(parameter != null && parameter instanceof DBLayer)
    		this.setVisible(false); // the database layer has been created, we are no longer neccessary
	}
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenuItem add;
    protected javax.swing.JList choice;
    protected javax.swing.JMenuItem edit;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JButton next;
    protected javax.swing.JPopupMenu popup;
    protected javax.swing.JCheckBox remember;
    protected javax.swing.JMenuItem remove;
    // End of variables declaration//GEN-END:variables
    
}
