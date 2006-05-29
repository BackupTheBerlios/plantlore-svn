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
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.TabTransfersFocus;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  Lada
 */
public class AddEditUserView extends javax.swing.JDialog  implements Observer {
    
    /**User manager model */
    private UserManager model;
    
    /**
     * Creates new form AddEditUserView
     */
    public AddEditUserView(UserManager model, javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        initComponents(); 
        new TabTransfersFocus(noteText);
        new TabTransfersFocus(editGroupTextArea);                
    }
    
     public void update(Observable observable, Object object)
    {
    }
    
     /*
      * nastaveni formulare pro add
      */
     public void setAddForm() {
         operationButton.setText(L10n.getString("UserManager.ButtonAdd"));
         createWhenuser.setText("");
         dropWhenLabel.setText("");
     }
     
      public void setEditForm() {
         operationButton.setText(L10n.getString("UserManager.ButtonEdit"));
         loginText.setEditable(false);
     }
     
     public void setDetailsForm() {
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
     }
     
     /**
      * Nacteni dat pro edit/details dialog
      */
     public void loadData() {
           //Get selected user object
           User user = model.getSelectedRecord();      
           loginText.setText(user.getLogin());
           passwordtext.setText(user.getPassword());
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
     *
     */
    public void close() {
        dispose();
    }
    
    /*
     *
     */
    public boolean checkNotNull() {
        if (this.loginText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Login") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.passwordtext.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Password") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.firstNameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.FirstName") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.surnameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Surname") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.emailText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("UserManager.Email") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
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
        jLabel2 = new javax.swing.JLabel();
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
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.UserDataPanel")));
        loginLabel.setText(L10n.getString("UserManager.Login"));

        firstNameLabel.setText(L10n.getString("UserManager.FirstName"));

        surnameLabel.setText(L10n.getString("UserManager.Surname"));

        emailLabel.setText(L10n.getString("UserManager.Email"));

        addressLabel.setText(L10n.getString("UserManager.Address"));

        createWhenuser.setText(L10n.getString("UserManager.CreateWhen"));

        loginText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginTextActionPerformed(evt);
            }
        });

        noteLabel.setText(L10n.getString("UserManager.Note"));

        noteText.setColumns(20);
        noteText.setRows(5);
        jScrollPane1.setViewportView(noteText);

        dropWhenLabel.setText(L10n.getString("UserManager.DropWhen"));

        jLabel2.setText(L10n.getString("UserManager.Password"));

        createWhenValueLabel.setText("");

        dropWhenValueLabel.setText("");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("UserManager.UserRightPanel")));
        administratorCheckBox.setText(L10n.getString("UserManager.Right.Administrator"));
        administratorCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        administratorCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        editAllCheckBox.setText(L10n.getString("UserManager.Right.EditAll"));
        editAllCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        editAllCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        addRightCheckBox.setText(L10n.getString("UserManager.Right.Add"));
        addRightCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        addRightCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        editGroupLabel.setText(L10n.getString("UserManager.Right.EditGroup"));
        editGroupLabel.setAutoscrolls(true);

        editGroupTextArea.setColumns(20);
        editGroupTextArea.setRows(5);
        jScrollPane3.setViewportView(editGroupTextArea);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(administratorCheckBox)
                    .add(editAllCheckBox)
                    .add(addRightCheckBox)
                    .add(editGroupLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
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
                .add(editGroupLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(loginLabel)
                    .add(addressLabel)
                    .add(emailLabel)
                    .add(surnameLabel)
                    .add(firstNameLabel)
                    .add(jLabel2)
                    .add(noteLabel)
                    .add(createWhenuser)
                    .add(dropWhenLabel))
                .add(34, 34, 34)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dropWhenValueLabel)
                    .add(createWhenValueLabel)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(surnameText)
                            .add(jScrollPane1)
                            .add(addressText)
                            .add(firstNameText)
                            .add(passwordtext)
                            .add(emailText)
                            .add(loginText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
                        .add(21, 21, 21)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(loginLabel)
                            .add(loginText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(passwordtext, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(firstNameLabel)
                            .add(firstNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(surnameLabel)
                            .add(surnameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(emailLabel)
                            .add(emailText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(10, 10, 10)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(addressLabel)
                            .add(addressText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(noteLabel)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(24, 24, 24)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createWhenuser)
                    .add(createWhenValueLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dropWhenLabel)
                    .add(dropWhenValueLabel))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        operationButton.setText("");

        closeButton.setText(L10n.getString("UserManager.ButtonClose"));

        helpButton.setText(L10n.getString("UserManager.ButtonHelp"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 275, Short.MAX_VALUE)
                        .add(operationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(14, 14, 14)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(28, 28, 28))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(closeButton)
                    .add(operationButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel loginLabel;
    protected javax.swing.JTextField loginText;
    private javax.swing.JLabel noteLabel;
    protected javax.swing.JTextArea noteText;
    protected javax.swing.JButton operationButton;
    protected javax.swing.JTextField passwordtext;
    private javax.swing.JLabel surnameLabel;
    protected javax.swing.JTextField surnameText;
    // End of variables declaration//GEN-END:variables
    
}
