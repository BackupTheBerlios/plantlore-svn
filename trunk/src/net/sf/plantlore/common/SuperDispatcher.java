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
 * @author kaimu
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
    	public Exception problem;
    	
    	public TaskInfo(Task task) {
    		this(task, true);
    	}
    	
    	public TaskInfo(Task task, boolean silent) {
    		this.task = task;
    		this.silent = silent;
    		followingTask = null;
    		result = null;
    		status = TaskStatus.Waiting;
    		problem = null;
    	}
    	
    	@Override
    	public String toString() {
    		return "<" + task.toString() + "[" + status + "]>" + (followingTask != null ? followingTask.toString() : "");
    	}
    }

    // Nobody but me will be able to have an instance of this class.
    private SuperDispatcher() { }
    
    
    
    private void dispatch(TaskInfo next) {
    	current = next;
    	if(current != null) {
    		logger.info("Dispatching task " + current + "...");
    		current.status = TaskStatus.Working;
			current.task.addObserver(this);
			current.task.start();
    	}
    }
    
    
    private void tryToDispatchNextTask() {
    	if( current == null ) 
    		dispatch( taskQueue.poll() ); // Dispatch the next independent task.
    	else {
    		// current != null
    		switch( current.status ) {
    		case Cancelled:
    		case Crashed:
    			dispatch( taskQueue.poll() ); // Dispatch the next independent task - the multitask has crashed!
    			break;
    		case Completed:
    			// Continue with the multitask (if possible) or dispatch the next independent task.
    			dispatch( current.followingTask != null ? current.followingTask : taskQueue.poll() );
    			break;
    		}
    	}
    }
    

    private synchronized void enqueueNewMultitask(Task...tasks) {
    	TaskInfo previous = null;
    	for( Task t : tasks ) {
    		TaskInfo info = new TaskInfo(t);
    		if(previous != null)
    			previous.followingTask = info; // Link the tasks of the multitask.
    		else
    			taskQueue.offer(info); // Enqueue the first task of the multitask into the Task-Queue
    		previous = info;
    	}
    	logger.debug("New multitask enqueued: " + taskQueue.peek());
    	tryToDispatchNextTask();
    }
    
    
    private synchronized void enqueueIndependentTasks(Task...tasks) {
    	for( Task t : tasks ) {
    		TaskInfo info;
    		taskQueue.offer( info = new TaskInfo(t) );
    		logger.debug("New task enqueued: " + info);
    	}
    	tryToDispatchNextTask();
    }
    
    
    private void undispatch(TaskStatus status) {
    	undispatch(status, null);
    }
    
    private void undispatch(TaskStatus status, Exception problem) {
    	if( status == TaskStatus.Crashed || status == TaskStatus.Cancelled ) {
    		for( TaskInfo info = current.followingTask; info != null; info.status = TaskStatus.Cancelled, info = info.followingTask );
    		current.problem = problem;
    	}
    	else if( status == TaskStatus.Completed )
    		current.result = current.task.getValue();
    	
    	current.status = status;
		current.task.deleteObserver(this);
		
		if(problem != null) {
			logger.error("SuperDispatcher: " + current + " crashed. " + problem.getMessage());
			logger.error("SuperDispatcher: LocalizedMsg = \"" + DefaultExceptionHandler.problemDescription(problem) + "\"");
		}
		else
			logger.debug("SuperDispatcher: " + current + " completed. Result = " + current.result);
		
		tryToDispatchNextTask();
    }
    
    
    // Nobody but this class will be able to call this method - because nobody else can have an instance of this class.
    // Everyone will see only the static methods.
	public synchronized void update(Observable task, Object msg) {
		// The current task has crashed!
		if( msg instanceof Exception )
			undispatch(TaskStatus.Crashed, (Exception)msg);
		// The current task has terminated successfully.
		if( msg instanceof Pair) {
	          Object command = ((Pair)msg).getFirst();
	          if( command instanceof Task.Message &&  (Task.Message)command == Task.Message.STOPPING )
	        	  undispatch(TaskStatus.Completed);
		}

	}

	
	
	
    
    
    public static void enqueueMultitask(Task...tasks) {
    	dispatcher.enqueueNewMultitask(tasks);
    }
    
    
    public static void enqueue(Task...tasks) {
    	dispatcher.enqueueIndependentTasks(tasks);
    }

    
}
