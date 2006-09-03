package net.sf.plantlore.client.createdb;

import java.awt.event.ActionEvent;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;

/**
 * The mapping of the button in the view to actions.
 * The User can click to start the Creation of the database using the previously 
 * supplied data.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class CreateDBAuthCtrl {
	
	private CreateDBAuthView view;
	private CreateDB model;
	
	public CreateDBAuthCtrl(CreateDB createModel, CreateDBAuthView authView) {
		this.model = createModel; 
		this.view = authView;
		
		view.cancel.setAction( new DefaultCancelAction(view) );

		view.create.setAction(new StandardAction("CreateDB.Create") {
			public void actionPerformed(ActionEvent arg0) {
				String user = view.user.getText();

				Task creation = model.createCreationTask(user, new String(view.password.getPassword()));
				new DefaultProgressBar(creation, view, true);
				creation.start();
				
				view.password.setText("");
			}
		});
	}

}
