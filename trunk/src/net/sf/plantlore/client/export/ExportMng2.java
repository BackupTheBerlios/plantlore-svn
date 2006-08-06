package net.sf.plantlore.client.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.RemoteException;

import net.sf.plantlore.client.export.builders.*;
import net.sf.plantlore.client.export.component.FileFormat;
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
 * <li><b>Projection</b> stores the list of all selected columns that should be 
 * 					exported (<i>projection</i> in the database terminology).</li>
 * <li><b>File</b> stores the name of file as the user has suggested it.</li>
 * <li><b>XFilter</b> suggests the final name of the <i>file</i>
 * 					and is used to determine which <i>builder</i> will be used
 * 					to produce the output.</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29 
 * @version 2.0
 * @see net.sf.plantlore.client.export.Builder
 */
public class ExportMng2 {
	
	
	public static final String ENCODING = "UTF-8";
	
	
	/**
	 * List of all filters the Export Manager is capable to handle.
	 */
	protected FileFormat[] filters = new FileFormat[] {
			new FileFormat(L10n.getString("Format.PlantloreNative"), false, false, ".xml", ".pln"),
			new FileFormat(L10n.getString("Format.XML"), true, true, ".xml"),
			new FileFormat(L10n.getString("Format.CSV"), true, true, ".txt", ".csv"),	
			new FileFormat(L10n.getString("Format.ABCD"), ".xml"),	
			new FileFormat(L10n.getString("Format.DC"), ".xml"),
			new FileFormat(L10n.getString("Format.StdOut"), true, false, ".out")
	};
	
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	private DBLayer db ;
	private Projection template;
	private Selection selection;
	private FileFormat filter;
	private String filename;
	private SelectQuery query = null;
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
	public ExportMng2(DBLayer dblayer, SelectQuery query, Selection selection, Projection template) {
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
	public ExportMng2(DBLayer dblayer) {
		setDBLayer(dblayer);
		setSelection(null);
	}
	
	/**
	 * Create a new Export manager and <b>mark all records AND columns as selected</b>.
	 * 
	 * @param dblayer	The database layer mediating the access to the database.
	 * @param query	The query defining the result set which is to be iterated over.
	 */
	public ExportMng2(DBLayer dblayer, SelectQuery query) {
		this(dblayer, query, null, null, null, null, false, null);
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
	public ExportMng2(
			DBLayer dblayer, 
			SelectQuery query, 
			Selection selection, 
			Projection template, 
			FileFormat filter, 
			String filename,
			boolean useProjections,
			Class rootTable) 
	{
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
	 * @param useProjections	True if the Export manager shall use projections instead of regular records.
	 */
	synchronized public void useProjections(boolean useProjections) {
		this.useProjections = useProjections;
	}
	
	/**
	 * If projections are used, the root table must be specified explicitely.
	 * The default root table is the Occurrence table.
	 * 
	 * @param rootTable	The root table (the table the query started with). 
	 */
	synchronized public void setRootTable(Class rootTable) {
		this.rootTable = rootTable;
	}
	
	/**
	 * Set a new DBLayer.
	 */
	synchronized public void setDBLayer(DBLayer dblayer) {
		if(query != null) try {
			db.closeQuery(query);
		} catch(RemoteException e) {
			// Never mind.
		}
		db = dblayer;
	}
	
	/**
	 * Store a copy of the <code>template</code>. 
	 * Null means all columns are selected.
	 */
	synchronized public void setTemplate(Projection template) {
		if(template == null)
			this.template = null;
		else 
			this.template = template.clone();
	}
	
	/**
	 * Store a copy of the <code>selection</code>.
	 * Null means all rows are selected.
	 */
	synchronized public void setSelection(Selection selection) {
		if(selection == null)
			this.selection = null;
		else 
			this.selection = selection.clone();
	}
	
	/**
	 * Set the active filter. The type of the filter will be used 
	 * to determine the appropriate extension of the file
	 * and to create a correct Builder (for the format this
	 * filter represents). 
	 */
	synchronized public void setActiveFileFilter(FileFormat filter) {
		this.filter = filter; 
	}
	
	/**
	 * Set the selected file. Into this file the builder will 
	 * spit its output. 
	 */
	synchronized public void setSelectedFile(String filename) { 
		this.filename = filename; 
	}
	
	
	/**
	 * Set a particular select query. The manager will execute this select query
	 * and update the <code>resultId</code> if Projections are not used.
	 * On the other hand, if Projections are used, it is the <code>setTemplate()</code>
	 * that executes the query after it adds desired projections.
	 */
	synchronized public void setSelectQuery(SelectQuery query) { 
		// Close the previous query!
		if(this.query != null) try {
			db.closeQuery(this.query);  // This must go here because of the RMI!
		} catch (RemoteException e) {
			// Never mind.
		}
		this.query = query;
	}
	
	
	
	
	
	/**
	 * Start the export procedure. The export will run in its own thread.
	 * 
	 * @throws ExportException	If information provided is not complete.
	 * @throws IOException	If anything with the file goes wrong (insufficient disk space, insufficient permissions).
	 */
	synchronized public ExportTask2 createExportTask() 
	throws ExportException, IOException, DBLayerException {
		// Check if all necessary components are valid.
		if( db == null )
			throw new ExportException(L10n.getString("Error.InvalidDBLayer"));
		if( query == null)
			throw new ExportException(L10n.getString("Error.InvalidQuery"));
		if( filter == null ) 
			throw new ExportException(L10n.getString("Error.InvalidFilter"));
		if( filename == null || filename.length() == 0 ) 
			throw new ExportException(L10n.getString("Error.MissingFileName"));
		if( useProjections && rootTable == null)
			throw new ExportException(L10n.getString("Error.InvalidRootTable"));
		if(template == null || template.isEmpty())
			template = new Projection().setEverything();
		if(selection == null || selection.isEmpty())
			selection = new Selection().all();
			
		
		logger.debug("Initializing the export environment.");
		
		// Prepare the query for projections.
		if(useProjections) {
			if( rootTable == AuthorOccurrence.class || rootTable == Author.class )
				template.addProjections( query, AuthorOccurrence.class, Author.class );
			else
				template.addProjections( query, 
					Occurrence.class, Plant.class, Metadata.class, Publication.class, 
					Habitat.class, Territory.class, Village.class, Phytochorion.class );
		}
		
		// Create a new file and writer (wrapper).
		Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filter.suggestName(filename)),
					ENCODING));
		if(writer == null) {
			logger.fatal("Unable to create a new Writer.");
			throw new ExportException(L10n.getString("Error.WriterNotCreated"));
		}
		
		logger.debug("Filename: "+ filename);
		
		// Create a new builder according to the selected format. 
		Builder builder;
		if(filter.getDescription().equals(L10n.getString("Format.CSV")))
			builder = new CSVBuilder(writer, template);
		else if(filter.getDescription().equals(L10n.getString("Format.DC")))                        
			builder = new DarwinCoreBuilder(writer);
		else if(filter.getDescription().equals(L10n.getString("Format.ABCD"))) 
			builder = new ABCDBuilder(writer);
		else if(filter.getDescription().equals(L10n.getString("Format.XML")))                        
			builder = new XMLBuilder2(template, writer);                       
		else if(filter.getDescription().equals(L10n.getString("Format.PlantloreNative")))                        
			builder = new XMLBuilder2(writer);                       
		else {
			builder = new TrainingBuilder(template);
		}

		
		// Start a new task.
		ExportTask2 t = new ExportTask2(db, query, writer, builder, selection);
		t.ignoreDead( filter.ignoreDead() );
		
		// Reset variables.
		query = null;
		template = null;
		selection = null;
		filter = null;
		filename = null;
		useProjections = false;
		rootTable = Occurrence.class;
		
		return t;
	}
	
	
		
	/**
	 * @return The list of filters describing formats this Export Manager can handle.
	 */
	public FileFormat[] getFileFormats() {
		return filters.clone();
	}


}
