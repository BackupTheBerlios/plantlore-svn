
package net.sf.plantlore.client.publications;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.TransferFocus;
import net.sf.plantlore.l10n.L10n;

/**
 * Dialog used for adding / editing publications.
 *
 * @author  Tomas Kovarik
 * @version 1.0, June 4, 2006
 */
public class AddPublicationView extends javax.swing.JDialog implements Observer {
    /** Model of the Publication manager MVC */
    private PublicationManager model;    

    /**
     * Creates new form AddPublicationView 
     * 
     * @param publModel Model of the PublicationManager MVC
     * @param parent parent window of this dialog
     * @param modal whether the dialog should be displayed as modal or not
     */
    public AddPublicationView(PublicationManager publModel, JDialog parent, boolean modal) {
        super(parent, modal);
        this.model = publModel;
        this.model.addObserver(this);         
        initComponents();
        // Set default button of the dialog
        getRootPane().setDefaultButton(this.saveBtn);        
        // Initialize help
        PlantloreHelp.addKeyHelp(PlantloreHelp.PUBLICATION_ADD, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.PUBLICATION_ADD, this.helpBtn);        
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        collectionNameField = new javax.swing.JFormattedTextField();
        publicationYearField = new javax.swing.JFormattedTextField();
        journalNameField = new javax.swing.JFormattedTextField();
        urlField = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        noteArea = new javax.swing.JTextArea();
        helpBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        referenceDetailField = new javax.swing.JFormattedTextField();
        journalAuthorField = new javax.swing.JFormattedTextField();

        setTitle(L10n.getString("addPublicationTitle"));
        jLabel1.setText(L10n.getString("addPublicationCollectionNameLbl"));

        jLabel2.setText(L10n.getString("addPublicationCollectionYearPublicationLbl"));

        jLabel3.setText(L10n.getString("addPublicationJournalNameLbl"));

        jLabel5.setText(L10n.getString("addPublicationJournalAuthorNameLbl"));

        jLabel7.setText(L10n.getString("addPublicationReferenceDetailLbl"));

        jLabel8.setText(L10n.getString("addPublicationNoteLbl"));

        jLabel9.setText(L10n.getString("requiredFieldLbl"));

        collectionNameField.setValue("");

        publicationYearField.setValue(0);
        publicationYearField.setValue(null);

        journalNameField.setValue("");

        urlField.setValue("");

        noteArea.setColumns(20);
        noteArea.setRows(5);
        TransferFocus.patch(noteArea);
        jScrollPane2.setViewportView(noteArea);

        helpBtn.setText(L10n.getString("Common.Help"));
        helpBtn.setMinimumSize(new java.awt.Dimension(150, 23));

        closeBtn.setText(L10n.getString("Common.Close"));
        closeBtn.setMinimumSize(new java.awt.Dimension(150, 23));

        saveBtn.setText(L10n.getString("Publication.Add.SaveButton"));
        saveBtn.setMinimumSize(new java.awt.Dimension(150, 23));

        jLabel10.setText(L10n.getString("addPublicationUrlLbl"));

        referenceDetailField.setValue("");

        journalAuthorField.setValue("");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE))
                            .add(jLabel10)
                            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jLabel3)
                                .add(10, 10, 10))
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .add(jLabel8))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, journalAuthorField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .add(collectionNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .add(publicationYearField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .add(journalNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, urlField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, referenceDetailField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)))
                    .add(jLabel9)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(helpBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 58, Short.MAX_VALUE)
                        .add(closeBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(collectionNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(publicationYearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(journalNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(journalAuthorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false)
                    .add(referenceDetailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(urlField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 60, Short.MAX_VALUE)
                        .add(jLabel9))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .add(16, 16, 16)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(saveBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(helpBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     *  Check whether compulsory items are present. At least one of the following items must be
     *  provided: collectionName, collectionYearPublication, journalAuthorName, journalName
     *
     *  @return true if one of the collectionName, collectionYearPublication, journalAuthorName, journalName is non-empty, false otherwise
     */
    public boolean checkCompulsory() {
        if (((Integer)publicationYearField.getValue() == null) && (((String)collectionNameField.getValue()).length() == 0) &&
            (((String)journalNameField.getValue()).length() == 0) && (((String)journalAuthorField.getValue()).length() == 0)) {
            JOptionPane.showMessageDialog(this, "At least one of the following fields must be filled in: Journal author, Year of publication, Journal name, Collection name", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            collectionNameField.requestFocus();
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
     *  Update contents of the fields on the form according to the data in the model. This method is 
     *  called when model notifies its observers.
     *  @param o    observable which notified observers
     *  @param arg  argument sent with notification
     */
    public void update(Observable o, Object arg) {
        // Load form fields with data from the model
        this.collectionNameField.setValue(model.getCollectionName());
        this.publicationYearField.setValue(model.getPublicationYear());
        this.journalNameField.setValue(model.getJournalName());
        this.journalAuthorField.setValue(model.getJournalAuthor());
        this.referenceDetailField.setValue(model.getReferenceDetail());
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
     *  Add PropertyChangeListener for the <b>collection name</b> field
     *  @param pcl PropertyChangeListener for the <b>collection name</b> field
     */
    void collectionNameAddPropertyChangeListener(PropertyChangeListener pcl) {
        collectionNameField.addPropertyChangeListener(pcl);
    }

    /**
     *  Add PropertyChangeListener for the <b>publication year</b> field
     *  @param pcl PropertyChangeListener for the <b>publication year</b> field
     */    
    void publicationYearAddPropertyChangeListener(PropertyChangeListener pcl) {
        publicationYearField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add PropertyChangeListener for the <b>journal name</b> field
     *  @param pcl PropertyChangeListener for the <b>journal name</b> field
     */
    void journalNameAddPropertyChangeListener(PropertyChangeListener pcl) {
        journalNameField.addPropertyChangeListener(pcl);
    }    
    
    /**
     *  Add PropertyChangeListener for the <b>journal author</b> field
     *  @param pcl PropertyChangeListener for the <b>journal author</b> field
     */
    void journalAuthorAddPropertyChangeListener(PropertyChangeListener pcl) {
        journalAuthorField.addPropertyChangeListener(pcl);
    }    
   
    /**
     *  Add PropertyChangeListener for the <b>reference detail</b> field
     *  @param pcl PropertyChangeListener for the <b>reference detail</b> field
     */
    void referenceDetailAddPropertyChangeListener(PropertyChangeListener pcl) {
        referenceDetailField.addPropertyChangeListener(pcl);
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
     *  Get the name of the collection.
     *  @return name of the collection
     */    
    public String getCollectionName() {
        return (String)collectionNameField.getValue();                
    }
    
    /**
     *  Get the year of publication.
     *  @return year of publication
     */    
    public Integer getPublicationYear() {
        return (Integer)publicationYearField.getValue();        
    }
    
    /**
     *  Get the name of the journal.
     *  @return name of the journal
     */    
    public String getJournalName() {
        return (String)journalNameField.getValue();        
    }
    
    /**
     *  Get the name of the journal author.
     *  @return name of the journal author
     */    
    public String getJournalAuthor() {
        return (String)journalAuthorField.getValue();
    }    
       
    /**
     *  Get the reference detail.
     *  @return reference detail
     */    
    public String getReferenceDetail() {
        return (String)referenceDetailField.getValue();        
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeBtn;
    private javax.swing.JFormattedTextField collectionNameField;
    private javax.swing.JButton helpBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JFormattedTextField journalAuthorField;
    private javax.swing.JFormattedTextField journalNameField;
    private javax.swing.JTextArea noteArea;
    private javax.swing.JFormattedTextField publicationYearField;
    private javax.swing.JFormattedTextField referenceDetailField;
    private javax.swing.JButton saveBtn;
    private javax.swing.JFormattedTextField urlField;
    // End of variables declaration//GEN-END:variables
    
}
