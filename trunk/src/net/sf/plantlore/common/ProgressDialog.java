/*
 * ProgressDialog.java
 *
 * Created on 30. leden 2006, 0:54
 *
 */

package net.sf.plantlore.common;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 *
 * @author Tomas Kovarik
 */
public class ProgressDialog {
    
    private JDialog progress;
    private JProgressBar progressBar;
    private Container container;    
    
    /** Creates a new instance of ProgressDialog */
    public ProgressDialog(JDialog owner, boolean indeterminate) {
        progress = new JDialog(owner, "Operation in progress...", true);        
        initComponents(indeterminate, 100);
    }

    /** Creates a new instance of ProgressDialog */
    public ProgressDialog(JDialog owner, boolean indeterminate, int maximum) {
        progress = new JDialog(owner, "Operation in progress...", true);        
        initComponents(indeterminate, maximum);
    }

    
    /** Creates a new instance of ProgressDialog */
    public ProgressDialog(JFrame owner, boolean indeterminate) {
        progress = new JDialog(owner, "Operation in progress...", true);
        initComponents(indeterminate, 100);
    }

    /** Creates a new instance of ProgressDialog */
    public ProgressDialog(JFrame owner, boolean indeterminate, int maximum) {
        progress = new JDialog(owner, "Operation in progress...", true);
        initComponents(indeterminate, maximum);
    }
    
    
    private void initComponents(boolean indeterminate, int maximum) {
        GridBagConstraints gridBagConstraints;
                
        progress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progress.setSize(320,40);                
        
        container = progress.getContentPane();
        container.setLayout(new GridBagLayout());        
        
        // Add progress bar to the dialog
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(300,20));
        progressBar.setMaximum(maximum);
        progressBar.setIndeterminate(indeterminate);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);                
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;        
        container.add(progressBar, gridBagConstraints);        
    }
    
    public void show() {
        progress.pack();
        progress.setVisible(true);
    }
    
    public void close() {
        progress.dispose();
    }
    
}
