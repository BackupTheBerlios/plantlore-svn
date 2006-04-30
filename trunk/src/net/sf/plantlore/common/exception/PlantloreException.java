package net.sf.plantlore.common.exception;

import java.io.Serializable;

/**
 * A common ancestor of all exceptions thrown by Plantlore.
 * The ancestor is Serializable so as to be able to travel
 * through the RMI framework.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29
 *
 */
public class PlantloreException extends Exception implements Serializable {

	public PlantloreException() { super(); }

	public PlantloreException(String message) { super(message); }
}
