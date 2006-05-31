package net.sf.plantlore.client.imports.table;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.export.component.XFilter;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;


public class TableImportMngCtrl {
	
	private TableImportMng model;
	private JFileChooser choice;
	
	public TableImportMngCtrl(TableImportMng model) {
		this.model = model;
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		choice.addChoosableFileFilter( new XFilter("Update files (*.xml)", "*.xml") );
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			int result = choice.showDialog(null, L10n.getString("Import.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				if(choice.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null,
							L10n.getString("Error.MissingFileName"),
							L10n.getString("Error.NothingSelected"),
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					Task task = model.createTableImportTask(choice.getSelectedFile().getAbsolutePath(), null);
					new TableImportProgressBar(task, null);
					task.start();
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

