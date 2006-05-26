package net.sf.plantlore.client.imports;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;


public class ImportMngCtrl {
	
	private ImportMng model;
	private ImportMngView view;
	
	private ImportProgressView  progressView;
	
	public ImportMngCtrl(ImportMng model, ImportMngView view, ImportProgressView progressView) {
		this.model = model; this.view = view; this.progressView = progressView;
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = view.choice.showDialog(null, L10n.getString("Import.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				
				if(view.choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("Error.MissingFileName"),
							L10n.getString("Error.NothingSelected"),
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				model.setSelectedFile( view.choice.getSelectedFile().getAbsolutePath() );
				try {
					model.start();
					progressView.setVisible(true);
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("Error.ImportFailed") + e.getMessage(),
							L10n.getString("Import.Failed"),
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}


}
