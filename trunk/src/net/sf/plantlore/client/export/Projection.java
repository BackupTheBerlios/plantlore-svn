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
 * The Projection class holds information about the selected columns and tables.
 * This information may used by builders  to decide, whether or not 
 * the currently considered column should be exported.
 * <br/>
 * Note: The Projection doesn't know anything about the structure
 * (mapping) of the database.  
 * <br/>
 * <ul>
 * <li><i>foreign key</i> a column of a table that refers to another table,</li>
 * <li><i>property</i> a column of a table that contains a (possibly null) value,
 * but doesn't refer to another table</li>
 * <li><i>nn</i> a property that cannot be null (defined in the db model)</li>
 * </ul>
 * <br/>
 * The list of projections can be applied to a query in order to set its projections.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-22
 * @version 1.3
 */
public class Projection {
	
	/** The list of all pairs Table.Column that are set. */
	private Collection<String> columns = new HashSet<String>(100);
	

	/** 
	 * Create a new Projection. 
	 * No columns are selected. 
	 */
	public Projection() {/* Nothing to be done. Just to have a default constructor. */}
	
	/**
	 * Create a new Projection. Use the same values as the supplied <code>projection</code>.
	 * 
	 * @param projection	The Projection that should be copied.
	 */
	public Projection(Projection projection) {
		columns = new HashSet<String>( projection.columns );
	}
	
	/**
	 * @return A shallow copy of this Projection.
	 */
	@Override
	public Projection clone() {
		return new Projection(this);
	}
	
	/** 
	 * Add the <code>table.column</code> to the list of projections.
	 * 
	 * @param table		The class representing the table.
	 * @param column	The name of the column.
	 * @return	Itself.
	 */
	public Projection set(Class table, String column) { 
		columns.add(table.getSimpleName()+ (column == null ? "" : "."+column));
		return this;
	}
	
	/**
	 * Remove the <code>table.column</code> from the list of projections.
	 * 
	 * @param table		The class representing the table.
	 * @param column	The name of the column.
	 * @return Itself.
	 */ 
	public Projection unset(Class table, String column) { 
		columns.remove(table.getSimpleName()+ (column == null ? "" : "."+column));
		return this;
	}
	
	/** 
	 * 
	 * @param table	The class representing the table.
	 * @param column	The name of the column.
	 * @return True if the <code>table.column</code> is part of the list of projections.
	 */
	public boolean isSet(Class table, String column) {
		return columns.contains(table.getSimpleName()+ (column == null ? "" : "."+column));
	}
	
	/** 
	 * Deselect all columns of all tables - deselect everything.
	 * 
	 *  @return Itself.
	 */
	public Projection unsetEverything() { 
		columns.clear();
		return this;
	}
	
	/** 
	 * Select all columns (properties) of all tables.
	 * 
	 *  @return Itself.
	 */
	public Projection setEverything() {
		for(Class table : Record.BASIC_TABLES)
			setAllProperties(table);
		return this;
	}
	
	/** 
	 * Select all properties of the <code>table</code>.
	 * 
	 * @param table	The table whose properties shall be set.
	 * @return Itself.
	 */
	public Projection setAllProperties(Class table) {
		try {
			for( String column : ((Record)table.newInstance()).getProperties() )
				set(table, column);
		} catch(Exception e) {/* This should not happen. */}
		return this;
	}
	
	/**
	 * Select all not-null properties of the specified <code>table</code>. 
	 * 
	 * @param table	The table whose properties shall be set.
	 * @return Itself.
	 */
	public Projection setAllNN(Class table) {
		try {
			Record record = ((Record)table.newInstance());
			List<String> nnProperties = record.getNN();
			nnProperties.removeAll(record.getForeignKeys());
			for( String column :  nnProperties )
				set(table, column);
		} catch(Exception e) {/* This should not happen. */}
		return this;
	}
	
	/**
	 * @return True if nothing is selected.
	 */
	public boolean isEmpty() {
		return columns.isEmpty();
	}
	
		
	/**
	 * Match this list of projections against another one.
	 * 
	 * @param t	The list against which the matching will take place.
	 * @return	True if this list of projections is a superset of the other.
	 */ 
	public boolean match(Projection t) { 
		return columns.containsAll(t.columns); 
	}
	
	
	private List<Pair<Class, String>> plan;
	
	/**
	 * Description of the columns. If projections are used 
	 * then Hibernate will return a list of values. 
	 * So as to be able to distinguish what value belongs
	 * to which column, we need the list of <code>table.column</code>.
	 * The description of the columns together with the list of values
	 * can be used to restore the original record.
	 * 
	 * @return	The list of <code>table.column</code> that identifies the name
	 * of the table and column where a value might belong. 
	 * 
	 *  @see net.sf.plantlore.client.export.ExportTask2#reconstruct(Object[])
	 */
	public List<Pair<Class, String>> getDescription() {
		return plan;
	}
	
	/**
	 * Add projections to the query according to the selected columns.
	 * 
	 * @param q	The query to be modified.
	 * @param tables	The important tables. The first table is considered the root table.
	 * @return The column description.
	 * 
	 * @see net.sf.plantlore.client.export.ExportTask2#reconstruct(Object[])
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
