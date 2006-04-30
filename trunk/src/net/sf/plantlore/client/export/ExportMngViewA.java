package net.sf.plantlore.client.export;


import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class ExportMngViewA {
	
	private ExportMng model;
	protected JFileChooser choice;
	
	
	
	public ExportMngViewA(ExportMng model) {
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
