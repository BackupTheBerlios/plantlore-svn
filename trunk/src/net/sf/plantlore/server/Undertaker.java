package net.sf.plantlore.server;

import net.sf.plantlore.middleware.DBLayer;

/**
 * The Undertaker can properly bury DBLayers, that were not properly destroyed. 
 *   
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-03-26
 * @version 1.0 final
  */
public interface Undertaker {
	
	/**
	 * Take care of the DBLayer that was not closed properly.
	 * 
	 * @param db		The DBLayer.
	 */
	void bury(DBLayer db);

}
