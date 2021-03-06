/*
 * DetailView.java
 *
 * Created on 2. červen 2006, 12:25
 */

package net.sf.plantlore.client.overview.detail;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import net.sf.plantlore.client.*;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author  fraktalek
 */
public class DetailView extends javax.swing.JDialog implements Observer {
    private Logger logger = Logger.getLogger(DetailView.class.getPackage().getName());
    private Detail model;
    private final static String EMPTY = "-";
    
    /** Creates new form DetailView */
    public DetailView(Detail model, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.model = model;
        model.addObserver(this);
        initComponents();
        
        new DefaultEscapeKeyPressed(this);
        setLocationRelativeTo(parent);
        setTitle(L10n.getString("Overview.DetailTitle"));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        sourceLabel = new javax.swing.JLabel();
        taxonLabel = new javax.swing.JLabel();
        projectLabel = new javax.swing.JLabel();
        herbariumLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        authorTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        territoryLabel = new javax.swing.JLabel();
        quadrantLabel = new javax.swing.JLabel();
        phytLabel = new javax.swing.JLabel();
        villageLabel = new javax.swing.JLabel();
        countryLabel = new javax.swing.JLabel();
        altitudeLabel = new javax.swing.JLabel();
        latitudeLabel = new javax.swing.JLabel();
        longitudeLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        habAreaLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        habNoteArea = new javax.swing.JTextArea();
        descAreaLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        placeDescArea = new javax.swing.JTextArea();
        occAreaLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        occNoteArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        prevButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        createdWhoLabel = new javax.swing.JLabel();
        createdWhenLabel = new javax.swing.JLabel();
        updatedWhoLabel = new javax.swing.JLabel();
        updatedWhenLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        sourceLabel.setText("jLabel1");

        taxonLabel.setText("jLabel1");

        projectLabel.setText("jLabel1");

        herbariumLabel.setText("jLabel1");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(taxonLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 543, Short.MAX_VALUE)
                        .add(projectLabel))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(sourceLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 543, Short.MAX_VALUE)
                        .add(herbariumLabel)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(taxonLabel)
                    .add(projectLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourceLabel)
                    .add(herbariumLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        authorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane1.setViewportView(authorTable);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        territoryLabel.setText("jLabel1");

        quadrantLabel.setText("jLabel1");

        phytLabel.setText("jLabel1");

        villageLabel.setText("jLabel1");

        countryLabel.setText("jLabel1");

        altitudeLabel.setText("jLabel1");

        latitudeLabel.setText("jLabel1");

        longitudeLabel.setText("jLabel2");

        dateLabel.setText("jLabel1");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dateLabel)
                    .add(altitudeLabel)
                    .add(latitudeLabel)
                    .add(longitudeLabel)
                    .add(countryLabel)
                    .add(territoryLabel)
                    .add(quadrantLabel)
                    .add(phytLabel)
                    .add(villageLabel))
                .addContainerGap(313, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(territoryLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(quadrantLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(phytLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(villageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(countryLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(altitudeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(latitudeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(longitudeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 120, Short.MAX_VALUE)
                .add(dateLabel)
                .addContainerGap())
        );

        habAreaLabel.setText(L10n.getString("Detail.HabNoteTitle"));

        habNoteArea.setColumns(20);
        habNoteArea.setEditable(false);
        habNoteArea.setLineWrap(true);
        habNoteArea.setRows(5);
        habNoteArea.setWrapStyleWord(true);
        jScrollPane4.setViewportView(habNoteArea);

        descAreaLabel.setText(L10n.getString("Detail.PlaceDescriptionTitle"));

        placeDescArea.setColumns(20);
        placeDescArea.setEditable(false);
        placeDescArea.setLineWrap(true);
        placeDescArea.setRows(5);
        placeDescArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(placeDescArea);

        occAreaLabel.setText(L10n.getString("Detail.OccNoteTitle"));

        occNoteArea.setColumns(20);
        occNoteArea.setEditable(false);
        occNoteArea.setLineWrap(true);
        occNoteArea.setRows(5);
        occNoteArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(occNoteArea);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(habAreaLabel)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(descAreaLabel)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(occAreaLabel)
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(descAreaLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(occAreaLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(habAreaLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        nextButton.setText("jButton2");

        prevButton.setText("jButton1");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(prevButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 491, Short.MAX_VALUE)
                .add(nextButton))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {nextButton, prevButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(nextButton)
                .add(prevButton))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {nextButton, prevButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        createdWhoLabel.setText("jLabel1");

        createdWhenLabel.setText("jLabel1");

        updatedWhoLabel.setText("jLabel1");

        updatedWhenLabel.setText("jLabel1");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(createdWhoLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 543, Short.MAX_VALUE)
                        .add(createdWhenLabel))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(updatedWhoLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 543, Short.MAX_VALUE)
                        .add(updatedWhenLabel)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createdWhoLabel)
                    .add(createdWhenLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(updatedWhoLabel)
                    .add(updatedWhenLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
//                new DetailView(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String msg = (String)arg;
            if (msg.equals("NEW_DETAIL_LOADED")) {
                if (model.source != null)
                    sourceLabel.setText(L10n.getFormattedString("Detail.Source",model.source));
                else
                    sourceLabel.setText(L10n.getFormattedString("Detail.Source",EMPTY));
                if (model.herbarium != null)
                    herbariumLabel.setText(L10n.getFormattedString("Detail.Herbarium",model.herbarium));
                else 
                    herbariumLabel.setText(L10n.getFormattedString("Detail.Herbarium",EMPTY));
                Calendar c = Calendar.getInstance(L10n.getCurrentLocale());
                c.set(Calendar.DAY_OF_MONTH,model.day);
                if (model.month != null)
                    c.set(Calendar.MONTH,model.month);
                c.set(Calendar.YEAR,model.year);
                Date d = c.getTime();
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,L10n.getCurrentLocale());
                dateLabel.setText(L10n.getFormattedString("Detail.Date",df.format(d)));
                occNoteArea.setText(model.occNote);
                habNoteArea.setText(model.habNote);
                placeDescArea.setText(model.placeDescription);
                DateFormat df2 = DateFormat.getDateInstance(DateFormat.LONG,L10n.getCurrentLocale());
                createdWhoLabel.setText(L10n.getFormattedString("Detail.CreatedWho",model.createdWho));
                createdWhenLabel.setText(L10n.getFormattedString("Detail.CreatedWhen",df2.format(model.createdWhen)));
                if (model.updatedWho != null) {
                    updatedWhoLabel.setText(L10n.getFormattedString("Detail.UpdatedWho",model.updatedWho));
                    updatedWhenLabel.setText(L10n.getFormattedString("Detail.UpdatedWhen",df2.format(model.updatedWhen)));
                } else  {
                    updatedWhoLabel.setText(L10n.getFormattedString("Detail.UpdatedWho",EMPTY));
                    updatedWhenLabel.setText(L10n.getFormattedString("Detail.UpdatedWhen",EMPTY));
                }
                if (model.altitude != null)
                    altitudeLabel.setText(L10n.getFormattedString("Detail.Altitude",model.altitude));
                else
                    altitudeLabel.setText(L10n.getFormattedString("Detail.Altitude",EMPTY));
                if (model.latitude != null)
                    latitudeLabel.setText(L10n.getFormattedString("Detail.Latitude",model.latitude));
                else
                    latitudeLabel.setText(L10n.getFormattedString("Detail.Latitude",EMPTY));
                if (model.longitude != null)
                    longitudeLabel.setText(L10n.getFormattedString("Detail.Longitude",model.longitude));
                else
                    longitudeLabel.setText(L10n.getFormattedString("Detail.Longitude",EMPTY));
                if (model.country != null)
                    countryLabel.setText(L10n.getFormattedString("Detail.Country",model.country));
                else
                    countryLabel.setText(L10n.getFormattedString("Detail.Country",EMPTY));
                if (model.quadrant != null)
                    quadrantLabel.setText(L10n.getFormattedString("Detail.Quadrant",model.quadrant));
                else
                    quadrantLabel.setText(L10n.getFormattedString("Detail.Quadrant",EMPTY));
                villageLabel.setText(L10n.getFormattedString("Detail.Village",model.village));
                projectLabel.setText(L10n.getFormattedString("Detail.Project",model.project));
                phytLabel.setText(L10n.getFormattedString("Detail.Phytochorion",model.phytName,model.phytCode));
//                phytCodeLabel.setText(L10n.getFormattedString("Detail.PhytCode",model.phytCode));
//                phytNameLabel.setText(L10n.getFormattedString("Detail.PhytName",model.phytName));
                taxonLabel.setText(L10n.getFormattedString("Detail.Taxon",model.taxon));
                territoryLabel.setText(L10n.getFormattedString("Detail.Territory",model.territory));
                //publication
                
                if (model.detailTableModel != null)
                    authorTable.setModel(model.detailTableModel);
                else {
                    logger.error("Detail: author table model is null.");
                }
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel altitudeLabel;
    private javax.swing.JTable authorTable;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JLabel createdWhenLabel;
    private javax.swing.JLabel createdWhoLabel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel descAreaLabel;
    private javax.swing.JLabel habAreaLabel;
    private javax.swing.JTextArea habNoteArea;
    private javax.swing.JLabel herbariumLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel latitudeLabel;
    private javax.swing.JLabel longitudeLabel;
    protected javax.swing.JButton nextButton;
    private javax.swing.JLabel occAreaLabel;
    private javax.swing.JTextArea occNoteArea;
    private javax.swing.JLabel phytLabel;
    private javax.swing.JTextArea placeDescArea;
    protected javax.swing.JButton prevButton;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JLabel quadrantLabel;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JLabel taxonLabel;
    private javax.swing.JLabel territoryLabel;
    private javax.swing.JLabel updatedWhenLabel;
    private javax.swing.JLabel updatedWhoLabel;
    private javax.swing.JLabel villageLabel;
    // End of variables declaration//GEN-END:variables
    
}
