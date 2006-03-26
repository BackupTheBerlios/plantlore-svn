package net.sf.plantlore.server;

import net.sf.plantlore.middleware.DBLayer;

public interface Undertaker {
	
	void bury(DBLayer db);

}
