package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;


//import net.sf.plantlore.l10n.L10n;

public class ExportMngCtrlB {
	
	private ExportMng model;
	private ExportMngViewB view;
	private ExportProgressView progressView;
	private ExportProgressCtrl progressCtrl;
	
	public ExportMngCtrlB(ExportMng model, ExportMngViewB view, 
			ExportProgressView progressView,  ExportProgressCtrl progressCtrl) {
		this.model = model; this.view = view; 
		this.progressView = progressView; this.progressCtrl = progressCtrl;
		view.next.addActionListener( new Next() );
	}
	
	
	class Next extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			Template t = view.tsm.getTemplate();
			if( t.isEmpty() )
				JOptionPane.showMessageDialog(view,
					L10n.getString("error.NoColumnsSelected"),
				    L10n.getString("error.NothingSelected"),
				    JOptionPane.WARNING_MESSAGE);
			else {
				view.setVisible(false);
				try {
					model.setTemplate( t ); // Set the new template.

					ExportTask task = model.createExportTask();
					progressCtrl.setModel(task); 
					progressView.setModel(task);
					
					task.execute();
					
					progressView.setVisible(true);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(view,
							L10n.getString("error.ExportFailed") + e,
							L10n.getString("export.Failed"),
						    JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

}
