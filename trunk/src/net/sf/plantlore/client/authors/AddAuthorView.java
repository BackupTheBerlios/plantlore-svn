/*
 * AddAuthorView.java
 *
 * Created on 20. leden 2006, 22:56
 *
 */

package net.sf.plantlore.client.authors;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Tomas Kovarik
 */
public class AddAuthorView implements Observer {

    private AuthorManager model;    
    private JDialog addAuthDialog;
    private Container container;    
    
    /** Creates a new instance of AddAuthorView */
    public AddAuthorView(AuthorManager authModel, JDialog owner) {
        this.model = authModel;
        this.model.addObserver(this); 

        // Create new modal dialog
        addAuthDialog = new JDialog(owner, "Add New Author", true);
        addAuthDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addAuthDialog.setSize(320,240);

        container = addAuthDialog.getContentPane();
        container.setLayout(new GridBagLayout());
        initComponents();        
        
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        
        nameLabel = new JLabel();
        surnameLabel = new JLabel();
        organizationLabel = new JLabel();
        roleLabel = new JLabel();
        addressLabel = new JLabel();
        phoneLabel = new JLabel();
        emailLabel = new JLabel();
        urlLabel = new JLabel();
        noteLabel = new JLabel();
        requiredLabel = new JLabel();
        
        nameField = new JFormattedTextField();
        surnameField = new JFormattedTextField();
        organizationField = new JFormattedTextField();
        roleField = new JFormattedTextField();
        phoneField = new JFormattedTextField(); 
        emailField = new JFormattedTextField();
        urlField = new JFormattedTextField();
        
        addressScrollPane = new JScrollPane();
        addressArea = new JTextArea();
        noteScrollPane = new JScrollPane();
        noteArea = new JTextArea();
                
        saveBtn = new JButton();
        closeBtn = new JButton();
        helpBtn = new JButton();
        
        // Add label with the first name
        nameLabel.setText("First name: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(nameLabel, gridBagConstraints);

        // Add label with the surname
        surnameLabel.setText("Surname: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(surnameLabel, gridBagConstraints);
        
        // Add label with organization
        organizationLabel.setText("Organization: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(organizationLabel, gridBagConstraints);        
                
        // Add label with the role
        roleLabel.setText("Role: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(roleLabel, gridBagConstraints);        
        
        // Add label with the address
        addressLabel.setText("Address: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(addressLabel, gridBagConstraints);        
        
        // Add label with the phone
        phoneLabel.setText("Phone: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(phoneLabel, gridBagConstraints);        
        
        // Add label with email
        emailLabel.setText("Email: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(emailLabel, gridBagConstraints);                
        
        // Add label with the address
        urlLabel.setText("URL: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(urlLabel, gridBagConstraints);        
        
        // Add label with the address
        noteLabel.setText("Note: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(noteLabel, gridBagConstraints);                
        
        // Add label describing required fields
        requiredLabel.setText("* Indicates required field");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(requiredLabel, gridBagConstraints);                        
        
        // Add field for name
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(nameField, gridBagConstraints);

        // Add field for surname
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(surnameField, gridBagConstraints);

        // Add field for organization
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(organizationField, gridBagConstraints);
        
        // Add field for role
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(roleField, gridBagConstraints);
               
        // Add field for phone
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(phoneField, gridBagConstraints);

        // Add field for email
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(emailField, gridBagConstraints);

        // Add field for url
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        container.add(urlField, gridBagConstraints);        
        
        // Add ScrollPane for the address
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        addressScrollPane.setMinimumSize(new java.awt.Dimension(200, 100));        
        addressScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));                        
        container.add(addressScrollPane, gridBagConstraints);                        
        
        addressScrollPane.add(addressArea);                
        addressScrollPane.setViewportView(addressArea);                
        
        // Add ScrollPane for the note
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;        
        gridBagConstraints.weighty = 0.5;                
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        noteScrollPane.setMinimumSize(new java.awt.Dimension(200, 100));        
        noteScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));                        
        container.add(noteScrollPane, gridBagConstraints);                        
        
        noteScrollPane.add(noteArea);                
        noteScrollPane.setViewportView(noteArea);                        
        
        // Add "Save Author" button
        saveBtn.setText("Save Author");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;        
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 5);
        saveBtn.setMinimumSize(new java.awt.Dimension(110, 25));        
        saveBtn.setPreferredSize(new java.awt.Dimension(110, 25));                        
        container.add(saveBtn, gridBagConstraints);

        // Add "Close" button
        closeBtn.setText("Close");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;        
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 120);
        closeBtn.setMinimumSize(new java.awt.Dimension(110, 25));        
        closeBtn.setPreferredSize(new java.awt.Dimension(110, 25));                        
        container.add(closeBtn, gridBagConstraints);        
        
        // Add "Help" button
        helpBtn.setText("Help");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 5);
        helpBtn.setMinimumSize(new java.awt.Dimension(110, 25));        
        helpBtn.setPreferredSize(new java.awt.Dimension(110, 25));                        
        container.add(helpBtn, gridBagConstraints);                
    }
    
    public boolean checkNonEmpty(String field) {
        if (field.equals("name") && (nameField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Name of the author is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (field.equals("surname") && (surnameField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Surname of the author is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            surnameField.requestFocus();
            return false;            
        }
        if (field.equals("organization") && (organizationField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Organization is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            organizationField.requestFocus();
            return false;            
        }
        if (field.equals("role") && (roleField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Role of the author is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            roleField.requestFocus();
            return false;                        
        }
        if (field.equals("address") && (addressArea.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Address is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            addressArea.requestFocus();
            return false;                        
        }
        if (field.equals("phone") && (phoneField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Phone is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;                        
        }
        if (field.equals("email") && (emailField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Email is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;                        
        }
        if (field.equals("url") && (urlField.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "URL is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            urlField.requestFocus();
            return false;                        
        }
        if (field.equals("note") && (noteArea.getText().length() == 0)) {
            JOptionPane.showMessageDialog(addAuthDialog, "Note is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            noteArea.requestFocus();
            return false;                        
        }        
        return true;
    }
    
    public void show() {
        addAuthDialog.setSize(400,450);        
        addAuthDialog.setVisible(true);
    }               

    public void setDialogEnabled(boolean enabled) {
        addAuthDialog.setEnabled(enabled);
    }
    
    public JDialog getDialog() {
        return this.addAuthDialog;
    }
    
    public void update(Observable o, Object arg) {
        
    }
    
    public void closeBtnAddActionListener(ActionListener al) {
        closeBtn.addActionListener(al);
    }
    
    public void helpBtnAddActionListener(ActionListener al) {
        helpBtn.addActionListener(al);
    }
    
    public void saveBtnAddActionListener(ActionListener al) {
        saveBtn.addActionListener(al);
    }    
        
    void firstNameAddFocusListener(FocusListener fl) {
        nameField.addFocusListener(fl);
    }
    
    void surnameAddFocusListener(FocusListener fl) {
        surnameField.addFocusListener(fl);
    }    
    
    void organizationAddFocusListener(FocusListener fl) {
        organizationField.addFocusListener(fl);
    }    
    
    void roleAddFocusListener(FocusListener fl) {
        roleField.addFocusListener(fl);
    }    
    
    void addressAddFocusListener(FocusListener fl) {
        addressArea.addFocusListener(fl);
    }    

    void phoneNumberAddFocusListener(FocusListener fl) {
        phoneField.addFocusListener(fl);
    }
    
    void emailAddFocusListener(FocusListener fl) {
        emailField.addFocusListener(fl);
    }    
    
    void urlAddFocusListener(FocusListener fl) {
        urlField.addFocusListener(fl);
    }    
    
    void noteAddFocusListener(FocusListener fl) {
        noteArea.addFocusListener(fl);
    }    
    
    public void close() {
        addAuthDialog.dispose();        
    }
    
    public String getFirstName() {
        return nameField.getText();        
    }
    
    public String getSurname() {
        return surnameField.getText();        
    }
    
    public String getOrganization() {
        return organizationField.getText();        
    }
    
    public String getRole() {
        return roleField.getText();        
    }
    
    public String getAddress() {
        return addressArea.getText();        
    }    
    
    public String getPhoneNumber() {
        return phoneField.getText();        
    }    
    
    public String getEmail() {
        return emailField.getText();        
    }    
    
    public String getUrl() {
        return urlField.getText();        
    }    
    
    public String getNote() {
        return noteArea.getText();        
    }        
        
    JLabel nameLabel;
    JLabel surnameLabel;
    JLabel organizationLabel;
    JLabel roleLabel;
    JLabel addressLabel;
    JLabel phoneLabel;
    JLabel emailLabel;
    JLabel urlLabel;
    JLabel noteLabel;
    JLabel requiredLabel;
    
    JFormattedTextField nameField;
    JFormattedTextField surnameField;
    JFormattedTextField organizationField;
    JFormattedTextField roleField;
    JFormattedTextField phoneField;
    JFormattedTextField emailField;
    JFormattedTextField urlField;
    
    JScrollPane addressScrollPane;
    JTextArea addressArea;
    JScrollPane noteScrollPane;
    JTextArea noteArea;

    JButton saveBtn;
    JButton closeBtn;    
    JButton helpBtn;    
}
