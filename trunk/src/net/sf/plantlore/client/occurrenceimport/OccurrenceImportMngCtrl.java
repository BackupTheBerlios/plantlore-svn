package net.sf.plantlore.client.occurrenceimport;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.client.export.component.FileFormat;

import net.sf.plantlore.common.DefaultProgressBarEx;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;

public class OccurrenceImportMngCtrl {
	
	private OccurrenceImportMng model;
	private JFileChooser choice;
	private AppCoreView view;

	public OccurrenceImportMngCtrl(OccurrenceImportMng model, AppCoreView view) {
		this.model = model;
		this.view = view;
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		choice.addChoosableFileFilter( new FileFormat(L10n.getString("Format.XML"), true, true, ".xml") ); 
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = choice.showDialog(view, L10n.getString("Import.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				if(choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(view,
							L10n.getString("Error.MissingFileName"),
							L10n.getString("Error.NothingSelected"),
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					Task task = model.createOccurrenceImportTask( choice.getSelectedFile().getAbsolutePath() );
					new DefaultProgressBarEx(task, view, true);
					task.start();
				} catch(Exception e) {
					JOptionPane.showMessageDialog(view,
							L10n.getString("Error.ImportFailed") + "\n" + e.getMessage(),
							L10n.getString("Import.Failed"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
}
