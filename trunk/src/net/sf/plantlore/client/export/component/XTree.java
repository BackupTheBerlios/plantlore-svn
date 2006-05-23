package net.sf.plantlore.client.export.component;

import java.awt.Color;
import java.awt.Component;


import javax.swing.JLabel;
import javax.swing.tree.*;

import net.sf.plantlore.common.record.*;

/**
 * The extension of a JTree that allows the User to see the database model
 * in a compact form. The tree expects the database hierarchy when it's created.
 * Two default hierarchies are already present - the <code>DefaultHierarchy</code>
 * which is loaded when no other hierarchy is specified and displays the database
 * model in the hierarchy, 
 * and the <code>SimpleHierarchy</code> which is a simplified version with
 * all tables as sub-root nodes.
 * Both hierarchies are hard-wired and must be updated if the database
 * model changes!
 * <br/>
 * The selected nodes are visualized to the User in another colour.
 * Collapsing a node <b>will cause</b> deselection of all of its selected
 * sub-nodes. This is a feature that will prevent the User from selecting
 * a group of columns and forgeting about them. 
 * <br/>
 * The XTree uses instances of XNode class - that is, the label of the
 * node is derived from the XNode.toString(). Should you have a desire
 * to implement the L10N, modify that method appropriatelly.
 * <br/>
 * The XTree cannot produce a Template - in order to create and update 
 * the Template "automatically" create your own TreeSelectionModel
 * that is capable of such an action. 
 * <pre>
 * XTree tree = new XTree( );
 * tree.setSelectionModel( new YSelectionModel() );
 * </pre>
 * where
 * <pre>
 * class TemplateSelectionModel extends DefaultTreeSelectionModel {
 * 		private Template template;
 * 
 *		// Use this to achieve compatibility with the default XTree selection model.
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
 *				XNode x  = (XNode) 
 *					((DefaultMutableTreeNode)path.getLastPathComponent())
 *					.getUserObject();
 *				template.unset(x.table, x.column);    // update the Template 
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
 * @see net.sf.plantlore.client.export.Template
 * @see net.sf.plantlore.client.export.ExportMng.XSelectionModel
 */
public class XTree extends javax.swing.JTree {
	
	private static Color NotNullColor = new Color(220, 50, 20);
	private static Color SelectedColor = new Color(20, 150, 20);
	private static Color DefaultColor = Color.BLACK;
	
	
	/**
	 * The Default Hierarchy Model of the Database Model.
	 */
	public static Object[] DefaultHierarchy = {
		new XNode(XTree.class, "Plantlore"),
		new Object[] { // OCCURENCE
			new XNode(Occurrence.class, null),
			new XNode(Occurrence.class, Occurrence.UNITIDDB, true),
			new XNode(Occurrence.class, Occurrence.UNITVALUE, true),
			new Object[] { // HABITAT
				new XNode(Habitat.class, null),
				new Object[] { // TERRITORY
					new XNode(Territory.class, null),
					new XNode(Territory.class, Territory.NAME, true)
				},
				new Object[] { // PHYTOCHORION
					new XNode(Phytochorion.class, null),
					new XNode(Phytochorion.class, Phytochorion.CODE, true),
					new XNode(Phytochorion.class, Phytochorion.NAME, true),
				},
				new XNode(Habitat.class, Habitat.QUADRANT),
				new XNode(Habitat.class, Habitat.DESCRIPTION),
				new Object[] { // NEAREST VILLAGE
					new XNode(Village.class, null),
					new XNode(Village.class, Village.NAME, true)
				},
				new XNode(Habitat.class, Habitat.COUNTRY),
				new XNode(Habitat.class, Habitat.ALTITUDE),
				new XNode(Habitat.class, Habitat.LATITUDE),
				new XNode(Habitat.class, Habitat.LONGITUDE),
				new XNode(Habitat.class, Habitat.NOTE),
			},
			new Object[] { // PLANT
				new XNode(Plant.class, null),
				new XNode(Plant.class, Plant.SURVEYTAXID, true),
				new XNode(Plant.class, Plant.TAXON, true),
				new XNode(Plant.class, Plant.GENUS),
				new XNode(Plant.class, Plant.SPECIES),
				new XNode(Plant.class, Plant.SCIENTIFICNAMEAUTHOR, true),
				new XNode(Plant.class, Plant.CZECHNAME),
				new XNode(Plant.class, Plant.SYNONYMS),
				new XNode(Plant.class, Plant.NOTE)
			},
			new XNode(Occurrence.class, Occurrence.YEARCOLLECTED, true),
			new XNode(Occurrence.class, Occurrence.MONTHCOLLECTED),
			new XNode(Occurrence.class, Occurrence.DAYCOLLECTED),
			new XNode(Occurrence.class, Occurrence.TIMECOLLECTED),
			new XNode(Occurrence.class, Occurrence.DATASOURCE),
			new Object[] { // PUBLICATION
				new XNode(Publication.class, null),
				new XNode(Publication.class, Publication.COLLECTIONNAME),
				new XNode(Publication.class, Publication.COLLECTIONYEARPUBLICATION),
				new XNode(Publication.class, Publication.JOURNALNAME),
				new XNode(Publication.class, Publication.JOURNALAUTHORNAME),
				new XNode(Publication.class, Publication.REFERENCECITATION, true),
				new XNode(Publication.class, Publication.REFERENCEDETAIL),
				new XNode(Publication.class, Publication.URL),
				new XNode(Publication.class, Publication.NOTE)
			},
			new XNode(Occurrence.class, Occurrence.HERBARIUM),
			new XNode(Occurrence.class, Occurrence.CREATEDWHEN, true),
			new XNode(Occurrence.class, Occurrence.UPDATEDWHO, true),
			new XNode(Occurrence.class, Occurrence.NOTE),
			new Object[] { // METADATA
				new XNode(Metadata.class, null),
				new XNode(Metadata.class, Metadata.TECHNICALCONTACTNAME, true),
				new XNode(Metadata.class, Metadata.TECHNICALCONTACTEMAIL),
				new XNode(Metadata.class, Metadata.TECHNICALCONTACTADDRESS),
				new XNode(Metadata.class, Metadata.CONTENTCONTACTNAME, true),
				new XNode(Metadata.class, Metadata.CONTENTCONTACTEMAIL),
				new XNode(Metadata.class, Metadata.CONTENTCONTACTADDRESS),
				new XNode(Metadata.class, Metadata.DATASETTITLE, true),
				new XNode(Metadata.class, Metadata.DATASETDETAILS),
				new XNode(Metadata.class, Metadata.SOURCEINSTITUTIONID, true),
				new XNode(Metadata.class, Metadata.SOURCEID, true),
				new XNode(Metadata.class, Metadata.OWNERORGANIZATIONABBREV),
				new XNode(Metadata.class, Metadata.DATECREATE, true),
				new XNode(Metadata.class, Metadata.DATEMODIFIED, true),
				new XNode(Metadata.class, Metadata.RECORDBASIS),
				new XNode(Metadata.class, Metadata.BIOTOPETEXT),				
			}
		},
		new Object[] { // AUTHOR
			new XNode(Author.class, null),
			new XNode(Author.class, Author.WHOLENAME),
			new XNode(Author.class, Author.ORGANIZATION),
			new XNode(Author.class, Author.PHONENUMBER),
			new XNode(Author.class, Author.ROLE),
			new XNode(Author.class, Author.ADDRESS),
			new XNode(Author.class, Author.EMAIL),
			new XNode(Author.class, Author.URL),
			new XNode(Author.class, Author.NOTE)
		},
		new Object[] { // AUTHOR-OCCURENCE
			new XNode(AuthorOccurrence.class, null),
			new XNode(AuthorOccurrence.class, AuthorOccurrence.ROLE),
			new XNode(AuthorOccurrence.class, AuthorOccurrence.NOTE)
		}
	};
	
	/**
	 * A simplified Hierarchy Model of the Database Model. 
	 */
	public static Object[] SimpleHierarchy = new Object[] {
		new XNode(XTree.class, "Plantlore DB"),
		new Object[] { // OCCURENCE
			new XNode(Occurrence.class, null),
			new XNode(Occurrence.class, Occurrence.UNITIDDB, true),
			new XNode(Occurrence.class, Occurrence.UNITVALUE, true),
			new XNode(Occurrence.class, Occurrence.YEARCOLLECTED, true),
			new XNode(Occurrence.class, Occurrence.MONTHCOLLECTED),
			new XNode(Occurrence.class, Occurrence.DAYCOLLECTED),
			new XNode(Occurrence.class, Occurrence.TIMECOLLECTED),
			new XNode(Occurrence.class, Occurrence.DATASOURCE),
			new XNode(Occurrence.class, Occurrence.HERBARIUM),
			new XNode(Occurrence.class, Occurrence.CREATEDWHEN, true),
			new XNode(Occurrence.class, Occurrence.UPDATEDWHO, true),
			new XNode(Occurrence.class, Occurrence.NOTE)
		},
		new Object[] { // TERRITORY
			new XNode(Territory.class, null),
			new XNode(Territory.class, Territory.NAME, true)
		},
		new Object[] { // PHYTOCHORION
			new XNode(Phytochorion.class, null),
			new XNode(Phytochorion.class, Phytochorion.CODE, true),
			new XNode(Phytochorion.class, Phytochorion.NAME, true),
		},
		new Object[] { // NEAREST VILLAGE
			new XNode(Village.class, null),
			new XNode(Village.class, Village.NAME, true)
		},
		new Object[] { // HABITAT
			new XNode(Habitat.class, null),
			new XNode(Habitat.class, Habitat.QUADRANT),
			new XNode(Habitat.class, Habitat.DESCRIPTION),
			new XNode(Habitat.class, Habitat.COUNTRY),
			new XNode(Habitat.class, Habitat.ALTITUDE),
			new XNode(Habitat.class, Habitat.LATITUDE),
			new XNode(Habitat.class, Habitat.LONGITUDE),
			new XNode(Habitat.class, Habitat.NOTE)
		},
		new Object[] { // PLANT
			new XNode(Plant.class, null),
			new XNode(Plant.class, Plant.SURVEYTAXID, true),
			new XNode(Plant.class, Plant.TAXON, true),
			new XNode(Plant.class, Plant.GENUS),
			new XNode(Plant.class, Plant.SPECIES),
			new XNode(Plant.class, Plant.SCIENTIFICNAMEAUTHOR, true),
			new XNode(Plant.class, Plant.CZECHNAME),
			new XNode(Plant.class, Plant.SYNONYMS),
			new XNode(Plant.class, Plant.NOTE)
		},
		new Object[] { // PUBLICATION
			new XNode(Publication.class, null),
			new XNode(Publication.class, Publication.COLLECTIONNAME),
			new XNode(Publication.class, Publication.COLLECTIONYEARPUBLICATION),
			new XNode(Publication.class, Publication.JOURNALNAME),
			new XNode(Publication.class, Publication.JOURNALAUTHORNAME),
			new XNode(Publication.class, Publication.REFERENCECITATION, true),
			new XNode(Publication.class, Publication.REFERENCEDETAIL),
			new XNode(Publication.class, Publication.URL),
			new XNode(Publication.class, Publication.NOTE)
		},
		new Object[] { // METADATA
			new XNode(Metadata.class, null),
			new XNode(Metadata.class, Metadata.TECHNICALCONTACTNAME, true),
			new XNode(Metadata.class, Metadata.TECHNICALCONTACTEMAIL),
			new XNode(Metadata.class, Metadata.TECHNICALCONTACTADDRESS),
			new XNode(Metadata.class, Metadata.CONTENTCONTACTNAME, true),
			new XNode(Metadata.class, Metadata.CONTENTCONTACTEMAIL),
			new XNode(Metadata.class, Metadata.CONTENTCONTACTADDRESS),
			new XNode(Metadata.class, Metadata.DATASETTITLE, true),
			new XNode(Metadata.class, Metadata.DATASETDETAILS),
			new XNode(Metadata.class, Metadata.SOURCEINSTITUTIONID, true),
			new XNode(Metadata.class, Metadata.SOURCEID, true),
			new XNode(Metadata.class, Metadata.OWNERORGANIZATIONABBREV),
			new XNode(Metadata.class, Metadata.DATECREATE, true),
			new XNode(Metadata.class, Metadata.DATEMODIFIED, true),
			new XNode(Metadata.class, Metadata.RECORDBASIS),
			new XNode(Metadata.class, Metadata.BIOTOPETEXT),			
		},
		new Object[] { // AUTHOR
			new XNode(Author.class, null),
			new XNode(Author.class, Author.WHOLENAME),
			new XNode(Author.class, Author.ORGANIZATION),
			new XNode(Author.class, Author.PHONENUMBER),
			new XNode(Author.class, Author.ROLE),
			new XNode(Author.class, Author.ADDRESS),
			new XNode(Author.class, Author.EMAIL),
			new XNode(Author.class, Author.URL),
			new XNode(Author.class, Author.NOTE)
		},
		new Object[] { // AUTHOR-OCCURENCE
			new XNode(AuthorOccurrence.class, null),
			new XNode(AuthorOccurrence.class, AuthorOccurrence.ROLE),
			new XNode(AuthorOccurrence.class, AuthorOccurrence.NOTE)
		}
	};
	
	/**
	 * Create a new XTree with the Default Hierarchy Model.
	 */
	public XTree( ) {
		this( DefaultHierarchy );
	}
	
	
	/**
	 * Create a new XTree with a specified Hierarchy Model.
	 * @param hierarchy The hierarchy to be processed.
	 */
	public XTree( Object[] hierarchy ) {
		super( processHierarchy(hierarchy) );
		
	    setCellRenderer( new XCellRenderer() );
	    setToggleClickCount(1);
	    setSelectionModel( new XSelectionModel() );
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
	 * SPecialized Cell Renderer that shows no icons and
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
				if( ((XNode)((DefaultMutableTreeNode)value).getUserObject()).nn )
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
