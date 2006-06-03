/*
 * AuthView.java
 *
 * Created on 9. duben 2006, 18:04
 */

package net.sf.plantlore.client.login;

import java.awt.Cursor;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import net.sf.plantlore.common.AutoComboBox;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;

/**
 *
 * @author  yaa
 */
public class AuthView extends javax.swing.JDialog implements Observer {
	
    /** Creates new form AuthView */
    public AuthView(Login model) {
    	model.addObserver(this);
        initComponents();
        getRootPane().setDefaultButton(next);
        setLocationRelativeTo(null); // center of the screen
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
        status = new javax.swing.JLabel();

        setTitle(L10n.getString("Login.Authentication"));
        setTitle(L10n.getString("Login.Authentication"));
        setModal(true);
        setResizable(false);
        jLabel1.setText(L10n.getString("Login.UserName"));

        jLabel2.setText(L10n.getString("Login.Password"));

        user.setEditable(true);

        next.setText(L10n.getString("Login.Authorize"));

        status.setText(" ...");
        status.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(password, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                            .add(user, 0, 223, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, next))
                .addContainerGap())
            .add(status, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(status, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    @Override
    public void setVisible(boolean arg0) {
    	next.setEnabled(true);
    	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    	super.setVisible(arg0);
    }
    
    /**
     * Reload the list of usernames according to the currently selected record.
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
    			// Exception! We must display the exception to the user.
    			else if(arg instanceof Exception) {
    				status.setText(L10n.getString("Login.Failed"));
    				next.setEnabled(true);
    				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    				JOptionPane.showMessageDialog(
    						null, 
    						((Exception)arg).getMessage(), 
    						L10n.getString("Error.LoginFailed"), 
    						JOptionPane.ERROR_MESSAGE);
    			}
    			// The database layer has been created, we are no longer neccessary
    			else if(arg instanceof DBLayer)
    				setVisible(false); 
    			// Some update information - display them in the status bar.
    			else if(arg instanceof String) 
    				status.setText("  " + (String)arg);
    		}
    	});
	}
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    protected javax.swing.JButton next;
    protected javax.swing.JPasswordField password;
    protected javax.swing.JLabel status;
    protected javax.swing.JComboBox user;
    // End of variables declaration//GEN-END:variables

	
    
}
