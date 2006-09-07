
package net.sf.plantlore.client.publications;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Main dialog of the PublicationManager used for searching publications and displaying the 
 * search results.
 * 
 * @author Tomas Kovarik
 * @version 1.0, June 4, 2006
 */
public class PublicationManagerView extends javax.swing.JDialog implements Observer {
    /** Model of the PublicationManager MVC */
    private PublicationManager model;
    /** Names of fields available for sorting the results */
    private String[] sortFields = {L10n.getString("Publications.Sortby.CollectionNameTitle"), L10n.getString("Publications.Sortby.PublicationYearTitle"), L10n.getString("Publications.Sortby.JournalNameTitle"), L10n.getString("Publications.Sortby.JournalAuthorTitle"), L10n.getString("Publications.Sortby.ReferenceCitationTitle"), L10n.getString("Publications.Sortby.ReferenceDetailTitle")};        
    /** Names of the columns in the search results */
    private String[] columnNames = new String [] {L10n.getString("Publications.ColumnTitle.CollectionNameTitle"), L10n.getString("Publications.ColumnTitle.PublicationYearTitle"), L10n.getString("Publications.ColumnTitle.JournalNameTitle"), L10n.getString("Publications.ColumnTitle.JournalAuthorTitle"), L10n.getString("Publications.ColumnTitle.ReferenceCitationTitle"), L10n.getString("Publications.ColumnTitle.ReferenceDetailTitle"), L10n.getString("Publications.ColumnTitle.PublicationUrlTitle")};    
    /** Contents of the table with the query result */
    private String[][] tableData;
    /** Instance of a Logger */
    private Logger logger;
    /** Variable used for setting maximu input size of text fields */
    PlainDocument pd;
    /**
     * Creates new form PublicationManagerView 
     * 
     * @param model     model of the PublicationManager MVC
     * @param parent    parent of this dialog
     * @param modal     boolean flag whether the dialog should be modal or not
     */
    public PublicationManagerView(PublicationManager model, JFrame parent, boolean modal) {        
        super(parent, modal);
        logger = Logger.getLogger(this.getClass().getPackage().getName());                        
        this.model = model;
        this.model.addObserver(this);
        initComponents();
        jScrollPane3.getViewport().setBackground(java.awt.Color.white);        
        // Initialize help
        PlantloreHelp.addKeyHelp(PlantloreHelp.PUBLICATION_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.PUBLICATION_MANAGER, this.helpBtn);        
        // Center the dialog on the screen
        this.setLocationRelativeTo(null);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        sortButtonGroup = new javax.swing.ButtonGroup();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listTable = new javax.swing.JTable();
        previousBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        totalResultLabel2 = new javax.swing.JLabel();
        totalRowsLabel = new javax.swing.JLabel();
        toDisplayedLabel2 = new javax.swing.JLabel();
        displayedLabel2 = new javax.swing.JLabel();
        displayedLabel = new javax.swing.JLabel();
        rowsField = new javax.swing.JFormattedTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        descRadio = new javax.swing.JRadioButton();
        ascRadio = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        sortCombo = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        collectionNameField = new javax.swing.JFormattedTextField();
        journalNameField = new javax.swing.JFormattedTextField();
        referenceCitationField = new javax.swing.JFormattedTextField();
        referenceDetailField = new javax.swing.JFormattedTextField();
        closeBtn = new javax.swing.JButton();
        helpBtn = new javax.swing.JButton();

        sortButtonGroup.add(ascRadio);
        sortButtonGroup.add(descRadio);
        sortButtonGroup.setSelected(ascRadio.getModel(), true);

        setTitle(L10n.getString("Publications.Title"));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Publications.List.Label")));
        listTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(listTable);

        previousBtn.setText(L10n.getString("Publications.Previous.Button"));

        nextBtn.setText(L10n.getString("Publications.Next.Button"));

        deleteBtn.setText(L10n.getString("Publications.Delete.Button"));

        editBtn.setText(L10n.getString("Publications.Edit.Button"));

        addBtn.setText(L10n.getString("Publications.Add.Button"));

        totalResultLabel2.setText(L10n.getString("Publications.TotalResults.Label"));

        totalRowsLabel.setText(((Integer)model.getResultRows()).toString()
        );

        toDisplayedLabel2.setText(L10n.getString("Publications.RowsToDisplay.Label"));

        displayedLabel2.setText(L10n.getString("Publications.DisplayedRows.Label"));

        displayedLabel.setText(L10n.getString("Publications.DisplayedRows.Label"));

        rowsField.setValue(model.getDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(previousBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalResultLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalRowsLabel)
                        .add(37, 37, 37)
                        .add(toDisplayedLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rowsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(89, 89, 89)
                        .add(displayedLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(displayedLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nextBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(previousBtn)
                    .add(totalResultLabel2)
                    .add(totalRowsLabel)
                    .add(toDisplayedLabel2)
                    .add(displayedLabel2)
                    .add(nextBtn)
                    .add(displayedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rowsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deleteBtn)
                    .add(editBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addBtn))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Publications.Search.Label")));
        jLabel11.setText(L10n.getString("Publications.Search.CollectionName.Label"));

        jLabel12.setText(L10n.getString("Publications.Search.JournalName.Label"));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Publications.Ordering.Label")));
        descRadio.setText(L10n.getString("Common.Descending"));
        descRadio.setActionCommand(L10n.getString("descending"));
        descRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        descRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        ascRadio.setText(L10n.getString("Common.Ascending"));
        ascRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ascRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel13.setText(L10n.getString("Publications.OrderBy.Label"));

        sortCombo.setModel(new DefaultComboBoxModel(sortFields));

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .add(9, 9, 9)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(descRadio)
                    .add(ascRadio)
                    .add(sortCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sortCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ascRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descRadio)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel14.setText(L10n.getString("Publications.Search.ReferenceCitation.Label"));

        jLabel15.setText(L10n.getString("Publications.Search.ReferenceDetail.Label"));

        searchBtn.setText(L10n.getString("Publications.Search.Button"));

        collectionNameField.setValue("");
        collectionNameField.setDocument(new FieldLengthLimit(Publication.getColumnSize(Publication.COLLECTIONNAME)));

        journalNameField.setValue("");
        journalNameField.setDocument(new FieldLengthLimit(Publication.getColumnSize(Publication.JOURNALNAME)));

        referenceCitationField.setValue("");
        referenceCitationField.setDocument(new FieldLengthLimit(Publication.getColumnSize(Publication.REFERENCECITATION)));

        referenceDetailField.setValue("");
        referenceDetailField.setDocument(new FieldLengthLimit(Publication.getColumnSize(Publication.REFERENCEDETAIL)));

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel11)
                            .add(jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(journalNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                            .add(collectionNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel14)
                            .add(jLabel15))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(referenceDetailField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                            .add(referenceCitationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)))
                    .add(searchBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel11)
                            .add(collectionNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel14)
                            .add(referenceCitationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel12)
                            .add(journalNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel15)
                            .add(referenceDetailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchBtn))
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        closeBtn.setText(L10n.getString("Common.Close"));

        helpBtn.setText(L10n.getString("Common.Help"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(helpBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 548, Short.MAX_VALUE)
                        .add(closeBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpBtn)
                    .add(closeBtn))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
      
/**
     *  Check whether the given field is empty or not. This is used for validating user input when 
     *  searching publications.
     *
     *  @param field field we want to check
     *  @return true if the field is empty, false otherwise
     */
    public boolean checkNonEmpty(String field) {
        if (field.equals("collectionName") && (collectionNameField.getText().length() == 0)) {
            return false;
        }
        if (field.equals("journalName") && (journalNameField.getText().length() == 0)) {
            return false;
        }
        if (field.equals("referenceCitation") && (referenceCitationField.getText().length() == 0)) {
            return false;
        }
        if (field.equals("referenceDetail") && (referenceDetailField.getText().length() == 0)) {
            return false;
        }        
        return true;
    }
    
    /**
     *  Set the state of the dialog to enabled or disabled
     *  @param enabled true to enable the dialog, false to disable it
     */
    public void setDialogEnabled(boolean enabled) {
        this.setEnabled(enabled);
    }
    
    /**
     *  Return an instance of this dialog.
     *  @return instance of this dialog
     */
    public JDialog getDialog() {
        return this;
    }
       
    /**
     *  Display generic error message.
     *  @param message Message we want to display
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, L10n.getString("Error.GenericMessageTitle"), JOptionPane.ERROR_MESSAGE);               
    }

    /**
     *  Method used for updating the view (called when the model has changed and notifyObservers() was called)
     *  @param obs Instance of an Observable which changed
     *  @param obj Object used as a parameter of the update
     */
    public void update(Observable obs, Object obj) {
        displayResults(model.getData());
    }

    /**
     *  Display search results in the result table.
     *
     *  @param results list of Publications with the search query results 
     */
    public void displayResults(ArrayList results) {
        this.tableData = new String[results.size()][];
        for (int i=0;i<results.size();i++) {            
            this.tableData[i] = new String[7];
            this.tableData[i][0] = ((Publication)results.get(i)).getCollectionName();
            if (((Publication)results.get(i)).getCollectionYearPublication() == null) {
                this.tableData[i][1] = new String("");
            } else {
                this.tableData[i][1] = ((Publication)results.get(i)).getCollectionYearPublication().toString();
            }
            this.tableData[i][2] = ((Publication)results.get(i)).getJournalName();
            this.tableData[i][3] = ((Publication)results.get(i)).getJournalAuthorName();
            this.tableData[i][4] = ((Publication)results.get(i)).getReferenceCitation();
            this.tableData[i][5] = ((Publication)results.get(i)).getReferenceDetail();
            this.tableData[i][6] = ((Publication)results.get(i)).getUrl();            
        }
        listTable.setModel(new DefaultTableModel(this.tableData, this.columnNames));       
        // Set total number of rows in the result
        totalRowsLabel.setText(model.getResultRows()+"");
        // Set the status of "Previous" button
        if (model.getCurrentFirstRow() > 1) {
            previousBtn.setEnabled(true);
        } else {
            previousBtn.setEnabled(false);
        }
        // Set the status of the "Next" button        
        if (model.getResultRows() >= (model.getDisplayRows()+model.getCurrentFirstRow())) {
            nextBtn.setEnabled(true);
        } else {
            nextBtn.setEnabled(false);            
        }
        int to = Math.min(model.getCurrentFirstRow()+model.getDisplayRows()-1, model.getResultRows());
        displayedLabel.setText(model.getCurrentFirstRow()+" - "+to);
    }
    
    /**
     *  Add ActionListener to close button.
     *
     *  @param al ActionListener to add
     */
    public void closeBtnAddActionListener(ActionListener al) {
        closeBtn.addActionListener(al);
    }
    
    /**
     *  Add ActionListener to help button.
     *
     *  @param al ActionListener to add
     */
    public void helpBtnAddActionListener(ActionListener al) {
        helpBtn.addActionListener(al);
    }

    /**
     *  Add ActionListener to Add Publication button.
     *
     *  @param al ActionListener to add
     */    
    public void addBtnAddActionListener(ActionListener al) {
        addBtn.addActionListener(al);
    }

    /**
     *  Add ActionListener to Edit publication button.
     *
     *  @param al ActionListener to add
     */    
    public void editBtnAddActionListener(ActionListener al) {
        editBtn.addActionListener(al);
    }

    /**
     *  Add ActionListener to Delete publication button.
     *
     *  @param al ActionListener to add
     */    
    public void deleteBtnAddActionListener(ActionListener al) {
        deleteBtn.addActionListener(al);
    }    

    /**
     *  Add ActionListener to Search publications button.
     *
     *  @param al ActionListener to add
     */    
    public void searchBtnAddActionlistener(ActionListener al) {
        searchBtn.addActionListener(al);
    }
    
    /**
     *  Add ActionListener to Prevoius button.
     *
     *  @param al ActionListener to add
     */    
    public void previousBtnAddActionListener(ActionListener al) {
        previousBtn.addActionListener(al);
    }
    
    /**
     *  Add ActionListener to Next button.
     *
     *  @param al ActionListener to add
     */
    public void nextBtnAddActionListener(ActionListener al) {
        nextBtn.addActionListener(al);
    }

    /**
     *  Close this dialog.
     */    
    public void close() {
        model.closeActiveQuery();
        this.dispose();
    }

    /**
     *  Get the instance of this dialog as JDialog
     */        
    public JDialog getFrame() {
        return this;
    }
    
    /**
     *  Get the collection name from the textfield.
     *  @return name of the collection from the textfield
     */        
    public String getCollectionName() {
        return (String)collectionNameField.getValue();
    }
    
    /**
     *  Get the journal name from the textfield.
     *  @return journal name from the textfield
     */        
    public String getJournalName() {
        return (String)journalNameField.getValue();
    }    
    
    /**
     *  Get the reference citation from the textfield.
     *  @return reference citation from the textfield
     */            
    public String getReferenceCitation() {
        return (String)referenceCitationField.getValue();
    }
    
    /**
     *  Get the reference detail from the textfield.
     *  @return reference detail from the textfield
     */                
    public String getReferenceDetail() {
        return (String)referenceDetailField.getValue();
    }    

    /**
     *  Get the number of rows to display per page
     *  @return number of rows to display per page
     */                
    public Integer getDisplayRows() {
        return (Integer)rowsField.getValue();
    }

    /**
     *  Set the number of rows to display per page
     *  @param value number of rows to display per page
     */                    
    public void setDisplayRows(int value) {
        this.rowsField.setValue(value);
    }    
    
    /**
     *  Return the field used for sorting the results of a search. 
     * 
     * @return field used for sorting. Constants for fields are defined in PublicationManager.java
     */
    public int getSortField() {        
        int field;
        switch (this.sortCombo.getSelectedIndex()) {
            case 0: field = PublicationManager.SORT_COLLECTION_NAME;
                    break;
            case 1: field = PublicationManager.SORT_PUBLICATION_YEAR;
                    break;
            case 2: field = PublicationManager.SORT_JOURNAL_NAME;
                    break;
            case 3: field = PublicationManager.SORT_JOURNAL_AUTHOR;
                    break;
            case 4: field = PublicationManager.SORT_REFERENCE_CITATION;
                    break;
            case 5: field = PublicationManager.SORT_REFERENCE_DETAIL;
                    break;                    
            default:field = PublicationManager.SORT_COLLECTION_NAME;
        }
        return field;
    }
    
    /**
     *  Get the direction of sorting.
     *  @return direction of sorting. 0 is ASCENDING, 1 is DESCENDING
     */
    public int getSortDirection() {
        if (this.sortButtonGroup.isSelected(this.ascRadio.getModel()) == true) {
            return 0;
        } else {
            return 1;
        }
    }
    
    /** 
     *  Add WindowListener to the main window
     *  @param wa WindowAdapter object acting as window listener
     */
    public void addPublicationWindowListener(WindowAdapter wa) {
        this.addWindowListener(wa);
    }    
    
    /**
     *  Add PropertyChangeListener to collection name field.
     *  @param pcl PropertyChangeListener for the collection name field
     */
    void collectionNameAddPropertyChangeListener(PropertyChangeListener pcl) {
        collectionNameField.addPropertyChangeListener(pcl);
    }
    
    /**
     *  Add PropertyChangeListener to journal name field.
     *  @param pcl PropertyChangeListener for the journal name field
     */    
    void journalNameAddPropertyChangeListener(PropertyChangeListener pcl) {
        journalNameField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add PropertyChangeListener to reference citation field.
     *  @param pcl PropertyChangeListener for the reference citation field
     */    
    void referenceCitationAddPropertyChangeListener(PropertyChangeListener pcl) {
        referenceCitationField.addPropertyChangeListener(pcl);
    }    

    /**
     *  Add PropertyChangeListener to referenceDetail field.
     *  @param pcl PropertyChangeListener for the referenceDetail field
     */    
    void referenceDetailAddPropertyChangeListener(PropertyChangeListener pcl) {
        referenceDetailField.addPropertyChangeListener(pcl);
    }        
        
    /**
     *  Add PropertyChangeListener to rows field.
     *  @param pcl PropertyChangeListener for the rows field
     */
    void rowsAddPropertyChangeListener(PropertyChangeListener pcl) {
        rowsField.addPropertyChangeListener(pcl);
    }        

    /**
     *  Add FocusListener to sort combo.
     *  @param fl FocusListener for the sort combo
     */    
    void sortAddFocusListener(FocusListener fl) {
        sortCombo.addFocusListener(fl);
    }
    
    /**
     *  Add FocusListener to ascending and descending radiobuttons.
     *  @param fl FocusListener for the ascending and descending radiobuttons
     */
    void sortDirectionAddFocusListener(FocusListener fl) {
        ascRadio.addFocusListener(fl);
        descRadio.addFocusListener(fl);
    }
    
    /**
     *  Display dialog with delete confirmation.
     *  @return true if delete confirmed, false otherwise
     */
    public boolean confirmDelete() {
        // JOptionPane results: 0 = Yes, 1 = No
        int res = JOptionPane.showConfirmDialog(this, L10n.getString("Publications.ConfirmDelete"), 
                                                L10n.getString("Publications.ConfirmDelete.Title"), JOptionPane.YES_NO_OPTION);
        if (res == 0) {
            return true;
        }
        return false;
    }
    
    /**
     *  Get index of the (first) selected publication in the table with the list of publications
     *  @return index of the (first) selected publication in the table
     */
    public int getSelectedPublication() {        
        return listTable.getSelectedRow();
    }

    /**
     *  Display dialog with the message saying that no row in the table with publications is selected
     */
    public void selectRowMsg() {
        JOptionPane.showMessageDialog(this, L10n.getString("Publications.SelectPublication"),
                                      L10n.getString("Publications.SelectPublication.Title"), JOptionPane.WARNING_MESSAGE);        
    }
    
    class FieldLengthLimit extends PlainDocument {
        private int limit;
        // optional uppercase conversion
        private boolean toUppercase = false;
        
        FieldLengthLimit(int limit) {
            super();
            this.limit = limit;
        }
        
        @Override
        public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            } else {
                super.insertString(offset, str.substring(0, limit), attr);
            }
        }
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton addBtn;
    private javax.swing.JRadioButton ascRadio;
    protected javax.swing.JButton closeBtn;
    private javax.swing.JFormattedTextField collectionNameField;
    protected javax.swing.JButton deleteBtn;
    private javax.swing.JRadioButton descRadio;
    protected javax.swing.JLabel displayedLabel;
    private javax.swing.JLabel displayedLabel2;
    protected javax.swing.JButton editBtn;
    protected javax.swing.JButton helpBtn;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JFormattedTextField journalNameField;
    protected javax.swing.JTable listTable;
    protected javax.swing.JButton nextBtn;
    protected javax.swing.JButton previousBtn;
    private javax.swing.JFormattedTextField referenceCitationField;
    private javax.swing.JFormattedTextField referenceDetailField;
    private javax.swing.JFormattedTextField rowsField;
    private javax.swing.JButton searchBtn;
    private javax.swing.ButtonGroup sortButtonGroup;
    private javax.swing.JComboBox sortCombo;
    private javax.swing.JLabel toDisplayedLabel2;
    private javax.swing.JLabel totalResultLabel2;
    private javax.swing.JLabel totalRowsLabel;
    // End of variables declaration//GEN-END:variables
    
}