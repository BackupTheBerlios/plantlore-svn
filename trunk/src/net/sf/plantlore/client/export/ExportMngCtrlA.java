package net.sf.plantlore.client.export;

import javax.swing.JFileChooser;

import net.sf.plantlore.client.export.component.XFilter;

public class ExportMngCtrlA {
	
	
	private ExportMng model;
	private ExportMngViewA view;
	
	private ExportMngViewB viewB;
	//private ExportMngCtrlB ctrlB;

	private ExportProgressView  progressView;
	
	public ExportMngCtrlA(ExportMng model, ExportMngViewA view, ExportProgressView progressView) {
		this.model = model; this.view = view; this.progressView = progressView;
		viewB = new ExportMngViewB();
		/*ctrlB = */new ExportMngCtrlB(model, viewB, progressView);
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = view.choice.showDialog(null, "Export");
			if( result == JFileChooser.APPROVE_OPTION ) {
				model.setSelectedFile( view.choice.getSelectedFile() );
				XFilter filter = (XFilter) view.choice.getFileFilter();
				model.setActiveFileFilter( filter );
				
				if( filter.isColumnSelectionEnabled() )
					viewB.setVisible(true);
				else
					progressView.setVisible(true);
			}
		}
	}

}
