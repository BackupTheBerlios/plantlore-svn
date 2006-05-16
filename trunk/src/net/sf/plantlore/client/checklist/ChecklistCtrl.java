package net.sf.plantlore.client.checklist;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.l10n.L10n;

public class ChecklistCtrl {
	
	private ChecklistView view;
	private AutoTextArea model;
	
	public ChecklistCtrl(ChecklistView view, AutoTextArea model) {
		this.view = view;
		this.model = model;
		view.load.setAction(new LoadCreate(0));
		view.save.setAction(new LoadCreate(1));
		view.clear.setAction(new ClearSelection());
		view.submit.setAction(new Submit());
	}
	
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
	
	class LoadCreate extends AbstractAction {
		private int type;
		
		public LoadCreate(int type) {
			this.type = type;
			switch(type){
			case 0:
				putValue(SMALL_ICON, Resource.createIcon("Load.gif"));
				putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.LoadTT"));
				break;
			case 1:
				putValue(SMALL_ICON, Resource.createIcon("Save.gif"));
				putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.SaveTT"));
				break;
			}
			
		}
		
		public void actionPerformed(ActionEvent arg0) {
			int result = view.choice.showDialog( null, type == 0 ? "Open" : "Create" );
			if( result == JFileChooser.APPROVE_OPTION ) {
				if(view.choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("error.MissingFileName"),
							L10n.getString("error.NothingSelected"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					switch(type) {
					case 0:
						view.checklist.load(view.choice.getSelectedFile().getAbsolutePath());
						break;
					case 1:
						view.checklist.save(view.choice.getSelectedFile().getAbsolutePath());
						break;
					}
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("error.InvalidChecklist"),
							L10n.getString("error.NothingSelected"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
		}
	}
	
	
	class ClearSelection extends AbstractAction {
		public ClearSelection() {
			System.out.println(System.getProperty("user.dir"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.ClearTT"));
            putValue(SMALL_ICON, Resource.createIcon("Clear.gif"));
        } 
		public void actionPerformed(ActionEvent arg0) {
			view.checklist.clearSelection();
		}
	}
}
