/*
 * ExportMngView.java
 *
 * Created on 29. duben 2006, 21:00
 */

package net.sf.plantlore.client.export;

import java.awt.Frame;

import net.sf.plantlore.client.export.component.ProjectionSelectionModel;
import net.sf.plantlore.client.export.component.ExtendedTree;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.l10n.L10n;

/**
 * Allows the User to select columns (i.e. properties of the records) that should be exported.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class ExportMngViewB extends javax.swing.JDialog {
	
	protected ProjectionSelectionModel tsm;
    
    /** Creates new form ExportMngView */
    public ExportMngViewB(Frame parent) {
    	super(parent, true);
    	
    	tsm = new ProjectionSelectionModel( new Projection() );
    	
        initComponents();
        getRootPane().setDefaultButton(next);
        setLocationRelativeTo(null); // center of the screen
                
        PlantloreHelp.addKeyHelp(PlantloreHelp.EXPORT, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.EXPORT, this.help);
        
        new DefaultEscapeKeyPressed( this );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new ExtendedTree();
        next = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        help = new javax.swing.JButton();

        setTitle(L10n.getString("Export.SelectColumns"));
        tree.setSelectionModel( tsm );
        jScrollPane1.setViewportView(tree);

        next.setText(L10n.getString("Export.Title"));

        cancel.setText(L10n.getString("Common.Cancel"));

        help.setText(L10n.getString("Common.Help"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(help)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 171, Short.MAX_VALUE)
                        .add(cancel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(next)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancel, next}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(next)
                    .add(cancel)
                    .add(help))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton cancel;
    protected javax.swing.JButton help;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JButton next;
    protected javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
    
}
