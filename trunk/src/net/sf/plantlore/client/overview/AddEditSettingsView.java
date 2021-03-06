/*
 * AddEditSettings.java
 *
 * Created on 25. srpen 2006, 13:04
 */

package net.sf.plantlore.client.overview;

import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  fraktalek
 */
public class AddEditSettingsView extends javax.swing.JDialog {
    AddEditSettings model;
    
    /** Creates new form AddEditSettings */
    public AddEditSettingsView(javax.swing.JDialog parent, boolean modal,AddEditSettings model) {
        super(parent, modal);
        this.model = model;
        
        initComponents();
        load();
        
        setLabels();
        DefaultEscapeKeyPressed dekp = new DefaultEscapeKeyPressed(this);

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(javax.swing.JDialog.HIDE_ON_CLOSE);
        setTitle(L10n.getString("AddEdit.SettingsTitle"));
    }
    
    private void load() {
        Set set = model.getEnabled();
        
        assert set != null;
        
        Iterator it = set.iterator();
        DefaultListModel dfm = (DefaultListModel)enabledList.getModel();
        while (it.hasNext())
            dfm.addElement(it.next());
        
        set = model.getDisabled();
        
        assert set != null;
        
        it = set.iterator();
        dfm = (DefaultListModel)disabledList.getModel();
        while (it.hasNext())
            dfm.addElement(it.next());
    }
    
    
    private void setLabels() {
        enabledLabel.setText(L10n.getString("AddEdit.Settings.Enabled"));
        disabledLabel.setText(L10n.getString("AddEdit.Settings.Disabled"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        enabledList = new JList(new DefaultListModel());
        jScrollPane2 = new javax.swing.JScrollPane();
        disabledList = new JList(new DefaultListModel());
        disableButton = new javax.swing.JButton();
        enableButton = new javax.swing.JButton();
        enabledLabel = new javax.swing.JLabel();
        disabledLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jScrollPane1.setViewportView(enabledList);

        jScrollPane2.setViewportView(disabledList);

        disableButton.setText("jButton1");

        enableButton.setText("jButton2");

        enabledLabel.setText("jLabel1");

        disabledLabel.setText("jLabel2");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(enableButton)
                            .add(disableButton)))
                    .add(enabledLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, disabledLabel)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {disableButton, enableButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.linkSize(new java.awt.Component[] {jScrollPane1, jScrollPane2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(enabledLabel)
                    .add(disabledLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(disableButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 146, Short.MAX_VALUE)
                        .add(enableButton)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {disableButton, enableButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new AddEditSettingsView(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton disableButton;
    protected javax.swing.JLabel disabledLabel;
    protected javax.swing.JList disabledList;
    protected javax.swing.JButton enableButton;
    protected javax.swing.JLabel enabledLabel;
    protected javax.swing.JList enabledList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
    
}
