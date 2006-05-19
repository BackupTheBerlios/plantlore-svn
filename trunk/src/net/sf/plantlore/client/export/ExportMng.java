package net.sf.plantlore.client.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

import net.sf.plantlore.client.export.builders.*;
import net.sf.plantlore.client.export.component.XFilter;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.exception.ExportException;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.*;

import org.apache.log4j.Logger;

/**
 * The Export Manager. This class controls the whole process of export -
 * starting with creation of all necessary participants
 * and ending with the final cleanup. 
 * <br/>
 * There are several entities involved in the export:
 * <ul>
 * <li><b>DBLayer</b> the database layer that will carry out the requests.
 * 					Mustn't be null!</li>
 * <li><b>Director</b> iterates over the <i>result set</i> 
 * 					and <i>selected records</i> passes to the <i>builder</i>,</li>
 * <li><b>Builder</b> writes the records to the <i>output</i>.</li>
 * <li><b>Selection</b> stores the list of all selected records 
 * 					(<i>restriction</i> in the database terminology).</li>
 * <li><b>SelectQuery</b> identifies the result set as well (in fact the resultId is derived from it).</li>
 * <li><b>Template</b> stores the list of all selected columns that should be 
 * 					exported (<i>projection</i> in the database terminology).</li>
 * <li><b>File</b> stores the name of file as the user has suggested it.</li>
 * <li><b>XFilter</b> suggests the final name of the <i>file</i>
 * 					and is used to determine which <i>builder</i> will be used
 * 					to produce the output.</li>
 * </ul>
 * <br/>
 * It is strongly recommended to <b>use one of the constructors</b>
 * instead of creating the object partially using setters!
 * The participating entities may depend on each other and
 * therefore their setters must be called in a specific order.
 * <br/>
 * The most important is the order of these entities:
 * <ol>
 * <li>UseProjections</li>
 * <li>RootTable</li>
 * <li>DBLayer</li>
 * <li>SelectQuery</li>
 * <li>Template</li>
 * </ol>
 * The first two steps may be omited if projections are not to be used.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29 
 * @version 2.0
 * @see net.sf.plantlore.client.export.DefaultDirector
 * @see net.sf.plantlore.client.export.Builder
 */
public class ExportMng extends Observable implements Observer {
	
	/**
	 * List of all filters the Export Manager is capable to handle.
	 */
	protected XFilter[] filters = new XFilter[] {
			new XFilter(L10n.getString("FilterPlantloreNative"), false, false, ".xml", ".pln"),
			new XFilter(L10n.getString("FilterXML"), true, true, ".xml"),
			new XFilter(L10n.getString("FilterCSV"), true, true, ".txt", ".csv"),	
			new XFilter(L10n.getString("FilterABCD"), ".xml"),	
			new XFilter(L10n.getString("FilterDC"), ".xml"),
			new XFilter(L10n.getString("FilterStdOut"), true, false, ".out")
	};
	
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	private DBLayer db ;
	private Template template;
	private Selection select;
	private XFilter filter;
	private String filename;
	private Integer resultId;
	private DefaultDirector director;
	private Builder builder;
	private boolean aborted = false, exportInProgress = false;
	private int results = -1, selectedResults = -1;
	private SelectQuery query = null;
	private boolean queryClosed = true;
	
	private Writer writer;
	private Thread current;
		
	
	private boolean useProjections = false;
	private Class rootTable = Occurrence.class;
	
	
	/**
	 * Create a new Export Manager.
	 * 
	 * @param dblayer The database layer mediating the access to the database.
	 * @param query	The query defining the result set which is to be iterated over.
	 * @param selection	The list of selected records. 
	 * @param template	The list of selected columns. <b>Null means everything is selected.</b>
	 */
	public ExportMng(DBLayer dblayer, SelectQuery query, Selection selection, Template template) 
	throws ExportException, DBLayerException, RemoteException {
		this(dblayer, query, selection, template, null, null, false, null);
	}

	
	/**
	 * Create a new Export manager.
	 * <b>Mark all records AND columns as selected</b>.
	 * <b>You will have to specify the SelectQuery</b>
	 * before you call <code>start()</code>. 
	 * 
	 * @param dblayer	The database layer mediating the access to the database.
	 */
	public ExportMng(DBLayer dblayer) 
	throws ExportException {
		setDBLayer(dblayer);
		setSelection(null);
	}
	
	/**
	 * Create a new Export manager and <b>mark all records AND columns as selected</b>.
	 * 
	 * @param dblayer	The database layer mediating the access to the database.
	 * @param query	The query defining the result set which is to be iterated over.
	 */
	public ExportMng(DBLayer dblayer, SelectQuery query) 
	throws ExportException, DBLayerException, RemoteException {
		setDBLayer(dblayer);
		setSelectQuery(query);
		setTemplate(null);
		setSelection(null);
	}
	
	
	/**
	 * Create a new Export manager.
	 * 
	 * @param dblayer	The database layer mediating the access to the database. Shouldn't be empty.
	 * @param query	The query defining the result set which is to be iterated over. Shouldn't be empty.
	 * @param selection	The list of selected records. Shouldn't be empty.
	 * @param template	The list of selected columns. <b>Null means everything is selected.</b>
	 * @param filter	The filter which will be used to determine the appropriate builder of the output.
	 * @param file	The name of the file where the output will be written.
	 * @param useProjections	Should projections be used.
	 * @param rootTable	The root table (only if projections are used).
	 */
	public ExportMng(
			DBLayer dblayer, 
			SelectQuery query, 
			Selection selection, 
			Template template, 
			XFilter filter, 
			String filename,
			boolean useProjections,
			Class rootTable) 
	throws ExportException, DBLayerException, RemoteException  {
		useProjections( useProjections );
		setRootTable( rootTable );
		setDBLayer(dblayer);
		setSelectQuery(query);
		setSelection(selection);
		setTemplate(template);
		setSelectedFile(filename);
		setActiveFileFilter(filter);
	}
	
	
	/**
	 * Sadly, some database engines cannot deal with bigger queries,
	 * which is why the Export Manager has to use projections.
	 * 
	 * @param useProjections	True if the Export manager shall use projections instead of regular records.
	 * @throws ExportException	If the export is already in progress.
	 */
	synchronized public void useProjections(boolean useProjections) 
	throws ExportException {
		if(isExportInProgress()) {
			logger.warn("Cannot change the usage of Projections while Export is still in progress!");
			throw new ExportException(L10n.getString("error.CannotChangeDuringExport"));
		}
		this.useProjections = useProjections;
	}
	
	/**
	 * If projections are used, the root table must be specified explicitely.
	 * The default root table is the Occurrence table.
	 * 
	 * @param rootTable	The root table (the table the query started with). 
	 * @throws ExportException	If the export is already in progress.
	 */
	synchronized public void setRootTable(Class rootTable) 
	throws ExportException {
		if(isExportInProgress()) {
			logger.warn("Cannot change the Root Table while Export is still in progress!");
			throw new ExportException(L10n.getString("error.CannotChangeDuringExport"));
		}
		this.rootTable = rootTable;
	}
	
	
	
	/**
	 * Set a new DBLayer.
	 * <b>Forget the current result set identificator AND/OR selection query</b>
	 * - those objects most probably refered to a result set of the previous dblayer! 
	 */
	synchronized public void setDBLayer(DBLayer dblayer)
	throws ExportException {
		if(isExportInProgress()) {
			logger.warn("Cannot change DBLayer while Export is still in progress!");
			throw new ExportException(L10n.getString("error.CannotChangeDuringExport"));
		}
		if(dblayer == null) { 
			logger.error("The database layer is null!");
			throw new ExportException(L10n.getString("error.InvalidDBLayer"));
		}
		db = dblayer;
		results = selectedResults = resultId = -1;
	}
	
	/**
	 * Store a copy of the <code>template</code>.
	 * <b>Null means everything is selected!</b>
	 * <br/>
	 * If projections are used, they will be added to the SelectQuery and
	 * the SelectQuery will be executed here.
	 * 
	 * @throws ExportException	If the export is already in progress.
	 */
	synchronized public void setTemplate(Template template) 
	throws ExportException {
		if(isExportInProgress()) {
			logger.warn("Cannot change the Template while Export is still in progress!");
			throw new ExportException(L10n.getString("error.CannotChangeDuringExport"));
		}
		if(query == null && useProjections) {
			logger.warn("Cannot set the Template before the Query is specified!");
			throw new ExportException(L10n.getString("error.InvalidSelectQuery"));
		}
		if(template == null) {
			logger.info("The list of selected columns is empty! Creating a new Template where every column is selected.");
			this.template = new Template();
			this.template.setEverything();
		}
		else this.template = template.clone();
		
		
		
		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		 * 
		 * 		The Template defines the projections. 
		 * 		It is vital that setSelectQuery is called PRIOR to the setTemplate!
		 * 
		 * 		The SelectQuery will be executed here, since it cannot be executed
		 * 		unless the projections are added. 
		 * 
		 * 				We love you, Firebird!
		 * 
		 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
		
		if(useProjections) {
			if( rootTable == AuthorOccurrence.class || rootTable == Author.class )
				this.template.addProjections( query, AuthorOccurrence.class, Author.class ); // USE THIS.TEMPLATE!
			else
				this.template.addProjections( query, 
					Occurrence.class, Plant.class, Metadata.class, Publication.class, 
					Habitat.class, Territory.class, Village.class, Phytochorion.class );
			//Execute the SelectQuery and update the resultId and the number of results.
			try {
				resultId = db.executeQuery( query );
				results = db.getNumRows( resultId );
				if(select != null) selectedResults = select.size( results );
			} catch(Exception e) {}
		}
	}
	
	/**
	 * Store a copy of the <code>selection</code>.
	 */
	synchronized public void setSelection(Selection selection)
	throws ExportException {
		if(isExportInProgress()) {
			logger.warn("Cannot change the Selection while Export is still in progress!");
			throw new ExportException(L10n.getString("error.CannotChangeDuringExport"));
		}
		if(selection == null || selection.isEmpty()) {
			logger.info("The list of selected records is empty! Creating a new Selection where everything is selected.");
			select = new Selection();
			select.all();
		}
		else select = selection.clone();

		selectedResults = -1;
		if(resultId >= 0 && results >= 0)
			selectedResults = selection.size(results);
	}
	
	/**
	 * Set the active filter. The type of the filter will be used 
	 * to determine the appropriate extension of the file
	 * and to create a correct Builder (for the format this
	 * filter represents). 
	 */
	synchronized public void setActiveFileFilter(XFilter filter) {
		if(filter == null)
			logger.warn("The filter is set to null!");
		this.filter = filter; 
	}
	
	/**
	 * Set the selected file. Into this file the builder will 
	 * spit its output. 
	 */
	synchronized public void setSelectedFile(String filename) { 
		if(filename == null || filename.length() == 0)
			logger.warn("The supplied file name is either null or an empty string!");
		this.filename = filename; 
	}
	
	
	/**
	 * Set a particular select query. The manager will execute this select query
	 * and update the <code>resultId</code> if Projections are not used.
	 * On the other hand, if Projections are used, it is the <code>setTemplate()</code>
	 * that executes the query after it adds desired projections.
	 */
	synchronized public void setSelectQuery(SelectQuery query) 
	throws ExportException, DBLayerException, RemoteException {
		if(isExportInProgress()) {
			logger.warn("Cannot change the SelectQuery while Export is still in progress!");
			throw new ExportException(L10n.getString("error.CannotChangeDuringExport"));
		}
		if(query == null)
			logger.warn("The select query is not valid - it is null!");

		// Discontinue using the previous query
		if(!queryClosed && this.query != null) {
			db.closeQuery(this.query);
			queryClosed = true;
		}
		this.query = query;
		
		if(this.query != null) {
			results = selectedResults = -1;
			if(useProjections) 
				resultId = null;
			else {
				resultId = db.executeQuery( query );
				results = db.getNumRows( resultId );
				if(select != null) selectedResults = select.size( results );
			}
		}
	}
	
	
	/**
	 * Start the export procedure. The export will run in its own thread.
	 * 
	 * @throws ExportException	If information provided is not complete.
	 * @throws IOException	If anything with the file goes wrong (insufficient disk space, insufficient permissions).
	 */
	synchronized public void start() 
	throws ExportException, IOException {
		// Check if we have all necessary components ready.
		if( db == null )
			throw new ExportException(L10n.getString("error.InvalidDBLayer"));
		if( filter == null ) 
			throw new ExportException(L10n.getString("error.InvalidFilter"));
		if( filename == null || filename.length() == 0 ) 
			throw new ExportException(L10n.getString("error.MissingFileName"));
		if( useProjections && rootTable == null)
			throw new ExportException(L10n.getString("error.InvalidRootTable"));
			
		
		logger.debug("Initializing the export environment.");
		aborted = false;
		
		// Create a new file.
		File file = new File( filter.suggestName(filename) );
		boolean append = ! file.createNewFile();
		
		// Create a new writer.
		writer = new FileWriter( file, append );
		if(writer == null) {
			logger.fatal("Unable to create a new Writer.");
			throw new ExportException(L10n.getString("error.WriterNotCreated"));
		}
		
		// Create a new builder according to the selected format.
		if(filter.getDescription().equals(L10n.getString("FilterCSV")))
			builder = new CSVBuilder(writer, template);
		else if(filter.getDescription().equals(L10n.getString("FilterXML")))
			builder = new TrainingBuilder(template);
		else 
			builder = new TrainingBuilder(template);

		// Create a new Director and run it in a separate thread.
		director = new DefaultDirector(
				builder, resultId, db, select, useProjections, 
				template.getDescription(), rootTable);
		director.ignoreDead( filter.ignoreDead() );
		director.addObserver(this);
		
		current = new Thread( director, "Export" );
		if(current == null) {
			logger.fatal("Unable to create a new thread.");
			throw new ExportException(L10n.getString("error.ThreadFailed"));
		}
		current.start();
		
		exportInProgress = true;
		
		// Register a cleanup procedure
		Thread monitor = new Thread(new Runnable() {
			public void run() {
				// Sleep until the thread is really dead.
				while( !sunExploded )
					try {
						current.join();
						break;
					}catch(InterruptedException e) {}
				// Dispose of the writer.
				try {
					writer.close();
				}catch(IOException e) {}
				exportInProgress = false;
				// Dispose of the query.
				try {
				if(!queryClosed && query != null) { 
					db.closeQuery( query );
					queryClosed = true;
				}
				}catch(RemoteException e) {}
				logger.debug("Environment cleaned up.");
				// Notify observers the export has ended.
				update(null, null);
				
			}
		}, "ExportMonitor");
		monitor.start();
	}
	
	
	/** Something that will not be true for a long time, at least the mankind hopes so. */
	private final boolean sunExploded = false;
	

	/**
	 * Abort the current export. 
	 */
	synchronized public void abort() {
		if(!exportInProgress) return;
		aborted = true; exportInProgress = false;
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
	public boolean isExportInProgress() {
		return exportInProgress;
	}
	
	/**
	 * @return The total number of results to be exported.
	 */
	public int getNumberOfResults() {
		return selectedResults;
	}
	
	/**
	 * @return The number of results that have already been exported.
	 */
	public int getNumberOfExported() {
		if(director == null) return 0;
		return director.exportedRecords();
	}
	
	/**
	 * @return The list of filters describing formats this Export Manager can handle.
	 */
	public XFilter[] getFilters() {
		return filters.clone();
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
