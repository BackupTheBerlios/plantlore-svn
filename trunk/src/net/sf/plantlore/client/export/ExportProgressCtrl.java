package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;

//import net.sf.plantlore.l10n.L10n;

public class ExportProgressCtrl {
	
	private ExportMng model;
	private ExportProgressView view;
	
		
	
	public ExportProgressCtrl(ExportMng model, ExportProgressView view) {
		this.model = model; this.view = view;
		view.abort.addActionListener( new Abort() );
	}
	
	class Abort extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			if(model.isExportInProgress()) {
				int response =
					JOptionPane.showOptionDialog(view,
							L10n.getString("question.AbortImport"),
							L10n.getString("export.Aborted"),
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
