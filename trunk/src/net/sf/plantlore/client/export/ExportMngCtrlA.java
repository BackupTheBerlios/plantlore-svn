package net.sf.plantlore.client.export;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.client.export.component.XFilter;
import net.sf.plantlore.l10n.L10n;

public class ExportMngCtrlA {
	
	
	private ExportMng model;
	private AppCoreView view;
	private JFileChooser choice;
	
	private ExportMngViewB viewB;
	//private ExportMngCtrlB ctrlB;

	private ExportProgressView progressView;
	private ExportProgressCtrl progressCtrl;
	
	public ExportMngCtrlA(ExportMng model, AppCoreView view, 
			ExportProgressView progressView, ExportProgressCtrl progressCtrl) {
		this.model = model; this.view = view; 
		this.progressView = progressView; this.progressCtrl = progressCtrl;
		viewB = new ExportMngViewB(view);
		/*ctrlB = */new ExportMngCtrlB(model, viewB, progressView, progressCtrl);
		
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		for( FileFilter filter: model.getFilters() )
			choice.addChoosableFileFilter(filter);
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			// The dialog must have a parent so that it is displayed correctly above it after ALT+TAB is pressed.
			int result = choice.showDialog(view, L10n.getString("Export.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				
				if(choice.getSelectedFile() == null) {
//					JOptionPane.showMessageDialog(null,
//							L10n.getString("Error.MissingFileName"),
//							L10n.getString("Error.NothingSelected"),
//						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				model.setSelectedFile( choice.getSelectedFile().getAbsolutePath() );
				XFilter filter = (XFilter) choice.getFileFilter();
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
							L10n.getString("Error.ExportFailed") + e.getMessage(),
							L10n.getString("Export.Failed"),
						    JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

}
