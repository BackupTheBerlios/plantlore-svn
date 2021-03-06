/*
 * DetailsHistoryView.java
 *
 * Created on 19. duben 2006, 14:39
 */

package net.sf.plantlore.client.history;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JDialog;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.l10n.L10n;

/**
 * View for the DetailsHistory dialog (part of the DetailsHistory MVC). Used for displaying the detailed information about the record.
 * 
 * @author  Lada Oberreiterova
 * @version 1.0
 */
public class DetailsHistoryView extends javax.swing.JDialog implements Observer {
    
    
	private static final long serialVersionUID = 2142390888514121396L;
	
	/**
     * Creates new form DetailsHistoryView     
     * @param parent parent of this dialog
     * @param modal boolean flag whether the dialog should be modal or not
     */
    public DetailsHistoryView(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);        
        initComponents();                
        //Init Help
        PlantloreHelp.addKeyHelp(PlantloreHelp.HISTORY_DATA, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.HISTORY_DATA, this.helpButton);        
        this.detailsTextArea.setEditable(false);
        setTitle(L10n.getString("WholeHistory.DetailTitle"));
        setLocationRelativeTo(null);
    }
        
    public void update(Observable observable, Object object) {
    } 
    
    /**
     * Display generic message containing detailed information about the record
     * @param detailsMessage Message we want to display     
     */
    public void setDetailsMessage(String detailsMessage) {
        this.detailsTextArea.setText(detailsMessage);
    }
       
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("History.DetailsPanel")));
        detailsTextArea.setColumns(20);
        detailsTextArea.setFont(new java.awt.Font("Arial", 0, 12));
        detailsTextArea.setRows(5);
        jScrollPane2.setViewportView(detailsTextArea);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );

        closeButton.setText(L10n.getString("Common.Cancel"));

        helpButton.setText(L10n.getString("Common.Help"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 193, Short.MAX_VALUE)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton)
                    .add(helpButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton closeButton;
    protected javax.swing.JTextArea detailsTextArea;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
    
}
