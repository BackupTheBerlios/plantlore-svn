package net.sf.plantlore.client.export.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.util.LinkedList;


import javax.swing.JLabel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;

import net.sf.plantlore.common.record.*;

/**
 * The extension of a JTree that allows the User to see the database model
 * in a compact form. The tree expects a database hierarchy - that hierarchy will
 * be traversed to build the tree.
 * <br/>
 * Two default hierarchies are already present - the <code>DefaultHierarchy</code>
 * which is loaded when no other hierarchy is specified and displays the database
 * model in the natural hierarchy (i.e. following the database model), 
 * and the <code>SimpleHierarchy</code> which is a simplified version with
 * all tables as sub-root nodes.
 * Both hierarchies are hard-wired and must be updated if the database
 * model changes!
 * <br/>
 * The selected (marked) nodes are visualized to the User in another colour.
 * Collapsing a node <b>will cause</b> deselection of all of its selected
 * sub-nodes. This is a feature that will prevent the User from selecting
 * a group of columns and forgeting about them. 
 * <br/>
 * The ExtendedTree uses instances of UserTreeNode class - that is, the label of the
 * node is derived from the UserTreeNode.toString(). 
 * <br/>
 * The ExtendedTree cannot produce a list of projections - in order to create and update 
 * the Projection "automatically" create your own TreeSelectionModel
 * that is capable of such an action. 
 * <pre>
 * ExtendedTree tree = new ExtendedTree( );
 * tree.setSelectionModel( new YSelectionModel() );
 * </pre>
 * where
 * <pre>
 * class ProjectionSelectionModel extends DefaultTreeSelectionModel {
 * 		private Projection projection;
 * 
 *		// Use this to achieve compatibility with the default ExtendedTree selection model.
 *		@Override 
 *		public void setSelectionPath(TreePath path) {
 *			if( isPathSelected(path) )
 *				removeSelectionPath(path);
 *			else
 *				addSelectionPath(path);
 *		}
 *		@Override 
 *		public void removeSelectionPaths(TreePath[] paths) {
 *			for(TreePath path : paths) {
 *				UserTreeNode x  = (UserTreeNode) 
 *					((DefaultMutableTreeNode)path.getLastPathComponent())
 *					.getUserObject();
 *				projection.unset(x.table, x.column);    // update the list of projections 
 *			} 
 *			super.removeSelectionPaths( paths ); 
 *		}
 *		@Override 
 *		public void addSelectionPaths(TreePath[] paths) {
 *			// ...similar...
 *		}
 * }
 * </pre>
 *   
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29
 * @version 1.0
 * @see net.sf.plantlore.client.export.Projection
 * @see net.sf.plantlore.client.export.ExportMng.ProjectionSelectionModel
 */
public class ExtendedTree extends javax.swing.JTree {
	
	private static Color NotNullColor = new Color(220, 50, 20);
	private static Color SelectedColor = new Color(20, 150, 20);
	private static Color DefaultColor = Color.BLACK;
	
	
	/**
	 * The Default Hierarchy Model of the Database Model.
	 */
	public static Object[] DefaultHierarchy = {
		new UserTreeNode(ExtendedTree.class, "Plantlore"),
		new Object[] { // OCCURENCE
			new UserTreeNode(Occurrence.class, null),
			new UserTreeNode(Occurrence.class, Occurrence.UNITIDDB, true),
			new UserTreeNode(Occurrence.class, Occurrence.UNITVALUE, true),
			new Object[] { // HABITAT
				new UserTreeNode(Habitat.class, null),
				new Object[] { // TERRITORY
					new UserTreeNode(Territory.class, null),
					new UserTreeNode(Territory.class, Territory.NAME, true)
				},
				new Object[] { // PHYTOCHORION
					new UserTreeNode(Phytochorion.class, null),
					new UserTreeNode(Phytochorion.class, Phytochorion.CODE, true),
					new UserTreeNode(Phytochorion.class, Phytochorion.NAME, true),
				},
				new UserTreeNode(Habitat.class, Habitat.QUADRANT),
				new UserTreeNode(Habitat.class, Habitat.DESCRIPTION),
				new Object[] { // NEAREST VILLAGE
					new UserTreeNode(NearestVillage.class, null),
					new UserTreeNode(NearestVillage.class, NearestVillage.NAME, true)
				},
				new UserTreeNode(Habitat.class, Habitat.COUNTRY),
				new UserTreeNode(Habitat.class, Habitat.ALTITUDE),
				new UserTreeNode(Habitat.class, Habitat.LATITUDE),
				new UserTreeNode(Habitat.class, Habitat.LONGITUDE),
				new UserTreeNode(Habitat.class, Habitat.NOTE),
			},
			new Object[] { // PLANT
				new UserTreeNode(Plant.class, null),
				new UserTreeNode(Plant.class, Plant.SURVEYTAXID, true),
				new UserTreeNode(Plant.class, Plant.TAXON, true),
				new UserTreeNode(Plant.class, Plant.GENUS),
				new UserTreeNode(Plant.class, Plant.SPECIES),
				new UserTreeNode(Plant.class, Plant.SCIENTIFICNAMEAUTHOR),
				new UserTreeNode(Plant.class, Plant.CZECHNAME),
				new UserTreeNode(Plant.class, Plant.SYNONYMS),
				new UserTreeNode(Plant.class, Plant.NOTE)
			},
			new UserTreeNode(Occurrence.class, Occurrence.YEARCOLLECTED, true),
			new UserTreeNode(Occurrence.class, Occurrence.MONTHCOLLECTED),
			new UserTreeNode(Occurrence.class, Occurrence.DAYCOLLECTED),
			new UserTreeNode(Occurrence.class, Occurrence.TIMECOLLECTED),
			new UserTreeNode(Occurrence.class, Occurrence.DATASOURCE),
			new Object[] { // PUBLICATION
				new UserTreeNode(Publication.class, null),
				new UserTreeNode(Publication.class, Publication.COLLECTIONNAME),
				new UserTreeNode(Publication.class, Publication.COLLECTIONYEARPUBLICATION),
				new UserTreeNode(Publication.class, Publication.JOURNALNAME),
				new UserTreeNode(Publication.class, Publication.JOURNALAUTHORNAME),
				new UserTreeNode(Publication.class, Publication.REFERENCECITATION, true),
				new UserTreeNode(Publication.class, Publication.REFERENCEDETAIL),
				new UserTreeNode(Publication.class, Publication.URL),
				new UserTreeNode(Publication.class, Publication.NOTE)
			},
			new UserTreeNode(Occurrence.class, Occurrence.HERBARIUM),
			new UserTreeNode(Occurrence.class, Occurrence.CREATEDWHEN, true),
			new UserTreeNode(Occurrence.class, Occurrence.UPDATEDWHO, true),
			new UserTreeNode(Occurrence.class, Occurrence.NOTE),
			new Object[] { // METADATA
				new UserTreeNode(Metadata.class, null),
				new UserTreeNode(Metadata.class, Metadata.TECHNICALCONTACTNAME, true),
				new UserTreeNode(Metadata.class, Metadata.TECHNICALCONTACTEMAIL),
				new UserTreeNode(Metadata.class, Metadata.TECHNICALCONTACTADDRESS),
				new UserTreeNode(Metadata.class, Metadata.CONTENTCONTACTNAME, true),
				new UserTreeNode(Metadata.class, Metadata.CONTENTCONTACTEMAIL),
				new UserTreeNode(Metadata.class, Metadata.CONTENTCONTACTADDRESS),
				new UserTreeNode(Metadata.class, Metadata.DATASETTITLE, true),
				new UserTreeNode(Metadata.class, Metadata.DATASETDETAILS),
				new UserTreeNode(Metadata.class, Metadata.SOURCEINSTITUTIONID, true),
				new UserTreeNode(Metadata.class, Metadata.SOURCEID, true),
				new UserTreeNode(Metadata.class, Metadata.OWNERORGANIZATIONABBREV),
				new UserTreeNode(Metadata.class, Metadata.DATECREATE, true),
				new UserTreeNode(Metadata.class, Metadata.DATEMODIFIED, true),
				new UserTreeNode(Metadata.class, Metadata.RECORDBASIS),
				new UserTreeNode(Metadata.class, Metadata.BIOTOPETEXT),				
			}
		},
		new Object[] { // AUTHOR
			new UserTreeNode(Author.class, null),
			new UserTreeNode(Author.class, Author.WHOLENAME),
			new UserTreeNode(Author.class, Author.ORGANIZATION),
			new UserTreeNode(Author.class, Author.PHONENUMBER),
			new UserTreeNode(Author.class, Author.ROLE),
			new UserTreeNode(Author.class, Author.ADDRESS),
			new UserTreeNode(Author.class, Author.EMAIL),
			new UserTreeNode(Author.class, Author.URL),
			new UserTreeNode(Author.class, Author.NOTE)
		},
		new Object[] { // AUTHOR-OCCURENCE
			new UserTreeNode(AuthorOccurrence.class, null),
			new UserTreeNode(AuthorOccurrence.class, AuthorOccurrence.ROLE),
			new UserTreeNode(AuthorOccurrence.class, AuthorOccurrence.NOTE)
		}
	};
	
	/**
	 * A simplified Hierarchy Model of the Database Model. 
	 */
	public static Object[] SimpleHierarchy = new Object[] {
		new UserTreeNode(ExtendedTree.class, "Plantlore DB"),
		new Object[] { // OCCURENCE
			new UserTreeNode(Occurrence.class, null),
			new UserTreeNode(Occurrence.class, Occurrence.UNITIDDB, true),
			new UserTreeNode(Occurrence.class, Occurrence.UNITVALUE, true),
			new UserTreeNode(Occurrence.class, Occurrence.YEARCOLLECTED, true),
			new UserTreeNode(Occurrence.class, Occurrence.MONTHCOLLECTED),
			new UserTreeNode(Occurrence.class, Occurrence.DAYCOLLECTED),
			new UserTreeNode(Occurrence.class, Occurrence.TIMECOLLECTED),
			new UserTreeNode(Occurrence.class, Occurrence.DATASOURCE),
			new UserTreeNode(Occurrence.class, Occurrence.HERBARIUM),
			new UserTreeNode(Occurrence.class, Occurrence.CREATEDWHEN, true),
			new UserTreeNode(Occurrence.class, Occurrence.UPDATEDWHO, true),
			new UserTreeNode(Occurrence.class, Occurrence.NOTE)
		},
		new Object[] { // TERRITORY
			new UserTreeNode(Territory.class, null),
			new UserTreeNode(Territory.class, Territory.NAME, true)
		},
		new Object[] { // PHYTOCHORION
			new UserTreeNode(Phytochorion.class, null),
			new UserTreeNode(Phytochorion.class, Phytochorion.CODE, true),
			new UserTreeNode(Phytochorion.class, Phytochorion.NAME, true),
		},
		new Object[] { // NEAREST VILLAGE
			new UserTreeNode(NearestVillage.class, null),
			new UserTreeNode(NearestVillage.class, NearestVillage.NAME, true)
		},
		new Object[] { // HABITAT
			new UserTreeNode(Habitat.class, null),
			new UserTreeNode(Habitat.class, Habitat.QUADRANT),
			new UserTreeNode(Habitat.class, Habitat.DESCRIPTION),
			new UserTreeNode(Habitat.class, Habitat.COUNTRY),
			new UserTreeNode(Habitat.class, Habitat.ALTITUDE),
			new UserTreeNode(Habitat.class, Habitat.LATITUDE),
			new UserTreeNode(Habitat.class, Habitat.LONGITUDE),
			new UserTreeNode(Habitat.class, Habitat.NOTE)
		},
		new Object[] { // PLANT
			new UserTreeNode(Plant.class, null),
			new UserTreeNode(Plant.class, Plant.SURVEYTAXID, true),
			new UserTreeNode(Plant.class, Plant.TAXON, true),
			new UserTreeNode(Plant.class, Plant.GENUS),
			new UserTreeNode(Plant.class, Plant.SPECIES),
			new UserTreeNode(Plant.class, Plant.SCIENTIFICNAMEAUTHOR),
			new UserTreeNode(Plant.class, Plant.CZECHNAME),
			new UserTreeNode(Plant.class, Plant.SYNONYMS),
			new UserTreeNode(Plant.class, Plant.NOTE)
		},
		new Object[] { // PUBLICATION
			new UserTreeNode(Publication.class, null),
			new UserTreeNode(Publication.class, Publication.COLLECTIONNAME),
			new UserTreeNode(Publication.class, Publication.COLLECTIONYEARPUBLICATION),
			new UserTreeNode(Publication.class, Publication.JOURNALNAME),
			new UserTreeNode(Publication.class, Publication.JOURNALAUTHORNAME),
			new UserTreeNode(Publication.class, Publication.REFERENCECITATION, true),
			new UserTreeNode(Publication.class, Publication.REFERENCEDETAIL),
			new UserTreeNode(Publication.class, Publication.URL),
			new UserTreeNode(Publication.class, Publication.NOTE)
		},
		new Object[] { // METADATA
			new UserTreeNode(Metadata.class, null),
			new UserTreeNode(Metadata.class, Metadata.TECHNICALCONTACTNAME, true),
			new UserTreeNode(Metadata.class, Metadata.TECHNICALCONTACTEMAIL),
			new UserTreeNode(Metadata.class, Metadata.TECHNICALCONTACTADDRESS),
			new UserTreeNode(Metadata.class, Metadata.CONTENTCONTACTNAME, true),
			new UserTreeNode(Metadata.class, Metadata.CONTENTCONTACTEMAIL),
			new UserTreeNode(Metadata.class, Metadata.CONTENTCONTACTADDRESS),
			new UserTreeNode(Metadata.class, Metadata.DATASETTITLE, true),
			new UserTreeNode(Metadata.class, Metadata.DATASETDETAILS),
			new UserTreeNode(Metadata.class, Metadata.SOURCEINSTITUTIONID, true),
			new UserTreeNode(Metadata.class, Metadata.SOURCEID, true),
			new UserTreeNode(Metadata.class, Metadata.OWNERORGANIZATIONABBREV),
			new UserTreeNode(Metadata.class, Metadata.DATECREATE, true),
			new UserTreeNode(Metadata.class, Metadata.DATEMODIFIED, true),
			new UserTreeNode(Metadata.class, Metadata.RECORDBASIS),
			new UserTreeNode(Metadata.class, Metadata.BIOTOPETEXT),			
		},
		new Object[] { // AUTHOR
			new UserTreeNode(Author.class, null),
			new UserTreeNode(Author.class, Author.WHOLENAME),
			new UserTreeNode(Author.class, Author.ORGANIZATION),
			new UserTreeNode(Author.class, Author.PHONENUMBER),
			new UserTreeNode(Author.class, Author.ROLE),
			new UserTreeNode(Author.class, Author.ADDRESS),
			new UserTreeNode(Author.class, Author.EMAIL),
			new UserTreeNode(Author.class, Author.URL),
			new UserTreeNode(Author.class, Author.NOTE)
		},
		new Object[] { // AUTHOR-OCCURENCE
			new UserTreeNode(AuthorOccurrence.class, null),
			new UserTreeNode(AuthorOccurrence.class, AuthorOccurrence.ROLE),
			new UserTreeNode(AuthorOccurrence.class, AuthorOccurrence.NOTE)
		}
	};
	
	/**
	 * Create a new ExtendedTree with the Default Hierarchy Model.
	 */
	public ExtendedTree( ) {
		this( DefaultHierarchy );
	}
	
		
	/**
	 * Create a new ExtendedTree using the specified Hierarchy Model.
	 * 
	 * @param hierarchy The hierarchy to be processed.
	 */
	public ExtendedTree( Object[] hierarchy ) {
		super( processHierarchy(hierarchy) );
		
	    setCellRenderer( new XCellRenderer() );
	    setToggleClickCount(1);
	    setSelectionModel( new XSelectionModel() );
	    addTreeExpansionListener( te = new XTreeExpansionListener() );
	    
	    for( KeyListener listener : getKeyListeners() ) 
	    	removeKeyListener(listener);
	}
	
	private XTreeExpansionListener te;
	
	
	/**
	 * Collapse all nodes (i.e. restore the tree to its initial state).
	 *
	 */
	public void collapseAll() {
		te.collapseAll();
	}
			
	
	/**
	 * Store all expanded nodes; capable of collapsing them all into the
	 * original state.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-07-30
	 *
	 */
	protected class XTreeExpansionListener implements TreeExpansionListener {
		
		private LinkedList<TreePath> paths = new LinkedList<TreePath>();

		public void treeExpanded(TreeExpansionEvent e) {
			if( !paths.contains(e.getPath()) )
				paths.addFirst( e.getPath() );
		}

		public void treeCollapsed(TreeExpansionEvent e) {
			// Not important
		}
		
		public void collapseAll() {
			for( TreePath path : paths )
				collapsePath( path );
			
			paths.clear();
		}
		
	}
	
	
	/**
	 * A modified selection model. A single click will 
	 * add a leaf to the current selection.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-04-29
	 */	
	public class XSelectionModel extends DefaultTreeSelectionModel {
		
		@Override 
		public void setSelectionPath(TreePath path) {
			if( isPathSelected(path) )
				removeSelectionPath(path);
			else
				addSelectionPath(path);
		}
		
	}
	
	
	/**
	 * Specialized Cell Renderer that shows no icons and
	 * displayes mandatory columns with a different coulour.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-04-29
	 */
	protected class XCellRenderer extends DefaultTreeCellRenderer {
		
		public XCellRenderer() {
			setOpenIcon(null); 
		    setClosedIcon(null); 
		    setLeafIcon(null);
		}
		
		@Override
		public Component getTreeCellRendererComponent(
				javax.swing.JTree tree, 
				Object value, 
				boolean selected, 
				boolean expanded, 
				boolean leaf, 
				int row, 
				boolean hasFocus) {
			// Let the default implementation handle the drawing.
			JLabel label =  (JLabel) 
				super.getTreeCellRendererComponent(tree, value, false, expanded, leaf, row, false);
			// Modify the result of the predecessor.
			if( selected && leaf ) 
				label.setForeground( SelectedColor );
			else
				if( ((UserTreeNode)((DefaultMutableTreeNode)value).getUserObject()).nn )
					label.setForeground( NotNullColor );
				else 
					label.setForeground( DefaultColor );
			return label;
		}
	}
	
	
	
	/**
	 * Transform the simple Object[] hierarchy into an internal representation 
	 * of the JTree.
	 * @param hierarchy The hierarchy to be converted.
	 * @return The root of the transformed hierarchy.
	 */
	private static DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(hierarchy[0]);
		DefaultMutableTreeNode child;
		for (int i = 1; i < hierarchy.length; i++) {
			Object nodeSpecifier = hierarchy[i];
			if (nodeSpecifier instanceof Object[]) // Ie node with children
				child = processHierarchy((Object[]) nodeSpecifier);
			else
				child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
			node.add(child);
		}
		return (node);
	}
	

}
