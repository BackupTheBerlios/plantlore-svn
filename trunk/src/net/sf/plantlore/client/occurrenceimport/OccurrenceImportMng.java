package net.sf.plantlore.client.occurrenceimport;

import static net.sf.plantlore.client.export.ExportMng2.ENCODING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.Observer;

import net.sf.plantlore.client.occurrenceimport.parsers.XMLOccurrenceParser;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * The Occurrence Import manager serves as an Occurrence Import Task factory. 
 * The Occurrence Import Manager gathers all information
 * needed for the creation of a new Occurrence Import Task.
 * <br/>
 * In order to create a new Occurrence Import task these information must be supplied:
 * <ul>   
 * <li>dblayer	The database layer mediating the access to the database.</li>
 * <li>filename	The name of the file where the records are stored.</li>
 * </ul>
 * <br/>
 * The Occurrence Import manager requires an Observer as well - 
 * this Observer is notified if some table was modified during the Import
 * so that the Application can reload the up-to-date content of
 * those modified tables.
 * 
 * @author kaimu
 * @since 2006-06-29 
 * @version 1.0
 */
public class OccurrenceImportMng {
	
	private Logger logger = Logger.getLogger(OccurrenceImportMng.class.getPackage().getName());
	private DBLayer db; 
	private Observer tableChangeObserver;
	
	/**
	 * Create a new Occurrence Import Manager. 
	 * 
	 * @param db		The database layer mediating the access to the database.
	 * @param tableChangeObserver	The observer that will be notified after the Occurrence Import task
	 * ends so that the content of the modified tables is reloaded from the database and distributed
	 * to other parts of the application.
	 */
	public OccurrenceImportMng(DBLayer db, Observer tableChangeObserver) {
		this.db = db;
		this.tableChangeObserver = tableChangeObserver;
	}
	
	/**
	 * 
	 * @param dblayer	Set a new database layer.
	 */
	public void setDBLayer(DBLayer dblayer) {
		this.db = dblayer;
	}
	
	/**
	 * Construct a new Occurrence Import task.
	 * 
	 * @param filename	The name of the file where the Occurrences are stored.
	 * @return	The task that will perform the import.
	 */
	synchronized public Task createOccurrenceImportTask(String filename) 
	throws ImportException, IOException, RemoteException, SAXException {
		// Check if we have all necessary components ready.
		if( db == null )
			throw new ImportException(L10n.getString("Error.InvalidDBLayer"));
		if( filename == null ) 
			throw new ImportException(L10n.getString("Error.MissingFileName"));
			
		logger.debug("Initializing the occurrence-import environment.");
		
		// Create a new reader.
		File file = new File( filename );
		if( file.isDirectory() ) {
			logger.error("Cannot import data from a directory - you must select a file.");
			throw new ImportException(L10n.getString("Error.InvalidFileName"));
		}
		Reader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file),
				ENCODING));
		if(reader == null) {
			logger.error("Unable to create a new Reader.");
			throw new ImportException(L10n.getString("Error.ReaderNotCreated"));
		}
		
		logger.debug("Preparing new OccurrenceImport task.");
		// Only one kind of parser is available - the XML parser.
		OccurrenceParser parser = new XMLOccurrenceParser(reader);
		Task occurrenceImportTask = new OccurrenceImportTask(db, parser);
		occurrenceImportTask.addObserver( tableChangeObserver );
		
		logger.debug("OccurrenceImport task prepared.");
		return occurrenceImportTask;
	}


}
