package net.sf.plantlore.client.imports;

import java.awt.Frame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sf.plantlore.l10n.L10n;

@Deprecated
public class ImportMngCtrl {
	
	private ImportMng model;
	protected JFileChooser choice;
	private Frame parent;
	private ImportProgressView  progressView;
	
	public ImportMngCtrl(ImportMng model, Frame view, ImportProgressView progressView) {
		this.model = model; 
		this.progressView = progressView;
		this.parent = view;
		
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		for( FileFilter filter: model.getFilters() )
			choice.addChoosableFileFilter(filter);
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = choice.showDialog(parent, L10n.getString("Import.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				
				if(choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("Error.MissingFileName"),
							L10n.getString("Error.NothingSelected"),
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				model.setSelectedFile( choice.getSelectedFile().getAbsolutePath() );
				try {
					model.start();
					progressView.reset();
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
