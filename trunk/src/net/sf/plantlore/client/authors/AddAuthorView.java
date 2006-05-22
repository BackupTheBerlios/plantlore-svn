/*
 * AddAuthorView.java
 *
 * Created on 30. duben 2006, 17:46
 */

package net.sf.plantlore.client.authors;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.TransferFocus;
import net.sf.plantlore.l10n.L10n;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Dialog used for adding / editing authors.
 *
 * @author  Tomas Kovarik
 * @version 1.0 BETA, May 1, 2006
 */
public class AddAuthorView extends javax.swing.JDialog implements Observer {
    /** Model of the Author manager MVC */
    private AuthorManager model;    
    
    /** 
     * Creates new form AddAuthorView 
     *  @param authModel Model of the AuthorManager MVC
     *  @param parent parent window of this dialog
     *  @param modal whether the dialog should be displayed as modal or not
     */
    public AddAuthorView(AuthorManager authModel, JDialog parent, boolean modal) {
        super(parent, modal);
        this.model = authModel;
        this.model.addObserver(this);         
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        nameField = new javax.swing.JFormattedTextField();
        organizationField = new javax.swing.JFormattedTextField();
        roleField = new javax.swing.JFormattedTextField();
        phoneField = new javax.swing.JFormattedTextField();
        emailField = new javax.swing.JFormattedTextField();
        urlField = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        addressArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        noteArea = new javax.swing.JTextArea();
        helpBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(L10n.getString("addAuthorTitle"));
        jLabel1.setText(L10n.getString("addAuthorNameLbl"));

        jLabel2.setText(L10n.getString("addAuthorOrganizationLbl"));

        jLabel3.setText(L10n.getString("addAuthorRoleLbl"));

        jLabel4.setText(L10n.getString("addAuthorAddressLbl"));

        jLabel5.setText(L10n.getString("addAuthorPhoneLbl"));

        jLabel6.setText(L10n.getString("addAuthorEmailLbl"));

        jLabel7.setText(L10n.getString("addAuthorUrlLbl"));

        jLabel8.setText(L10n.getString("addAuthorNoteLbl"));

        jLabel9.setText(L10n.getString("requiredFieldLbl"));

        nameField.setValue("");
        nameField.setDocument(new FieldLengthLimit(50));

        organizationField.setValue("");
        organizationField.setDocument(new FieldLengthLimit(50));

        roleField.setValue("");
        roleField.setDocument(new FieldLengthLimit(30));

        phoneField.setValue("");
        phoneField.setDocument(new FieldLengthLimit(20));

        emailField.setValue("");
        emailField.setDocument(new FieldLengthLimit(100));

        urlField.setValue("");
        urlField.setDocument(new FieldLengthLimit(255));

        addressArea.setColumns(20);
        addressArea.setRows(5);
        TransferFocus.patch(addressArea);
        jScrollPane1.setViewportView(addressArea);

        noteArea.setColumns(20);
        noteArea.setRows(5);
        TransferFocus.patch(noteArea);
        jScrollPane2.setViewportView(noteArea);

        helpBtn.setText(L10n.getString("helpBtn"));

        closeBtn.setText(L10n.getString("closeBtn"));

        saveBtn.setText(L10n.getString("saveAuthorBtn"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(jLabel3)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, roleField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, organizationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(phoneField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(emailField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(urlField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(helpBtn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 37, Short.MAX_VALUE)
                        .add(closeBtn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveBtn)
                        .add(13, 13, 13))
                    .add(layout.createSequentialGroup()
                        .add(jLabel9)
                        .addContainerGap(293, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(organizationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(roleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(phoneField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(emailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(urlField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(closeBtn)
                    .add(saveBtn))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     *  Check whether given compulsory field is blank. If it is, display appropriate message and give this field focus.
     *
     *  @param field string identifier of the field. possible values are: <i>name, surname, organization, role, address, phone, email, url, note</i>
     *  @return true if the field is non-empty, false otherwise
     */
    public boolean checkNonEmpty(String field) {
        if (field.equals("name") && (nameField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Name of the author is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (field.equals("organization") && (organizationField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Organization is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            organizationField.requestFocus();
            return false;            
        }
        if (field.equals("role") && (roleField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Role of the author is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            roleField.requestFocus();
            return false;                        
        }
        if (field.equals("address") && (addressArea.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Address is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            addressArea.requestFocus();
            return false;                        
        }
        if (field.equals("phone") && (phoneField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Phone is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;                        
        }
        if (field.equals("email") && (emailField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Email is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;                        
        }
        if (field.equals("url") && (urlField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "URL is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            urlField.requestFocus();
            return false;                        
        }
        if (field.equals("note") && (noteArea.getText().length() == 0)) {
            JOptionPane.showMessageDialog(this, "Note is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            noteArea.requestFocus();
            return false;                        
        }        
        return true;
    }
    
    /**
     *  Enable/disable this dialog
     *
     *  @param enabled specifies whether dialog should be enabled or disabled
     */
    public void setDialogEnabled(boolean enabled) {
        this.setEnabled(enabled);
    }
    
    /**
     *  Return instance of this dialog
     *  @return instance of this dialog
     */
    public JDialog getDialog() {
        return this;
    }
    
    /**
     *  Update contents of the fields on the form according to the data in the model. This method is called when model notifies its observers.
     */
    public void update(Observable o, Object arg) {
        // Load form fields with data from the model
        this.nameField.setValue(model.getName());
        this.organizationField.setValue(model.getOrganization());
        this.roleField.setValue(model.getRole());
        this.addressArea.setText(model.getAddress());
        this.phoneField.setValue(model.getPhoneNumber());
        this.emailField.setValue(model.getEmail());
        this.urlField.setValue(model.getUrl());
        this.noteArea.setText(model.getNote());
    }
    
    /**
     *  Add ActionListener for the <b>close</b> button
     *  @param al ActionListener for the <b>close</b> button
     */
    public void closeBtnAddActionListener(ActionListener al) {
        closeBtn.addActionListener(al);
    }
    
    /**
     *  Add ActionListener for the <b>help</b> button
     *  @param al ActionListener for the <b>help</b> button
     */
    public void helpBtnAddActionListener(ActionListener al) {
        helpBtn.addActionListener(al);
    }

    /**
     *  Add ActionListener for the <b>save</b> button
     *  @param al ActionListener for the <b>save</b> button
     */    
    public void saveBtnAddActionListener(ActionListener al) {
        saveBtn.addActionListener(al);
    }    
        
    /**
     *  Add PropertyChangeListener for the <b>name</b> field
     *  @param pcl PropertyChangeListener for the <b>name</b> field
     */
    void nameAddPropertyChangeListener(PropertyChangeListener pcl) {
        nameField.addPropertyChangeListener(pcl);
    }

    /**
     *  Add PropertyChangeListener for the <b>organization</b> field
     *  @param pcl PropertyChangeListener for the <b>organization</b> field
     */    
    void organizationAddPropertyChangeListener(PropertyChangeListener pcl) {
        organizationField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add PropertyChangeListener for the <b>role</b> field
     *  @param pcl PropertyChangeListener for the <b>role</b> field
     */
    void roleAddPropertyChangeListener(PropertyChangeListener pcl) {
        roleField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add FocusListener for the <b>address</b> field
     *  @param pcl FocusListener for the <b>address</b> field
     */
    void addressAddFocusListener(FocusListener fl) {
        addressArea.addFocusListener(fl);
    }    

    /**
     *  Add PropertyChangeListener for the <b>phoneNumber</b> field
     *  @param pcl PropertyChangeListener for the <b>role</b> field
     */
    void phoneNumberAddPropertyChangeListener(PropertyChangeListener pcl) {
        phoneField.addPropertyChangeListener(pcl);
    }
    
    /**
     *  Add PropertyChangeListener for the <b>email</b> field
     *  @param pcl PropertyChangeListener for the <b>email</b> field
     */
    void emailAddPropertyChangeListener(PropertyChangeListener pcl) {
        emailField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add PropertyChangeListener for the <b>url</b> field
     *  @param pcl PropertyChangeListener for the <b>url</b> field
     */
    void urlAddPropertyChangeListener(PropertyChangeListener pcl) {
        urlField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add FocusListener for the <b>note</b> field
     *  @param pcl FocusListener for the <b>note</b> field
     */
    void noteAddFocusListener(FocusListener fl) {
        noteArea.addFocusListener(fl);
    }  
    
    /**
     *  Close this dialog.
     */    
    public void close() {
        this.dispose();        
    }
    
    /**
     *  Get the name of the author.
     *  @return name of the author
     */    
    public String getName() {
        return (String)nameField.getValue();                
    }
    
    /**
     *  Get the organization of the author.
     *  @return organization of the author
     */    
    public String getOrganization() {
        return (String)organizationField.getValue();        
    }
    
    /**
     *  Get the role of the author.
     *  @return role of the author
     */    
    public String getRole() {
        return (String)roleField.getValue();        
    }
    
    /**
     *  Get the address of the author.
     *  @return address of the author
     */    
    public String getAddress() {
        return addressArea.getText();        
    }    
    
    /**
     *  Get the phoneNumber of the author.
     *  @return phoneNumber of the author
     */    
    public String getPhoneNumber() {
        return (String)phoneField.getValue();        
    }    
    
    /**
     *  Get the email of the author.
     *  @return email of the author
     */    
    public String getEmail() {
        return (String)emailField.getValue();        
    }    
    
    /**
     *  Get the URL of the author.
     *  @return URL of the author
     */    
    public String getUrl() {
        return (String)urlField.getValue();        
    }    
    
    /**
     *  Get the note of the author.
     *  @return note of the author
     */    
    public String getNote() {
        return noteArea.getText();        
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
    private javax.swing.JTextArea addressArea;
    private javax.swing.JButton closeBtn;
    private javax.swing.JFormattedTextField emailField;
    private javax.swing.JButton helpBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JFormattedTextField nameField;
    private javax.swing.JTextArea noteArea;
    private javax.swing.JFormattedTextField organizationField;
    private javax.swing.JFormattedTextField phoneField;
    private javax.swing.JFormattedTextField roleField;
    private javax.swing.JButton saveBtn;
    private javax.swing.JFormattedTextField urlField;
    // End of variables declaration//GEN-END:variables
    
}
