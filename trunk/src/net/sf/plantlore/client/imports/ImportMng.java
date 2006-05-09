package net.sf.plantlore.client.imports;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

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
	
	
	/**
	 * Create a new Import Manager. 
	 * This manager is primarily intented for import of the occurrence data.
	 * 
	 * @param db	The database layer.
	 * @param user	The User that is currently logged in.
	 * @param filename	The name of the file where the data reside.
	 * @throws ImportException	If some of the parameters are not valid.
	 */
	public ImportMng(DBLayer db, User user, String filename) 
	throws ImportException {
		setDBLayer(db);
		setUser(user);
		setSelectedFile(filename);
	}
	
	/**
	 * Create a new Import Manager. 
	 * This manager is primarily intented for import of the occurrence data.
	 * 
	 * @param db	The database layer.
	 * @param user	The User that is currently logged in.
	 * @throws ImportException	If some of the parameters are not valid.
	 */
	public ImportMng(DBLayer db, User user) 
	throws ImportException {
		this(db, user, null);
	}
	
	
	/**
	 * Set a new database layer.
	 * 
	 * @param dblayer	The database layer to be used.
	 * @throws ImportException	If the parameter is not valid.
	 */
	synchronized public void setDBLayer(DBLayer dblayer) 
	throws ImportException {
		if(dblayer == null) { 
			logger.error("The database layer is null!");
			throw new ImportException("The database layer cannot be null!");
		}
		db = dblayer;
	}
	
	/**
	 * Set another user.
	 * @param user	The new User.
	 * @throws ImportException	If the parameter is not valid.
	 */
	synchronized public void setUser(User user) 
	throws ImportException {
		if(user == null) { 
			logger.error("The user is null!");
			throw new ImportException("The user cannot be null!");
		}
		this.user = user;
	}
	
	
	/**
	 * Set the selected file. 
	 * This file contains the data. 
	 * 
	 * @param filename
	 */
	synchronized public void setSelectedFile(String filename) { 
		if(filename == null)
			logger.warn("The selected file is null!");
		this.filename = filename; 
	}
	
	
	/**
	 * Make a decision any time the user's intervention is required.
	 * There are several occasions which need the user's supervision,
	 * some of them are: 
	 * <ul>
	 * <li>when the record contained in the database is newer than the one contained in the file,</li>
	 * <li>when a part of the record is shared and the intended operation is update -
	 * 			this may affect more records</li>
	 * </ul>
	 * 
	 * @param decision	The decision the user has made.
	 */
	public void makeDecision(Action decision) {
		if(director != null) 
			director.makeDecision(decision);
	}
	
	/**
	 * If the user no longer wants to be bothered with decision-making
	 * he can instruct the import manager to remember his last decision
	 * and apply it whenever necessary.
	 * <br/>
	 * In this case, the Import Director will always respect the decision
	 * made when older record should replace a newer.
	 * 
	 * @param arg	True if the User should no longer be asked.
	 */
	public void setAskAboutTime(boolean arg) {
		if(director != null) 
			director.useLastDecisionInTimeIssues(arg);
	}
	
	/**
	 * If the user no longer wants to be bothered with decision-making
	 * he can instruct the import manager to remember his last decision
	 * and apply it whenever necessary.
	 * <br/>
	 * In this case, the Import Director will always respect the decision
	 * that has been made when a shared record is to be updated.
	 * 
	 * @param arg	True if the User should no longer be asked.
	 */
	public void setAskAboutInsert(boolean arg) {
		if(director != null) 
			director.useLastDecisionInUpdateInsertIssues(arg);
	}
	
	/**
	 * Start the import procedure. The import will run in its own thread.
	 * 
	 * @throws ImportException	If the information provided is not complete.
	 * @throws IOException	If anything with the file goes wrong (disk failure, insufficient permissions).
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
		
		current = new Thread( director, "Import" );
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
					}catch(InterruptedException e) {} 
				// Dispose of the reader.
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
	
	/** Something that will not be true for a long time, at least the mankind hopes so. */
	private boolean universeImploded = false;

	/**
	 * Abort the current import. 
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
	 * @return The number of results that have already been imported into the database.
	 */
	public int getNumberOfImported() {
		if(director == null) return 0;
		return director.importedRecords();
	}
	
	
	/**
	 * 
	 * @return	The record that is currently loaded from the database
	 * and the record loaded from the file.
	 */
	public TableModel getProcessedRecords() {
		return (director == null) ? null : 
			new RecordTable(
					director.getProcessedRecordInDatabase(), 
					director.getProcessedRecordFromFile());
	}
	
	/**
	 * 
	 * @return	The record that caused problems (exceptions).
	 */
	public TableModel getProblematicRecord() {
		return (director == null) ? null : new RecordTable( director.getProblematic() );
	}
	
	
	/**
	 * Notify the observers - some of our components has changed its state.
	 * The parameter can carry either information about progress
	 * or an exception that has to be dealt with.
	 */
	public void update(Observable source, Object parameter) {
		setChanged(); notifyObservers( parameter );
	}
	
	
	private class RecordTable extends AbstractTableModel {
		
		ArrayList<String>[] value;
		String[] columnNames;
			
		
		/**
		 * Display the record and all its properties
		 * so that the user can see the problem.
		 * 
		 * @param record
		 */
		public RecordTable(Record record) {
			value = new ArrayList[2];
			value[0] = new ArrayList<String>(20);
			value[1] = new ArrayList<String>(20);
			columnNames = new String[] { "Property", "Value" };
			traverse(record);
		}
		
		/**
		 * Display the records and all their properties
		 * so that the user can compare them.
		 * 
		 * @param a	The first record (the record in the database).
		 * @param b	The second record (the record in the file).
		 */
		public RecordTable(Record a, Record b) {
			value = new ArrayList[3];
			value[0] = new ArrayList<String>(20);
			value[1] = new ArrayList<String>(20);
			value[2] = new ArrayList<String>(20);
			columnNames = new String[] { "Property", "Value (in DB)", "Value (in file)" };
			traverse(a, b);
		}
		
		
		private void traverse(Record...r) {
			int n;
			if(r[0] != null) n = 0; else if(r.length >= 2 && r[1] != null) n = 1;
			else return;
			Class table = r[n].getClass(); 
				
			for( String property : r[n].getProperties()) {
				value[0].add(table.getSimpleName()+"."+property);
				for(int i = 0; i < r.length; i++) { 
					Object v = (r[i] == null) ? null : r[i].getValue(property);
					value[i].add( (v == null) ? "" : v.toString() );
				}
			}
			for( String key : r[n].getForeignKeys() )
				if(r.length == 1)
					traverse((Record)r[0].getValue(key));
				else
					traverse((Record)r[0].getValue(key), (Record)r[1].getValue(key));
		}
		

		public int getRowCount() {
			return columnNames.length;
		}

		public int getColumnCount() {
			return value.length;
		}
		
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public Object getValueAt(int row, int column) {
			return value[column].get(row);
		}
		
	}


}
