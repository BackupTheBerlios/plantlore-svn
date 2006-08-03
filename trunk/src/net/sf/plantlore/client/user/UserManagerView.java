/*
 * UserManagerView.java
 *
 * Created on 23. duben 2006, 11:43
 */

package net.sf.plantlore.client.user;

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
public class UserManagerView extends javax.swing.JDialog implements Observer{
    
    //Whole UserManager model
    private UserManager model;  
    //data
    private Object[][] data;
    
    /**
     * Creates new form UserManagerView
     */
    public UserManagerView(UserManager model, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        initComponents();
        PlantloreHelp.addKeyHelp(PlantloreHelp.USER_MANAGER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.USER_MANAGER, this.helpButton);        
        sortButtonGroup.add(sortAscendingRadioButton);
        sortButtonGroup.add(sortDescendingRadioButton);
        sortButtonGroup.setSelected(sortAscendingRadioButton.getModel(), true);
        showUserbuttonGroup.add(showAllUserRadioBUtton);
        showUserbuttonGroup.add(showCurrentUserRadioButton);
        showUserbuttonGroup.setSelected(showAllUserRadioBUtton.getModel(),true);
        tableUserList.setRowSelectionAllowed(true);
        tableUserList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        tableUserList.setModel(new UserManagerTableModel(model));  
       
    }
    
    public void update(Observable observable, Object object)
    {
    }
    
     /**
     *
     */
    public void close() {
        dispose();
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
    
    /**
     * Display error message saying that no row of table has been selected.
     */
    public void selectRowMessage() {
    	JOptionPane.showMessageDialog(this, L10n.getString("Warning.EmptySelection"), L10n.getString("Warning.EmptySelectionTitle"), JOptionPane.WARNING_MESSAGE);               
    }             
    
     public int messageDelete(String message) {
    	int okCancle = JOptionPane.showConfirmDialog(this, L10n.getString("Question.DropUser"), L10n.getString("Question.DropUserTitle"), JOptionPane.OK_CANCEL_OPTION);
    	return okCancle;
    }          
    
     /**
     *  Display info message saying that no search field has been filled in.
     */
    public void showSearchInfoFillMessage() {
        JOptionPane.showMessageDialog(this, L10n.getString("Information.SearchUser"), L10n.getString("Information.SearchUserTitle"), JOptionPane.INFORMATION_MESSAGE);       
    }
    
    public void showSearchInfoMessage() {
        JOptionPane.showMessageDialog(this, L10n.getString("Information.NoUserInResult"), L10n.getString("Information.NoUserInResultTitle"), JOptionPane.INFORMATION_MESSAGE);                
    }
     
     public int getSortDirection() {
        if (this.sortButtonGroup.isSelected(this.sortAscendingRadioButton.getModel()) == true) {
            return 0;
        } else {
            return 1;
        }
    }
    
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
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 725, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
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
                    .add(detailsButton)
                    .add(addButtons)
                    .add(editButtons)
                    .add(deleteButton))
                .addContainerGap())
        );

        closeButton.setText(L10n.getString("UserManager.ButtonClose"));

        helpButton.setText(L10n.getString("UserManager.ButtonHelp"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserMnager.SearchPanel")));
        wholeNameLabel.setText(L10n.getString("UserManager.Name"));

        loginLabel.setText(L10n.getString("UserManager.Login"));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.SortPanel")));
        sortDescendingRadioButton.setText(L10n.getString("UserManager.SortDescending"));
        sortDescendingRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sortDescendingRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        sortAscendingRadioButton.setText(L10n.getString("UserManager.SortAscending"));
        sortAscendingRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sortAscendingRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel5.setText(L10n.getString("UserManager.DataSort"));

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
                    .add(jLabel5)
                    .add(sortComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sortAscendingRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sortDescendingRadioButton))
        );

        emailLabel.setText(L10n.getString("UserManager.Email"));

        addressLabel.setText(L10n.getString("UserManager.Address"));

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
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                            .add(loginSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .add(wholeNameSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(emailLabel)
                            .add(addressLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(addressSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .add(emailSearchText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)))
                    .add(searchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(17, 17, 17)
                .add(showActiveUserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jPanel3, 0, 88, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, showActiveUserPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
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
                        .add(searchButton)))
                .addContainerGap())
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
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 556, Short.MAX_VALUE)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 756, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
