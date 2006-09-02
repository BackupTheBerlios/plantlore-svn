package net.sf.plantlore.client.tableimport;

import net.sf.plantlore.common.record.Record;

/**
 * Store the information about the record(s) obtained from the input.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-10
  */
public class DataHolder {
	/**
	 * The action that should be performed with the record(s).
	 */
	public TableParser.Action action;
	/**
	 * The record from the file that should be processed.
	 */
	public Record record;
	/**
	 * Additional record from the file that may be required in order to carry out the 
	 * intended action (such as update) properly.
	 */
	public Record replacement;
}
