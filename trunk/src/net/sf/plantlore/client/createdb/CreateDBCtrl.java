package net.sf.plantlore.client.createdb;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JOptionPane;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.l10n.L10n;


/**
 * The mapping of the button in the view to action
 * that opens the second part of the dialog - the Authentication.
 * 
 * Also the field validity check is performed.
 * 
 * @author kaimu
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
		
		view.cancel.setAction( new DefaultCancelAction(view) );
                view.databaseEngineAddFocusListener(new DatabaseEngineComboFocusListener());
		view.next.setAction( new StandardAction("Login.Next") {
			public void actionPerformed(ActionEvent arg0) {
				String 
					engine = ((javax.swing.JTextField)view.databaseEngine.getEditor().getEditorComponent()).getText(),
					identifier = view.databaseIdentifier.getText(),
					alias = view.databaseAlias.getText();
                                if (!identifier.matches("[A-Za-z0-9][A-Za-z0-9]*")) {
					JOptionPane.showMessageDialog(
    						view, 
    						L10n.getString("Error.BadDatabaseIdentifier"), 
    						L10n.getString("Error.BadDatabaseIdentifierTitle"), 
    						JOptionPane.ERROR_MESSAGE);   
                                        return;
                                }
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
				
				model.setDBInfo(alias, engine, port, identifier); 
				authView.setVisible(true);
			}
		});
	}
       
   /**
     *  Focus listener for the <strong>DatabaseEngine combobox</strong>. After losing focus
     *  automaticaly loads default port for the given database.
     */
    class DatabaseEngineComboFocusListener implements FocusListener {
        public void focusLost(FocusEvent e) {
            switch (view.getDatabaseEngine()) {
                case 0: // PostgreSQL
                    view.setDatabasePort(PlantloreConstants.POSTGRE_PORT);
                    break;
                case 1: // Firebird
                    view.setDatabasePort(PlantloreConstants.FIREBIRD_PORT);
                    break;                    
                case 2: // MySQL
                    view.setDatabasePort(PlantloreConstants.MYSQL_PORT);
                    break;                    
                case 3: // Oracle
                    view.setDatabasePort(PlantloreConstants.ORACLE_PORT);
                    break;
            }
        }        

        public void focusGained(FocusEvent e) {
            // Empty, no action when focus gained
        }
    }            	

}
