/*
 * HabitatTree.java
 *
 * Created on 7. srpen 2006, 12:36
 */

package net.sf.plantlore.client.overview.tree;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JTree;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  fraktalek
 */
public class HabitatTreeView extends javax.swing.JDialog implements Observer {
    HabitatTree model;
    
    /** Creates new form HabitatTree */
    public HabitatTreeView(java.awt.Frame parent, boolean modal, HabitatTree model) {
        super(parent, modal);
        this.model = model;
        initComponents();
        model.addObserver(this);
        setLocationRelativeTo(parent);
        searchMenuItem.setText(L10n.getString("Overview.Tree.SearchMenuItem"));
        refreshMenuItem.setText(L10n.getString("Overview.Tree.RefreschMenuItem"));
        deleteMenuItem.setText(L10n.getString("Overview.Tree.DeleteMenuItem"));
        setTitle(L10n.getString("Overview.HabitatTree"));
        new DefaultEscapeKeyPressed(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        popupMenu = new javax.swing.JPopupMenu();
        searchMenuItem = new javax.swing.JMenuItem();
        refreshMenuItem = new javax.swing.JMenuItem();
        addMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        habitatTree = new JTree(model.getTreeModel());
        jPanel1 = new javax.swing.JPanel();
        searchButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();

        searchMenuItem.setText("Search");
        popupMenu.add(searchMenuItem);

        refreshMenuItem.setText("Refresh");
        popupMenu.add(refreshMenuItem);

        addMenuItem.setText("Item");
        popupMenu.add(addMenuItem);

        deleteMenuItem.setText("Item");

        habitatTree.setComponentPopupMenu(popupMenu);
        jScrollPane1.setViewportView(habitatTree);

        searchButton.setText("jButton1");

        refreshButton.setText("jButton2");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(searchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 209, Short.MAX_VALUE)
                .add(refreshButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(searchButton)
                .add(refreshButton))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HabitatTreeView(new javax.swing.JFrame(), true, null).setVisible(true);
            }
        });
    }

    public void update(Observable o, Object arg) {
        if (arg != null && arg instanceof String) {
            if (arg.equals("LOADED_DATA")) {
                habitatTree.expandRow(0);                
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenuItem addMenuItem;
    protected javax.swing.JMenuItem deleteMenuItem;
    protected javax.swing.JTree habitatTree;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JPopupMenu popupMenu;
    protected javax.swing.JButton refreshButton;
    protected javax.swing.JMenuItem refreshMenuItem;
    protected javax.swing.JButton searchButton;
    protected javax.swing.JMenuItem searchMenuItem;
    // End of variables declaration//GEN-END:variables
    
}
