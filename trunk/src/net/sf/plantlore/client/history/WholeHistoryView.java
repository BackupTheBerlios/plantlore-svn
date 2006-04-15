/*
 * WholeHistoryView.java
 *
 * Created on 14. duben 2006, 18:21
 */

package net.sf.plantlore.client.history;

import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  Lada
 */
public class WholeHistoryView extends javax.swing.JDialog implements Observer{
    
    //Whole History model
    private WholeHistory model;  
    //data
    private Object[][] data;
    
    /** Creates new form WholeHistoryView */
    public WholeHistoryView(WholeHistory model, java.awt.Frame parent, boolean modal) {
        
        super(parent, modal);
        this.model = model;
        initComponents();
        this.tableHistoryList.setRowSelectionAllowed(true);
        this.tableHistoryList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        this.tableHistoryList.setModel(new WholeHistoryTableModel(model));                
    }
    
    public void update(Observable observable, Object object)
    {
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
        tableHistoryList = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        undoToDateButton = new javax.swing.JButton();
        toDisplayLabel = new javax.swing.JLabel();
        displayedLabel = new javax.swing.JLabel();
        totalResultLabel = new javax.swing.JLabel();
        totalResultValueLabel = new javax.swing.JLabel();
        displayedValueLabel = new javax.swing.JLabel();
        toDisplayValueTextField = new javax.swing.JTextField();
        nextButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Whole History"));
        tableHistoryList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tableHistoryList);

        undoToDateButton.setText(L10n.getString("undoToDate"));

        toDisplayLabel.setText(L10n.getString("toDisplay"));

        displayedLabel.setText(L10n.getString("displayed"));

        totalResultLabel.setText(L10n.getString("totalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());
        toDisplayValueTextField.setAutoscrolls(false);

        nextButton.setText(L10n.getString("nextButton"));

        previousButton.setText(L10n.getString("prevButton"));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(undoToDateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(totalResultLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(totalResultValueLabel))
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(displayedLabel)
                                    .add(toDisplayLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(10, 10, 10)
                                        .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(displayedValueLabel))))
                        .addContainerGap())
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, nextButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, previousButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                        .addContainerGap(15, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(undoToDateButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 94, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(totalResultLabel)
                    .add(totalResultValueLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(displayedLabel)
                    .add(displayedValueLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(toDisplayLabel)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(previousButton)
                .add(4, 4, 4)
                .add(nextButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );

        okButton.setText(L10n.getString("Ok"));

        closeButton.setText(L10n.getString("Close"));

        helpButton.setText(L10n.getString("Help"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(300, 300, 300)
                        .add(okButton)
                        .add(20, 20, 20)
                        .add(closeButton)
                        .add(27, 27, 27)
                        .add(helpButton))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 29, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton)
                    .add(okButton)
                    .add(helpButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WholeHistoryView(null, new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
  
    /**
     *
     */
    public void close() {
        dispose();
    }
    
     public int messageUndo(String message) {
    	int okCancle = JOptionPane.showConfirmDialog(this, message, "Information about selected date for undo", JOptionPane.OK_CANCEL_OPTION);
    	return okCancle;
    }
     
    public void messageUndoSelection() {
    	JOptionPane.showMessageDialog(this, "No row was selected.", "Information about selected row", JOptionPane.ERROR_MESSAGE);               
    } 
    
    /**Rows to display */
    public Integer getDisplayRows() { 
        Integer countRows;
        try {
            countRows = Integer.parseInt(toDisplayValueTextField.getText());
        }catch (NumberFormatException e){            
            countRows = 0;
        }
        return countRows;
    }
    
    /**Rows to display*/
    public void setDisplayRows(Integer value) {
        this.toDisplayValueTextField.setText(value.toString());
    }  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton closeButton;
    private javax.swing.JLabel displayedLabel;
    protected javax.swing.JLabel displayedValueLabel;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JButton nextButton;
    protected javax.swing.JButton okButton;
    protected javax.swing.JButton previousButton;
    protected javax.swing.JTable tableHistoryList;
    private javax.swing.JLabel toDisplayLabel;
    protected javax.swing.JTextField toDisplayValueTextField;
    private javax.swing.JLabel totalResultLabel;
    protected javax.swing.JLabel totalResultValueLabel;
    protected javax.swing.JButton undoToDateButton;
    // End of variables declaration//GEN-END:variables
    
}
