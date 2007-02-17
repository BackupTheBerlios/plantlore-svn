package net.sf.plantlore.common.exception;

/**
 * An exception thrown every time something during the export
 * went wrong - typically when some variables are not initialized
 * properly and the export cannot start the execution.
 * <br/>
 * This exception is <b>not</b> thrown if anything goes wrong with the
 * output (not enough disk space, insufficient permissions). 
 * 
 * @author kaimu
 * @since 2006-04-29
 *
 */
public class ExportException extends PlantloreException {
	
	private static final long serialVersionUID = 2006060411004L;
	
	public ExportException() { super(); }
	
	public ExportException(String message) { super(message); }
	

}
