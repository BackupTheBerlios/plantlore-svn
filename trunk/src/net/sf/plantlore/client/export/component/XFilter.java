package net.sf.plantlore.client.export.component;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

/**
 * An implementation of a FileFilter.
 * The XFilter stores a list of extensions
 * that are related to a particular format.
 * <br/>
 * For example: 
 * <code>Comma Separated Values (*.txt, *.csv)</code>
 * will store <code>".txt", ".csv"</code>.
 *  
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29
 * @version 1.0
 */
public class XFilter extends FileFilter {
	
	private String description;
	private ArrayList<String> extensions;
	private boolean columnSelection = false;
	
	/**
	 * Create a new XFilter.
	 * 
	 * @param description The description of the filter.
	 * @param extensions	The list of extensions that are related to this filter.
	 */
	public XFilter(String description, String... extensions) {
		this.description = description;
		this.extensions = new ArrayList<String>( extensions.length );
		for(String ext : extensions) this.extensions.add(ext);
	}
	
	/**
	 * Create a new XFilter.
	 * 
	 * @param description	The description of the filter.
	 * @param columnSelectionEnabled	The format allows further modifications.
	 * @param extensions	The list of extensions that are related to this filter.
	 */
	public XFilter(String description, boolean columnSelectionEnabled, String... extensions) {
		this(description, extensions);
		this.columnSelection = columnSelectionEnabled;
	}

	/**
	 * @return True if the column selection is available for this format.
	 */
	public boolean isColumnSelectionEnabled() {
		return this.columnSelection;
	}

	/**
	 * Decide whether the file meets the requierements - has the correct extension
	 * or it is in fact a directory.
	 */
	@Override
	public boolean accept(File file) {
		if( file != null) {
			if(file.isDirectory()) return true;
			String name = file.getName() ;
			int dot = name.lastIndexOf(".");
			if(dot < 0) return false;
			if(extensions.contains( name.substring(dot) )) return true;
		}
		return false;
	}

	/**
	 * @return The description of the format.
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * Decide whether the file already has a valid extension.
	 * A valid extension is an extension that belongs to
	 * the list of extensions of this format.
	 * 
	 * @param file	The file to be tested.
	 * @return	True if the file has a valid extension of this format.
	 */
	public boolean hasExtension(File file) {
		int dot = file.getName().lastIndexOf(".");
		if(dot < 0) return false;
		if(extensions.contains( file.getName().substring(dot) )) return true;
		return false;
	}
	
	/**
	 * Suggest the name for a file based on the list of extensions.
	 * If the file already has an extension, the name is not changed.
	 * An extension is added, if the file has not a valid extension. 
	 * 
	 * @param file	The file the name will be derived from.
	 * @return The suggested name for this file.
	 * @see net.sf.plantlore.client.export.component.XFilter#hasExtension(File)
	 */
	public String suggestName(File file) {
		if(hasExtension(file)) return file.getName();
		else return file.getName() + extensions.get(0);
	}
	
}