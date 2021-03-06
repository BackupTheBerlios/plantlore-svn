﻿package net.sf.plantlore.client.login;

import java.awt.Cursor;
import java.awt.Dialog;
import java.util.Observable;
import java.util.Observer;

import net.sf.plantlore.common.AutoComboBox;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;

/**
 * The view allows the User to specify the Authentication information
 * - the User name (or the Account name) and the Password of that account.
 * 
 * The list of User names is reloaded accoring to the currently selected record
 * in the LoginView.
 * 
 * @author kaimu
 */
public class AuthView extends javax.swing.JDialog implements Observer {
	
    
    public AuthView(Dialog parent, Login model) {
    	super(parent, true);
    	
    	model.addObserver(this);
        initComponents();
        getRootPane().setDefaultButton(next);
        setLocationRelativeTo(null); // center of the screen
        
        new DefaultEscapeKeyPressed( this );
        
        DocumentSizeFilter.patch(password, 20);
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
        ((AutoComboBox)user).setStrict(false);
        password = new javax.swing.JPasswordField();
        next = new javax.swing.JButton();
        discard = new javax.swing.JButton();

        setTitle(L10n.getString("Login.Authentication"));
        setTitle(L10n.getString("Login.Authentication"));
        setModal(true);
        setResizable(false);
        jLabel1.setText(L10n.getString("Login.UserName"));

        jLabel2.setText(L10n.getString("Login.Password"));

        user.setEditable(true);

        next.setText(L10n.getString("Login.Authorize"));

        discard.setText(L10n.getString("Common.Cancel"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(password, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                            .add(user, 0, 223, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(next)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(discard)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {discard, next}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

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
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(discard)
                    .add(next))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    @Override
    public void setVisible(boolean arg0) {
    	next.setEnabled(true);
    	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		// Discard the password!
		password.setText("");
    	super.setVisible(arg0);
    }
    
    /**
     * Reload the list of usernames according to the currently selected record.
     * Hide the dialog if it is no longer necessary.
     */
    public void update(Observable source, final Object arg) {
    	java.awt.EventQueue.invokeLater(new Runnable() {
    		public void run() {
    			if(arg == null || arg instanceof DBInfo) {
    				DBInfo selected = (DBInfo)arg;
    				if(selected == null) return;
    				user.removeAllItems();
    				((AutoComboBox)user).addItems(selected.users);
    				password.setText("");
    				setTitle(L10n.getString("Login.ConnectingTo") + " " + selected.toString());
    			}
    			// The database layer has been created, we are no longer neccessary
    			else if(arg instanceof DBLayer) 
    				setVisible(false);
    		}
    	});
	}
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton discard;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    protected javax.swing.JButton next;
    protected javax.swing.JPasswordField password;
    protected javax.swing.JComboBox user;
    // End of variables declaration//GEN-END:variables

	
    
}
