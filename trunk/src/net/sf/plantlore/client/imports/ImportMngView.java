package net.sf.plantlore.client.imports;

import javax.swing.JFileChooser;


public class ImportMngView {
	
	private ImportMng model;
	protected JFileChooser choice;
	
	
	public ImportMngView(ImportMng model) {
		this.model = model;
		initComponents();
	}
	
	
	private void initComponents() {
		choice = new JFileChooser();
	}
	
	
	
}
