/*
 * ProgressBar.java
 *
 * Created on 28. květen 2006, 23:31
 */

package net.sf.plantlore.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Window;
import java.util.Observable;
import java.util.Observer;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

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
@Deprecated
public abstract class ProgressBarNew extends javax.swing.JDialog implements Observer {
    protected Logger logger;
    protected Task task;
    private int statusFieldWidth;
    private double charSizeApprox = 180/27; //in 180 pixel wide JTextField first 27 characters are visible in Matisse
    protected Window parent;
    
    private boolean ended = false;
    
    /** Creates a new progress bar, initially invisible. It becomes visible after it receives
     * a STARTING Message from the Task.
     *
     * @param task the task to be monitored
     * @param parent the parent frame of this dialog
     * @modal whether to open in modal mode
     *
     */
    public ProgressBarNew(Task task, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        this.parent = parent;
        this.task = task;
        initialize();
    }
    
    public ProgressBarNew(Task task, javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);

        this.parent = parent;
        this.task = task;        
        initialize();        
    }    
   
    
    protected void initialize() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
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
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task.addObserver(this);    
        setLocationRelativeTo(parent);
    }
    
    /** Makes the ProgressBar visible, must be called from EDT.
     *
     */
    private synchronized void start() {
        if (ended)//we probably received message.STOPPING earlier than message.STARTING => do not begin again
            return;
        setVisible(true);
        logger.debug(""+this+" VISIBLE");
    }
    
    /** Makes the ProgressBar invisible and disposes it.
     * Must be called from EDT!
     *
     */
    private synchronized void stop() {
        ended = true;
        setVisible(false);
        dispose(); // FIXME: Should not this method be invoked from the EDT?                    
        parent.setCursor(Cursor.getDefaultCursor());
        logger.debug(""+this+" STOPPING " + task );
        afterStopping();        
        
    }
    
    /** Changes the ProgressBar's state.
     * Must be called from EDT!
     */
    private synchronized void changePosition(int position) {
        if (ended) // we have already received a message that the task ended, do nothing
            return;
        
        progressBar.setValue(position);
    }
    
    /** Changes the ProgressBar's message.
     * Must be called from EDT!
     */
    private synchronized void changeMessage(String message) {
        if (ended) // we have already received a message that the task ended, do nothing
            return;
        
        if (message == null) //just to be sure
            message = "";
        
        statusField.setText(message);
        progressBar.setString(message);
        int curWidth = statusField.getWidth();
        if (message.length() > (curWidth/charSizeApprox)) {
            int newWidth = new Double(message.length()*charSizeApprox).intValue();
            setSize(getWidth()+(newWidth-curWidth),getHeight());
        }        
    }
    
    /** Changes representation of the task length.
     * Must be called from EDT!
     */
    private synchronized void changeLength(int length) {
        if (ended) // we have already received a message that the task ended, do nothing
            return;        
        
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }
        progressBar.setMaximum(length);
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
        setUndecorated(true);
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEtchedBorder()));
        progressBar.setForeground(java.awt.Color.green);

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
     * Will set WAIT_CURSOR to it's parent and will restore it to defaultCursor
     * after it disappears.
     *
     * Invokes exceptionHanlder on received exceptions.
     */
    public void update(final Observable o, final Object arg) {
        final ProgressBarNew pb = this;
        if (arg instanceof Pair) {
            Pair p = (Pair)arg;
            Object first = p.getFirst();
            if (first instanceof Task.Message) {
                Task.Message msg = (Task.Message)first;
                Object value = p.getSecond();
                logger.debug(""+this+" received message: "+msg);
                switch (msg) {
                    case STARTING:
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                start();
                            }
                        });
                        break;
                    case POSITION_CHANGED:
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                changePosition(task.getPosition());
                            }
                        });
                        break;
                    case MESSAGE_CHANGED:
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                changeMessage(task.getStatusMessage());
                            }
                        });
                        break;
                    case LENGTH_CHANGED:
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                changeLength(task.getLength());
                            }
                        });
                        break;
                    case STOPPING:
                    	try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                stop();
                            }
                        });
                    	}catch(Exception e) {}
                        Dispatcher.getDispatcher().finished();
                        break;
                    case STOPPED:
                        afterStopped(value);
                        break;                    
                }//switch
            }//instanceof Message

        }//instanceof Pair
        
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
    
    public Task getTask() {
        return task;
    }
    
    public void afterStopped(Object value) {
    	// Empty implementation = do nothing.        
    }
    
    /** Be careful, will run in EDT! (if ProgressBar used properly)
     *
     */
    public void afterStopping() {
    	
    }

    public String toString() {
        return "ProgressBarNew("+this.hashCode()+")";
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    protected javax.swing.JProgressBar progressBar;
    protected javax.swing.JTextField statusField;
    // End of variables declaration//GEN-END:variables
    
}
