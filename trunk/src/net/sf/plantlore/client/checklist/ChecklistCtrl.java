package net.sf.plantlore.client.checklist;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.l10n.L10n;

/**
 * Binding of the buttons to actions.
 * 
 * @author kaimu
 * @since 2006-05-16
 */
public class ChecklistCtrl {
	
	private ChecklistView view;
	private AutoTextArea model;
	
	/**
	 * Create a new Checklist Controller.
	 * 
	 * @param view	Actions will be bound to this ChecklistView's buttons.
	 * @param model	The AutoTextArea that will receive the selected plants.
	 */
	public ChecklistCtrl(ChecklistView checklistView, AutoTextArea autoTextArea) {
		this.view = checklistView;
		this.model = autoTextArea;
		view.load.setAction(new LoadCreate(0));
		view.save.setAction(new LoadCreate(1));
		view.clear.setAction(new ClearSelection());
		view.submit.setAction(new Submit());
		view.restore.setAction(new Restore());
		view.cancel.setAction( new DefaultCancelAction(view) );
		
		model.addPropertyChangeListener(
				AutoTextArea.ALLOWED_VALUES_CHANGED,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent arg0) {
						view.checklist.setListData( model.getAllowedValues() );
					}
				});
	}
	
	/**
	 * Copy all selected plants from the checklist into the AutoTextArea
	 * and hide the Checklist.
	 * @see net.sf.plantlore.common.AutoTextArea#addLines(Object[])
	 */
	class Submit extends AbstractAction {
		public Submit() {
			putValue(NAME, L10n.getString("Checklist.Submit")); 
            putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.SubmitTT"));
        } 
		public void actionPerformed(ActionEvent arg0) {
			model.addLines( view.checklist.getSelectedValues() );
			view.setVisible(false);
		}
	}
	
	/**
	 * Either create and save a new checklist (from the current selection),
	 * or load a previously saved checklist from a file.
	 */
	class LoadCreate extends AbstractAction {
		private int type;
		
		/**
		 * Create a new Load/Create button controller.
		 * @param type 0 = Load, 1 = Save (Create)
		 */
		public LoadCreate(int type) {
			this.type = type;
			ImageIcon icon;
			switch(type){
			case 0:
				icon = Resource.createIcon("Load.gif");
				if(icon == null) putValue(NAME, L10n.getString("Checklist.Load"));
				else putValue(SMALL_ICON, icon);
				putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.LoadTT"));
				break;
			case 1:
				icon = Resource.createIcon("Save.gif");
				if(icon == null) putValue(NAME, L10n.getString("Checklist.Save"));
				else putValue(SMALL_ICON, icon);
				putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.SaveTT"));
				break;
			}
			
		}
		
		public void actionPerformed(ActionEvent unused) {
			int result = view.choice.showDialog( null, 
					type == 0 ? L10n.getString("Checklist.Load") : L10n.getString("Checklist.Save") );
			if( result == JFileChooser.APPROVE_OPTION ) {
				if(view.choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("Error.MissingFileName"),
							L10n.getString("Error.NothingSelected"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					switch(type) {
					case 0:
						((Checklist)view.checklist).load(view.choice.getSelectedFile().getAbsolutePath());
						break;
					case 1:
						((Checklist)view.checklist).save(view.choice.getSelectedFile().getAbsolutePath());
						break;
					}
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("Error.InvalidChecklist"),
							L10n.getString("Error.NothingSelected"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}
	}
	
	/**
	 * Deselect all plants in the checklist.
	 */
	class ClearSelection extends AbstractAction {
		public ClearSelection() {
            putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.ClearTT"));
            ImageIcon icon = Resource.createIcon("Clear.gif");
			if(icon == null) putValue(NAME, L10n.getString("Checklist.Clear"));
			else putValue(SMALL_ICON, icon);
        } 
		public void actionPerformed(ActionEvent arg0) {
			view.checklist.clearSelection();
		}
	}
	
	/**
	 * Restore the checklist to its original state (right after the creation).
	 */
	class Restore extends AbstractAction {
		public Restore() {
            putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.RestoreTT"));
            ImageIcon icon = Resource.createIcon("Restore.gif");
			if(icon == null) putValue(NAME, L10n.getString("Checklist.Restore"));
			else putValue(SMALL_ICON, icon);
        } 
		public void actionPerformed(ActionEvent arg0) {
			((Checklist)view.checklist).restore();
		}
	}
}
