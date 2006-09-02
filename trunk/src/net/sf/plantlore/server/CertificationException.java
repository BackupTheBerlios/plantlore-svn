package net.sf.plantlore.server;

/**
 * The Certification exception states that the access to the Administration
 * of the Server was denied, usually due to the incorrect authorization key.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-30
 *
 */
public class CertificationException extends Exception {
	
	private static final long serialVersionUID = 2006060411064L;
	
	public CertificationException() { super(); }
	
	public CertificationException(String msg) { super(msg); }
	

}
