package net.sf.plantlore.client.imports;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import net.sf.plantlore.client.imports.Parser.Action;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;


public class ImportMng extends Observable implements Observer {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	private DBLayer db ;
	private String filename;
	private DefaultDirector director;
	private Parser parser;
	private User user;
	private boolean aborted = false, importInProgress = false;
	
	private Reader reader;
	private Thread current;
	
	
	public ImportMng(DBLayer db, User user, String filename) 
	throws ImportException {
		setDBLayer(db);
		setUser(user);
		setSelectedFile(filename);
	}
	
	
	public ImportMng(DBLayer db, User user) 
	throws ImportException {
		this(db, user, null);
	}
	
	
	
	synchronized public void setDBLayer(DBLayer dblayer) 
	throws ImportException {
		if(dblayer == null) { 
			logger.error("The database layer is null!");
			throw new ImportException("The database layer cannot be null!");
		}
		db = dblayer;
	}
	
	synchronized public void setUser(User user) 
	throws ImportException {
		if(user == null) { 
			logger.error("The user is null!");
			throw new ImportException("The user cannot be null!");
		}
		this.user = user;
	}
	
	
	/**
	 * Set the selected file. Into this file the builder will 
	 * spit its output. 
	 */
	synchronized public void setSelectedFile(String filename) { 
		if(filename == null)
			logger.warn("The selected file is null!");
		this.filename = filename; 
	}
	
	
	
	public void makeDecision(Action decision) {
		if(director != null) 
			director.makeDecision(decision);
	}
	
	
	public void setAskAboutTime(boolean arg) {
		if(director != null) 
			director.useLastDecisionInTimeIssues(arg);
	}
	
	
	public void setAskAboutInsert(boolean arg) {
		if(director != null) 
			director.useLastDecisionInUpdateInsertIssues(arg);
	}
	
	/**
	 * Start the import procedure. The import will run in its own thread.
	 * 
	 * @throws ImportException	If the information provided is not complete.
	 * @throws IOException	If anything with the file goes wrong (insufficient disk space, insufficient permissions).
	 */
	synchronized public void start() 
	throws ImportException, IOException {
		// Check if we have all necessary components ready.
		if( db == null )
			throw new ImportException("There is no point in starting an import - the DBLayer is not set!");
		if( filename == null ) 
			throw new ImportException("The Filename is not set!");
			
		
		logger.debug("Initializing the import environment.");
		aborted = false;
		
		// Create a new reader.
		File file = new File( filename );
		reader = new FileReader( file );
		if(reader == null) {
			logger.fatal("Unable to create a new Reader.");
			throw new ImportException("Unable to create a new Reader.");
		}
		
		// Create a new parser according to the format.
		//parser = new ...
		

		// Create a new Director and run it in a separate thread.
		director = new DefaultDirector(db, parser, user);
		director.addObserver(this);
		
		current = new Thread( director, "Export" );
		if(current == null) {
			logger.fatal("Unable to create a new thread.");
			throw new ImportException("Unable to create a new thread.");
		}
		current.start();
		
		importInProgress = true;
		
		// Register a cleanup procedure
		Thread monitor = new Thread(new Runnable() {
			public void run() {
				// Sleep until the thread is really dead.
				while( !universeImploded )
					try {
						current.join();
						break;
					}catch(InterruptedException e) {} // FIXME: join the thread again
				// Dispose of the writer.
				try {
					reader.close();
				}catch(IOException e) {}
				importInProgress = false;
				logger.debug("Environment cleaned up.");
				// Notify observers the export has ended.
				update(null, null);
			}
		}, "ImportMonitor");
		monitor.start();
	}
	
	
	private boolean universeImploded = false;

	/**
	 * Abort the current export. You <b>must call</b> <code>finish()</code> 
	 * after calling <code>abort()</code>. 
	 */
	synchronized public void abort() {
		if(!importInProgress) return;
		aborted = true; importInProgress = false;
		if(director != null) director.abort();
		setChanged(); notifyObservers();
	}
	
	/**
	 * @return True if the export was aborted.
	 */
	public boolean isAborted() {
		return aborted;
	}
	
	/**
	 * @return True if an export procedure already runs.
	 */
	public boolean isImportInProgress() {
		return importInProgress;
	}
	
	
	/**
	 * @return The number of results that have already been exported.
	 */
	public int getNumberOfImported() {
		if(director == null) return 0;
		return director.importedRecords();
	}
	
	
	public Record getProcessedRecordFromFile() {
		return (director == null) ? null : director.getProcessedRecordFromFile();
	}
	
	
	public Record getProcessedRecordInDatabase() {
		return (director == null) ? null : director.getProcessedRecordInDatabase();
	}
	
	public Record getProblematicRecord() {
		return (director == null) ? null : director.getProblematic();
	}
	
	
	/**
	 * Notify the observers - some of our components has changed its state.
	 * The parameter can carry either information about progress
	 * or an exception that has to be dealt with.
	 */
	public void update(Observable source, Object parameter) {
		setChanged(); notifyObservers( parameter );
	}


}
