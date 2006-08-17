/*
 * UserManagerView.java
 *
 * Created on 23. duben 2006, 11:43
 */

package net.sf.plantlore.client.user;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.TransferFocus;
import net.sf.plantlore.l10n.L10n;

/** Creates new form UserManagerView
 * 
 * @param model model of the UserManager MVC
 * @param parent parent of this dialog
 * @param modal boolean flag whether the dialog should be modal or not
 */
public class UserManagerView extends javax.swing.JDialog implements Observer{
    
    /** Model of UserManager MVC */
    private UserManager model;      
    
    /**
     * Creates new form UserManagerView
     */
    public UserManagerView(UserManager model, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        initComponents();        
        // getRootPane().setDefaultButton(closeButton);
        PlantloreHelp.addKeyHelp(PlantloreHelp.USER_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.USER_MANAGER, this.helpButton);                
    }
    
    /**
     * Reload the view dialog or display some kind of error.
     */
    public void update(Observable observable, Object object)
    {
    	// Check whether we have some kind of error to display
        if (model.isError()) {
            showErrorMessage(L10n.getString("Common.ErrorMessageTitle"), model.getError());                          
            return;
        } 
    }
    
    /**
     * Initialize actual data for displaying in view dialog.     
     */
    public void initialize() {  
    	model.setDisplayRows(UserManager.DEFAULT_DISPLAY_ROWS);
        model.setCurrentFirstRow(1);
    	sortButtonGroup.add(sortAscendingRadioButton);
        sortButtonGroup.add(sortDescendingRadioButton);
        sortButtonGroup.setSelected(sortAscendingRadioButton.getModel(), true);
        showUserbuttonGroup.add(showAllUserRadioBUtton);
        showUserbuttonGroup.add(showCurrentUserRadioButton);
        showUserbuttonGroup.setSelected(showAllUserRadioBUtton.getModel(),true);
        tableUserList.setRowSelectionAllowed(true);
        tableUserList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        tableUserList.setModel(new UserManagerTableModel(model));  
        previousButton.setEnabled(false);
        nextButton.setEnabled(true);
        if (UserManager.DEFAULT_DISPLAY_ROWS >= model.getResultRows()) {
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
     public int getSortDirection() {
        if (this.sortButtonGroup.isSelected(this.sortAscendingRadioButton.getModel()) == true) {
            return 0;
        } else {
            return 1;
        }
    }
    
     /**
      * Get information about user selection - display all users or only active users
      * @return zero if user want to display only active user, one in other way
      */
      public int getShowUserDirection() {
        if (this.showUserbuttonGroup.isSelected(this.showAllUserRadioBUtton.getModel()) == true) {
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
        if (field.equals("login") && (loginSearchText.getText().length() == 0)) {
            return false;
        }
        if (field.equals("name") && (wholeNameSearchText.getText().length() == 0)) {
            return false;
        }
        if (field.equals("email") && (emailSearchText.getText().length() == 0)) {
            return false;
        }
        if (field.equals("address") && (addressSearchText.getText().length() == 0)) {
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
        showUserbuttonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUserList = new javax.swing.JTable();
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
        wholeNameLabel = new javax.swing.JLabel();
        loginLabel = new javax.swing.JLabel();
        wholeNameSearchText = new javax.swing.JTextField();
        loginSearchText = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        sortDescendingRadioButton = new javax.swing.JRadioButton();
        sortAscendingRadioButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        sortComboBox = new javax.swing.JComboBox();
        emailLabel = new javax.swing.JLabel();
        emailSearchText = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressSearchText = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        showActiveUserPanel = new javax.swing.JPanel();
        showAllUserRadioBUtton = new javax.swing.JRadioButton();
        showCurrentUserRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.UserList")));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        tableUserList.setModel(new javax.swing.table.DefaultTableModel(
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
        TransferFocus.patch(tableUserList);
        jScrollPane1.setViewportView(tableUserList);

        previousButton.setText(L10n.getString("UserManager.ButtonPrev"));

        nextButton.setText(L10n.getString("UserManager.ButtonNext"));

        deleteButton.setText(L10n.getString("UserManager.ButtonDrop"));

        editButtons.setText(L10n.getString("UserManager.ButtonEdit"));

        addButtons.setText(L10n.getString("UserManager.ButtonAdd"));

        detailsButton.setText(L10n.getString("UserManager.ButtonDetails"));

        totalResultLabel.setText(L10n.getString("UserManager.TotalResult"));

        totalResultValueLabel.setText(((Integer)model.getResultRows()).toString());

        toDisplayedLabel.setText(L10n.getString("UserManager.RowsToDisplay"));

        toDisplayValueTextField.setText(((Integer)model.getDisplayRows()).toString());

        displayedLabel.setText(L10n.getString("UserManager.Displayed"));

        displayedValueLabel.setText(model.getCurrentDisplayRows());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(previousButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                                .add(totalResultLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(totalResultValueLabel)
                                .add(37, 37, 37)
                                .add(toDisplayedLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(detailsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(25, 25, 25)
                                .add(displayedLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(displayedValueLabel))
                            .add(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(addButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(editButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(deleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(nextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(totalResultLabel)
                    .add(toDisplayedLabel)
                    .add(displayedLabel)
                    .add(toDisplayValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(displayedValueLabel)
                    .add(previousButton)
                    .add(nextButton)
                    .add(totalResultValueLabel))
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(detailsButton)
                    .add(addButtons)
                    .add(editButtons)
                    .add(deleteButton))
                .addContainerGap())
        );

        closeButton.setText(L10n.getString("UserManager.ButtonClose"));

        helpButton.setText(L10n.getString("UserManager.ButtonHelp"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserMnager.SearchPanel")));
        wholeNameLabel.setText(L10n.getString("UserManager.Name")+ ": ");

        loginLabel.setText(L10n.getString("UserManager.Login")+ ": ");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.SortPanel")));
        sortDescendingRadioButton.setText(L10n.getString("UserManager.SortDescending"));
        sortDescendingRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sortDescendingRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        sortAscendingRadioButton.setText(L10n.getString("UserManager.SortAscending"));
        sortAscendingRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sortAscendingRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel5.setText(L10n.getString("UserManager.DataSort")+ ": ");

        sortComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { L10n.getString("UserManager.Login"), L10n.getString("UserManager.FirstName"), L10n.getString("UserManager.Surname"), L10n.getString("UserManager.CreateWhen")}));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .add(19, 19, 19)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sortDescendingRadioButton)
                    .add(sortAscendingRadioButton)
                    .add(sortComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sortComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sortAscendingRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sortDescendingRadioButton))
        );

        emailLabel.setText(L10n.getString("UserManager.Email")+ ": ");

        addressLabel.setText(L10n.getString("UserManager.Address")+ ": ");

        searchButton.setText(L10n.getString("UserManager.ButtonSearch"));

        showActiveUserPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.ShowPanel")));
        showAllUserRadioBUtton.setText(L10n.getString("UserManager.ShowAllUser"));
        showAllUserRadioBUtton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showAllUserRadioBUtton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        showCurrentUserRadioButton.setText(L10n.getString("UserManager.ShowActiveUser"));
        showCurrentUserRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showCurrentUserRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout showActiveUserPanelLayout = new org.jdesktop.layout.GroupLayout(showActiveUserPanel);
        showActiveUserPanel.setLayout(showActiveUserPanelLayout);
        showActiveUserPanelLayout.setHorizontalGroup(
            showActiveUserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(showActiveUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(showActiveUserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(showAllUserRadioBUtton)
                    .add(showCurrentUserRadioButton))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        showActiveUserPanelLayout.setVerticalGroup(
            showActiveUserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(showActiveUserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(showAllUserRadioBUtton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(showCurrentUserRadioButton)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(loginLabel)
                            .add(wholeNameLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(loginSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .add(wholeNameSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(emailLabel)
                            .add(addressLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(addressSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .add(emailSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))
                    .add(searchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(17, 17, 17)
                .add(showActiveUserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(emailLabel)
                            .add(emailSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(loginLabel)
                            .add(loginSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(addressLabel)
                            .add(addressSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(wholeNameLabel)
                            .add(wholeNameSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(16, 16, 16)
                        .add(searchButton))
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(showActiveUserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(11, 11, 11))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 568, Short.MAX_VALUE)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(15, 15, 15)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(closeButton))
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
                new UserManagerView(null, new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton addButtons;
    private javax.swing.JLabel addressLabel;
    protected javax.swing.JTextField addressSearchText;
    protected javax.swing.JButton closeButton;
    protected javax.swing.JButton deleteButton;
    protected javax.swing.JButton detailsButton;
    private javax.swing.JLabel displayedLabel;
    protected javax.swing.JLabel displayedValueLabel;
    protected javax.swing.JButton editButtons;
    private javax.swing.JLabel emailLabel;
    protected javax.swing.JTextField emailSearchText;
    protected javax.swing.JButton helpButton;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel loginLabel;
    protected javax.swing.JTextField loginSearchText;
    protected javax.swing.JButton nextButton;
    protected javax.swing.JButton previousButton;
    protected javax.swing.JButton searchButton;
    protected javax.swing.JPanel showActiveUserPanel;
    protected javax.swing.JRadioButton showAllUserRadioBUtton;
    protected javax.swing.JRadioButton showCurrentUserRadioButton;
    protected javax.swing.ButtonGroup showUserbuttonGroup;
    protected javax.swing.JRadioButton sortAscendingRadioButton;
    protected javax.swing.ButtonGroup sortButtonGroup;
    protected javax.swing.JComboBox sortComboBox;
    protected javax.swing.JRadioButton sortDescendingRadioButton;
    protected javax.swing.JTable tableUserList;
    protected javax.swing.JTextField toDisplayValueTextField;
    private javax.swing.JLabel toDisplayedLabel;
    private javax.swing.JLabel totalResultLabel;
    protected javax.swing.JLabel totalResultValueLabel;
    private javax.swing.JLabel wholeNameLabel;
    protected javax.swing.JTextField wholeNameSearchText;
    // End of variables declaration//GEN-END:variables
    
}
