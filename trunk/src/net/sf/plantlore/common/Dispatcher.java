/*
 * Dispatcher.java
 *
 * Created on 3. září 2006, 12:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class Dispatcher {
    private Logger logger = Logger.getLogger(Dispatcher.class.getPackage().getName());
    private boolean taskRunning = false;
    private DefaultProgressBarNew dpb;
    private Task task;
    private static Dispatcher dispatcher = new Dispatcher();
    private static ProgressBarManager pbm;
    
    private Dispatcher() {
    }
 
    public synchronized boolean dispatch(Task task, JFrame parent, boolean stoppable) {
        if (taskRunning) {
            logger.debug("Dispatcher: task already RUNNING, RETURNING.");
            return false;
        }
        
        taskRunning = true;
        this.task = task;
        //dpb = new DefaultProgressBarNew(task, parent, true);

        pbm.initialize();
        pbm.setParent(parent);
        pbm.setTask(task);
        
        task.start();
        
        return true;
    }

    public synchronized boolean dispatch(Task task, JDialog parent, boolean stoppable) {
        if (taskRunning) {
            logger.debug("Dispatcher: task already RUNNING, RETURNING.");
            return false;
        }
        
        taskRunning = true;
        this.task = task;
        //dpb = new DefaultProgressBarNew(task, parent, true);

        pbm.initialize();
        pbm.setParent(parent);
        pbm.setTask(task);
        
        task.start();
        
        return true;
    }

    /** Tells the dispatcher that the progressbar and task finished.
     *
     * This tells the dispatcher that it is safe to dispatch another task.
     */
    public synchronized void finished() {
        taskRunning = false;
        pbm.removeTask();
        pbm.setParent((JDialog)null);
/*        do{
        	try{ Thread.sleep(50); } catch(Exception e) {}
        } while( dpb.isShowing() ); */
        logger.debug("Dispatcher: "+task+" finished, free to dispatch another.");
    }    
    
    public static Dispatcher getDispatcher() {
        if (pbm == null) //FIXME: think of a better kind of exception
            throw new IllegalArgumentException("You have to call Dispatcher.initialize() first!");
        
        return dispatcher;
    }
    
    public static void initialize(JProgressBar pb) {
        if (pb == null) {
            throw new IllegalArgumentException("Dispatcher can't be initialized with null progress bar!");
        }
        pbm = new ProgressBarManager(pb);
    }
}

