/*
 * HistoryView.java
 *
 * Created on 14. duben 2006, 11:55
 */

package net.sf.plantlore.client.history;

import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.l10n.L10n;

/**
 * View for the main History dialog (part of the History MVC). Used for displaying the search results.
 * @author  Lada Oberreiterová
 * @version 1.0
 */
public class HistoryView extends javax.swing.JDialog implements Observer{
        
	private static final long serialVersionUID = -6749177153586329145L;
	/** Model of the History MVC*/
	private History model;      
    
    /**
     * Creates new form HistoryView
     * @param model model of the History MVC
     * @param parent parent of this dialog
     * @param modal boolean flag whether the dialog should be modal or not
     */
    public HistoryView(History model, java.awt.Frame parent, boolean modal) {
                
        super(parent, modal);
        this.model = model;
        // Register observer
        model.addObserver(this);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        initComponents();
        //Init Help
        PlantloreHelp.addKeyHelp(PlantloreHelp.HISTORY_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.HISTORY_MANAGER, this.helpButton);        
        getTable().setModel(new HistoryTableModel(model));         
        previousButton.setEnabled(false);
        if (getTable().getRowCount() <= model.getDisplayRows()) {
        	nextButton.setEnabled(false);
        }
    }
    
    /**
     * Reload the view dialog or display some kind of error.
     */
      public void update(Observable observable, Object object)
    {                                
    	  //Check whether we have some kind of error to display
          if (model.isError()) {
              showErrorMessage(model.getError());                          
              return;
          } 
    } 
      
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        taxonLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        taxonValueLabel = new javax.swing.JLabel();
        authorValueLabel = new javax.swing.JLabel();
        locationValueLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        whenInserLabel = new javax.swing.JLabel();
        whoInsertLabel = new javax.swing.JLabel();
        whenInsertValueLabel = new javax.swing.JLabel();
        whoInsertValueLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableEditList = new javax.swing.JTable();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        unselectAllButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        totalResultLabel = new javax.swing.JLabel();
        totalResultValueLabel = new javax.swing.JLabel();
        toDisplayLabel = new javax.swing.JLabel();
        toDisplayValueTextField = new javax.swing.JTextField();
        displayedLabel = new javax.swing.JLabel();
        displayedValueLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("History.DetailsRecordPanel")));
        taxonLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        taxonLabel.setText(L10n.getString("History.Taxon"));

        authorLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        authorLabel.setText(L10n.getString("History.Author"));

        locationLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        locationLabel.setText(L10n.getString("History.Location"));

        taxonValueLabel.setText(model.getNamePlant());

        authorValueLabel.setText(model.getNameAuthor());

        locationValueLabel.setText(model.getLocation());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(taxonLabel)
                    .add(authorLabel)
                    .add(locationLabel))
                .add(83, 83, 83)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(locationValueLabel)
                    .add(authorValueLabel)
                    .add(taxonValueLabel))
                .addContainerGap(424, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(taxonLabel)
                    .add(taxonValueLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(authorLabel)
                    .add(authorValueLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(locationLabel)
                    .add(locationValueLabel))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("History.RecordCreatedPanel")));
        whenInserLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        whenInserLabel.setText(L10n.getString("History.WhenInsert"));

        whoInsertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        whoInsertLabel.setText(L10n.getString("History.WhoInsert"));

        String whenInsert = (model.getWhen() == null) ? null : DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(model.getWhen());
        whenInsertValueLabel.setText(whenInsert);

        whoInsertValueLabel.setText(model.getNameUser());

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(whenInserLabel)
                    .add(whoInsertLabel))
                .add(80, 80, 80)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(whoInsertValueLabel)
                    .add(whenInsertValueLabel))
                .addContainerGap(427, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(whenInserLabel)
                    .add(whenInsertValueLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(whoInsertLabel)
                    .add(whoInsertValueLabel))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("History.ChangesRecordPanel")));
        tableEditList.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableEditList);

        previousButton.setText(L10n.getString("History.ButtonPrev"));

        nextButton.setText(L10n.getString("History.ButtonNext"));

        selectAllButton.setText(L10n.getString("History.ButtonSelectAll"));

        unselectAllButton.setText(L10n.getString("History.ButtonUnselectAll"));

        undoButton.setText(L10n.getString("History.ButtonUndoSelected"));

        totalResultLabel.setText(L10n.getString("History.TotalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayLabel.setText(L10n.getString("History.RowToDisplay"));

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());
        toDisplayValueTextField.setAutoscrolls(false);
        toDisplayValueTextField.setMinimumSize(new java.awt.Dimension(16, 19));

        displayedLabel.setText(L10n.getString("History.Displayed"));

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(totalResultLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(totalResultValueLabel)
                .add(17, 17, 17)
                .add(toDisplayLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 21, Short.MAX_VALUE)
                .add(displayedLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(displayedValueLabel)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(totalResultLabel)
                    .add(totalResultValueLabel)
                    .add(toDisplayLabel)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(displayedLabel)
                    .add(displayedValueLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(326, 326, 326)
                        .add(undoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(selectAllButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(unselectAllButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(previousButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 33, Short.MAX_VALUE)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(previousButton)
                    .add(nextButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectAllButton)
                    .add(undoButton)
                    .add(unselectAllButton))
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
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 364, Short.MAX_VALUE)
                                .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(20, 20, 20)
                                .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(okButton)
                    .add(closeButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents       
    
    /**
     *  Display generic error message.
     *  @param message Message we want to display
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, L10n.getString("Common.ErrorMessageTitle"), JOptionPane.ERROR_MESSAGE);               
    }  
   
    /**
     * Display generic message
     * @param message Message we want to display
     * @return information about user selection. <ul><li> 0 if user press OK button</li> <li>1 if user press Cancle button</li> <li>2 if warnnig message has been displayed</li></ul>
     */
    public int messageUndo(String message) {
        if (message.equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("Warning.EmptySelection"), L10n.getString("Warning.EmptySelectionTitle"), JOptionPane.ERROR_MESSAGE);               
            return 2;
        } else {
            int okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("Question.UndoRecord") + message, L10n.getString("Question.UndoRecordTitle"), JOptionPane.OK_CANCEL_OPTION);
            return okCancle;
        }
    }

    /**
     * Close this dialog.     
     */
    public void close() {
        dispose();
    }
  
    /**
     * Get main table whitch displays changes of Records
     * @return main table whitch displays changes of Records
     */
    public JTable getTable()
    {
    	return this.tableEditList;
    }
    
    /** 
     * Set number of rows in results
     * @param resultRows number of rows in results
     */
    public void setCountResutl(Integer resultRows)
    {
    	this.totalResultValueLabel.setText(resultRows.toString());
    }
    
    /**
     * Set number of rows displayed on active page 
     * @param displayedRows number of rows displayed on active page
     */
    public void setCurrentRowsInfo(String displayedRows)
    {
    	this.displayedValueLabel.setText(displayedRows);
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
     * Set numger of rows to displaying on one page
     * @param value numger of rows to displaying on one page
     */
    public void setDisplayRows(Integer value) {
        this.toDisplayValueTextField.setText(value.toString());
    }  
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorLabel;
    private javax.swing.JLabel authorValueLabel;
    protected javax.swing.JButton closeButton;
    private javax.swing.JLabel displayedLabel;
    private javax.swing.JLabel displayedValueLabel;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel locationValueLabel;
    protected javax.swing.JButton nextButton;
    protected javax.swing.JButton okButton;
    protected javax.swing.JButton previousButton;
    protected javax.swing.JButton selectAllButton;
    private javax.swing.JTable tableEditList;
    private javax.swing.JLabel taxonLabel;
    private javax.swing.JLabel taxonValueLabel;
    private javax.swing.JLabel toDisplayLabel;
    protected javax.swing.JTextField toDisplayValueTextField;
    private javax.swing.JLabel totalResultLabel;
    private javax.swing.JLabel totalResultValueLabel;
    protected javax.swing.JButton undoButton;
    protected javax.swing.JButton unselectAllButton;
    private javax.swing.JLabel whenInserLabel;
    private javax.swing.JLabel whenInsertValueLabel;
    private javax.swing.JLabel whoInsertLabel;
    private javax.swing.JLabel whoInsertValueLabel;
    // End of variables declaration//GEN-END:variables
    
}
