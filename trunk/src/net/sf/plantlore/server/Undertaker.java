package net.sf.plantlore.server;

import net.sf.plantlore.middleware.DBLayer;

/**
 * 
 *   
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-03-26
  */
public interface Undertaker {
	
	void bury(DBLayer db);

}
