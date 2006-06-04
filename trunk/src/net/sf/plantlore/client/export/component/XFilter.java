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
	private boolean ignoreDead = true;
	
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
	public XFilter(String description, boolean columnSelectionEnabled, boolean ignoreDead, String... extensions) {
		this(description, extensions);
		this.columnSelection = columnSelectionEnabled;
		this.ignoreDead = ignoreDead;
	}

	/**
	 * @return True if the column selection is available for this format.
	 */
	public boolean isColumnSelectionEnabled() {
		return this.columnSelection;
	}
	
	/**
	 *	@return True if dead records should be omited.
	 */
	public boolean ignoreDead() {
		return ignoreDead;
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
	 * @param filename	The file to be tested.
	 * @return	True if the file has a valid extension of this format.
	 */
	public boolean hasExtension(String filename) {
		int dot = filename.lastIndexOf(".");
		if(dot < 0) return false;
		if(extensions.contains( filename.substring(dot) )) return true;
		return false;
	}
	
	/**
	 * Suggest the name for a file based on the list of extensions.
	 * If the file already has an extension, the name is not changed.
	 * An extension is added, if the file has not a valid extension. 
	 * 
	 * @param filename	The name of the file.
	 * @return The suggested name for this file.
	 * @see net.sf.plantlore.client.export.component.XFilter#hasExtension(String)
	 */
	public String suggestName(String filename) {
		if(hasExtension(filename)) 
			return filename;
		return filename + extensions.get(0);
	}
	
}