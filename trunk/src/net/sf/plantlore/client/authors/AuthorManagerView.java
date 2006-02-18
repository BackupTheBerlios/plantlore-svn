
/* AuthorManagerView.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.authors;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Observer;
import java.util.Observable; 
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.plantlore.common.record.AuthorRecord;

/**
 *
 * @author Tomas Kovarik
 * @version 1.0, Jan 15, 2006
 */
public class AuthorManagerView implements Observer {
    
    private AuthorManager model;
    
    private JDialog authDialog;
    private Container container;    
    private String[] columnNames = new String [] {"Name", "Organization", "Role", "Phone number", "Email", "URL"};
    private String[][] tableData;   
    
    /** Creates a new instance of AuthorManagerView */
    public AuthorManagerView(AuthorManager model, JFrame owner) {
        this.model = model;
        this.model.addObserver(this); 
        
        // Create new modal dialog
        authDialog = new JDialog(owner, "Author Manager", true);
        authDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        authDialog.setLocationRelativeTo(null);
        authDialog.setSize(320,240);

        container = authDialog.getContentPane();
        container.setLayout(new GridBagLayout());
        initComponents();        
    }
    
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
                
        listPanel = new JPanel();
        searchPanel = new JPanel();
        sortPanel = new JPanel();
        closeBtn = new JButton();
        helpBtn = new JButton();
        nameLabel = new JLabel();
        organizationLabel = new JLabel();
        roleLabel = new JLabel();
        emailLabel = new JLabel();
        sortLabel = new JLabel();
        nameField = new JFormattedTextField();
        organizationField = new JFormattedTextField();
        roleField = new JFormattedTextField();
        emailField = new JFormattedTextField();
        sortCombo = new JComboBox();
        listTable = new JTable();
        listScrollPane = new JScrollPane();
        addBtn = new JButton();
        editBtn = new JButton();
        deleteBtn = new JButton();
        previousBtn = new JButton();
        nextBtn = new JButton();
        searchBtn = new JButton();
        totalRowsDescLabel = new JLabel();
        totalRowsLabel = new JLabel();
        currentRowsLabel = new JLabel();
        displayedLabel = new JLabel();
        displayRowsLabel = new JLabel();
        rowsField = new JFormattedTextField();        
        ascRadio = new JRadioButton();
        descRadio = new JRadioButton();
        sortButtonGroup = new ButtonGroup();
        
        String[] sortFields = {"Name", "Organization", "Role", "Email", "Phone number", "URL"};
        sortCombo.setModel(new DefaultComboBoxModel(sortFields));
        
        sortButtonGroup.add(ascRadio);
        sortButtonGroup.add(descRadio);
        sortButtonGroup.setSelected(ascRadio.getModel(), true);
        
        listPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" List of authors "));
        listPanel.setLayout(new java.awt.GridBagLayout());        
        
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Search authors "));
        searchPanel.setLayout(new java.awt.GridBagLayout());        
        
        // Add panel with list of authors
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0; 
        gridBagConstraints.weighty = 0.5;         
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        gridBagConstraints.fill = GridBagConstraints.BOTH;       
        container.add(listPanel, gridBagConstraints);
        
        // Add panel with the search form
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;                
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);        
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;                
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        container.add(searchPanel, gridBagConstraints);        
        
        sortPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Sorting "));
        sortPanel.setLayout(new java.awt.GridBagLayout());        
        
        // Add panel with sorting criteria
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(-5, 5, 5, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        gridBagConstraints.fill = GridBagConstraints.BOTH;   
        searchPanel.add(sortPanel, gridBagConstraints);
        
        
        // Add close button
        closeBtn.setText("Close");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        container.add(closeBtn, gridBagConstraints);

        // Add help button        
        helpBtn.setText("Help");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        container.add(helpBtn, gridBagConstraints);
        
        // Add labels to the search panel
        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 7, 5);
        searchPanel.add(nameLabel, gridBagConstraints);        
        
        organizationLabel.setText("Organization:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        searchPanel.add(organizationLabel, gridBagConstraints);

        roleLabel.setText("Role:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 7, 5);
        searchPanel.add(roleLabel, gridBagConstraints);        

        emailLabel.setText("Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        searchPanel.add(emailLabel, gridBagConstraints);        
                
        sortLabel.setText("Sort by:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        sortPanel.add(sortLabel, gridBagConstraints);                
        
        // Add fields to the search panel        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        nameField.setValue("");
        searchPanel.add(nameField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.7;        
        organizationField.setValue("");        
        searchPanel.add(organizationField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 5);
        gridBagConstraints.weightx = 0.3;        
        roleField.setValue("");        
        searchPanel.add(roleField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        emailField.setValue("");        
        searchPanel.add(emailField, gridBagConstraints);

        // Add ComboBox with the list of fields for sorting
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        sortCombo.setPreferredSize(new Dimension(150,20));
        sortPanel.add(sortCombo, gridBagConstraints);        
        
        // Add RadioButtons for ascending / descending sort
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        ascRadio.setText("Ascending");
        sortPanel.add(ascRadio, gridBagConstraints);        

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        descRadio.setText("Descending");
        sortPanel.add(descRadio, gridBagConstraints);        
       
        // Add search button to the search panel
        searchBtn.setText("Search Authors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        searchPanel.add(searchBtn, gridBagConstraints);        
        
        // Add ScrollPane for the listTable        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.weighty = 0.5;                
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        listScrollPane.setMinimumSize(new java.awt.Dimension(500, 100));        
        listScrollPane.setPreferredSize(new java.awt.Dimension(500, 100));                        
        listPanel.add(listScrollPane, gridBagConstraints);                        
        
        listScrollPane.add(listTable);                
        listScrollPane.setViewportView(listTable);        
        
        // Add "add" button to listPanel
        addBtn.setText("Add author");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;        
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 235);
        addBtn.setMinimumSize(new java.awt.Dimension(110, 25));        
        addBtn.setPreferredSize(new java.awt.Dimension(110, 25));                                        
        listPanel.add(addBtn, gridBagConstraints);        
        
        // Add edit button to listPanel
        editBtn.setText("Edit author");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;        
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 120);
        editBtn.setMinimumSize(new java.awt.Dimension(110, 25));        
        editBtn.setPreferredSize(new java.awt.Dimension(110, 25));                                        
        listPanel.add(editBtn, gridBagConstraints);        
        
        // Add delete button to listPanel
        deleteBtn.setText("Delete author");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;        
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        deleteBtn.setMinimumSize(new java.awt.Dimension(110, 25));        
        deleteBtn.setPreferredSize(new java.awt.Dimension(110, 25));                                                
        listPanel.add(deleteBtn, gridBagConstraints);        
        
        // Add Previous button to the listPanel
        previousBtn.setText("Previous");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        previousBtn.setMinimumSize(new java.awt.Dimension(100, 25));        
        previousBtn.setPreferredSize(new java.awt.Dimension(100, 25));                                                
        // Button is disabled by default
        previousBtn.setEnabled(false);
        listPanel.add(previousBtn, gridBagConstraints);        
        
        // Add Next button to the listPanel
        nextBtn.setText("Next");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        nextBtn.setMinimumSize(new java.awt.Dimension(100, 25));        
        nextBtn.setPreferredSize(new java.awt.Dimension(100, 25));                                                
        // Button is disabled by default
        nextBtn.setEnabled(false);
        listPanel.add(nextBtn, gridBagConstraints);        
        
        // Add labels showing number of rows in the result
        totalRowsDescLabel.setText("Total rows in the result:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        listPanel.add(totalRowsDescLabel, gridBagConstraints);                

        totalRowsLabel.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 5);
        listPanel.add(totalRowsLabel, gridBagConstraints);                        
        
        // Add labels showing number of rows in the result
        currentRowsLabel.setText("Currently displayed:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        listPanel.add(currentRowsLabel, gridBagConstraints);                

        displayedLabel.setText("---");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 135, 0, 5);
        listPanel.add(displayedLabel, gridBagConstraints);                        
        
        // Add label and text field with the number of rows to display
        displayRowsLabel.setText("Rows to display:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        listPanel.add(displayRowsLabel, gridBagConstraints);                
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 110, 0, 5);
        rowsField.setMinimumSize(new java.awt.Dimension(50, 20));        
        rowsField.setPreferredSize(new java.awt.Dimension(50, 20));                
        rowsField.setValue(model.getDisplayRows());
        listPanel.add(rowsField, gridBagConstraints);                         
        
        authDialog.pack();
    }
    
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
    
    public void setDialogEnabled(boolean enabled) {
        authDialog.setEnabled(enabled);
    }
    
    public JDialog getDialog() {
        return this.authDialog;
    }
    
    public void showSearchErrorMessage() {
        JOptionPane.showMessageDialog(authDialog, "Please fill in at least one search field", "Missing search data", JOptionPane.ERROR_MESSAGE);       
    }
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(authDialog, message, "Error", JOptionPane.ERROR_MESSAGE);               
    }
    
    public void update(Observable obs, Object obj) {       
        // Check whether we have some kind of error to display
        if (model.isError()) {
//            showErrorMessage(model.getErrorMessage());            
            return;
        } else {
            displayResults(model.getData());
        }
    }

    public void displayResults(ArrayList results) {
        this.tableData = new String[results.size()][];
        for (int i=0;i<results.size();i++) {            
            this.tableData[i] = new String[6];
            this.tableData[i][0] = ((AuthorRecord)results.get(i)).getFirstName()+" "+((AuthorRecord)results.get(i)).getSurname();
            this.tableData[i][1] = ((AuthorRecord)results.get(i)).getOrganization();
            this.tableData[i][2] = ((AuthorRecord)results.get(i)).getRole();
            this.tableData[i][3] = ((AuthorRecord)results.get(i)).getPhoneNumber();
            this.tableData[i][4] = ((AuthorRecord)results.get(i)).getEmail();
            this.tableData[i][5] = ((AuthorRecord)results.get(i)).getUrl();
        }
        listTable.setModel(new DefaultTableModel(this.tableData, this.columnNames));       
        // Set total number of rows in the result
        totalRowsLabel.setText(model.getResult().getNumRows()+"");
        // Set the status of "Previous" button
        if (model.getCurrentFirstRow() > 1) {
            previousBtn.setEnabled(true);
        } else {
            previousBtn.setEnabled(false);
        }
        // Set the status of the "Next" button        
        if (model.getResult().getNumRows() >= (model.getDisplayRows()+model.getCurrentFirstRow())) {
            nextBtn.setEnabled(true);
        } else {
            nextBtn.setEnabled(false);            
        }
        int to = Math.min(model.getCurrentFirstRow()+model.getDisplayRows()-1, model.getResult().getNumRows());
        displayedLabel.setText(model.getCurrentFirstRow()+" - "+to);
    }
    
    public void closeBtnAddActionListener(ActionListener al) {
        closeBtn.addActionListener(al);
    }
    
    public void helpBtnAddActionListener(ActionListener al) {
        helpBtn.addActionListener(al);
    }
    
    public void addBtnAddActionListener(ActionListener al) {
        addBtn.addActionListener(al);
    }
    
    public void editBtnAddActionListener(ActionListener al) {
        editBtn.addActionListener(al);
    }

    public void deleteBtnAddActionListener(ActionListener al) {
        deleteBtn.addActionListener(al);
    }    
    
    public void searchBtnAddActionlistener(ActionListener al) {
        searchBtn.addActionListener(al);
    }
    
    public void previousBtnAddActionListener(ActionListener al) {
        previousBtn.addActionListener(al);
    }
    
    public void nextBtnAddActionListener(ActionListener al) {
        nextBtn.addActionListener(al);
    }
    
    
    public void show() {
        authDialog.setSize(800,600);
        authDialog.setLocationRelativeTo(null);
        authDialog.setVisible(true);
    }
    
    public void close() {
        authDialog.dispose();
    }

    public JDialog getFrame() {
        return this.authDialog;
    }
    
    public String getName() {
        return (String)nameField.getValue();
    }

    public String getRole() {
        return (String)roleField.getValue();
    }    
    
    public String getOrganization() {
        return (String)organizationField.getValue();
    }
    
    public String getEmail() {
        return (String)emailField.getValue();
    }    
    
    public Integer getDisplayRows() {
        return (Integer)rowsField.getValue();
    }
    
    public void setDisplayRows(int value) {
        this.rowsField.setValue(value);
    }    
    
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
    
    public int getSortDirection() {
        if (this.sortButtonGroup.isSelected(this.ascRadio.getModel()) == true) {
            return 0;
        } else {
            return 1;
        }
    }
    
    void nameAddPropertyChangeListener(PropertyChangeListener pcl) {
        nameField.addPropertyChangeListener(pcl);
    }
    
    void organizationAddPropertyChangeListener(PropertyChangeListener pcl) {
        organizationField.addPropertyChangeListener(pcl);
    }    
    
    void roleAddPropertyChangeListener(PropertyChangeListener pcl) {
        roleField.addPropertyChangeListener(pcl);
    }    

    void emailAddPropertyChangeListener(PropertyChangeListener pcl) {
        emailField.addPropertyChangeListener(pcl);
    }        
        
    void rowsAddPropertyChangeListener(PropertyChangeListener pcl) {
        rowsField.addPropertyChangeListener(pcl);
    }        

    void sortAddFocusListener(FocusListener fl) {
        sortCombo.addFocusListener(fl);
    }
    
    void sortDirectionAddFocusListener(FocusListener fl) {
        ascRadio.addFocusListener(fl);
        descRadio.addFocusListener(fl);
    }
    
    public boolean confirmDelete() {
        // JOptionPane results: 0 = Yes, 1 = No
        int res = JOptionPane.showConfirmDialog(this.authDialog, "Do you really want to delete selected author?", 
                                                "Delete author", JOptionPane.YES_NO_OPTION);
        if (res == 0) {
            return true;
        }
        return false;
    }

    public int getSelectedAuthor() {        
        return listTable.getSelectedRow();
    }

    public void selectRowMsg() {
        JOptionPane.showMessageDialog(this.authDialog, "Please select at least one author from the list",
                                      "Select author", JOptionPane.WARNING_MESSAGE);        
    }

    public void showSearchInfoMessage() {
        JOptionPane.showMessageDialog(this.authDialog, "No authors with the given attributes were found. Please modify search criteria.",
                                      "No search results", JOptionPane.INFORMATION_MESSAGE);                
    }
    
    // Swing components on the form
    JPanel listPanel;
    JPanel searchPanel;
    JPanel sortPanel;
    JButton closeBtn;
    JButton helpBtn;
    JLabel nameLabel;
    JLabel organizationLabel;
    JLabel roleLabel;
    JLabel emailLabel;
    JLabel sortLabel;
    JFormattedTextField nameField;
    JFormattedTextField organizationField;    
    JFormattedTextField roleField;
    JFormattedTextField emailField;    
    JTable listTable;
    JButton addBtn;
    JButton editBtn;
    JButton deleteBtn;    
    JButton previousBtn;
    JButton nextBtn;
    JButton searchBtn;
    JLabel totalRowsDescLabel;
    JLabel totalRowsLabel;
    JLabel displayRowsLabel;
    JLabel currentRowsLabel;
    JLabel displayedLabel;
    JFormattedTextField rowsField;            
    JScrollPane listScrollPane;    
    JComboBox sortCombo;
    JRadioButton ascRadio;
    JRadioButton descRadio;
    ButtonGroup sortButtonGroup;
}
