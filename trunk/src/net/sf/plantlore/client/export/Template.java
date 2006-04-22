package net.sf.plantlore.client.export;

import java.util.Collection;
import java.util.HashSet;


/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-22
 */
public class Template {
	
	private Class root = null;
	private Collection<String> tableColumn = new HashSet<String>(20);
	
		
	public void setRootTable(Class table) { root = table; }
	public Class getRootTable() { return root; }
	
	public void setTable(Class table) { tableColumn.add(table.toString()); }
	public void unsetTable(Class table) { tableColumn.remove(table.toString()); }
	public boolean isSetTable(Class table) { return tableColumn.contains(table.toString()); } 
	
	public void set(Class table, String column) { tableColumn.add(table+"."+column); }
	public void unset(Class table, String column) { tableColumn.remove(table+"."+column); }
	public boolean isSet(Class table, String column) { return tableColumn.contains(table+"."+column); }
	
	public void unsetAll() { tableColumn.clear(); }
	
	public boolean match(Template t) { return tableColumn.containsAll(t.tableColumn); }

}
