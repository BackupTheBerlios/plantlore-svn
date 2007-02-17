package net.sf.plantlore.common.exception;

import java.io.Serializable;

/**
 * A common ancestor of all exceptions thrown by Plantlore.
 * The ancestor is Serializable so as to be able to travel
 * through the RMI framework.
 * 
 * @author kaimu
 * @since 2006-04-29
 *
 */
public class PlantloreException extends Exception implements Serializable {
	
	private static final long serialVersionUID = 2006060411032L;

	public PlantloreException() { super(); }

	public PlantloreException(String message) { super(message); }
	
	public PlantloreException(Throwable exception) { super(exception); }
	
	public PlantloreException(String message, Throwable exception) { super(message, exception); }
}
