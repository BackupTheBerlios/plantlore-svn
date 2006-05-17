/*
 * WholeHistoryView.java
 *
 * Created on 14. duben 2006, 18:21
 */

package net.sf.plantlore.client.history;

import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  Lada
 */
public class WholeHistoryView extends javax.swing.JDialog implements Observer{
    
    //Whole History model
    private History model;  
  
    /** Creates new form WholeHistoryView */
    public WholeHistoryView(History model, java.awt.Frame parent, boolean modal) {
        
        super(parent, modal);
        this.model = model;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
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
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        totalResultLabel = new javax.swing.JLabel();
        totalResultValueLabel = new javax.swing.JLabel();
        toDisplayLabel = new javax.swing.JLabel();
        toDisplayValueTextField = new javax.swing.JTextField();
        displayedLabel = new javax.swing.JLabel();
        displayedValueLabel = new javax.swing.JLabel();
        clearHistoryButton = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        undoToDateButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("wholeHistoryPanel")));
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

        previousButton.setText(L10n.getString("prevButton"));

        nextButton.setText(L10n.getString("nextButton"));

        totalResultLabel.setText(L10n.getString("totalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayLabel.setText(L10n.getString("toDisplay"));

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());
        toDisplayValueTextField.setAutoscrolls(false);

        displayedLabel.setText(L10n.getString("displayed"));

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        clearHistoryButton.setText(L10n.getString("clearHistory"));
        clearHistoryButton.setInheritsPopupMenu(true);

        detailsButton.setText(L10n.getString("detailsHistory"));

        undoToDateButton.setText(L10n.getString("undoToDate"));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(previousButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 69, Short.MAX_VALUE)
                        .add(totalResultLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalResultValueLabel)
                        .add(34, 34, 34)
                        .add(toDisplayLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(40, 40, 40)
                        .add(displayedLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(displayedValueLabel)
                        .add(146, 146, 146))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(534, Short.MAX_VALUE)
                        .add(detailsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(undoToDateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(clearHistoryButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(nextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(toDisplayLabel)
                    .add(totalResultValueLabel)
                    .add(totalResultLabel)
                    .add(nextButton)
                    .add(previousButton)
                    .add(displayedLabel)
                    .add(displayedValueLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(25, 25, 25)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(clearHistoryButton)
                    .add(undoToDateButton)
                    .add(detailsButton))
                .addContainerGap())
        );

        okButton.setText(L10n.getString("Ok"));

        closeButton.setText(L10n.getString("Close"));

        helpButton.setText(L10n.getString("Help"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 586, Short.MAX_VALUE)
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(closeButton)
                    .add(okButton))
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
     
    public void messageSelection() {
    	JOptionPane.showMessageDialog(this, "No row was selected.", "Information about selected row", JOptionPane.ERROR_MESSAGE);               
    }
    
    public int messageClearHistory() {
    	int okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("clearHistoryMessage"), "Information", JOptionPane.OK_CANCEL_OPTION);
    	return okCancle;
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
    protected javax.swing.JButton clearHistoryButton;
    protected javax.swing.JButton closeButton;
    protected javax.swing.JButton detailsButton;
    private javax.swing.JLabel displayedLabel;
    protected javax.swing.JLabel displayedValueLabel;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
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
