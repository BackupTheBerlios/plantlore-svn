package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;


//import net.sf.plantlore.l10n.L10n;
@Deprecated
public class ExportMngCtrlB {
	
	private ExportMng model;
	private ExportMngViewB view;
	private ExportProgressView progressView;
	private ExportProgressCtrl progressCtrl;
	
	public ExportMngCtrlB(ExportMng model, ExportMngViewB view, 
			ExportProgressView progressView,  ExportProgressCtrl progressCtrl) {
		this.model = model; this.view = view; 
		this.progressView = progressView; this.progressCtrl = progressCtrl;
		view.next.setAction( new Next() );
	}
	
	
	class Next extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			Projection t = view.tsm.getTemplate();
			view.setVisible(false);
			
			
			try {
				model.setTemplate( t ); // Set the new template.
				
				ExportTask task = model.createExportTask();
				progressCtrl.setModel(task); 
				progressView.setModel(task);
				
				task.execute();
				
				progressView.setVisible(true);
				
				view.tsm.clearSelection();
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(view,
						L10n.getString("Error.ExportFailed") + e.getMessage(),
						L10n.getString("Export.Failed"),
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

}
