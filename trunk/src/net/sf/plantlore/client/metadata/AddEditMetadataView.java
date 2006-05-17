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
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  Lada
 */
public class AddEditMetadataView extends javax.swing.JDialog  implements Observer {
    
    /** Metadata manager model */
    private MetadataManager model;
    
    /**
     * Creates new form AddEditMetadataView
     */
    public AddEditMetadataView(MetadataManager model, javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        initComponents();        
    }
    
     public void update(Observable observable, Object object)
    {
    }
    
     /*
      * nastaveni formulare pro add
      */
     public void setAddForm() {
         operationButton.setText("Add");         
         dateCreateText.setVisible(false);
         dateModifiedText.setVisible(false);         
     }
     
      public void setEditForm() {
         operationButton.setText("Edit");
         dateCreateText.setEditable(false);
         dateModifiedText.setEditable(false);
         dateModifiedEmptyLable.setVisible(false);
         createDateChooser.setVisible(false);
     }
     
     public void setDetailsForm() {
       operationButton.setText("Ok");
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
      * Nacteni dat do dialogu pro DETAILs, EDIT
      */     
     public void loadData() {
           //nacteni dat do dialogu
           Metadata metadata = model.getSelectedRecord();  
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
           //FIXME: vsechny Integery v DB nastavit defautlne na nulu,aby to pri prevodech na string nedelalo neplechu           
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
        if (this.sourceInstirutionIdText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Institution is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.technicalContactNameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Technical contact name is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.contentContactNameText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Content contact name is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.dataSetTitleText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Project name is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            return false;
        } else  if (this.sourceIdText.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Code of source name is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (this.createDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date creating of project is a compulsory field. Please fill it in.", "Missing compulsory field", JOptionPane.ERROR_MESSAGE);
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
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Metadata"));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Institution"));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Technical contact"));
        technicalContactNameLabel.setText(L10n.getString("metadata.technicalContactName"));

        technicalContactEmailLabel.setText(L10n.getString("metadata.technicalContactEmail"));

        technivalContactAddressLabel.setText(L10n.getString("metadata.technicalContactAddress"));

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
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(technicalContactAddressText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(technicalContactNameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(technicalContactEmailText))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Content contact"));
        contentContactNameLabel.setText(L10n.getString("metadata.contentContactName"));

        contentContactEmail.setText(L10n.getString("metadata.contentContactEmail"));

        contectContactAddressLabel.setText(L10n.getString("metadata.contentContactAddress"));

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
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(contentContactNameText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .add(contentContactEmailText)
                    .add(contectContactAddressText))
                .addContainerGap(47, Short.MAX_VALUE))
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

        sourceInstitutionIdLabel.setText(L10n.getString("metadata.sourceInstitutionId"));

        abbrevLabel.setText(L10n.getString("metadata.ownerOrganizationAbbrev"));

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
                        .add(63, 63, 63)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(abbrevText)
                            .add(sourceInstirutionIdText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)))
                    .add(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
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
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Project - data"));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataSetTitleLabel.setText(L10n.getString("metadata.dataSetTitle"));

        dataSetDetailsLabel.setText(L10n.getString("metadata.dataSetDetails"));

        sourceIdLabel.setText(L10n.getString("metadata.sourceId"));

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
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(sourceIdText)
                    .add(dataSetDetailsText)
                    .add(dataSetTitleText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
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
        recordbasisLabel.setText(L10n.getString("metadata.recordbasis"));

        biotopetextLabel.setText(L10n.getString("metadata.biotopetext"));

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
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(biotopetextText)
                    .add(recordbasisText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                .addContainerGap(66, Short.MAX_VALUE))
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
        dateCreateLabel.setText(L10n.getString("metadata.dateCreate"));

        dateModifiedLabel.setText(L10n.getString("metadata.dateModified"));

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
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(dateModifiedText)
                    .add(dateCreateText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(25, 25, 25)
                        .add(createDateChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(57, 57, 57)
                        .add(dateModifiedEmptyLable)))
                .addContainerGap(202, Short.MAX_VALUE))
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
                    .add(dateModifiedText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dateModifiedEmptyLable))
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
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        closeButton.setText(L10n.getString("Close"));

        operationButton.setText("Add");

        helpButton.setText(L10n.getString("Help"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(helpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(operationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
