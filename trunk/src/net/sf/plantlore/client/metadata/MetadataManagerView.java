/*
 * MetadataManagerView.java
 *
 * Created on 23. duben 2006, 11:43
 */

package net.sf.plantlore.client.metadata;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.text.PlainDocument;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.TransferFocus;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.l10n.L10n;

/**
 * View for the main MetadataManager dialog (part of the History MVC). Used for displaying the search results.
 * 
 * @author  Lada Oberreiterova
 * @version 1.0
 */
public class MetadataManagerView extends javax.swing.JDialog implements Observer{
    
	private static final long serialVersionUID = 251086612790983605L;
	/** Model of the MetadataManager MVC*/
    private MetadataManager model;  
 
    /** Creates new form MetadataManagerView
     * 
     * @param model model of the MetadataManager MVC
     * @param parent parent of this dialog
     * @param modal boolean flag whether the dialog should be modal or not
     */
    public MetadataManagerView(MetadataManager model, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        model.addObserver(this);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);        
        initComponents();
        //getRootPane().setDefaultButton(closeButton);
        //Init Help
        PlantloreHelp.addKeyHelp(PlantloreHelp.METDATA_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.METDATA_MANAGER, this.helpButton);    
        setTitle(L10n.getString("MetadataManager.Title"));
        setSizeRestrictions();
        setLocationRelativeTo(null);
    }
    
    private void setSizeRestrictions() {
        PlainDocument pd = (PlainDocument) dataSetTitleText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(Metadata.getColumnSize(Metadata.DATASETTITLE)));

        pd = (PlainDocument) this.sourceInstitutionIdText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(Metadata.getColumnSize(Metadata.SOURCEINSTITUTIONID)));
        
        pd = (PlainDocument) this.sourceIdText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(Metadata.getColumnSize(Metadata.SOURCEID)));
    }
    
    /**
     * Reload the view dialog or display some kind of error.
     */
      public void update(Observable observable, Object object)
    {                                
    	  //Check whether we have some kind of error to display
          if (model.isError()) {
              showErrorMessage(L10n.getString("Common.ErrorMessageTitle"), model.getError());                          
              return;
          } 
    }  
      
      /**
       * Initialize actual data for displaying in view dialog.     
       */
      public void initialize() {   
    	  model.setDisplayRows(MetadataManager.DEFAULT_DISPLAY_ROWS);
          model.setCurrentFirstRow(1);
    	  previousButton.setEnabled(false);
    	  nextButton.setEnabled(true);    	   	  
          if (MetadataManager.DEFAULT_DISPLAY_ROWS >= model.getResultRows()) {
          	nextButton.setEnabled(false);
          }
          sortButtonGroup.add(sortAscendingRadioButton);
          sortButtonGroup.add(sortDescendingRadioButton);
          sortButtonGroup.setSelected(sortAscendingRadioButton.getModel(), true);
          this.tableMetadataList.setRowSelectionAllowed(true);
          this.tableMetadataList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
          this.tableMetadataList.setModel(new MetadataManagerTableModel(model));          
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
    
     /**
     *  Close this dialog.
     */
    public void close() {
        dispose();
    }
    
    /**
     *  Get the number of rows to display per page
     *  @return number of rows to display per page
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
     *  Set the number of rows to display per page
     *  @param value number of rows to display per page
     */  
    public void setDisplayRows(Integer value) {
        this.toDisplayValueTextField.setText(value.toString());
    }  
    
    /**
     *  Get the direction of sorting.
     *  @return direction of sorting. 0 is ASCENDING, 1 is DESCENDING
     */
    int getSortDirection() {
        if (this.sortButtonGroup.isSelected(this.sortAscendingRadioButton.getModel()) == true) {
            return 0;
        } else {
            return 1;
        }
    }
     
     /**
     *  Check whether the given field is emty or not. This is used for validating user input when searching
     *  user.
     *
     *  @param field field we want to check
     *  @return true if the field is empty, false otherwise
     */
    public boolean checkNonEmpty(String field) {
        if (field.equals("sourceInstitutionId") && (sourceInstitutionIdText.getText().length() == 0)) {
            return false;
        }
        if (field.equals("sourceId") && (sourceIdText.getText().length() == 0)) {
            return false;
        }
        if (field.equals("dataSetTitle") && (dataSetTitleText.getText().length() == 0)) {
            return false;
        }              
        return true;
    }
    
    /**
     *  Display generic error message.
     *  @param title title of error message
     *  @param message error message we want to display
     */
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);               
    }
    
    /**
     *  Display generic error message.     
     *  @param message error message we want to display
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, L10n.getString("Common.ErrorMessageTitle"), JOptionPane.ERROR_MESSAGE);               
    }
    
      /**
     * Display warning message.
     * @param title title of warning message
     * @param message warning message we want to display
     */
    public void showWarningMessage(String title, String message) {    	
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);               
    }             
    
    /**
     * Display OK_CANCLE message
     * @param title title of question message
     * @param message message containing same question for user
     * @return information which button was selected - OK or Cancle 
     */
     public int showQuestionMessage(String title, String message) {
    	int okCancle = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.OK_CANCEL_OPTION);
    	return okCancle;
    }          
    
     /**
     *  Display info message 
     *  @param title title of info message
     *  @param message information message we want to display
     */
    public void showInfoMessage(String title, String message) {
         JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);       
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        sortButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableMetadataList = new javax.swing.JTable();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButtons = new javax.swing.JButton();
        addButtons = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        totalResultLabel = new javax.swing.JLabel();
        totalResultValueLabel = new javax.swing.JLabel();
        toDisplayedLabel = new javax.swing.JLabel();
        toDisplayValueTextField = new javax.swing.JTextField();
        displayedLabel = new javax.swing.JLabel();
        displayedValueLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        sourceInstitutionIdLabel = new javax.swing.JLabel();
        sourceLabel = new javax.swing.JLabel();
        sourceInstitutionIdText = new javax.swing.JTextField();
        sourceIdText = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        sortDescendingRadioButton = new javax.swing.JRadioButton();
        sortAscendingRadioButton = new javax.swing.JRadioButton();
        dataSortLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JComboBox();
        dataSetTitleLabel = new javax.swing.JLabel();
        dataSetTitleText = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.ListPanel")));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        tableMetadataList.setModel(new javax.swing.table.DefaultTableModel(
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
        TransferFocus.patch(tableMetadataList);
        jScrollPane1.setViewportView(tableMetadataList);

        previousButton.setText(L10n.getString("Common.Prev"));

        nextButton.setText(L10n.getString("Common.Next"));

        deleteButton.setText(L10n.getString("MetadataManager.ButtonDelete"));

        editButtons.setText(L10n.getString("MetadataManager.ButtonEdit"));

        addButtons.setText(L10n.getString("MetadataManager.ButtonAdd"));

        detailsButton.setText(L10n.getString("MetadataManager.ButtonDetails"));

        totalResultLabel.setText(L10n.getString("UserManager.TotalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayedLabel.setText(L10n.getString("MetadataManager.RowToDisplay"));

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());

        displayedLabel.setText(L10n.getString("MetadataManager.Displayed"));

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(previousButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(56, 56, 56)
                                .add(totalResultLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(totalResultValueLabel)
                                .add(37, 37, 37)
                                .add(toDisplayedLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(25, 25, 25)
                                .add(displayedLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(displayedValueLabel)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(detailsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(editButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(nextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(totalResultLabel)
                    .add(toDisplayedLabel)
                    .add(displayedLabel)
                    .add(totalResultValueLabel)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(displayedValueLabel)
                    .add(previousButton)
                    .add(nextButton))
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(editButtons)
                    .add(deleteButton)
                    .add(addButtons)
                    .add(detailsButton))
                .addContainerGap())
        );

        closeButton.setText(L10n.getString("Common.Cancel"));

        helpButton.setText(L10n.getString("Common.Help"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.SearchPanel")));
        sourceInstitutionIdLabel.setText(L10n.getString("MetadataManager.SourceInstitutionId") + ": "
        );

        sourceLabel.setText(L10n.getString("MetadataManager.SourceId")+ ": ");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.SortPanel")));
        sortDescendingRadioButton.setText(L10n.getString("MetadataManager.SortDescending"));
        sortDescendingRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sortDescendingRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        sortAscendingRadioButton.setText(L10n.getString("MetadataManager.SortAscending"));
        sortAscendingRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sortAscendingRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        dataSortLabel.setText(L10n.getString("MetadataManager.DataSort")+ ": ");

        sortField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { L10n.getString("MetadataManager.SourceInstitutionId"), L10n.getString("MetadataManager.SourceId"), L10n.getString("MetadataManager.DataSetTitle"), L10n.getString("MetadataManager.TechnicalContactName"),L10n.getString("MetadataManager.ContentContactName"), L10n.getString("MetadataManager.DateCreate"), L10n.getString("MetadataManager.DateModified")}));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(dataSortLabel)
                .add(19, 19, 19)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sortDescendingRadioButton)
                    .add(sortAscendingRadioButton)
                    .add(sortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 199, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataSortLabel)
                    .add(sortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sortAscendingRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sortDescendingRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dataSetTitleLabel.setText(L10n.getString("MetadataManager.DataSetTitle")+ ": ");

        searchButton.setText(L10n.getString("MetadataManager.ButtonSearch"));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(sourceInstitutionIdLabel)
                            .add(sourceLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(sourceInstitutionIdText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .add(sourceIdText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                        .add(15, 15, 15)
                        .add(dataSetTitleLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dataSetTitleText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                    .add(searchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(31, 31, 31)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(sourceInstitutionIdLabel)
                            .add(sourceInstitutionIdText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(dataSetTitleLabel)
                            .add(dataSetTitleText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(sourceLabel)
                            .add(sourceIdText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(17, 17, 17)
                        .add(searchButton))
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 665, Short.MAX_VALUE)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(16, 16, 16)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(closeButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton addButtons;
    protected javax.swing.JButton closeButton;
    private javax.swing.JLabel dataSetTitleLabel;
    protected javax.swing.JTextField dataSetTitleText;
    private javax.swing.JLabel dataSortLabel;
    protected javax.swing.JButton deleteButton;
    protected javax.swing.JButton detailsButton;
    private javax.swing.JLabel displayedLabel;
    protected javax.swing.JLabel displayedValueLabel;
    protected javax.swing.JButton editButtons;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JButton nextButton;
    protected javax.swing.JButton previousButton;
    protected javax.swing.JButton searchButton;
    protected javax.swing.JRadioButton sortAscendingRadioButton;
    private javax.swing.ButtonGroup sortButtonGroup;
    protected javax.swing.JRadioButton sortDescendingRadioButton;
    protected javax.swing.JComboBox sortField;
    protected javax.swing.JTextField sourceIdText;
    private javax.swing.JLabel sourceInstitutionIdLabel;
    protected javax.swing.JTextField sourceInstitutionIdText;
    private javax.swing.JLabel sourceLabel;
    protected javax.swing.JTable tableMetadataList;
    protected javax.swing.JTextField toDisplayValueTextField;
    private javax.swing.JLabel toDisplayedLabel;
    private javax.swing.JLabel totalResultLabel;
    protected javax.swing.JLabel totalResultValueLabel;
    // End of variables declaration//GEN-END:variables
    
}
