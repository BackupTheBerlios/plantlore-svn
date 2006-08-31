/*
 * CreateDBView.java
 *
 * Created on 29. srpen 2006, 15:21
 */

package net.sf.plantlore.client.createdb;

import java.awt.Frame;
import java.util.Observable;
import java.util.Observer;

import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  yaa
 */
public class CreateDBView extends javax.swing.JDialog implements Observer {
	
	    
    /** Creates new form CreateDBView */
    public CreateDBView(Frame parent, CreateDB model) {
    	super(parent, true);
    	
    	model.addObserver(this);
    	initComponents();
    	
        setLocationRelativeTo(null); // center of the screen
        getRootPane().setDefaultButton(next);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        databasePort = new javax.swing.JTextField();
        databaseEngine = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        databaseIdentifier = new javax.swing.JTextField();
        leaveEmpty = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        databaseAlias = new javax.swing.JTextField();
        next = new javax.swing.JButton();

        setTitle(L10n.getString("CreateDB.Title"));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("CreateDB.DatabaseSettings")));
        jLabel1.setText(L10n.getString("Login.Database"));

        jLabel2.setText(L10n.getString("Login.DatabasePort"));

        databasePort.setToolTipText(L10n.getString("Login.DatabasePortTT"));

        databaseEngine.setEditable(true);
        databaseEngine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "postgresql", "firebirdsql", "mysql", "oraclesql" }));
        databaseEngine.setToolTipText(L10n.getString("Login.DatabaseTT"));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(databaseEngine, 0, 144, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(databasePort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(databaseEngine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(databasePort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("CreateDB.NewDatabaseOptions")));
        jLabel4.setText(L10n.getString("Login.DatabaseIdentifier"));

        databaseIdentifier.setToolTipText(L10n.getString("Login.DatabaseIdentifierTT"));

        leaveEmpty.setText(L10n.getString("CreateDB.LeaveEmpty"));
        leaveEmpty.setToolTipText(L10n.getString("CreateDB.LeaveEmptyTT"));
        leaveEmpty.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        leaveEmpty.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel5.setText(L10n.getString("Login.Alias"));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(jLabel4))
                        .add(11, 11, 11)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(databaseAlias, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                            .add(databaseIdentifier, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(leaveEmpty)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(databaseAlias, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(databaseIdentifier, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(leaveEmpty)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        next.setText(L10n.getString("Login.Next"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(next))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(next)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField databaseAlias;
    protected javax.swing.JComboBox databaseEngine;
    protected javax.swing.JTextField databaseIdentifier;
    protected javax.swing.JTextField databasePort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    protected javax.swing.JCheckBox leaveEmpty;
    protected javax.swing.JButton next;
    // End of variables declaration//GEN-END:variables
    
    
	public void update(Observable source, final Object parameter) {
		java.awt.EventQueue.invokeLater(new Runnable() {
    		public void run() {
    			if(parameter instanceof DBInfo)
    				setVisible(false);
    		}
		});
	}
    
}