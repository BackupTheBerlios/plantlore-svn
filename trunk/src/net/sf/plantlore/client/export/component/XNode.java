package net.sf.plantlore.client.export.component;

/**
 * A representation of a node in an XTree.
 * Every node corresponds to a <code>column</code> of a <code>table</code>.
 *  
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29
 * @see net.sf.plantlore.client.export.component.XTree
 */
public class XNode {
	/** The table whose columns this node represents. */
	public Class table;
	/** The column of the table this node represents. */
	public String column;
	/** Is it a not-null property in the database? */
	public boolean nn;
	
	/**
	 * Create a new XNode. The node represents 
	 * a (possibly mandatory) column of a table.
	 */
	public XNode(Class table, String column, boolean notNullProperty) {
		this.table = table; nn = notNullProperty; this.column = column;
	}
	
	/**
	 * Create a new XNode. The node represents a column of a table.
	 */
	public XNode(Class table, String column) {
		this(table, column, false);
	}
	
	/**
	 * Get the name of the column.
	 * TODO: L10n may be useful here!
	 */
	@Override
	public String toString() {
		return column == null ? table.getSimpleName() :  column;
	}
}