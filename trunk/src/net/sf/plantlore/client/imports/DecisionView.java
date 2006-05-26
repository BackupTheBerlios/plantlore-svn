/*
 * DecisionView.java
 *
 * Created on 9. květen 2006, 22:21
 */

package net.sf.plantlore.client.imports;

import java.util.Observable;
import java.util.Observer;

import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  yaa
 */
public class DecisionView extends javax.swing.JFrame implements Observer {
	
	ImportMng model;

	
    /** Creates new form DecisionView */
    public DecisionView(ImportMng model) {
    	this.model = model;
    	
        initComponents();
        
        setLocationRelativeTo(null); // center of the screen
        this.model.addObserver(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        question = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recordView = new javax.swing.JTable();
        leave = new javax.swing.JButton();
        update = new javax.swing.JButton();
        remember = new javax.swing.JCheckBox();

        setTitle(L10n.getString("Import.DecisionExpected"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        question.setText("It does not do to dwell on dreams and forget to live.");

        recordView.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(recordView);

        leave.setText("Leave");

        update.setText("Update");

        remember.setText(L10n.getString("Import.RememberDecision"));
        remember.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        remember.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(question, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(remember)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 23, Short.MAX_VALUE)
                        .add(update)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(leave)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {leave, update}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(question)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(leave)
                    .add(update)
                    .add(remember))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JButton leave;
    protected javax.swing.JLabel question;
    protected javax.swing.JTable recordView;
    protected javax.swing.JCheckBox remember;
    protected javax.swing.JButton update;
    // End of variables declaration//GEN-END:variables

    
    
//    private boolean timeIssuesResolved = false, sharedIssuesResolved = false;

    
	public void update(Observable source, Object parameter) {
		if(parameter instanceof Record)
			// The record in the database is newer than the one in the file.
			if(parameter instanceof Occurrence) {
				question.setText(L10n.getString("Question.NewerRecord"));
				recordView.setModel( model.getProcessedRecords() );
				leave.setText(L10n.getString("Import.Skip"));
				update.setText(L10n.getString("Import.Replace"));
				remember.setSelected(false);
				setVisible(true);
			}
			// There is a shared record in the database that is to be updated.
			else {
				question.setText(L10n.getString("Question.SharedRecord"));
				recordView.setModel( model.getProblematicRecord() );
				leave.setText(L10n.getString("Import.Insert"));
				update.setText(L10n.getString("Import.Update"));
				remember.setSelected(false);
				setVisible(true);
			}
	}
    
}
