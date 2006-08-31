package net.sf.plantlore.client.createdb;

import java.awt.event.ActionEvent;

import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;


public class CreateDBAuthCtrl {
	
	private CreateDBAuthView view;
	private CreateDB model;
	
	public CreateDBAuthCtrl(CreateDB createModel, CreateDBAuthView authView) {
		this.model = createModel; 
		this.view = authView;

		view.create.setAction(new StandardAction("CreateDB.Create") {
			public void actionPerformed(ActionEvent arg0) {
				String user = view.user.getText();

				Task creation = model.createCreationTask(user, new String(view.password.getPassword()));
				new DefaultProgressBar(creation, view, true);
				creation.start();
			}
		});
	}

}
