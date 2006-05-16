package net.sf.plantlore.client.resources;

import javax.swing.ImageIcon;

public class Resource {

	public static ImageIcon createIcon(String name) {
		return new ImageIcon( Resource.class.getResource(name) );
	}
}
