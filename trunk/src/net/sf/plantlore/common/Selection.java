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
	 * Create a new empty selection.
	 */
	public Selection() {}
	
	/**
	 * Create a new selection containing the same records 
	 * as the other <code>selection</code>
	 * @param selection The selection to duplicate.
	 */
	public Selection(Selection selection) {
		selected = new HashSet<Integer>( selection.selected );
		inverted = selection.inverted;
	}
	
	/**
	 * Add another record to the selection. 
	 * @param id	The primary key of the record.
	 */
	public Selection add(Integer id) {
		if( !inverted ) selected.add(id);
		else selected.remove(id);
		return this;
	}
		
	/**
	 * Remove a selected record from the selection (deselect).
	 * @param id Of the record that is deselected.
	 */
	public Selection remove(Integer id) {
		if( !inverted ) selected.remove(id);
		else selected.add(id);
		return this;
	}
	
	/**
	 * Invert the current selection.
	 */
	public Selection invert() { 
		inverted = !inverted;
		return this;
	}

	/**
	 * Deselect all records.
	 */
	public Selection none() { 
		selected.clear(); inverted = false;
		return this;
	}

	/**
	 * Select all records.
	 */
	public Selection all() { 
		selected.clear(); inverted = true;
		return this;
	}

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
	
	/**
	 * @param total The number of all records (from which the selection is made).
	 * @return The number of selected records.
	 */
	public int size(int total) {
		return inverted ? total - selected.size() : selected.size();
	}
	
	/**
	 * @return true if nothing is selected;
	 */
	public boolean isEmpty() {
		return selected.isEmpty() ^ inverted;
	}
	
	@Override
	public Selection clone() {
		return new Selection( this );
	}

}
