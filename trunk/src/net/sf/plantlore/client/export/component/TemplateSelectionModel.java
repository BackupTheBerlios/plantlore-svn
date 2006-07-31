package net.sf.plantlore.client.export.component;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import net.sf.plantlore.client.export.Projection;

/**
 * A TreeSelectionModel modified to update a Projection. 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 29.4.2006
 * @see net.sf.plantlore.client.export.component.ExtendedTree
 * @see net.sf.plantlore.client.export.component.UserTreeNode
 */
public class TemplateSelectionModel extends DefaultTreeSelectionModel {
	
	private Projection template;
	
	
	
	public TemplateSelectionModel(Projection template) {
		this.template = template;
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
	 * Update the Projection appropriately.
	 */
	@Override 
	public void removeSelectionPaths(TreePath[] paths) {
		for(TreePath path : paths) {
			if(path == null) continue;
			Object node = path.getLastPathComponent();
			if(node instanceof DefaultMutableTreeNode) {
				UserTreeNode x  = (UserTreeNode) ((DefaultMutableTreeNode)node).getUserObject();
				template.unset(x.table, x.column);  
			}
		}
		super.removeSelectionPaths( paths );
	}
	
	/**
	 * Update the Projection appropriately.
	 */
	@Override 
	public void addSelectionPaths(TreePath[] paths) {
		for(TreePath path : paths) {
			Object node = path.getLastPathComponent();
			if(node instanceof DefaultMutableTreeNode) {
				UserTreeNode x  = (UserTreeNode) ((DefaultMutableTreeNode)node).getUserObject();
				// Select table.column records (not the table only). 
				if(x.column != null) template.set(x.table, x.column);
			}
		}
		super.addSelectionPaths( paths );
	}
	
	@Override
	public void clearSelection() {
		super.clearSelection();
		template.unsetEverything();
	}
			
	/**
	 * 
	 * @return A copy of the inner template that stores the list of selected columns.
	 */
	public Projection getTemplate() {
		return template;
	}
	
}
