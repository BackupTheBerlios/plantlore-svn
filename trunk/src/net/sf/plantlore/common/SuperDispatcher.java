/**
 * 
 */
package net.sf.plantlore.common;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import org.apache.log4j.Logger;

/**
 * @author yaa
 *
 */
public class SuperDispatcher implements Observer {
	
	
    private Logger logger = Logger.getLogger(SuperDispatcher.class.getPackage().getName());
    private TaskInfo current = null;
    private Queue<TaskInfo> taskQueue = new LinkedList<TaskInfo>();
    
    
    private static SuperDispatcher dispatcher = new SuperDispatcher();
    
    public enum TaskStatus { Waiting, Working, Completed, Cancelled, Crashed };
    
    
    private class TaskInfo {
    	public Task task;
    	public boolean silent;
    	public TaskInfo followingTask;
    	public Object result;
    	public TaskStatus status;
    	
    	public TaskInfo(Task task, boolean silent) {
    		this.task = task;
    		this.silent = silent;
    		followingTask = null;
    		result = null;
    		status = TaskStatus.Waiting;
    	}
    }

    
    private SuperDispatcher() { }
    
    
    
    private void dispatch(TaskInfo next) {
    	current = next;
    	if(current != null) {
    		logger.info("Dispatching task " + current.task);
    		current.status = TaskStatus.Working;
			current.task.addObserver(this);
			current.task.start();
    	}
    }
    
    private void dispatchNext() {
    	if(current != null && current.followingTask != null)
    		dispatch(current.followingTask);
    	else
    		dispatch( taskQueue.poll() );
    }
    
    
    private void tryToDispatchNextTask() {
    	if( current == null ) 
    		dispatchNext();
    	else {
    		switch( current.status ) {
    		case Cancelled:
    		case Crashed:
    		case Completed:
    			dispatchNext();
    			break;
    		}
    	}
    }
    

    private synchronized void enqueueNewMultitask(Task...tasks) {
    	TaskInfo previous = null;
    	for( Task t : tasks ) {
    		TaskInfo info = new TaskInfo(t, true);
    		if(previous != null)
    			previous.followingTask = info;
    		previous = info;
    		taskQueue.offer(info);
    	}
    	logger.info("New multitask enqueued.");
    	tryToDispatchNextTask();
    }
    
    
    private synchronized void enqueueIndependentTasks(Task...tasks) {
    	for( Task t : tasks )
    		taskQueue.offer( new TaskInfo(t, true) );
    	logger.info("New tasks enqueued.");
    	tryToDispatchNextTask();
    }
    
    
    
	public synchronized void update(Observable task, Object msg) {
		// The current task has crashed!
		if( msg instanceof Exception ) {
			current.status = TaskStatus.Crashed;
			for( TaskInfo info = current.followingTask; info != null; info.status = TaskStatus.Cancelled, info = info.followingTask );
			current.task.deleteObserver(this);
			logger.info("Task " + current.task + " crashed! " + msg);
			tryToDispatchNextTask();
		}
		// The current task has terminated successfully.
		if( msg instanceof Pair) {
	          Pair p = (Pair)msg;
	          Object command = p.getFirst();
	          if( command instanceof Task.Message &&  (Task.Message)command == Task.Message.STOPPING ) {
	        	  current.task.deleteObserver(this);
	        	  current.status = TaskStatus.Completed;
	        	  current.result = current.task.getValue();
	        	  logger.info("Task " + current.task + " completed! Returned value is " + current.result);
	        	  tryToDispatchNextTask();
	          }
		}

	}

	
	
	
    
    
    public static void enqueueMultitask(Task...tasks) {
    	dispatcher.enqueueNewMultitask(tasks);
    }
    
    
    public static void enqueue(Task...tasks) {
    	dispatcher.enqueueIndependentTasks(tasks);
    }

    
}
