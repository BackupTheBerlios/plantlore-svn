
package net.sf.plantlore.client.authors;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.l10n.L10n;

/**
 * Main dialog of the AuthorManager used for searching authors and displaying the search results.
 *
 * @author  Tomas Kovarik
 * @version 1.0 BETA, May 1, 2006
 */
public class AuthorManagerView extends javax.swing.JDialog implements Observer {
    /** Model of the AuthorManager MVC */
    private AuthorManager model;
    /** Names of fields available for sorting the results */
    private String[] sortFields = {L10n.getString("Author.Sort.Name"), L10n.getString("Author.Sort.Organization"), L10n.getString("Author.Sort.Role"), L10n.getString("Author.Sort.Email"), L10n.getString("Author.Sort.Phone"), L10n.getString("Author.Sort.Url")};        
    /** Names of the columns in the search results */
    private String[] columnNames = new String [] {L10n.getString("Author.Table.Name"), L10n.getString("Author.Table.Organization"), L10n.getString("Author.Table.Role"), L10n.getString("Author.Table.Phone"), L10n.getString("Author.Table.Email"), L10n.getString("Author.Table.Url")};    
    /** Contents of the table with the query result */
    private String[][] tableData;
    
    PlantloreHelp help;
    /**
     * Creates new form AuthorManagerView 
     * 
     * @param model     model of the AuthorManager MVC
     * @param parent    parent of this dialog
     * @param modal     boolean flag whether the dialog should be modal or not
     */
    public AuthorManagerView(AuthorManager model, JFrame parent, boolean modal) {        
        super(parent, modal);
        this.model = model;
        this.model.addObserver(this);         
        initComponents();
        jScrollPane3.getViewport().setBackground(java.awt.Color.white);
        PlantloreHelp.addKeyHelp(PlantloreHelp.AUTHOR_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.AUTHOR_MANAGER, this.helpBtn);
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
        nameField = new javax.swing.JFormattedTextField();
        organizationField = new javax.swing.JFormattedTextField();
        roleField = new javax.swing.JFormattedTextField();
        emailField = new javax.swing.JFormattedTextField();
        closeBtn = new javax.swing.JButton();
        helpBtn = new javax.swing.JButton();

        sortButtonGroup.add(ascRadio);
        sortButtonGroup.add(descRadio);
        sortButtonGroup.setSelected(ascRadio.getModel(), true);

        setTitle(L10n.getString("authorManager"));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Author.list")));
        jScrollPane3.setBackground(java.awt.Color.white);
        listTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(listTable);

        previousBtn.setText(L10n.getString("Common.Previous"));

        nextBtn.setText(L10n.getString("Common.Next"));

        deleteBtn.setText(L10n.getString("Author.DeleteButton"));

        editBtn.setText(L10n.getString("Author.EditButton"));

        addBtn.setText(L10n.getString("Author.AddButton"));

        totalResultLabel2.setText(L10n.getString("Author.TotalResults"));

        totalRowsLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayedLabel2.setText(L10n.getString("Author.RowsToDisplay"));

        displayedLabel2.setText(L10n.getString("Author.DisplayedRowsLabel"));

        displayedLabel.setText("---");

        rowsField.setValue(model.getDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 825, Short.MAX_VALUE)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(previousBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalResultLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalRowsLabel)
                        .add(33, 33, 33)
                        .add(toDisplayedLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel7Layout.createSequentialGroup()
                                .add(rowsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(89, 89, 89)
                                .add(displayedLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(displayedLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                                .add(addBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(editBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(2, 2, 2)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(deleteBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(nextBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
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
                    .add(editBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(deleteBtn)
                    .add(addBtn))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Author.SearchLabel")));
        jLabel11.setText(L10n.getString("Author.Search.Name"));

        jLabel12.setText(L10n.getString("Author.Search.Organization"));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Author.Sorting")));
        descRadio.setText(L10n.getString("Author.Descending"));
        descRadio.setActionCommand(L10n.getString("descending"));
        descRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        descRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        ascRadio.setText(L10n.getString("Author.Ascending"));
        ascRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ascRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel13.setText(L10n.getString("Author.SortBy"));

        sortCombo.setModel(new DefaultComboBoxModel(sortFields));

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, descRadio)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, ascRadio)
                    .add(sortCombo, 0, 151, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(sortCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ascRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descRadio)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel14.setText(L10n.getString("Author.Search.Role"));

        jLabel15.setText(L10n.getString("Author.Search.Email"));

        searchBtn.setText(L10n.getString("Author.SearchButton"));

        nameField.setValue("");

        organizationField.setValue("");

        roleField.setValue("");

        emailField.setValue("");

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
                            .add(organizationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .add(nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel14)
                            .add(jLabel15))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(emailField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .add(roleField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)))
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
                            .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel14)
                            .add(roleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel12)
                            .add(organizationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel15)
                            .add(emailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 617, Short.MAX_VALUE)
                        .add(closeBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpBtn)
                    .add(closeBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     *  Check whether the given field is emty or not. This is used for validating user input when searching
     *  authors.
     *
     *  @param field field we want to check
     *  @return true if the field is empty, false otherwise
     */
    public boolean checkNonEmpty(String field) {
        if (field.equals("name") && (nameField.getText().length() == 0)) {
            return false;
        }
        if (field.equals("email") && (emailField.getText().length() == 0)) {
            return false;
        }
        if (field.equals("role") && (roleField.getText().length() == 0)) {
            return false;
        }
        if (field.equals("organization") && (organizationField.getText().length() == 0)) {
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
     *  Return an instance of this Dialog.
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
     */
    public void update(Observable obs, Object obj) {       
        displayResults(model.getData());
    }

    /**
     *  
     */
    public void displayResults(ArrayList results) {
        this.tableData = new String[results.size()][];
        for (int i=0;i<results.size();i++) {            
            this.tableData[i] = new String[6];
            this.tableData[i][0] = ((Author)results.get(i)).getWholeName();
            this.tableData[i][1] = ((Author)results.get(i)).getOrganization();
            this.tableData[i][2] = ((Author)results.get(i)).getRole();
            this.tableData[i][3] = ((Author)results.get(i)).getPhoneNumber();
            this.tableData[i][4] = ((Author)results.get(i)).getEmail();
            this.tableData[i][5] = ((Author)results.get(i)).getUrl();
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
     *  Add ActionListener to Add author button.
     *
     *  @param al ActionListener to add
     */    
    public void addBtnAddActionListener(ActionListener al) {
        addBtn.addActionListener(al);
    }

    /**
     *  Add ActionListener to Edit author button.
     *
     *  @param al ActionListener to add
     */    
    public void editBtnAddActionListener(ActionListener al) {
        editBtn.addActionListener(al);
    }

    /**
     *  Add ActionListener to Delete author button.
     *
     *  @param al ActionListener to add
     */    
    public void deleteBtnAddActionListener(ActionListener al) {
        deleteBtn.addActionListener(al);
    }    

    /**
     *  Add ActionListener to Search authors button.
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
     *  Get the name of the author from the textfield.
     *  @return name of the author from the textfield
     */        
    public String getName() {
        return (String)nameField.getValue();
    }
    
    /**
     *  Get the role of the author from the textfield.
     *  @return role of the author from the textfield
     */        
    public String getRole() {
        return (String)roleField.getValue();
    }    
    
    /**
     *  Get the organization from the textfield.
     *  @return organization from the textfield
     */            
    public String getOrganization() {
        return (String)organizationField.getValue();
    }
    
    /**
     *  Get the email from the textfield.
     *  @return email from the textfield
     */                
    public String getEmail() {
        return (String)emailField.getValue();
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
     *  @return field used for sorting. Constants for fields are defined in AuthorManager.java
     */
    public int getSortField() {        
        int field;
        switch (this.sortCombo.getSelectedIndex()) {
            case 0: field = AuthorManager.SORT_NAME;
                    break;
            case 1: field = AuthorManager.SORT_ORGANIZATION;
                    break;
            case 2: field = AuthorManager.SORT_ROLE;
                    break;
            case 3: field = AuthorManager.SORT_EMAIL;
                    break;
            case 4: field = AuthorManager.SORT_PHONE;
                    break;
            case 5: field = AuthorManager.SORT_URL;
                    break;                    
            default:field = AuthorManager.SORT_NAME;
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
     *  Add PropertyChangeListener to name field.
     *  @param pcl PropertyChangeListener for the name field
     */
    void nameAddPropertyChangeListener(PropertyChangeListener pcl) {
        nameField.addPropertyChangeListener(pcl);
    }
    
    /**
     *  Add PropertyChangeListener to organization field.
     *  @param pcl PropertyChangeListener for the organization field
     */    
    void organizationAddPropertyChangeListener(PropertyChangeListener pcl) {
        organizationField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add PropertyChangeListener to role field.
     *  @param pcl PropertyChangeListener for the role field
     */    
    void roleAddPropertyChangeListener(PropertyChangeListener pcl) {
        roleField.addPropertyChangeListener(pcl);
    }    

    /**
     *  Add PropertyChangeListener to email field.
     *  @param pcl PropertyChangeListener for the email field
     */    
    void emailAddPropertyChangeListener(PropertyChangeListener pcl) {
        emailField.addPropertyChangeListener(pcl);
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
     *  @param pcl FocusListener for the sort combo
     */    
    void sortAddFocusListener(FocusListener fl) {
        sortCombo.addFocusListener(fl);
    }
    
    /**
     *  Add FocusListener to ascending and descending radiobuttons.
     *  @param pcl FocusListener for the ascending and descending radiobuttons
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
        int res = JOptionPane.showConfirmDialog(this, L10n.getString("Author.ConfirmDelete"), 
                                                L10n.getString("Author.ConfirmDeleteTitle"), JOptionPane.YES_NO_OPTION);
        if (res == 0) {
            return true;
        }
        return false;
    }
    
    /**
     *  Get index of the (first) selected author in the table with the list of authors
     *  @return index of the (first) selected author in the table
     */
    public int getSelectedAuthor() {        
        return listTable.getSelectedRow();
    }

    /**
     *  Display dialog with the message saying that no row in the table with authors is selected
     */
    public void selectRowMsg() {
        JOptionPane.showMessageDialog(this, L10n.getString("Author.NoAuthorSelected"),
                                      L10n.getString("Author.NoAuthorSelectedTitle"), JOptionPane.WARNING_MESSAGE);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton addBtn;
    private javax.swing.JRadioButton ascRadio;
    protected javax.swing.JButton closeBtn;
    protected javax.swing.JButton deleteBtn;
    private javax.swing.JRadioButton descRadio;
    protected javax.swing.JLabel displayedLabel;
    private javax.swing.JLabel displayedLabel2;
    protected javax.swing.JButton editBtn;
    private javax.swing.JFormattedTextField emailField;
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
    protected javax.swing.JTable listTable;
    private javax.swing.JFormattedTextField nameField;
    protected javax.swing.JButton nextBtn;
    private javax.swing.JFormattedTextField organizationField;
    protected javax.swing.JButton previousBtn;
    private javax.swing.JFormattedTextField roleField;
    private javax.swing.JFormattedTextField rowsField;
    private javax.swing.JButton searchBtn;
    private javax.swing.ButtonGroup sortButtonGroup;
    private javax.swing.JComboBox sortCombo;
    private javax.swing.JLabel toDisplayedLabel2;
    private javax.swing.JLabel totalResultLabel2;
    private javax.swing.JLabel totalRowsLabel;
    // End of variables declaration//GEN-END:variables
    
}