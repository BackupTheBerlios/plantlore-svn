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
    
    private Dispatcher() {
    }
 
    public synchronized boolean dispatch(Task task, JFrame parent, boolean stoppable) {
        if (taskRunning) {
            System.out.println("Dispatcher: task already RUNNING, RETURNING.");
            return false;
        }
        
        taskRunning = true;
        this.task = task;
        dpb = new DefaultProgressBarNew(task, parent, true);
        
        task.start();
        
        return true;
    }

    /** Tells the dispatcher that the progressbar and task finished.
     *
     * This tells the dispatcher that it is safe to dispatch another task.
     */
    public synchronized void finished() {
        taskRunning = false;
        task.deleteObservers();
        logger.debug("Dispatcher: "+task+" finished, free to dispatch another.");
    }    
    
    public static Dispatcher getDispatcher() {
        return dispatcher;
    }
}

