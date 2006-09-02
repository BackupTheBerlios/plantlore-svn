package net.sf.plantlore.client.tableimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.Observer;

import org.apache.log4j.Logger;

import net.sf.plantlore.client.tableimport.parsers.UnifiedTableParser;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import static net.sf.plantlore.client.export.ExportMng2.ENCODING;

/**
 * The Table Import manager serves as a Table Import Task factory. 
 * The Table Import Manager gathers all information needed for the creation of a new Table Import Task.
 * <br/>
 * In order to create a new Table Import task these information must be supplied:
 * <ul>   
 * <li>dblayer	The database layer mediating the access to the database.</li>
 * <li>filename	The name of the file where the records are stored.</li>
 * </ul>
 * <br/>
 * The Table Import manager requires an Observer as well - 
 * this Observer is notified if the immutable table is modified during the Import
 * so that the Application can reload the up-to-date content of
 * that modified table.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-08 
 * @version 1.0
 */
public class TableImportMng {
	
	private Logger logger = Logger.getLogger(TableImportMng.class.getPackage().getName());
	private DBLayer db; 
	private Observer tableChangeObserver;
	
	/**
	 * Create a new Occurrence Import Manager. 
	 * 
	 * @param db		The database layer mediating the access to the database.
	 * @param tableChangeObserver	The observer that will be notified after the Table Import task
	 * ends so that the content of the modified table is reloaded and redistributed
	 * to other parts of the application.
	 */
	public TableImportMng(DBLayer db, Observer tableChangeObserver) {
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
	 * Construct a new Table Import task.
	 * 
	 * @param filename	The name of the file where the records are stored.
	 * @return	The task that will perform the import.
	 */
	synchronized public Task createTableImportTask(String filename) 
	throws ImportException, IOException, RemoteException {
		// Check if we have all necessary components ready.
		if( db == null )
			throw new ImportException(L10n.getString("Error.InvalidDBLayer"));
		if( filename == null ) 
			throw new ImportException(L10n.getString("Error.MissingFileName"));
			
		logger.debug("Initializing the table-import environment.");
		
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
		
		UnifiedTableParser parser = new UnifiedTableParser(reader);
		Task tableImportTask = new TableImportTask(db, parser);
		tableImportTask.addObserver( tableChangeObserver );
		
		return tableImportTask;
	}

	
}
