/*
 * ProgressBarApp.java
 *
 * Created on 3. září 2006, 22:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import java.awt.Cursor;
import java.awt.Window;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class ProgressBarManager implements Observer {
    private Logger logger = Logger.getLogger(ProgressBarManager.class.getPackage().getName());
    private boolean ended = false;
    private JProgressBar progressBar;
    private Window parent;
    private Task task;
    
    /** Creates a new instance of ProgressBarApp */
    public ProgressBarManager(JProgressBar progressBar) {
        this.progressBar = progressBar;
        progressBar.setString("");
        progressBar.setStringPainted(true);
    }

    public void setParent(JDialog parent) {
        this.parent = parent;       
    }

    public void setParent(JFrame parent) {
        this.parent = parent;       
    }

    public synchronized void initialize() {
        progressBar.setString("");
        ended = false;
    }
    
    public synchronized void setTask(Task task) {
        this.task = task;
        task.addObserver(this);
    }
    
    public synchronized void removeTask() {
        task.deleteObserver(this);
        task = null;
    }
    
    /** Makes the ProgressBar visible, must be called from EDT.
     *
     */
    private synchronized void start() {
        if (ended)//we probably received message.STOPPING earlier than message.STARTING => do not begin again
            return;

        if (task.isDeterminate()) {
            progressBar.setIndeterminate(false);
            progressBar.setMinimum(0);
            progressBar.setMaximum(task.getLength());
        } else {
            progressBar.setIndeterminate(true);
        }
        
        if (parent != null)
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        task.addObserver(this);    
        
        progressBar.setString("");
        progressBar.setVisible(true);
        logger.debug(""+this+" VISIBLE");
    }
    
    /** Makes the ProgressBar invisible and disposes it.
     * Must be called from EDT!
     *
     */
    private synchronized void stop() {
        ended = true;
        progressBar.setVisible(false);
        if (parent != null)
            parent.setCursor(Cursor.getDefaultCursor());
        logger.debug(""+this+" STOPPING " + task );
        //!!! afterStopping();        
        Dispatcher.getDispatcher().finished();
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
        
        progressBar.setString(message);
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
    
    public void update(Observable o, Object arg) {
        final ProgressBarManager pb = this;
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
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                stop();
                            }
                        });
                        break;
                    case STOPPED:
                        //!!! afterStopped(value);
                        break;                    
                }//switch
            }//instanceof Message

        }//instanceof Pair
        
        if (arg instanceof Exception) {
            task.stop();
            DefaultExceptionHandler.handle(parent, (Exception) arg, L10n.getString("Error.General"));
        }        
    }
    
    public String toString() {
        return "ProgressBarManager ";
    }
}
