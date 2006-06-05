package net.sf.plantlore.client.export.component;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import net.sf.plantlore.client.export.Template;

/**
 * A TreeSelectionModel modified to update its Template. 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 29.4.2006
 * @see net.sf.plantlore.client.export.component.XTree
 * @see net.sf.plantlore.client.export.component.XNode
 */
public class TemplateSelectionModel extends DefaultTreeSelectionModel {
	
	private Template xtemplate = new Template();
	
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
	 * Update the Template appropriately.
	 */
	@Override 
	public void removeSelectionPaths(TreePath[] paths) {
		for(TreePath path : paths) {
			if(path == null) continue;
			Object node = path.getLastPathComponent();
			if(node instanceof DefaultMutableTreeNode) {
				XNode x  = (XNode) ((DefaultMutableTreeNode)node).getUserObject();
				xtemplate.unset(x.table, x.column);  
			}
		}
		super.removeSelectionPaths( paths );
	}
	
	/**
	 * Update the Template appropriately.
	 */
	@Override 
	public void addSelectionPaths(TreePath[] paths) {
		for(TreePath path : paths) {
			Object node = path.getLastPathComponent();
			if(node instanceof DefaultMutableTreeNode) {
				XNode x  = (XNode) ((DefaultMutableTreeNode)node).getUserObject();
				// Select table.column records (not the table only). 
				if(x.column != null) xtemplate.set(x.table, x.column);
			}
		}
		super.addSelectionPaths( paths );
	}
	
	@Override
	public void clearSelection() {
		super.clearSelection();
		xtemplate.unsetEverything();
	}
			
	/**
	 * 
	 * @return A copy of the inner template that stores the list of selected columns.
	 */
	public Template getTemplate() {
		return xtemplate.clone();
	}
	
}
