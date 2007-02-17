package net.sf.plantlore.common;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.l10n.L10n;

/**
 * Standard Action introduces the extended constructor that can
 * automatically assign a caption, a short description,
 * and an image to the button. 
 * 
 * @author kaimu
 */
public abstract class StandardAction extends AbstractAction {
	
	/**
	 * Create a new StandardAction.
	 * 
	 * @param text The base string that will be used to obtain the caption, the tooltip text
	 * and an image from the L10N.
	 */
	public StandardAction(String text) {
		putValue(NAME, L10n.getString(text));
		putValue(SHORT_DESCRIPTION, L10n.getString(text+"TT"));
        ImageIcon icon = Resource.createIcon(text+".gif");
		if(icon != null) putValue(SMALL_ICON, icon);
	}


}
