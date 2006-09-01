package net.sf.plantlore.client.createdb;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.l10n.L10n;


/**
 * The mapping of the button in the view to action
 * that opens the second part of the dialog - the Authentication.
 * 
 * Also the field validity check is performed.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class CreateDBCtrl {
	
	private CreateDBView view;
	private CreateDB model;
	
	private CreateDBAuthView authView;
	
	
	public CreateDBCtrl(CreateDB dbCreateModel, CreateDBView dbCreateView) {
		this.model = dbCreateModel;
		this.view = dbCreateView;
		
		authView = new CreateDBAuthView( view, model );
		/*authCtrl =*/ new CreateDBAuthCtrl( model, authView );
		
		view.next.setAction( new StandardAction("Login.Next") {
			public void actionPerformed(ActionEvent arg0) {
				String 
					engine = ((javax.swing.JTextField)view.databaseEngine.getEditor().getEditorComponent()).getText(),
					identifier = view.databaseIdentifier.getText(),
					alias = view.databaseAlias.getText();
				boolean leaveEmpty = view.leaveEmpty.isSelected();
				int port = -1;
				try { 
					port = Integer.parseInt(view.databasePort.getText());
				} catch(NumberFormatException e) { /* Leave it. */ }
				
				if( port < 0 || engine.length() == 0 || identifier.length() == 0 || alias.length() == 0 ) {
					// Announce the problem.
					JOptionPane.showMessageDialog(
    						view, 
    						L10n.getString("Error.MissingSeveralCompulsoryFields"), 
    						L10n.getString("Error.MissingCompulsoryFieldTitle"), 
    						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				model.setDBInfo(alias, engine, port, identifier, leaveEmpty); 
				authView.setVisible(true);
			}
		});
	}
	

}
