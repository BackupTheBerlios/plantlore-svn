package net.sf.plantlore.server;

/**
 * The Certification exception is thrown if the access to the Administration
 * of the Server was denied, usually due to the incorrect authorization key.
 * 
 * @author kaimu
 * @since 2006-05-30
 *
 */
public class CertificationException extends Exception {
	
	private static final long serialVersionUID = 2006060411064L;

	/**
	 * @param msg	The message describing the reason why the Authorization
	 * failed.
	 */
	public CertificationException(String msg) { super(msg); }
	

}
