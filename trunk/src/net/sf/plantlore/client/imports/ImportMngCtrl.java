package net.sf.plantlore.client.imports;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class ImportMngCtrl {
	
	private ImportMng model;
	private ImportMngView view;
	
	private ImportProgressView  progressView;
	
	public ImportMngCtrl(ImportMng model, ImportMngView view, ImportProgressView progressView) {
		this.model = model; this.view = view; this.progressView = progressView;
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = view.choice.showDialog(null, "Import");
			if( result == JFileChooser.APPROVE_OPTION ) {
				
				if(view.choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							"You must insert a name!",
						    "Nothing selected...",
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				model.setSelectedFile( view.choice.getSelectedFile().getAbsolutePath() );
				try {
					model.start();
					progressView.setVisible(true);
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null,
							"Unable to start the import procedure!\n" + e,
							"Import failed...",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}


}
