package net.sf.plantlore.client.imports.table;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.client.export.component.FileFormat;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;


public class TableImportMngCtrl {
	
	private TableImportMng model;
	private JFileChooser choice;
	private AppCoreView view;
	
	public TableImportMngCtrl(TableImportMng model, AppCoreView view) {
		this.model = model;
		this.view = view;
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		choice.addChoosableFileFilter( new FileFormat("Table update files (*.xml)", ".xml") );
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
					Task task = model.createTableImportTask(choice.getSelectedFile().getAbsolutePath(), null);
					new DefaultProgressBar(task, view, true);
					task.start();
				} catch(Exception e) {
					JOptionPane.showMessageDialog(view,
							L10n.getString("Error.ImportFailed") + " " + e.getMessage(),
							L10n.getString("Import.Failed"),
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

}

