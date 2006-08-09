package net.sf.plantlore.common;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.l10n.L10n;

public abstract class StandardAction extends AbstractAction {
	
	public StandardAction(String text) {
		putValue(NAME, L10n.getString(text));
		putValue(SHORT_DESCRIPTION, L10n.getString(text+"TT"));
        ImageIcon icon = Resource.createIcon(text+".gif");
		if(icon != null) putValue(SMALL_ICON, icon);
	}


}
