package net.sf.plantlore.client.export;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.export.component.XFilter;
import net.sf.plantlore.l10n.L10n;

public class ExportMngCtrlA {
	
	
	private ExportMng model;
	private ExportMngViewA view;
	
	private ExportMngViewB viewB;
	//private ExportMngCtrlB ctrlB;

	private ExportProgressView progressView;
	private ExportProgressCtrl progressCtrl;
	
	public ExportMngCtrlA(ExportMng model, ExportMngViewA view, 
			ExportProgressView progressView, ExportProgressCtrl progressCtrl) {
		this.model = model; this.view = view; 
		this.progressView = progressView; this.progressCtrl = progressCtrl;
		viewB = new ExportMngViewB();
		/*ctrlB = */new ExportMngCtrlB(model, viewB, progressView, progressCtrl);
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = view.choice.showDialog(null, "Export");
			if( result == JFileChooser.APPROVE_OPTION ) {
				
				if(view.choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("error.MissingFileName"),
							L10n.getString("error.NothingSelected"),
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				model.setSelectedFile( view.choice.getSelectedFile().getAbsolutePath() );
				XFilter filter = (XFilter) view.choice.getFileFilter();
				model.setActiveFileFilter( filter );
				
				if( filter.isColumnSelectionEnabled() )
					viewB.setVisible(true);
				else try {
					ExportTask task = model.createExportTask();
					progressCtrl.setModel(task); 
					progressView.setModel(task);
					
					task.execute();
					
					progressView.setVisible(true);
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("error.ExportFailed") + e,
							L10n.getString("export.Failed"),
						    JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

}
