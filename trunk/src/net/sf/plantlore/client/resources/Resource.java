package net.sf.plantlore.client.resources;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Resource manager. 
 * The class can return icons stored in the resource directory.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-16
 *	@version beta
 */
public class Resource {

	/**
	 * Return an icon from the resource directory.
	 * 
	 * @param name	The name of the icon.
	 * @return	The ImageIcon or null if there is no such resource.
	 */
	public static ImageIcon createIcon(String name) {
		URL url = Resource.class.getResource(name);
		if(url == null) return null;
		else return new ImageIcon( url );
	}
}
