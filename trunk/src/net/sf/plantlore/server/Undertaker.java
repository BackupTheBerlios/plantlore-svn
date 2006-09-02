package net.sf.plantlore.server;

import net.sf.plantlore.middleware.DBLayer;

/**
 * The Undertaker can destroy stranded DBLayers properly.
 * A Database Layer is considered stranded if the client, for whom it was created,
 * crashes or the network connection is lost. 
 *   
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-03-26
 * @version 1.0 
  */
public interface Undertaker {
	
	/**
	 * Take care of the DBLayer that was not closed properly.
	 * 
	 * @param db		The DBLayer.
	 */
	void bury(DBLayer db);

}
