/*
 * AuthView.java
 *
 * Created on 9. duben 2006, 18:04
 */

package net.sf.plantlore.client.login;

import java.util.Observable;
import java.util.Observer;

import net.sf.plantlore.common.AutoComboBox;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  yaa
 */
public class AuthView extends javax.swing.JDialog implements Observer {
	
	private Login model;
    
    /** Creates new form AuthView */
    public AuthView(Login model) {
    	this.model = model;
    	model.addObserver(this);
        initComponents();
        setLocationRelativeTo(null); // center of the screen
        setModal(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        user = new AutoComboBox();
        password = new javax.swing.JPasswordField();
        next = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jLabel1.setText(L10n.getString("Username") + ":");

        jLabel2.setText(L10n.getString("Password") + ":");
        
        user.setStrict(false);
        
        next.setText(L10n.getString("Authorize"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(password, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                            .add(user, 0, 222, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 199, Short.MAX_VALUE)
                        .add(next)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(user, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(password, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(next)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    /**
     * Reload the list of usernames according to the currently selected record.
     */
    public void update(Observable arg0, Object arg1) {
		DBInfo selected = model.getSelected();
		if(selected == null) return;

		user.removeAllItems();
		user.addItems(selected.users);
		
		setTitle(L10n.getString("ConnectingTo") + " " + selected.toString());
	}
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton next;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel jLabel2;
    protected javax.swing.JPasswordField password;
    protected AutoComboBox user;
    // End of variables declaration//GEN-END:variables

	
    
}
