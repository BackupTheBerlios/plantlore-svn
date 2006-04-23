package net.sf.plantlore.common;

import java.util.Collection;
import java.util.HashSet;

import net.sf.plantlore.common.record.Record;

/**
 * A list of selected records. The selection allows
 * selection of new records, removal of selected records (deselection),
 * selection of all records, deselection of all records,
 * and inversion of the current selection.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 */
public class Selection {
	
	private Collection<Integer> selected = new HashSet<Integer>(100);
	private boolean inverted = false;
	
	/**
	 * Add another record to the selection.
	 * @param id	The primary key of the record.
	 */
	public void add(Integer id) { selected.add(id); }
		
	/**
	 * Remove a selected record from the selection (deselect).
	 * @param id Of the record that is deselected.
	 */
	public void remove(Integer id) { selected.remove(id); }
	
	/**
	 * Invert the current selection.
	 */
	public void invert() { inverted = !inverted; }

	/**
	 * Deselect all records.
	 */
	public void none() { selected.clear(); inverted = false; }

	/**
	 * Select all records.
	 */
	public void all() { selected.clear(); inverted = true; }

	/**
	 * @param id	The primary key of the record.
	 * @return true if a record with this primary key is selected.
	 */
	public boolean contains(Integer id) { return selected.contains(id) ^ inverted; }
	
	/**
	 * @param r	The record.
	 * @return true if the record is selected.
	 */
	public boolean contains(Record r) { return selected.contains(r.getId()) ^ inverted; }

}
