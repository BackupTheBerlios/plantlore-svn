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
import net.sf.plantlore.common.PlantloreHelp;
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
        PlantloreHelp.addKeyHelp(PlantloreHelp.HISTORY_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.HISTORY_MANAGER, this.helpButton);        
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
        clearHistoryButton = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        undoToDateButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        totalResultLabel = new javax.swing.JLabel();
        totalResultValueLabel = new javax.swing.JLabel();
        toDisplayLabel = new javax.swing.JLabel();
        toDisplayValueTextField = new javax.swing.JTextField();
        displayedLabel = new javax.swing.JLabel();
        displayedValueLabel = new javax.swing.JLabel();
        nextButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("History.Whole.Panel")));
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

        previousButton.setText(L10n.getString("History.ButtonPrev"));

        clearHistoryButton.setText(L10n.getString("History.Whole.ButtonClearHistory"));
        clearHistoryButton.setInheritsPopupMenu(true);

        detailsButton.setText(L10n.getString("History.ButtonDetails"));

        undoToDateButton.setText(L10n.getString("History.Whole.ButtonUndoToDate"));

        totalResultLabel.setText(L10n.getString("History.TotalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayLabel.setText(L10n.getString("History.RowsToDisplay"));

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());
        toDisplayValueTextField.setAutoscrolls(false);

        displayedLabel.setText(L10n.getString("History.Displayed"));

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(totalResultLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(totalResultValueLabel)
                .add(31, 31, 31)
                .add(toDisplayLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(30, 30, 30)
                .add(displayedLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(displayedValueLabel)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(totalResultLabel)
                    .add(toDisplayLabel)
                    .add(totalResultValueLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(displayedLabel)
                    .add(displayedValueLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addContainerGap())
        );

        nextButton.setText(L10n.getString("History.ButtonNext"));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(detailsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(undoToDateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(previousButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 46, Short.MAX_VALUE)
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(12, 12, 12)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(nextButton)
                            .add(clearHistoryButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 747, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(previousButton)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(nextButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(detailsButton)
                            .add(undoToDateButton)
                            .add(clearHistoryButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap())
        );

        okButton.setText(L10n.getString("History.ButtonOk"));

        closeButton.setText(L10n.getString("History.ButtonClose"));

        helpButton.setText(L10n.getString("History.ButtonHelp"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 462, Short.MAX_VALUE)
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 768, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
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
    	int okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("Question.Undo") + "  "+ message, L10n.getString("Question.UndoTitle"), JOptionPane.OK_CANCEL_OPTION);
    	return okCancle;
    }
     
    public void messageSelection() {
    	JOptionPane.showMessageDialog(this, L10n.getString("Warning.EmptySelection"), L10n.getString("Warning.EmptySelectionTitle"), JOptionPane.ERROR_MESSAGE);               
    }
    
    public int messageClearHistory() {
    	int okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("Question.ClearHistoryMessage"), L10n.getString("Question.ClearHistoryMessageTitle"), JOptionPane.OK_CANCEL_OPTION);
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
