/*
 * SynonymSearch.java
 *
 * Created on 23. říjen 2006, 16:08
 */

package net.sf.plantlore.client.synonyms;

import java.util.Observable;
import java.util.Observer;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  yaa
 */
public class PlantSearchView extends javax.swing.JDialog implements Observer {
    
    private PlantSearch model;
    
    /** Creates new form SynonymSearch */
    public PlantSearchView(javax.swing.JDialog parent, PlantSearch model) {
        super(parent, true);
        
        this.model = model;
        
        initComponents();
        setLocationRelativeTo(parent);
        
        setTitle(L10n.getString("PlantSearch.Title"));
        jLabel1.setText(L10n.getString("PlantSearch.Contains"));
        
        new DefaultEscapeKeyPressed( this );
        getRootPane().setDefaultButton( find );
        model.addObserver(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        find = new javax.swing.JButton();
        insert = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        results = new javax.swing.JList();
        cancel = new javax.swing.JButton();
        pattern = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jLabel1.setText("Contains:");

        find.setText("Find");

        insert.setText("Insert");

        results.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(results);

        cancel.setText("Cancel");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pattern, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(find))
                    .add(layout.createSequentialGroup()
                        .add(insert)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancel)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancel, insert}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(find)
                    .add(pattern, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancel)
                    .add(insert))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public void update(Observable o, Object arg) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Pair<String,Plant>[] plants = model.getResults();
                if(plants != null) {
                    results.setListData( plants );
                    if(plants.length > 0) {
                    	results.setSelectedIndex(0);
                    	results.ensureIndexIsVisible(0);
                    	results.requestFocus();
                    }
                }
                else
                    results.setListData(new Object[0]);
            }
        });
    }
    

    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton cancel;
    protected javax.swing.JButton find;
    protected javax.swing.JButton insert;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JTextField pattern;
    protected javax.swing.JList results;
    // End of variables declaration//GEN-END:variables
    
}
