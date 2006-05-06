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
import net.sf.plantlore.server.DBLayerException;

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
 * <li><b>ResultID</b> identifies the result set.</li>
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
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29 
 * @see net.sf.plantlore.client.export.DefaultDirector
 * @see net.sf.plantlore.client.export.Builder
 */
public class ExportMng extends Observable implements Observer {
	
	/**
	 * List of all filters the Export Manager is capable to handle.
	 */
	private XFilter[] filters = new XFilter[] {
			new XFilter(L10n.getString("FilterPlantloreNative"), ".xml", ".pln"),
			new XFilter(L10n.getString("FilterXML"), true, ".xml"),
			new XFilter(L10n.getString("FilterCSV"), true, ".txt", ".csv"),	
			new XFilter(L10n.getString("FilterABCD"), ".xml"),	
			new XFilter(L10n.getString("FilterDC"), ".xml"),
			new XFilter(L10n.getString("FilterStdOut"), true, ".out")
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
	
	private Writer writer;
	private Thread current;
		
	
	/**
	 * Create a new Export Manager.
	 * 
	 * @param dblayer The database layer mediating the access to the database.
	 * @param result	The result set identificator which is to be iterated over.
	 * @param selection	The list of selected records. 
	 * @param template	The list of selected columns. <b>Null means everything is selected.</b>
	 */
	public ExportMng(DBLayer dblayer, int result, Selection selection, Template template) 
	throws ExportException {
		setDBLayer(dblayer);
		setResultId(result);
		setSelection(selection);
		setTemplate(template); 
	}
	
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
		setDBLayer(dblayer);
		setSelectQuery(query);
		setSelection(selection);
		setTemplate(template);
	}

	/**
	 * Create a new Export manager and <b>mark all records AND columns as selected</b>.
	 * 
	 * @param dblayer	The database layer mediating the access to the database.
	 * @param result	The result set identificator which is to be iterated over.
	 */
	public ExportMng(DBLayer dblayer, int result) 
	throws ExportException {
		setDBLayer(dblayer);
		setResultId(result);
		setTemplate(null);
		
		Selection select = new Selection(); select.all();
		setSelection(select);
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
		
		Selection select = new Selection(); select.all();
		setSelection(select);
	}
	
	/**
	 * Create a new Export manager and <b>mark all columns as selected</b>.
	 * 
	 * @param dblayer	The database layer mediating the access to the database. 
	 * @param result	The result set identificator which is to be iterated over.
	 * @param selection	The list of selected records. Shouldn't be empty.
	 */
	public ExportMng(DBLayer dblayer, int result, Selection selection) 
	throws ExportException{
		this(dblayer, result, selection, null);
	}
	
	/**
	 * Create a new Export manager.
	 * 
	 * @param dblayer	The database layer mediating the access to the database. Shouldn't be empty.
	 * @param result	The result set identificator which is to be iterated over. Shouldn't be empty.
	 * @param selection	The list of selected records. Shouldn't be empty.
	 * @param template	The list of selected columns. <b>Null means everything is selected.</b>
	 * @param filter	The filter which will be used to determine the appropriate builder of the output.
	 * @param file	The name of the file where the output will be written.
	 */
	public ExportMng(DBLayer dblayer, int result, Selection selection, Template template, XFilter filter, String filename) 
	throws ExportException {
		this(dblayer, result, selection, template);
		setSelectedFile(filename);
		setActiveFileFilter(filter);
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
	 */
	public ExportMng(DBLayer dblayer, SelectQuery query, Selection selection, Template template, XFilter filter, String filename) 
	throws ExportException, DBLayerException, RemoteException  {
		setDBLayer(dblayer);
		setSelectQuery(query);
		setSelection(selection);
		setTemplate(template);
		setSelectedFile(filename);
		setActiveFileFilter(filter);
	}
	
	/**
	 * Set a new DBLayer.
	 * <b>Forget the current result set identificator AND/OR selection query</b>
	 * - those objects most probably refered to a result set of the previous dblayer! 
	 */
	synchronized public void setDBLayer(DBLayer dblayer)
	throws ExportException {
		if(dblayer == null) { 
			logger.error("The database layer is null!");
			throw new ExportException("The database layer cannot be null!");
		}
		db = dblayer;
		results = selectedResults = resultId = -1;
	}
	
	/**
	 * Store a copy of the <code>template</code>.
	 * <b>Null means everything is selected!</b>
	 */
	synchronized public void setTemplate(Template template) {
		if(template == null) {
			this.template = new Template();
			this.template.setEverything();
		}
		else this.template = template.clone();
	}
	
	/**
	 * Store a copy of the <code>selection</code>.
	 */
	synchronized public void setSelection(Selection selection) {
		if(selection == null || selection.isEmpty())
			logger.warn("The list of selected records is empty!");

		if(selection == null) select = new Selection();
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
			logger.warn("The active filter is null!");
		this.filter = filter; 
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
	
	/**
	 * Set another result identificator of a Result Set. 
	 */
	synchronized public void setResultId(Integer result) { 
		if(result < 0) 
			logger.warn("The result set identificator is null!");

		this.resultId = result;
		results = -1;
		if(resultId >= 0)
			try {	results = db.getNumRows(resultId); } catch(Exception e) {}
	}
	
	/**
	 * Set a particular select query. The manager will execute this select query
	 * and update the <code>resultId</code>. 
	 */
	synchronized public void setSelectQuery(SelectQuery query) 
	throws ExportException, DBLayerException, RemoteException {
		if(query == null)
			logger.warn("The select query is null!");

		// Discontinue using the previous query
		if(this.query != null) db.closeQuery(this.query);
		this.query = query;
		
		if(this.query != null) {
			results = selectedResults = -1;
			resultId = db.executeQuery( query );
			results = db.getNumRows( resultId );
			if(select != null) selectedResults = select.size( results );
		}
	}
	
	
	/**
	 * Start the export procedure. The export procedure
	 * will run in its own thread.
	 * 
	 * @param append	True if the Builder shall append its output to an already existing file.
	 * @throws ExportException	If the information provided is not complete.
	 * @throws IOException	If anything with the file goes wrong (insufficient disk space, insufficient permissions).
	 */
	synchronized public void start() 
	throws ExportException, IOException {
		// Check if we have all necessary components ready.
		if( db == null )
			throw new ExportException("There is no point in starting an export - the DBLayer is not set!");
		if( filter == null ) 
			throw new ExportException("The Filter is not set!");
		if( filename == null ) 
			throw new ExportException("The Filename is not set!");
		if( select.isEmpty() )
			throw new ExportException("There is no point in starting an export - the list of selected records is empty!");
			
		
		logger.debug("Initializing the export environment.");
		aborted = false;
		
		// Create a new file.
		File file = new File( filter.suggestName(filename) );
		System.out.println( ">>> " + file );
		boolean append = ! file.createNewFile();
		
		// Create a new writer.
		writer = new FileWriter( file, append );
		if(writer == null) {
			logger.fatal("Unable to create a new Writer.");
			throw new ExportException("Unable to create a new Writer.");
		}
		
		// Create a new builder according to the selected format.
		if(filter.getDescription().equals(L10n.getString("FilterCSV")))
			builder = new CSVBuilder(writer, template);
		else if(filter.getDescription().equals(L10n.getString("FilterXML")))
			builder = new TrainingBuilder(template);
		else 
			builder = new TrainingBuilder(template);

		// Create a new Director and run it in a separate thread.
		director = new DefaultDirector(builder, resultId, db, select);
		director.addObserver(this);
		
		current = new Thread( director, "Export" );
		if(current == null) {
			logger.fatal("Unable to create a new thread.");
			throw new ExportException("Unable to create a new thread.");
		}
		current.start();
		
		exportInProgress = true;
		
		// Register a cleanup procedure
		Thread monitor = new Thread(new Runnable() {
			public void run() {
				try {
					// Sleep until the thread is really dead.
					current.join();
					// Dispose of the writer.
					writer.close();
					exportInProgress = false;
					// Dispose of the query.
					if(query != null) db.closeQuery( query );
					logger.debug("Environment cleaned up.");
					// Notify observers the export has ended.
					update(null, null);
				}catch(Exception e) {}
			}
		}, "ExportMonitor");
		monitor.start();
	}
	

	/**
	 * Abort the current export. You <b>must call</b> <code>finish()</code> 
	 * after calling <code>abort()</code>. 
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
