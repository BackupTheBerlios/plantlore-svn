/*
 * WholeHistoryView.java
 *
 * Created on 23. červenec 2006, 14:37
 */

package net.sf.plantlore.client.history;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.common.TransferFocus;

/**
 * View for the main WholeHistory dialog (part of the WholeHistory MVC). Used for displaying the search results.
 * @author  Lada Oberreiterová
 * @version 1.0
 */
public class WholeHistoryView extends javax.swing.JDialog implements Observer {
       
	private static final long serialVersionUID = -8216564649155252124L;
	private History model;  
  
    /**
     * Creates new form WholeHistoryView
     * @param model model of the WholeHistory MVC
     * @param parent parent of this dialog
     * @param modal boolean flag whether the dialog should be modal or not
     */
    public WholeHistoryView(History model, java.awt.Frame parent, boolean modal) {
        
        super(parent, modal);
        this.model = model;
        // Register observer
        model.addObserver(this);        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        initComponents();        
        getRootPane().setDefaultButton(closeButton);            
        // Init Help
        PlantloreHelp.addKeyHelp(PlantloreHelp.HISTORY_DATA, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.HISTORY_DATA, this.helpButton);    
        setTitle(L10n.getString("WholeHistory.Title"));
        setLocationRelativeTo(null);
    }
    
     /**
     * Reload the view dialog or display some kind of error.
     */
    public void update(Observable observable, Object object)
    {
    	// Check whether we have some kind of error to display
        if (model.isError()) {
            showErrorMessage(model.getError());                          
            return;
        } 
    }
    
    /**
     * Initialize actual data for displaying in view dialog.     
     */
    public void initialize() {
    	model.setDisplayRows(History.DEFAULT_DISPLAY_ROWS);
        model.setCurrentFirstRow(1);
    	this.tableHistoryList.setRowSelectionAllowed(true);
        this.tableHistoryList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        this.tableHistoryList.setModel(new WholeHistoryTableModel(model));     
        previousButton.setEnabled(false);
        nextButton.setEnabled(true);
        if ((History.DEFAULT_DISPLAY_ROWS >= model.getResultRows())) {
        	nextButton.setEnabled(false);
        }
        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());
        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());
        displayedValueLabel.setText(model.getCurrentDisplayRows());
    }
    
    /**
     *  Shows and inicialize actual data or hides this dialog depending on the value of parameter visible. 
     *  @param visible if true, shows this component and initialize actual data; otherwise, hides this component
     */
    public void setVisible(boolean visible) {
    	if (visible) initialize();
    	else model.closeQuery();
    	super.setVisible(visible);
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
        clearHistoryButton = new javax.swing.JButton();
        undoToDateButton = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        totalResultLabel = new javax.swing.JLabel();
        totalResultValueLabel = new javax.swing.JLabel();
        toDisplayLabel = new javax.swing.JLabel();
        toDisplayValueTextField = new javax.swing.JTextField();
        displayedLabel = new javax.swing.JLabel();
        displayedValueLabel = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("History.Whole.Panel")));
        jScrollPane1.setBackground(java.awt.Color.white);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
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
        TransferFocus.patch(tableHistoryList);
        jScrollPane1.setViewportView(tableHistoryList);

        previousButton.setText(L10n.getString("Common.Prev"));

        nextButton.setText(L10n.getString("Common.Next"));

        clearHistoryButton.setText(L10n.getString("History.Whole.ButtonClearHistory"));

        undoToDateButton.setText(L10n.getString("History.Whole.ButtonUndoToDate"));

        detailsButton.setText(L10n.getString("History.ButtonDetails"));

        totalResultLabel.setText(L10n.getString("History.TotalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayLabel.setText(L10n.getString("History.RowsToDisplay"));

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());

        displayedLabel.setText(L10n.getString("History.Displayed"));

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(previousButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(50, 50, 50)
                        .add(totalResultLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalResultValueLabel)
                        .add(41, 41, 41)
                        .add(toDisplayLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(50, 50, 50)
                        .add(displayedLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(displayedValueLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 77, Short.MAX_VALUE)
                        .add(nextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(detailsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(undoToDateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(clearHistoryButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(previousButton)
                    .add(nextButton)
                    .add(totalResultLabel)
                    .add(totalResultValueLabel)
                    .add(toDisplayLabel)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(displayedValueLabel)
                    .add(displayedLabel))
                .add(13, 13, 13)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(clearHistoryButton)
                    .add(undoToDateButton)
                    .add(detailsButton))
                .addContainerGap())
        );

        helpButton.setText(L10n.getString("Common.Help"));

        closeButton.setText(L10n.getString("Common.Cancel"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 628, Short.MAX_VALUE)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(closeButton))
                .add(20, 20, 20))
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
     * Close this dialog.
     */
    public void close() {
        dispose();
    }
    
    /**
     *  Display generic error message.
     *  @param message Message we want to display
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, L10n.getString(message), L10n.getString("Common.ErrorMessageTitle"), JOptionPane.ERROR_MESSAGE);               
    }  
    
    /**
     * Display generic message
     * @param message Message we want to display
     * @return information about user selection. <ul><li> 0 if user press OK button</li> <li>1 if user press Cancle button</li></ul>
     */
    public int messageUndo(String message) {
    	int okCancle = 1;
    	if (message.equals("clearHistory")) {
    		okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("Question.ClearHistoryMessage"), L10n.getString("Question.ClearHistoryMessageTitle"), JOptionPane.OK_CANCEL_OPTION);
    	}else {
    		okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("Question.Undo") + "  "+ message, L10n.getString("Question.UndoTitle"), JOptionPane.OK_CANCEL_OPTION);
    	}
    	return okCancle;
    }
    
    /**
     * Display generic warning message
     * @param message Message we want to display     
     */
    public void messageSelection() {
    	JOptionPane.showMessageDialog(this, L10n.getString("Warning.EmptySelection"), L10n.getString("Warning.EmptySelectionTitle"), JOptionPane.ERROR_MESSAGE);               
    }
      
    /**
     * Get numger of rows to displaying on one page
     * @return numger of rows to displaying on one page
     */
    public Integer getDisplayRows() { 
        Integer countRows;
        try {
            countRows = Integer.parseInt(toDisplayValueTextField.getText());
        }catch (NumberFormatException e){            
            countRows = 0;
        }
        return countRows;
    }
    
    /** 
     * Set number of rows in results
     * @param resultRows number of rows in results
     */
    public void setCountResult(Integer resultRows)
    {
    	if (resultRows != null)
    		this.totalResultValueLabel.setText(resultRows.toString());
    }    
    
    /**
     * Set numger of rows to displaying on one page
     * @param value numger of rows to displaying on one page
     */
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
    protected javax.swing.JButton previousButton;
    protected javax.swing.JTable tableHistoryList;
    private javax.swing.JLabel toDisplayLabel;
    protected javax.swing.JTextField toDisplayValueTextField;
    private javax.swing.JLabel totalResultLabel;
    protected javax.swing.JLabel totalResultValueLabel;
    protected javax.swing.JButton undoToDateButton;
    // End of variables declaration//GEN-END:variables
    
}
