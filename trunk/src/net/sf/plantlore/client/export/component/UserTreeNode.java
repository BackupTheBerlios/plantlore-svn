package net.sf.plantlore.client.export.component;

import net.sf.plantlore.l10n.L10n;

/**
 * A representation of a node in an ExtendedTree.
 * Every node corresponds to a <code>column</code> of a <code>table</code>.
 * This is meant merely as a holder object - the hierarchy is represented elsewhere
 * and in a different way.
 * 
 * @author kaimu
 * @since 2006-04-29
 * @see net.sf.plantlore.client.export.component.ExtendedTree
 */
public class UserTreeNode {
	/** The table whose column this node represents. */
	public Class table;
	/** The column of the table this node represents. */
	public String column;
	/** Is it a not-null property in the database? */
	public boolean nn;
	
	/**
	 * Create a new UserTreeNode. 
	 * The node represents one (possibly mandatory) column of a table.
	 */
	public UserTreeNode(Class table, String column, boolean notNullProperty) {
		this.table = table; 
		this.nn = notNullProperty; 
		this.column = column;
	}
	
	/**
	 * Create a new UserTreeNode. The node represents a column of a table.
	 */
	public UserTreeNode(Class table, String column) {
		this(table, column, false);
	}
	
	/**
	 * Get the name of the column.
	 */
	@Override
	public String toString() {
		return L10n.getString(table.getSimpleName() + ((column != null) ? "." +  column : ""));
	}
}