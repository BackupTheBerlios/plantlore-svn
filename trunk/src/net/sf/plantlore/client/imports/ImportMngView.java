package net.sf.plantlore.client.imports;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class ImportMngView {
	
	private ImportMng model;
	protected JFileChooser choice;
	
	
	public ImportMngView(ImportMng model) {
		this.model = model;
		initComponents();
	}
	
	
	private void initComponents() {
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		for( FileFilter filter: model.getFilters() )
			choice.addChoosableFileFilter(filter);
	}
	
	
	
}
