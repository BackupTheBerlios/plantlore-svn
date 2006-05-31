package net.sf.plantlore.client.imports.table;

import javax.swing.JOptionPane;

import net.sf.plantlore.common.ProgressBar;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;

public class TableImportProgressBar extends ProgressBar {
	
	
	public TableImportProgressBar(Task task, java.awt.Frame parent) {
		super(task, parent, true);
	}

	
	@Override
	public void exceptionHandler(Exception ex) {
		JOptionPane.showMessageDialog( 
				null, 
				ex.getMessage(), 
				L10n.getString("Import.Failed"), 
				JOptionPane.ERROR_MESSAGE );
		getTask().stop();
	}

}
