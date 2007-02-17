package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import net.sf.plantlore.client.export.component.FileFormat;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.l10n.L10n;

/**
 * The mapping of the button in the view to action
 * that opens the second part of the dialog - the Column Selection -
 * or Creates and starts the Export task.
 * 
 * Also the field validity check is performed.
 * 
 * @author kaimu
 */
public class ExportMngCtrl2 {
	
	
	private ExportMng2 model;
	private JFrame parentView;
	private JFileChooser choice;
	
	private ExportMngViewB viewB;
	

	
	public ExportMngCtrl2(ExportMng2 model, JFrame view) {
		this.model = model; 
		this.parentView = view;
		
		viewB = new ExportMngViewB(view);
		viewB.next.setAction( new NextAction() );
		viewB.cancel.setAction( new DefaultCancelAction(view) );
		choice = new JFileChooser();
		choice.setAcceptAllFileFilterUsed(false);
		for( FileFilter filter: model.getFileFormats() )
			choice.addChoosableFileFilter(filter);
	}
	
	/**
	 * Let the User choose the name and format of the file and
	 * then either proceed to the column selection, or create and start the new Export task.
	 * 
	 * @param visible	True if the dialog should be opened.
	 */
	public void setVisible(boolean visible) {
		if(visible) {
			// The dialog must have a parent so that it is displayed correctly after ALT+TAB is pressed.
			int result = choice.showDialog(parentView, L10n.getString("Export.Title"));
			if( result == JFileChooser.APPROVE_OPTION ) {
				model.setSelectedFile( choice.getSelectedFile().getAbsolutePath() );
				FileFormat format = (FileFormat) choice.getFileFilter();
				model.setFileFormat( format );
				
				if( format.isColumnSelectionEnabled() ) {
					// Reset the the tree to the initial state.
					((net.sf.plantlore.client.export.component.ExtendedTree)(viewB.tree)).collapseAll();
					viewB.setVisible(true);
				}
				else 
					performExport();
			}
		}
	}
	
	/**
	 * Proceed to the column selection dialog or createa and start the Export task, 
	 * if the format doesn't support column selection. 
	 *
	 */
	private class NextAction extends StandardAction {
		public NextAction() {
			super("Export.Title");
		}
		public void actionPerformed(ActionEvent arg0) {
			// Get the list of projections.
			Projection t = viewB.tsm.getProjections();
			viewB.setVisible(false);
			// Supply the list of projections to the ExportManager (Export task factory).
			model.setProjections( t );
			model.useProjections(true);
			viewB.tsm.clearSelection();
			
			performExport();
		}
	}

	/**
	 * Create the Export task and start it.
	 */
	private void performExport() {
		try {
			ExportTask2 export = model.createExportTask();
			Dispatcher.getDispatcher().dispatch( export, parentView, true );
		} catch(Exception e) {
			DefaultExceptionHandler.handle(parentView, e, L10n.getString("Export.Failed"));
		}
	}

}
