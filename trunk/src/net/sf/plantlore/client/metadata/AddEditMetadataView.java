/*
 * AddEditMetadataView.java
 *
 * Created on 23. duben 2006, 15:44
 */

package net.sf.plantlore.client.metadata;

import java.text.DateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.l10n.L10n;

/**
 * View for the Add/Edit metadata dialog in the MetadataManager MVC.
 *
 * @author  Lada Oberreiterova
 * @version 1.0
 */
public class AddEditMetadataView extends javax.swing.JDialog  implements Observer {
    
    /** Metadata manager model */
    private MetadataManager model;
    
    /**
     * Creates new form AddEditMetadataView
     * @param model model of MetadataManager MVC     
     * @param parent parent of this dialog
     * @param modal boolean flag whether the dialog should be modal or not
     */
    public AddEditMetadataView(MetadataManager model, javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        initComponents();        
        //getRootPane().setDefaultButton(closeButton); 
        PlantloreHelp.addKeyHelp(PlantloreHelp.METDATA_ADD, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.METDATA_ADD, this.helpButton);        
    }
    
     public void update(Observable observable, Object object)
    {
    }
    
     /**
      * Set add form. 
      */
     public void setAddForm() {
         operationButton.setText(L10n.getString("MetadataManager.ButtonAdd"));         
         dateCreateText.setVisible(false);
         dateModifiedText.setVisible(false);  
         technicalContactNameText.setText("");
         technicalContactEmailText.setText("");
         technicalContactAddressText.setText("");
         contentContactNameText.setText("");
         contentContactEmailText.setText("");
         contectContactAddressText.setText("");
         dataSetTitleText.setText("");
         dataSetDetailsText.setText("");
         sourceInstirutionIdText.setText("");
         sourceIdText.setText("");
         abbrevText.setText("");
         dateCreateText.setText("");
         dateModifiedText.setText("");
         recordbasisText.setText("");
         biotopetextText.setText("");  
         createDateChooser.setDate(new Date());
     }
     
     /**
      * Set edit form.      
      */
      public void setEditForm() {
         operationButton.setText(L10n.getString("MetadataManager.ButtonEdit"));
         dateCreateText.setEditable(false);
         dateModifiedText.setEditable(false);
         dateModifiedEmptyLable.setVisible(false);
         createDateChooser.setVisible(false);
     }
     
     /**
      * Set details form.      
      */ 
     public void setDetailsForm() {
       operationButton.setText(L10n.getString("MetadataManager.ButtonOk"));
       operationButton.setVisible(false);
       technicalContactNameText.setEditable(false);
       technicalContactEmailText.setEditable(false);
       technicalContactAddressText.setEditable(false);
       contentContactNameText.setEditable(false);
       contentContactEmailText.setEditable(false);
       contectContactAddressText.setEditable(false);
       dataSetTitleText.setEditable(false);
       dataSetDetailsText.setEditable(false);
       sourceInstirutionIdText.setEditable(false);
       sourceIdText.setEditable(false);
       abbrevText.setEditable(false);
       dateCreateText.setEditable(false);
       dateModifiedText.setEditable(false);
       recordbasisText.setEditable(false);
       biotopetextText.setEditable(false);   
       dateModifiedEmptyLable.setVisible(false);
       createDateChooser.setVisible(false);
     }
     
     /**
      * Load data to edit or details dialog
      */     
     public void loadData() {
           //load data
           Metadata metadata = model.getMetadataRecord();  
           technicalContactNameText.setText(metadata.getTechnicalContactName());
           technicalContactEmailText.setText(metadata.getTechnicalContactEmail());
           technicalContactAddressText.setText(metadata.getTechnicalContactAddress());
           contentContactNameText.setText(metadata.getContentContactName());
           contentContactEmailText.setText(metadata.getContentContactEmail());
           contectContactAddressText.setText(metadata.getContentContactAddress());
           dataSetTitleText.setText(metadata.getDataSetTitle());
           dataSetDetailsText.setText(metadata.getDataSetDetails());
           sourceInstirutionIdText.setText(metadata.getSourceInstitutionId());
           sourceIdText.setText(metadata.getSourceId());
           abbrevText.setText(metadata.getOwnerOrganizationAbbrev());
           dateCreateText.setValue(metadata.getDateCreate());
           createDateChooser.setDate(new Date());           
           dateModifiedText.setValue(metadata.getDateModified());
           recordbasisText.setText(metadata.getRecordBasis());
           biotopetextText.setText(metadata.getBiotopeText());            
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
        if (this.sourceInstirutionIdText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("MetadataManager.SourceInstitutionId") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.technicalContactNameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("MetadataManager.TechnicalContactName") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.contentContactNameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("MetadataManager.ContentContactName") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.dataSetTitleText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("MetadataManager.DataSetTitle") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else  if (this.sourceIdText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, L10n.getString("MetadataManager.SourceId") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.createDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, L10n.getString("MetadataManager.DateCreate") + L10n.getString("Error.MissingCompulsoryField"), L10n.getString("Error.MissingCompulsoryFieldTitle"), JOptionPane.ERROR_MESSAGE);
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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        technicalContactNameLabel = new javax.swing.JLabel();
        technicalContactEmailLabel = new javax.swing.JLabel();
        technivalContactAddressLabel = new javax.swing.JLabel();
        technicalContactNameText = new javax.swing.JTextField();
        technicalContactEmailText = new javax.swing.JTextField();
        technicalContactAddressText = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        contentContactNameLabel = new javax.swing.JLabel();
        contentContactEmail = new javax.swing.JLabel();
        contectContactAddressLabel = new javax.swing.JLabel();
        contentContactNameText = new javax.swing.JTextField();
        contentContactEmailText = new javax.swing.JTextField();
        contectContactAddressText = new javax.swing.JTextField();
        sourceInstitutionIdLabel = new javax.swing.JLabel();
        abbrevLabel = new javax.swing.JLabel();
        sourceInstirutionIdText = new javax.swing.JTextField();
        abbrevText = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        dataSetTitleLabel = new javax.swing.JLabel();
        dataSetDetailsLabel = new javax.swing.JLabel();
        sourceIdLabel = new javax.swing.JLabel();
        dataSetTitleText = new javax.swing.JTextField();
        dataSetDetailsText = new javax.swing.JTextField();
        sourceIdText = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        recordbasisLabel = new javax.swing.JLabel();
        biotopetextLabel = new javax.swing.JLabel();
        recordbasisText = new javax.swing.JTextField();
        biotopetextText = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        dateCreateLabel = new javax.swing.JLabel();
        dateModifiedLabel = new javax.swing.JLabel();
        dateCreateText = new JFormattedTextField(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()));
        dateModifiedText = new JFormattedTextField(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()));
        createDateChooser = new com.toedter.calendar.JDateChooser();
        dateModifiedEmptyLable = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        operationButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.MetadataDetailsPanel")));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.InstitutionPanel")));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.TechnicalContectPanel")));
        technicalContactNameLabel.setText(L10n.getString("MetadataManager.TechnicalContactName"));

        technicalContactEmailLabel.setText(L10n.getString("MetadataManager.TechnicalContactEmail"));

        technivalContactAddressLabel.setText(L10n.getString("MetadataManager.TechnicalContactAddress"));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(technivalContactAddressLabel)
                    .add(technicalContactEmailLabel)
                    .add(technicalContactNameLabel))
                .add(23, 23, 23)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(technicalContactAddressText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(technicalContactNameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(technicalContactEmailText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(technicalContactNameLabel)
                    .add(technicalContactNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(technicalContactEmailLabel)
                    .add(technicalContactEmailText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(technivalContactAddressLabel)
                    .add(technicalContactAddressText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.ContentContactPanel")));
        contentContactNameLabel.setText(L10n.getString("MetadataManager.ContentContactName"));

        contentContactEmail.setText(L10n.getString("MetadataManager.ContentContactEmail"));

        contectContactAddressLabel.setText(L10n.getString("MetadataManager.ContentContactAddress"));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contentContactNameLabel)
                    .add(contentContactEmail)
                    .add(contectContactAddressLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contentContactNameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(contentContactEmailText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(contectContactAddressText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .add(47, 47, 47))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(contentContactNameLabel)
                    .add(contentContactNameText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(contentContactEmail)
                    .add(contentContactEmailText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(contectContactAddressLabel)
                    .add(contectContactAddressText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        sourceInstitutionIdLabel.setText(L10n.getString("MetadataManager.SourceInstitutionId"));

        abbrevLabel.setText(L10n.getString("MetadataManager.OwnerOrganizationAbbrev"));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(sourceInstitutionIdLabel)
                            .add(abbrevLabel))
                        .add(21, 21, 21)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(abbrevText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .add(sourceInstirutionIdText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .add(335, 335, 335))
                    .add(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourceInstitutionIdLabel)
                    .add(sourceInstirutionIdText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(abbrevLabel)
                    .add(abbrevText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(19, 19, 19)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("MetadataManager.ProjektDataPanel")));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataSetTitleLabel.setText(L10n.getString("MetadataManager.DataSetTitle"));

        dataSetDetailsLabel.setText(L10n.getString("MetadataManager.DataSetDetails"));

        sourceIdLabel.setText(L10n.getString("MetadataManager.SourceId"));

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dataSetTitleLabel)
                    .add(dataSetDetailsLabel)
                    .add(sourceIdLabel))
                .add(24, 24, 24)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sourceIdText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(dataSetDetailsText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(dataSetTitleText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .add(21, 21, 21))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataSetTitleLabel)
                    .add(dataSetTitleText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataSetDetailsLabel)
                    .add(dataSetDetailsText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourceIdLabel)
                    .add(sourceIdText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        recordbasisLabel.setText(L10n.getString("MetadataManager.Recordbasis"));

        biotopetextLabel.setText(L10n.getString("MetadataManager.Biotopetext"));

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(recordbasisLabel)
                    .add(biotopetextLabel))
                .add(14, 14, 14)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(biotopetextText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                    .add(recordbasisText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                .add(66, 66, 66))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(recordbasisLabel)
                    .add(recordbasisText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(biotopetextLabel)
                    .add(biotopetextText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dateCreateLabel.setText(L10n.getString("MetadataManager.DateCreate"));

        dateModifiedLabel.setText(L10n.getString("MetadataManager.DateModified"));

        dateModifiedEmptyLable.setText("--------");

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dateCreateLabel)
                    .add(dateModifiedLabel))
                .add(63, 63, 63)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dateModifiedText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .add(dateCreateText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(25, 25, 25)
                        .add(createDateChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(57, 57, 57)
                        .add(dateModifiedEmptyLable)))
                .add(202, 202, 202))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(dateCreateLabel)
                        .add(dateCreateText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(createDateChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dateModifiedLabel)
                    .add(dateModifiedEmptyLable)
                    .add(dateModifiedText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        closeButton.setText(L10n.getString("MetadataManager.ButtonClose"));

        operationButton.setText("");

        helpButton.setText(L10n.getString("MetadataManager.ButtonHelp"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 360, Short.MAX_VALUE)
                        .add(operationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(helpButton)
                    .add(closeButton)
                    .add(operationButton))
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
                new AddEditMetadataView(null, new javax.swing.JDialog(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abbrevLabel;
    protected javax.swing.JTextField abbrevText;
    private javax.swing.JLabel biotopetextLabel;
    protected javax.swing.JTextField biotopetextText;
    protected javax.swing.JButton closeButton;
    private javax.swing.JLabel contectContactAddressLabel;
    protected javax.swing.JTextField contectContactAddressText;
    private javax.swing.JLabel contentContactEmail;
    protected javax.swing.JTextField contentContactEmailText;
    private javax.swing.JLabel contentContactNameLabel;
    protected javax.swing.JTextField contentContactNameText;
    protected com.toedter.calendar.JDateChooser createDateChooser;
    private javax.swing.JLabel dataSetDetailsLabel;
    protected javax.swing.JTextField dataSetDetailsText;
    private javax.swing.JLabel dataSetTitleLabel;
    protected javax.swing.JTextField dataSetTitleText;
    private javax.swing.JLabel dateCreateLabel;
    protected javax.swing.JFormattedTextField dateCreateText;
    protected javax.swing.JLabel dateModifiedEmptyLable;
    private javax.swing.JLabel dateModifiedLabel;
    protected javax.swing.JFormattedTextField dateModifiedText;
    protected javax.swing.JButton helpButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    protected javax.swing.JButton operationButton;
    private javax.swing.JLabel recordbasisLabel;
    protected javax.swing.JTextField recordbasisText;
    private javax.swing.JLabel sourceIdLabel;
    protected javax.swing.JTextField sourceIdText;
    protected javax.swing.JTextField sourceInstirutionIdText;
    private javax.swing.JLabel sourceInstitutionIdLabel;
    protected javax.swing.JTextField technicalContactAddressText;
    private javax.swing.JLabel technicalContactEmailLabel;
    protected javax.swing.JTextField technicalContactEmailText;
    private javax.swing.JLabel technicalContactNameLabel;
    protected javax.swing.JTextField technicalContactNameText;
    private javax.swing.JLabel technivalContactAddressLabel;
    // End of variables declaration//GEN-END:variables
    
}
