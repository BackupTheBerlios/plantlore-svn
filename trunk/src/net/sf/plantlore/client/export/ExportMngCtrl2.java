package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.client.export.component.FileFormat;
import net.sf.plantlore.common.DefaultProgressBarEx;
import net.sf.plantlore.l10n.L10n;

public class ExportMngCtrl2 {
	
	
	private ExportMng2 model;
	private AppCoreView parentView;
	private JFileChooser choice;
	
	private ExportMngViewB viewB;
	

	
	public ExportMngCtrl2(ExportMng2 model, AppCoreView view) {
		this.model = model; 
		this.parentView = view;
		
		viewB = new ExportMngViewB(view);
		viewB.next.setAction( new NextAction() );
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		for( FileFilter filter: model.getFileFormats() )
			choice.addChoosableFileFilter(filter);
	}
	
	
	public void setVisible(boolean visible) {
		if(visible) {
			// The dialog must have a parent so that it is displayed correctly after ALT+TAB is pressed.
			int result = choice.showDialog(parentView, L10n.getString("Export.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				model.setSelectedFile( choice.getSelectedFile().getAbsolutePath() );
				FileFormat filter = (FileFormat) choice.getFileFilter();
				model.setActiveFileFilter( filter );
				
				if( filter.isColumnSelectionEnabled() )
					viewB.setVisible(true);
				else 
					performExport();
			}
		}
	}
	
	
	private class NextAction extends AbstractAction {
		public NextAction() {
			putValue(NAME, L10n.getString("Export.Title"));
		}
		public void actionPerformed(ActionEvent arg0) {
			Projection t = viewB.tsm.getProjections();
			viewB.setVisible(false);
			model.setTemplate( t ); // Set the new template.
			viewB.tsm.clearSelection();
			
			performExport();
		}
	}

	
	private void performExport() {
		try {
			ExportTask2 export = model.createExportTask();
			new DefaultProgressBarEx(export, parentView, true);
			export.start();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(parentView,
					e.getMessage(),
					L10n.getString("Export.Failed"),
					JOptionPane.WARNING_MESSAGE);
		}
	}

}
