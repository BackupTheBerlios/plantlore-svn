package net.sf.plantlore.client.imports;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;


public class ImportProgressCtrl {
	
	
	private ImportMng model;
	private ImportProgressView view;
	
		
	
	public ImportProgressCtrl(ImportMng model, ImportProgressView view) {
		this.model = model; this.view = view;
		view.abort.addActionListener( new Abort() );
	}
	
	class Abort extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			if(model.isImportInProgress()) {
				int response =
					JOptionPane.showOptionDialog(view,
							L10n.getString("Question.AbortImport"),
							L10n.getString("Import.Aborted"),
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							null,
							null);
				if(response == JOptionPane.OK_OPTION) {
					model.abort();
				}
			}
			else
				view.setVisible(false);
		}
	}
	

}
