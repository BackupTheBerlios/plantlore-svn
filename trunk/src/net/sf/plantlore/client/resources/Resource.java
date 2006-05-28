package net.sf.plantlore.client.resources;

import java.net.URL;

import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * Resource manager. 
 * The class can return icons stored in the resource directory.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-16
 *	@version beta
 */
public class Resource {
    private static Logger logger = Logger.getLogger(Resource.class.getPackage().getName());
	/**
	 * Return an icon from the resource directory.
	 * 
	 * @param name	The name of the icon.
	 * @return	The ImageIcon or null if there is no such resource.
	 */
	public static ImageIcon createIcon(String name) {
		URL url = Resource.class.getResource(name);
		if(url == null) {
                    logger.warn("Missing resource for "+name);
                    return null;
                } else return new ImageIcon( url );
	}
}
