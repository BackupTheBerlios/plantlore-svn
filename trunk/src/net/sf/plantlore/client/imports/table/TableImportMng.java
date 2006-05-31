package net.sf.plantlore.client.imports.table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import net.sf.plantlore.client.imports.table.parsers.UnifiedTableParser;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import static net.sf.plantlore.client.export.ExportMng.ENCODING;

/**
 * ImportTask factory.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-08
 * @version 1.0
 */
public class TableImportMng {
	
	private Logger logger = Logger.getLogger(TableImportMng.class.getPackage().getName());
	private DBLayer db; 
	
	public TableImportMng(DBLayer db) 
	throws ImportException {
		setDBLayer( db );
	}
	
	synchronized public void setDBLayer(DBLayer dblayer) 
	throws ImportException {
		if(dblayer == null) { 
			logger.error("The database layer is null!");
			throw new ImportException(L10n.getString("Error.InvalidDBLayer"));
		}
		db = dblayer;
	}
	
	/**
	 * Start the import procedure. The import will run in its own thread.
	 */
	synchronized public Task createTableImportTask(String filename, Class table) 
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
		
		TableParser parser = null;
		try {
			UnifiedTableParser p = new UnifiedTableParser(reader, null);
			table = p.getRootTable();
			parser = p;
		} catch(ParserException e) {
			logger.fatal("The format of the file is corrupted!");
			throw new ImportException(L10n.getString("Error.FileFormatCorrupted"));
		}

		return new TableImportTask(db, table, parser);
	}

	
}
