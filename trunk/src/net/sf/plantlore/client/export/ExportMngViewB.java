/*
 * ExportMngView.java
 *
 * Created on 29. duben 2006, 21:00
 */

package net.sf.plantlore.client.export;

import net.sf.plantlore.client.export.component.TemplateSelectionModel;
import net.sf.plantlore.client.export.component.XTree;

/**
 *
 * @author  yaa
 */
public class ExportMngViewB extends javax.swing.JDialog {
	
	protected TemplateSelectionModel tsm;
    
    /** Creates new form ExportMngView */
    public ExportMngViewB() {
    	tsm = new TemplateSelectionModel();
    	
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new XTree();
        next = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jScrollPane1.setViewportView(tree);

        next.setText("Export");
        
        tree.setSelectionModel( tsm );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, next))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(next)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JButton next;
    protected XTree tree;
    // End of variables declaration//GEN-END:variables
    
}