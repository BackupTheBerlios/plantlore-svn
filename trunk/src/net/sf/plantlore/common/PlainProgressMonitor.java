/**
 * 
 */
package net.sf.plantlore.common;

import java.util.Observable;
import java.util.Observer;

/**
 * The Plain Progress Monitor watches all supplied tasks and prints the messages they are producing to the standard output.
 * This may be handy if Plantlore is supposed to run without GUI - the User might find it inconvenient to scan the log
 * for possible errors, that's why the Plain Progress Monitor mirrors all messages to the STDOUT.
 * 
 * Registered tasks (i.e. those that should be monitored) are unregistered automatically when the task stops
 * (either because it has crashed or terminated normally).
 * 
 * @author Erik Kratochvíl
 *
 */
public class PlainProgressMonitor {

	/*
	 * Why do I have an inner class that implements the Observer interface 
	 * when the PlainProgressMonitor itself could implement it?
	 * 
	 * The answer is simple: Objects using PlainProgressMonitor do not see the update() method
	 * (and thus they cannot call it). This way the interface of the PlainProgressMonitor remains clean -
	 * everybody can see only the two public synchronized methods.
	 */
	private class Announcer implements Observer {
		
		public synchronized void update(Observable source, Object parameter) {
			if(source instanceof Task) {
				Task t = (Task) source;
				if( parameter instanceof Exception ) {
					System.out.println( DefaultExceptionHandler.problemDescription((Exception)parameter) );
					unregisterTask( t );
				} 
				else if( parameter instanceof Pair ) {
					Pair p = (Pair)parameter;
					Object first = p.getFirst();
					
					if( first instanceof Task.Message ) {
						Task.Message msg = (Task.Message) first;
						if( msg == Task.Message.MESSAGE_CHANGED )
							System.out.println( t.getStatusMessage() );
						else if( msg == Task.Message.STOPPING )
							unregisterTask( t );
					}
				}
			}
		}	
		
	}
	
	private Announcer a = new Announcer();
	
	/**
	 * Monitor the supplied tasks and mirror the messages they produce to the STDOUT.
	 * Tasks are unregistered automatically.
	 * 
	 * @param tasks	The tasks to be monitored.
	 * @return	itself
	 */
	public synchronized PlainProgressMonitor registerTask(Task...tasks) {
		for( Task t : tasks)
			t.addObserver(a);
		return this;
	}

	/**
	 * Unregister previously registered tasks.
	 * 
	 * @param tasks	Tasks that should no longer be monitored by this monitor.
	 */
	public synchronized void unregisterTask(Task...tasks) {
		for( Task t : tasks )
			t.deleteObserver(a);
	}
	
	

}
