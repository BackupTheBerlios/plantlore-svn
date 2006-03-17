package net.sf.plantlore.server;

import java.util.Collection;

public interface Tracker<E> {
	
	Collection<? extends E>	getClients();
	
}
