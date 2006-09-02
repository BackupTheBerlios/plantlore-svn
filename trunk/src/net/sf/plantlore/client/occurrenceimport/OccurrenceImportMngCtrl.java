package net.sf.plantlore.client.occurrenceimport;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.client.export.component.FileFormat;

import net.sf.plantlore.common.DefaultProgressBarEx;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;

/**
 * Allow the User to specify the name of the file where the Occurrence records
 * are stored. Then create an Occurrence Import task and start it.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-14
 */
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
	
	/**
	 * Display the dialog where the User can choose the file name,
	 * create the Occurrence Import task and start it.
	 * 
	 * @param visible	True if the GUI should become visible.
	 */
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
