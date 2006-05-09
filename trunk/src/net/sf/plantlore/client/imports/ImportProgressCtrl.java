package net.sf.plantlore.client.imports;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


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
							"The import procedure will be aborted.",
							"Abort import",
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
