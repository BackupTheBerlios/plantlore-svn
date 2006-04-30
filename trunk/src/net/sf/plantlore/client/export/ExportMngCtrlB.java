package net.sf.plantlore.client.export;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


//import net.sf.plantlore.l10n.L10n;

public class ExportMngCtrlB {
	
	private ExportMng model;
	private ExportMngViewB view;
	private ExportProgressView progressView;
	
	public ExportMngCtrlB(ExportMng model, ExportMngViewB view, ExportProgressView progressView) {
		this.model = model; this.view = view; this.progressView = progressView;
		view.next.addActionListener( new Next() );
	}
	
	
	class Next extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			Template t = view.tsm.getTemplate();
			if( t.isEmpty() )
				JOptionPane.showMessageDialog(view,
					"You must select some columns!\nThere would be no point in running the export procedure otherwise.",
				    "Template empty",
				    JOptionPane.WARNING_MESSAGE);
			else {
				view.setVisible(false);
				model.setTemplate( t ); // Set the new template.
				try {
					model.start();
					progressView.setVisible(true);
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(view,
							"Unable to start the export procedure!\n" + e,
						    "Export failed...",
						    JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

}
