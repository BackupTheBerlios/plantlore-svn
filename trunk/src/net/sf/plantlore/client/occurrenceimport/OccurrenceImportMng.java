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

public class OccurrenceImportMng {
	
	private Logger logger = Logger.getLogger(OccurrenceImportMng.class.getPackage().getName());
	private DBLayer db; 
	private Observer tableChangeObserver;
	
	public OccurrenceImportMng(DBLayer db, Observer tableChangeObserver) {
		this.db = db;
		this.tableChangeObserver = tableChangeObserver;
	}
	
	public void setDBLayer(DBLayer dblayer) {
		this.db = dblayer;
	}
	
	/**
	 * 
	 */
	synchronized public Task createOccurrenceImportTask(String filename) 
	throws ImportException, IOException, RemoteException, SAXException {
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
		
		OccurrenceParser parser = new XMLOccurrenceParser(reader);
		Task occurrenceImportTask = new OccurrenceImportTask(db, parser);
		occurrenceImportTask.addObserver( tableChangeObserver );
		
		return occurrenceImportTask;
	}


}
