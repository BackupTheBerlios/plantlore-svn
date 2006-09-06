/*
 * AddEditUserView.java
 *
 * Created on 23. duben 2006, 15:44
 */

package net.sf.plantlore.client.user;

import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.text.PlainDocument;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.TransferFocus;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;


/**
 * View for the Add/Edit user dialog in the UserManager MVC.
 *
 * @author  Lada Oberreiterova
 * @version 1.0
 */
public class AddEditUserView extends javax.swing.JDialog  implements Observer {
    
    /**User manager model */
    private UserManager model;
    
    /**
     * Creates new form AddEditUserView
     * @param model model of UserManager MVC     
     * @param parent parent of this dialog
     * @param modal boolean flag whether the dialog should be modal or not
     */
    public AddEditUserView(UserManager model, javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);        
        initComponents(); 
        PlantloreHelp.addKeyHelp(PlantloreHelp.USER_ADD, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.USER_ADD, this.helpButton);        
        TransferFocus.patch(noteText);
        TransferFocus.patch(editGroupTextArea);   
        setSizeRestrictions();
        setLocationRelativeTo(null);
    }
    
    
     private void setSizeRestrictions() {
        PlainDocument pd = (PlainDocument) loginText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.LOGIN)));

        pd = (PlainDocument) passwordtext.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.PSWD)));
        
        pd = (PlainDocument) firstNameText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.FIRSTNAME)));

        pd = (PlainDocument) surnameText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.SURNAME)));
        
        pd = (PlainDocument) emailText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.EMAIL)));
        
        pd = (PlainDocument) addressText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.ADDRESS)));
        
        pd = (PlainDocument) noteText.getDocument();
        pd.setDocumentFilter(new DocumentSizeFilter(User.getColumnSize(User.NOTE)));                   
    }
     
     public void update(Observable observable, Object object)
    {
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
      * Set add form
      */
     public void setAddForm() {    
         setTitle(L10n.getString("UserManager.AddTitle"));
         operationButton.setText(L10n.getString("UserManager.ButtonAdd"));         
         createWhenuser.setText("");        
         dropWhenLabel.setText("");        
         this.loginText.setText("");        
         this.passwordtext.setText("");         
         this.firstNameText.setText("");        
         this.surnameText.setText("");        
         this.emailText.setText("");         
         this.addressText.setText("");        
         this.administratorCheckBox.setSelected(false);        
         this.editAllCheckBox.setSelected(false);         
         this.addRightCheckBox.setSelected(false);         
         this.editGroupTextArea.setText("");        
         this.noteText.setText("");        
         this.editGroupTextArea.setText("");        
         
         Pair<String, Integer>[] users = model.getUsers();
         String[] choices = new String[users.length];
         for (int i = 0; i < users.length; i++) {
             choices[i] = users[i].getFirst();
         }
         ((AutoTextArea)editGroupTextArea).setChoices(choices);
     }
     
      public void setEditForm() {
          setTitle(L10n.getString("UserManager.EditTitle"));
         operationButton.setText(L10n.getString("UserManager.ButtonEdit"));
         loginText.setEditable(false);
         passwordLabel.setText(L10n.getString("UserManager.Password")+ ": (**) ");
         
         Pair<String, Integer>[] users = model.getUsers();
         String[] choices = new String[users.length];
         for (int i = 0; i < users.length; i++) {
             choices[i] = users[i].getFirst();             
         }
         ((AutoTextArea)editGroupTextArea).setChoices(choices);
     }
     
      /**
       * Set Details form       
       */
     public void setDetailsForm() {
         setTitle(L10n.getString("UserManager.DetailTitle"));
       operationButton.setText(L10n.getString("UserManager.ButtonOk"));
       this.loginText.setEditable(false);
       this.passwordtext.setEditable(false);
       this.firstNameText.setEditable(false);
       this.surnameText.setEditable(false);
       this.emailText.setEditable(false);
       this.addressText.setEditable(false);
       this.administratorCheckBox.setEnabled(false);
       this.editAllCheckBox.setEnabled(false);       
       this.addRightCheckBox.setEnabled(false);
       this.editGroupTextArea.setEditable(false);
       this.noteText.setEditable(false);
       this.editGroupTextArea.setEditable(false);
       passwordtext.setText("******");
     }
     
     /**
      * Load data for edit/details dialog
      */
     public void loadData() {
           //Get selected user object
           User user = model.getUserRecord();      
           loginText.setText(user.getLogin());
           passwordtext.setText("");
           firstNameText.setText(user.getFirstName());
           surnameText.setText(user.getSurname());
           emailText.setText(user.getEmail());
           addressText.setText(user.getAddress());                        
           noteText.setText(user.getNote());
           createWhenValueLabel.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(user.getCreateWhen()));
           if (user.getDropWhen() == null) {
                dropWhenValueLabel.setText("---");
           } else {
                dropWhenValueLabel.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(user.getDropWhen()));
           }
           //Right
           Right right = user.getRight();
           //editGroup          
           if (right.getEditGroup() == null || right.getEditGroup().equals("")) {
               editGroupTextArea.setText("");               
           } else {
               editGroupTextArea.setText(model.getEditGroup(right.getEditGroup()));           
           }
           if (right.getAdministrator() == 1) {
               administratorCheckBox.setSelected(true);
           } else {
               administratorCheckBox.setSelected(false);
           }
           if (right.getEditAll() == 1) {
               editAllCheckBox.setSelected(true);                   
           } else {
               editAllCheckBox.setSelected(false);                   
           }               
           if (right.getAdd() == 1) {
               addRightCheckBox.setSelected(true);
           } else {
               addRightCheckBox.setSelected(false);
           }
     }
     
      /**
     * Display error message saying that login exists in database. Admin has to fill in different login.
     */
    public void checUniqueLoginMessage(String login) {
    	JOptionPane.showMessageDialog(this, L10n.getString("Error.DuplicateLogin"), L10n.getString("Error.DuplicateLoginTitle"), JOptionPane.ERROR_MESSAGE);               
    }    
     
     /**
     * Close this dialog.
     */
    public void close() {
        dispose();
    }
    
    /**
     *  Check whether the given field is empty or not. This is used for validating user input when 
     *  add or edit metadata.
     */
    public boolean checkNotNull() {
        if (this.loginText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Login") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.passwordtext.getText().equals("") && model.getOperation().equals(UserManager.ADD)) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Password") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.firstNameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.FirstName") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.surnameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Surname") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } 
        return true;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        loginLabel = new javax.swing.JLabel();
        firstNameLabel = new javax.swing.JLabel();
        surnameLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        createWhenuser = new javax.swing.JLabel();
        loginText = new javax.swing.JTextField();
        firstNameText = new javax.swing.JTextField();
        emailText = new javax.swing.JTextField();
        addressText = new javax.swing.JTextField();
        noteLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        noteText = new javax.swing.JTextArea();
        dropWhenLabel = new javax.swing.JLabel();
        passwordtext = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        createWhenValueLabel = new javax.swing.JLabel();
        dropWhenValueLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        administratorCheckBox = new javax.swing.JCheckBox();
        editAllCheckBox = new javax.swing.JCheckBox();
        addRightCheckBox = new javax.swing.JCheckBox();
        editGroupLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Pair<String, Integer>[] users = model.getUsers();
        String[] choices = new String[users.length];
        for (int i = 0; i < users.length; i++) {
            choices[i] = users[i].getFirst();
        }
        editGroupTextArea = new AutoTextArea(choices, this);
        surnameText = new javax.swing.JTextField();
        operationButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(L10n.getString("UserManager.AddTitle"));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.UserDataPanel")));
        loginLabel.setText(L10n.getString("UserManager.Login") + ": (*) ");

        firstNameLabel.setText(L10n.getString("UserManager.FirstName")+ ": (*) ");

        surnameLabel.setText(L10n.getString("UserManager.Surname")+ ": (*) ");

        emailLabel.setText(L10n.getString("UserManager.Email"));

        addressLabel.setText(L10n.getString("UserManager.Address")+ ": ");

        createWhenuser.setText(L10n.getString("UserManager.CreateWhen")+ ": ");

        loginText.setNextFocusableComponent(passwordtext);
        loginText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginTextActionPerformed(evt);
            }
        });

        firstNameText.setNextFocusableComponent(surnameText);

        emailText.setNextFocusableComponent(addressText);

        addressText.setNextFocusableComponent(noteText);

        noteLabel.setText(L10n.getString("UserManager.Note")+ ": ");

        noteText.setColumns(20);
        noteText.setRows(5);
        noteText.setNextFocusableComponent(administratorCheckBox);
        jScrollPane1.setViewportView(noteText);

        dropWhenLabel.setText(L10n.getString("UserManager.DropWhen")+ ": ");

        passwordtext.setNextFocusableComponent(firstNameText);

        passwordLabel.setText(L10n.getString("UserManager.Password")+ ": (*) ");

        createWhenValueLabel.setText("");

        dropWhenValueLabel.setText("");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.UserRightPanel")));
        administratorCheckBox.setText(L10n.getString("UserManager.Right.Administrator"));
        administratorCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        administratorCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        administratorCheckBox.setNextFocusableComponent(editAllCheckBox);

        editAllCheckBox.setText(L10n.getString("UserManager.Right.EditAll"));
        editAllCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        editAllCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        editAllCheckBox.setNextFocusableComponent(addRightCheckBox);

        addRightCheckBox.setText(L10n.getString("UserManager.Right.Add"));
        addRightCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        addRightCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        addRightCheckBox.setNextFocusableComponent(editGroupTextArea);

        editGroupLabel.setText(L10n.getString("UserManager.Right.EditGroup"));
        editGroupLabel.setAutoscrolls(true);

        editGroupTextArea.setColumns(20);
        editGroupTextArea.setRows(5);
        editGroupTextArea.setNextFocusableComponent(operationButton);
        jScrollPane3.setViewportView(editGroupTextArea);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(administratorCheckBox)
                    .add(editAllCheckBox)
                    .add(addRightCheckBox)
                    .add(editGroupLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(administratorCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editAllCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addRightCheckBox)
                .add(12, 12, 12)
                .add(editGroupLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        surnameText.setNextFocusableComponent(emailText);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(loginLabel)
                            .add(surnameLabel)
                            .add(passwordLabel)
                            .add(noteLabel)
                            .add(createWhenuser)
                            .add(dropWhenLabel)
                            .add(firstNameLabel)
                            .add(emailLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(surnameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                    .add(firstNameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                    .add(passwordtext, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                    .add(loginText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
                            .add(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(emailText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(29, 29, 29)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(dropWhenValueLabel)
                                    .add(createWhenValueLabel)))
                            .add(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                    .add(addressText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))))
                    .add(addressLabel))
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(loginLabel)
                            .add(loginText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(passwordLabel)
                            .add(passwordtext, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(firstNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(firstNameLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(surnameLabel)
                            .add(surnameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(emailText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(emailLabel))
                        .add(8, 8, 8)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(addressLabel)
                            .add(addressText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(noteLabel)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 36, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createWhenuser)
                    .add(createWhenValueLabel))
                .add(7, 7, 7)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dropWhenLabel)
                    .add(dropWhenValueLabel))
                .addContainerGap())
        );

        operationButton.setText("");
        operationButton.setNextFocusableComponent(closeButton);

        closeButton.setText(L10n.getString("Common.Cancel"));
        closeButton.setNextFocusableComponent(helpButton);

        helpButton.setText(L10n.getString("Common.Help"));
        helpButton.setNextFocusableComponent(loginText);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 254, Short.MAX_VALUE)
                        .add(operationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(operationButton)
                    .add(closeButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginTextActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_loginTextActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddEditUserView(null, new javax.swing.JDialog(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox addRightCheckBox;
    private javax.swing.JLabel addressLabel;
    protected javax.swing.JTextField addressText;
    protected javax.swing.JCheckBox administratorCheckBox;
    protected javax.swing.JButton closeButton;
    protected javax.swing.JLabel createWhenValueLabel;
    protected javax.swing.JLabel createWhenuser;
    protected javax.swing.JLabel dropWhenLabel;
    private javax.swing.JLabel dropWhenValueLabel;
    protected javax.swing.JCheckBox editAllCheckBox;
    private javax.swing.JLabel editGroupLabel;
    protected javax.swing.JTextArea editGroupTextArea;
    private javax.swing.JLabel emailLabel;
    protected javax.swing.JTextField emailText;
    private javax.swing.JLabel firstNameLabel;
    protected javax.swing.JTextField firstNameText;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel loginLabel;
    protected javax.swing.JTextField loginText;
    private javax.swing.JLabel noteLabel;
    protected javax.swing.JTextArea noteText;
    protected javax.swing.JButton operationButton;
    protected javax.swing.JLabel passwordLabel;
    protected javax.swing.JTextField passwordtext;
    private javax.swing.JLabel surnameLabel;
    protected javax.swing.JTextField surnameText;
    // End of variables declaration//GEN-END:variables
    
}
