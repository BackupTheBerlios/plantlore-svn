/*
 * ItemView.java
 *
 * Created on 9. duben 2006, 17:55
 */

package net.sf.plantlore.client.login;

import java.util.Observable;
import java.util.Observer;


import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  yaa
 */
public class ItemView extends javax.swing.JDialog implements Observer {
	
	private Login model;
    
    /** Creates new form ItemView */
    public ItemView(Login model) {
		this.model = model;
		model.addObserver(this);
		initComponents();
		getRootPane().setDefaultButton(next);
		setLocationRelativeTo(null);
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
        jLabel3 = new javax.swing.JLabel();
        alias = new javax.swing.JTextField();
        host = new javax.swing.JTextField();
        db = new javax.swing.JTextField();
        next = new javax.swing.JButton();

        setTitle(L10n.getString("Login.Change"));
        setTitle(L10n.getString("Login.Add"));
        setModal(true);
        setResizable(false);
        jLabel1.setText(L10n.getString("Login.Alias"));

        jLabel2.setText(L10n.getString("Login.HostName"));

        jLabel3.setText(L10n.getString("Login.Database"));

        next.setText(L10n.getString("Login.Change"));

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
                            .add(jLabel2)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(db, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                            .add(host, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                            .add(alias, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, next))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(alias, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(host, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(db, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(next)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    /**
     * Fill all fields with information obtained from the currently selected record.
     */
    public void update(Observable arg0, Object arg1) {
		DBInfo info = model.getSelected();
		if(info == null) return;
		alias.setText(info.alias); 
		host.setText(info.host + ((info.port != 1099) ? " : " + info.port : ""));
		db.setText(info.db);
	}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField alias;
    protected javax.swing.JTextField db;
    protected javax.swing.JTextField host;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    protected javax.swing.JButton next;
    // End of variables declaration//GEN-END:variables
    
}
