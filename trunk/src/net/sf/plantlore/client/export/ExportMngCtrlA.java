package net.sf.plantlore.client.export;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
				
				if(view.choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							"You must insert a name!",
						    "Nothing selected...",
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				model.setSelectedFile( view.choice.getSelectedFile().getAbsolutePath() );
				XFilter filter = (XFilter) view.choice.getFileFilter();
				model.setActiveFileFilter( filter );
				
				if( filter.isColumnSelectionEnabled() )
					viewB.setVisible(true);
				else try {
					model.start();
					progressView.setVisible(true);
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null,
							"Unable to start the export procedure!\n" + e,
						    "Export failed...",
						    JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

}
