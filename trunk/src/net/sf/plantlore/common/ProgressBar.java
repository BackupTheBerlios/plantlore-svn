/*
 * ProgressBar.java
 *
 * Created on 28. květen 2006, 23:31
 */

package net.sf.plantlore.common;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

/** ProgressBar dialog that works as an observer of a given Task.
 *
 * The dialog sets itself visible after it receives a STARTING Message, disposes
 * itself after getting a STOPPED Message and in the time between it updates it's
 * state according to POSITION_CHANGED, MESSAGE_CHANGED and LENGHT_CHANGED messages.
 *
 * If constructed with an indeterminate Task works in indeterminate mode until it
 * receives a LENGHT_CHANGED message from the Task. Then it switches to determinate mode.
 *
 * Exceptions trhown by the task are processed by the abstract exceptionHandler() method.
 *
 * @author  fraktalek
 */
public abstract class ProgressBar extends javax.swing.JDialog implements Observer {
    private Task task;
    private int statusFieldWidth;
    private double charSizeApprox = 180/27; //in 180 pixel wide JTextField first 27 characters are visible in Matisse
    
    /** Creates a new progress bar, initially invisible. It becomes visible after it receives
     * a STARTING Message from the Task.
     *
     * @param task the task to be monitored
     * @param parent the parent frame of this dialog
     * @modal whether to open in modal mode
     *
     */
    public ProgressBar(Task task, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.task = task;
        
        initComponents();
        
        if (task.isDeterminate()) {
            progressBar.setIndeterminate(false);
            progressBar.setMinimum(0);
            progressBar.setMaximum(task.getLength());
        } else {
            progressBar.setIndeterminate(true);
        }
        statusFieldWidth = statusField.getWidth();
        statusField.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
       
        task.addObserver(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        statusField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEtchedBorder()));

        statusField.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        statusField.setBorder(null);
        statusField.setDisabledTextColor(javax.swing.UIManager.getDefaults().getColor("Button.foreground"));
        statusField.setEnabled(false);
        statusField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusFieldActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, statusField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void statusFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_statusFieldActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new ProgressBar(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    /** Updates the progress bar according to messages from the task.
     *
     * Adjusts widht of the dialog in case it receives a longer text message
     * form the task than it is possible to display in current widht.
     *
     * Invokes exceptionHanlder on received exceptions.
     */
    public void update(Observable o, Object arg) {
        if (arg instanceof Task.Message) {
            Task.Message msg = (Task.Message)arg;
            switch (msg) {
                case STARTING:
                    if (isModal()) {
                        //we are in modal state so we have to show ourselves in a new thread
                        //so that we don't block the task's thread from continuing it's computation
                        Thread t = new Thread(new Runnable(){
                                                    public void run() { setVisible(true); }
                                              });
                        // Start the thread so that the dialog will show.
                        t.start(); 
                    } else {
                        setVisible(true);
                    }
                    break;
                case POSITION_CHANGED:
                    progressBar.setValue(task.getPosition());
                    break;
                case MESSAGE_CHANGED:
                    String text = task.getStatusMessage();
                    statusField.setText(text);
                    progressBar.setString(text);
                    int curWidth = statusField.getWidth();
                    if (text.length() > (curWidth/charSizeApprox)) {
                        int newWidth = new Double(text.length()*charSizeApprox).intValue();
                        //statusField.setSize(newWidth,statusField.getHeight());
                        this.setSize(this.getWidth()+(newWidth-curWidth),this.getHeight());
                        System.out.println("changed field width by "+(newWidth-curWidth));
                    }
                    break;
                case LENGTH_CHANGED:
                    if (progressBar.isIndeterminate()) {
                        progressBar.setIndeterminate(false);
                    }
                    progressBar.setMaximum(task.getLength());
                    break;
                case STOPPED:
                    afterStopped();
                    setVisible(false);
                    dispose();
                    break;                    
            }
        }
        
        if (arg instanceof Exception) {
            exceptionHandler((Exception)arg);
        }
    }
    
    /** This method receives exceptions thrown by the task.
     * 
     * You have to call task.proceed() if you want the task to continue it's 
     * computation.
     */
    public abstract void exceptionHandler(Exception ex);
    
    
    public void afterStopped() {
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField statusField;
    // End of variables declaration//GEN-END:variables
    
}
