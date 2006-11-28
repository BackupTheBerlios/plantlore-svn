/**
 * 
 */
package net.sf.plantlore.common;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
 * @author yaa
 *
 */
public class PlainProgressMonitor implements Observer {
	
	private Logger logger;

	
	/**
	 * 
	 */
	public PlainProgressMonitor(Logger logger) {
		this.logger = logger;
	}
	
	public synchronized PlainProgressMonitor registerTask(Task...tasks) {
		for( Task t : tasks)
			t.addObserver(this);
		return this;
	}

	public synchronized void unregisterTask(Task...tasks) {
		for( Task t : tasks )
			t.deleteObserver(this);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable source, Object parameter) {
		if( parameter instanceof Exception ) {
			logger.error("Task " + source + " failed! " + ((Exception)parameter).getMessage());
			System.out.println(((Exception)parameter).getMessage());
		} else if( parameter instanceof Pair ) {
            Pair p = (Pair)parameter;
            Object first = p.getFirst();
            if (first instanceof Task.Message && ((Task.Message)first) == Task.Message.MESSAGE_CHANGED)
                System.out.println( p.getSecond() );
		}
	}

}
