/*
 * Task.java
 *
 * Created on 28. květen 2006, 22:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import java.util.Observable;

/** A convenience class for long task implementation.
 *
 * The task() method is run in a new thread and observers are notified about
 * changes of this object's state.
 *
 * @author fraktalek
 */
public abstract class Task extends Observable {
    private SwingWorker worker;    
    //private Object value;
    public static enum Message {POSITION_CHANGED, MESSAGE_CHANGED, STOPPING, STOPPED, STARTING, STARTED, PROCEEDING, LENGTH_CHANGED};
    private int length = 0;
    private int position = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statusMessage = "";
    private boolean determinate;
    private PostTaskAction action;
    private String name;
    
    /** Creates new Task in determinate mode.
     */
    public Task(int length) {
        this.length = length;   
        determinate = true;
    }    
    
    /** Creates a new task in indereminate mode.
     *
     */
    public Task() {
        determinate = false;
    }
    
    /**
     * Create a new task with a name. This can greatly simplify identification of tasks.
     * 
     * @param name	The name or description of the task.
     */
    public Task(String name) {
    	this.name = name;
    }
    
    public void setPostTaskAction(PostTaskAction action) {
        this.action = action;
    }
    
    /** Returns length of this task.
     *
     */
    public int getLength() {
        return length;
    }
    
    /** Sets the length of this task and notifies observers about this change.
     *
     */
    public void setLength(int length) {
        this.length = length;
        determinate = false;
        setChanged();
        notifyObservers(new Pair<Message,Object>(Message.LENGTH_CHANGED, null));
    }
    
    /** Returns current state of the task's computation.
     *
     */
    public int getPosition() {
        return position;
    }
    
    /** Sets the position and informs observers about this change.
     *
     */
    public void setPosition(int pos) {
        this.position = pos;
        if (position > length) { 
            done = true;
            position = length;
        }
        
        setChanged();
        notifyObservers(new Pair<Message,Object>(Message.POSITION_CHANGED,null));
    }
    
    /** Sets the status message and informs observers about this change.
     *
     */
    public void setStatusMessage(String message) {
        this.statusMessage = message;
        setChanged();
        notifyObservers(new Pair<Message,Object>(Message.MESSAGE_CHANGED,null));
    }
    
    /** Informs whether this task is determinate, which means that the tak's length is known.
     * Either it was constructed using the Task(int length) constructor or there was a call
     * to setLength() method.
     *
     * @return true if it is determinate
     * @return false otherwise
     */
    public boolean isDeterminate() {
        return determinate;
    }
    
    /** Informs whether the task is completed.
     *
     * @return true if completed
     * @return false otherwise
     *
     */
    public boolean isDone() {
        return done;
    }
    
    /** Informs about the task's cancellation.
     *
     * @return true if the task was canceled by a call to stop()
     * @return false otherwise
     *
     */
    public boolean isCanceled() {
        return canceled;
    }
    
    /** Returns the current status message.
     *
     */
    public String getStatusMessage() {
        return statusMessage;
    }
    
    /** Returns the value constructed by the task in case it has finished already
     * or returns null otherwise.
     *<br/>
     * If you have to wait for the moment the task stops, use the <code>get()</code> method instead.
     * @see #get()
     */
    public Object getValue() {
        if (worker == null)
            return null;
        
        return worker.getValue();
    }
    
//    /** 
//     * Returns the value returned by the task. 
//     * If the task did not finish its computations yet,
//     * this method waits until the result is available.
//     */
//    public Object get() {
//    	if(worker != null)
//    		return worker.get();
//    	return null;
//    }
    
    /** Abstract method that does the actual computation.
     *
     * May throw an Exception. Observers are then notified about that and receive
     * the Exception in argument.
     */
    public abstract Object task() throws Exception;
    
    /** Starts the task.
     *
     * The task() method is called using a new SwingWorker() and observers are notified
     * that the task is about to begin.
     * Consequent calls to this method after interruption by an exception may clear the
     * internal state of this object.
     *
     * In case the task throws an exception, observers are notified about it and receive
     * the exception as an argument.
     *
     */
    public void start() {
    	
    	// In case the task is to be started again. 
    	done = canceled = false;
    	
        worker = new SwingWorker() {
            public Object construct() {
                Object value = null;
                
                try {
                    setChanged();
                    notifyObservers(new Pair<Message,Object>(Message.STARTING,null));
                    value = task();
                    setChanged();
                    notifyObservers(new Pair<Message,Object>(Message.STOPPING,null));
                    if (action != null) {
                        action.afterStopped(value);
                    }
                } catch (Exception ex) {
                    setChanged();
                    notifyObservers(ex);
                }
                return value;
            }
        };
        
        worker.start();
    }

    /** DON'T USE! NOT WELL TESTED!! + doesn't support afterStopping(), etc. 
     * 
     * Continues processing of the task.
     *
     * This method is supposed to be called after the task was interrupted by an exception.
     * Call to this method is almost the same as to start() but Observers are notified that
     * the task is about to continue it's computation and this object's internal state is preserved.
     * 
     *
     */
    @Deprecated
    public void proceed() {
        worker = new SwingWorker() {
            public Object construct() {
                Object value = null;
                
                try {
                    setChanged();
                    notifyObservers(new Pair<Message,Object>(Message.PROCEEDING,null));
                    value = task();
                } catch (Exception ex) {
                    setChanged();
                    notifyObservers(ex);
                }
                return value;
            }
        };
        
        worker.start();        
    }
    
    /** Informs observers that the computation stopped.
     *
     */
    public void fireStopped(Object value) {
        setChanged();
        notifyObservers(new Pair<Message,Object>(Message.STOPPED,value));
    }
    
    /** Informs observers that the computation has started (and is no longer beginning).
     *
     */
    public void fireStarted() {
        setChanged();
        notifyObservers(new Pair<Message,Object>(Message.STARTED,null));
    }
    
    /** Stops (cancels) the task.
     *
     * To be more precise, consequent calls to isCanceled() will return true.
     * Observers are notified that the task is about to stop.
     */
    public void stop() {
        canceled = true;
        setChanged();
        notifyObservers(new Pair<Message,Object>(Message.STOPPING,null));
    }
    
    /**
     * Forcibly stops (cancels) the task.
     * <br/>
     * This call will interrupt any blocking IO operation, wait(), sleep(), and join() 
     * that might be blocking the current task. 
     * This should stop the task immediately. 
     */
    public void kill() {
    	canceled = true;
    	
    	if(worker != null)
    		worker.interrupt();
    	
    	setChanged();
        notifyObservers(new Pair<Message,Object>(Message.STOPPING,null));
    }
    
    public String toString() {
    	if(name == null)
    		return "Task("+this.hashCode()+")";
    	return name;
    }
    
}


