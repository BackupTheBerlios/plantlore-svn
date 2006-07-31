package net.sf.plantlore.client.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.middleware.SelectQuery;
import static net.sf.plantlore.common.PlantloreConstants.PROJ_PROPERTY;


/**
 * The template holds information about the selected columns and tables.
 * This information may used by builders and access rights managers
 * to decide, whether or not the currently
 * considered column should be exported / is accessible.
 * <br/>
 * Note: The template doesn't know anything about the structure
 * (mapping) of the database.  
 * <br/>
 * A little dictionary:
 * <ul>
 * <li><i>foreign key</i> a column of a table that refers to another table,</li>
 * <li><i>property</i> a column of a table that contains a (possibly null) value,
 * but doesn't refer to another table</li>
 * <li><i>nn</i> a property that cannot be null (defined in the db model)</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-22
 * @version 1.1
 */
public class Projection {
	
	/** The list of all pairs Table.Column that are set. */
	private Collection<String> columns = new HashSet<String>(100);
	
	

	
	/** Create a new template. */
	public Projection() {/* Nothing to be done. Just to have a default constructor. */}
	
	/** A copy constructor. */
	public Projection(Projection template) {
		columns = new HashSet<String>( template.columns );
	}
	
	
	@Override
	public Projection clone() {
		return new Projection(this);
	}
	
	
	/** Select the <code>table.column</code>. */
	public Projection set(Class table, String column) { 
		System.out.println("+ "+table.getSimpleName()+"."+column);
		columns.add(table.getSimpleName()+ (column == null ? "" : "."+column));
		return this;
	}
	
	/** Unselect the <code>table.column</code>. */
	public Projection unset(Class table, String column) { 
		System.out.println("- "+table.getSimpleName()+"."+column);
		columns.remove(table.getSimpleName()+ (column == null ? "" : "."+column));
		return this;
	}
	
	/** @return true if the <code>table.column</code> is set.*/
	public boolean isSet(Class table, String column) {
		return columns.contains(table.getSimpleName()+ (column == null ? "" : "."+column));
	}
	
	/** Unselect all columns of all tables. */
	public Projection unsetEverything() { 
		columns.clear();
		return this;
	}
	
	/** Select all columns (properties) of all tables. */
	public Projection setEverything() {
		for(Class table : Record.BASIC_TABLES)
			setAllProperties(table);
		return this;
	}
	
	/** Select all not null columns (properties). */
	public void setEverythingNN() {
		// Implementation missing!
	}
	
	/** Select all properties of this <code>table</code>. */
	public Projection setAllProperties(Class table) {
		try {
			for( String column : ((Record)table.newInstance()).getProperties() )
				set(table, column);
		} catch(Exception e) {/* Not good :/. */}
		return this;
	}
	
	/** Select all not-null properties of the specified <code>table</code>. */
	public Projection setAllNN(Class table) {
		try {
			Record record = ((Record)table.newInstance());
			List<String> nnProperties = record.getNN();
			nnProperties.removeAll(record.getForeignKeys());
			for( String column :  nnProperties )
				set(table, column);
		} catch(Exception e) {/* Not good. */}
		return this;
	}
	
	/**
	 * @return true if nothing is selected.
	 */
	public boolean isEmpty() {
		return columns.isEmpty();
	}
	
		
	/**
	 * Match this template against another one.
	 * 
	 * @param t	The template against which the matching will be done.
	 * @return	True if this template is a superset of the other template.
	 */ 
	public boolean match(Projection t) { 
		return columns.containsAll(t.columns); 
	}
	
	
	private List<Pair<Class, String>> plan;
	
	public List<Pair<Class, String>> getDescription() {
		return plan;
	}
	
	/**
	 * Add projections to the query according to the selected columns.
	 * 
	 * @param q	The query to be modified.
	 * @param tables	The important tables. The first table is considered the root table.
	 * @return The column description.
	 */
	public List<Pair<Class, String>> addProjections(SelectQuery q, Class...tables) {
		plan = new ArrayList<Pair<Class,String>>(20);
		for(int i = 0; i < tables.length; i++)
			addProjections(q, tables[i], i == 0);
		return plan;
	}
	
	
	private void addProjections(SelectQuery q, Class table, boolean omitAlias) {
		try {
			Record r = (Record)table.newInstance();
			for(String property : r.getProperties())
				if( isSet(table, property) ) {
					q.addProjection(PROJ_PROPERTY, (omitAlias ? property : Record.alias(table)+"."+property));
					plan.add(new Pair<Class, String>(table, property));
				}
		} catch(Exception e) {/* Not good. */}
	}
	
	@Override
	public String toString() {
		return columns.toString();
	}
	
}
