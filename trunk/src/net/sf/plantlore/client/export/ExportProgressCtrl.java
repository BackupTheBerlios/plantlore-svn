package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

//import net.sf.plantlore.l10n.L10n;

public class ExportProgressCtrl implements Observer {
	
	private ExportMng model;
	private ExportProgressView view;
	
	private Action abort = new Abort(), close = new Close(), current = close;
	
	
	
	public ExportProgressCtrl(ExportMng model, ExportProgressView view) {
		this.model = model; this.view = view;
		view.abort.addActionListener( current );
		model.addObserver(this);
	}
	
	class Abort extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			int response =
				JOptionPane.showOptionDialog(view,
					    "The export procedure will be aborted.",
					    "Abort export",
					    JOptionPane.OK_CANCEL_OPTION,
					    JOptionPane.WARNING_MESSAGE,
					    null,
					    null,
					    null);
				if(response == JOptionPane.OK_OPTION) {
					model.abort();
				}
		}
	}
	
	class Close extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			view.setVisible(false);		
		}
	}

	/**
	 * Switch the behaviour of the abort button appropriatelly.
	 */
	public void update(Observable source, Object parameter) {
		boolean runs = model.isExportInProgress();
		if(runs && current == close) {
			view.abort.removeActionListener(current);
			current = abort;
			view.abort.addActionListener(current);
			view.abort.setText("Abort");
		} else if(!runs && current == abort) {
			view.abort.removeActionListener(current);
			current = close;
			view.abort.addActionListener(current);
			view.abort.setText("Close");
		}
		
		
	}

}
