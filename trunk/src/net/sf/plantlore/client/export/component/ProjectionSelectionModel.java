package net.sf.plantlore.client.export.component;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import net.sf.plantlore.client.export.Projection;

/**
 * A TreeSelectionModel modified to update a list of projections. 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29
 * @see net.sf.plantlore.client.export.component.ExtendedTree
 * @see net.sf.plantlore.client.export.component.UserTreeNode
 */
public class ProjectionSelectionModel extends DefaultTreeSelectionModel {
	
	private Projection projections;
	
	
	/**
	 * Create a new ProjectionSelectionModel and use the supplied projections
	 * to store the selected columns.
	 * 
	 * @param projections
	 */
	public ProjectionSelectionModel(Projection projections) {
		this.projections = projections;
	}
	
	/**
	 * Setting a selection path
	 * adds the selection path if it is not selected
	 * or removes it if it is selected.
	 */
	@Override 
	public void setSelectionPath(TreePath path) {
		if( isPathSelected(path) )
			removeSelectionPath(path);
		else
			addSelectionPath(path);
	}
	
	/**
	 * Update the list of projections appropriately.
	 */
	@Override 
	public void removeSelectionPaths(TreePath[] paths) {
		for(TreePath path : paths) {
			if(path == null) continue;
			Object node = path.getLastPathComponent();
			if(node instanceof DefaultMutableTreeNode) {
				UserTreeNode x  = (UserTreeNode) ((DefaultMutableTreeNode)node).getUserObject();
				projections.unset(x.table, x.column);  
			}
		}
		super.removeSelectionPaths( paths );
	}
	
	/**
	 * Update the list of projections appropriately.
	 */
	@Override 
	public void addSelectionPaths(TreePath[] paths) {
		for(TreePath path : paths) {
			Object node = path.getLastPathComponent();
			if(node instanceof DefaultMutableTreeNode) {
				UserTreeNode x  = (UserTreeNode) ((DefaultMutableTreeNode)node).getUserObject();
				// Select table.column records (not the table only). 
				if(x.column != null) projections.set(x.table, x.column);
			}
		}
		super.addSelectionPaths( paths );
	}
	
	@Override
	public void clearSelection() {
		super.clearSelection();
		projections.unsetEverything();
	}
			
	/**
	 * 
	 * @return The list of projections (the list of selected columns).
	 */
	public Projection getProjections() {
		return projections;
	}
	
}
