package net.sf.plantlore.common;

import java.util.Collection;
import java.util.HashSet;

import net.sf.plantlore.common.record.Record;


public class Selection {
	
	private Collection<Long> selected = new HashSet(100);
	private boolean inverted = false;
	
	
	public void add(Long id) { selected.add(id); }
		
	public void remove(Long id) { selected.remove(id); }
	
	public void invert() { inverted = !inverted; }
	
	public void none() { selected.clear(); inverted = false; }
	
	public void all() { selected.clear(); inverted = true; }
	
	public boolean contains(Long id) { return selected.contains(id) ^ inverted; }
	
	public boolean contains(Record r) { return selected.contains(r.getId()) ^ inverted; }

}
