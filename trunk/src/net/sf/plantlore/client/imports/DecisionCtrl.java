package net.sf.plantlore.client.imports;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.plantlore.client.imports.Parser.Action;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.l10n.L10n;

public class DecisionCtrl {
	
	ImportMng model;
	DecisionView view;
	
	
	public DecisionCtrl(ImportMng model, DecisionView view) {
		this.model = model; this.view = view;
		view.leave.setAction( new Skip() );
		view.update.setAction( new Update() );
		view.remember.setAction( new RememberDecision() );
	}

	
	class Skip extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			Object issue = model.getIssue();
			if(issue instanceof Record)
				if(issue instanceof Occurrence)
					model.makeDecision(Action.UNKNOWN);
				else
					model.makeDecision(Action.INSERT);
			view.setVisible(false);
		}
	}
	
	
	class Update extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			Object issue = model.getIssue();
			if(issue instanceof Record)
				if(issue instanceof Occurrence)
					model.makeDecision(Action.UPDATE);
				else
					model.makeDecision(Action.UPDATE);
			view.setVisible(false);
		}
	}
	
	class RememberDecision extends AbstractAction {
		public RememberDecision() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Import.RememberDecisionTT"));
			putValue(NAME, L10n.getString("Import.RememberDecision"));
		}
		public void actionPerformed(ActionEvent arg0) {
			Object issue = model.getIssue();
			if(issue instanceof Record)
				if(issue instanceof Occurrence)
					model.setAskAboutTime( view.remember.isSelected() );
				else
					model.setAskAboutInsert( view.remember.isSelected() );
		}
	}
	
}
