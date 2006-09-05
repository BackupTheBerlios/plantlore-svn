package net.sf.plantlore.client.export;

import static net.sf.plantlore.common.PlantloreConstants.RESTR_EQ;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
 * ExportManager serves as a Export Task factory. The Export Manager gathers all information
 * needed for the creation of a new Export Task.
 * <br/>
 * In order to create a new Export task these information must be supplied:
 * <ul>   
 * <li>dblayer	The database layer mediating the access to the database.</li>
 * <li>query	The query defining the result set which is to be iterated over.</li>
 * <li>selection	The list of selected records. Null means all records from the query should be exported.</li>
 * <li>projections	The list of selected columns. Null means all columns are selected.</li>
 * <li>format	The format which will be used to determine the appropriate builder of the output.</li>
 * <li>file	The name of the file where the output will be written.</li>
 * <li>useProjections		Should projections be used (projections may reduce the amount of time required by export).</li>
 * <li>rootTable	The root table (only if projections are used).</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-29 
 * @version 2.0
 */
public class ExportMng2 {
	
	/**
	 * The encoding that should be used for all files.
	 */
	public static final String ENCODING = "UTF-8";
	
	
	/**
	 * List of all file formats the Export Manager is capable to handle.
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
	private Projection projections;
	private Selection selection;
	private FileFormat format;
	private String filename;
	private SelectQuery query = null;
	private boolean useProjections = false;
	private Class rootTable = Occurrence.class;
	
	
	/**
	 * Create a new Export Manager.
	 * 
	 * @param dblayer The database layer mediating the access to the database.
	 * @param query	The query defining the result set which is to be iterated over.
	 * @param selection	The list of selected records. Null means all records from the query should be exported.
	 * @param projection	The list of selected columns. Null means all columns are selected.
	 */
	public ExportMng2(DBLayer dblayer, SelectQuery query, Selection selection, Projection projection) {
		this(dblayer, query, selection, projection, null, null, false, null);
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
	 * @param selection	The list of selected records. Null means all records from the query should be exported.
	 * @param projections	The list of selected columns. Null means all columns are selected.
	 * @param format	The format which will be used to determine the appropriate builder of the output.
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
		setProjections(template);
		setSelectedFile(filename);
		setFileFormat(filter);
	}
	
	
	/**
	 * @param useProjections	True if the Export manager shall use projections instead of regular records.
	 * Projections may save some time if the number of exported columns is small.
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
	 * 
	 * @param dblayer The database layer mediating the access to the database. Shouldn't be empty.
	 */
	synchronized public void setDBLayer(DBLayer dblayer) {
		db = dblayer;
	}
	
	/**
	 * Store a copy of the list of <code>projections</code>. 
	 * 
	 * @param projections The list of columns the User wants to export. 
	 * Null means that all columns should be sellected.
	 */
	synchronized public void setProjections(Projection projections) {
		if(projections == null)
			this.projections = null;
		else 
			this.projections = projections.clone();
	}
	
	/**
	 * Store a copy of the list of selected rows (records).
	 * 
	 * @param selection	The list of records that should be exported. Null means that all records are selected.
	 */
	synchronized public void setSelection(Selection selection) {
		if(selection == null)
			this.selection = null;
		else 
			this.selection = selection.clone();
	}
	
	/**
	 * Set the file format that should be Used to store the exported records. 
	 * 
	 * @param format	The file format that will be used to store the selected records.
	 */
	synchronized public void setFileFormat(FileFormat format) {
		this.format = format; 
	}
	
	/**
	 * Set the name of the file into which the records will be exported.
	 * 
	 * @param filename	The name of the file where the records will be written.
	 */
	synchronized public void setSelectedFile(String filename) { 
		this.filename = filename; 
	}
	
	/**
	 * Set the query that defines the superset of records from which some
	 * (those that are marked in the Selection) records will be exported.
	 * The query must not be executed! The Export Task will execute it itself.
	 * 
	 * @param query	The query that defines the superset of records from which some
	 * will be exported.
	 */
	synchronized public void setSelectQuery(SelectQuery query) { 
		// Close the previous query!
		if(this.query != null) try {
			db.closeQuery(this.query);
		} catch (Exception e) {
			// Never mind.
		}
		this.query = query;
	}
	
	/**
	 *	Create a new Export Task. The task is ready to be started.
	 * <br/>
	 * All previously supplied information will be reset to default values
	 * (except the database layer) - in order to create a new Export task,
	 * all information must be supplied again.
	 * 
	 * @return	The new Export Task ready to be started.
	 */
	synchronized public ExportTask2 createExportTask() 
	throws ExportException, IOException, DBLayerException {
		// Check if all necessary components are valid.
		if( db == null )
			throw new ExportException(L10n.getString("Error.InvalidDBLayer"));
		if( query == null)
			throw new ExportException(L10n.getString("Error.InvalidQuery"));
		if( format == null ) 
			throw new ExportException(L10n.getString("Error.InvalidFilter"));
		if( filename == null || filename.length() == 0 ) 
			throw new ExportException(L10n.getString("Error.MissingFileName"));
		if( useProjections && rootTable == null)
			throw new ExportException(L10n.getString("Error.InvalidRootTable"));
		if(projections == null || projections.isEmpty())
			projections = new Projection().setEverything();
		if(selection == null || selection.isEmpty())
			selection = new Selection().all();
			
		//useProjections = true;
		
		logger.debug("Creating necessary participants.");
		projections.set(Occurrence.class, Occurrence.ID);
		projections.set(Occurrence.class, Deletable.DELETED);
		projections.set(AuthorOccurrence.class, Deletable.DELETED);
		projections.set(Habitat.class, Deletable.DELETED);
		
		// Prepare the query for projections.
		if(useProjections) {
			if( rootTable == AuthorOccurrence.class || rootTable == Author.class )
				projections.addProjections( query, AuthorOccurrence.class, Author.class );
			else
				projections.addProjections( query, 
					Occurrence.class, Plant.class, Metadata.class, Publication.class, 
					Habitat.class, Territory.class, NearestVillage.class, Phytochorion.class );
		}
		
		// Create a new file and writer (wrapper).
		Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(format.suggestName(filename)),
					ENCODING));
		if(writer == null) {
			logger.fatal("Unable to create a new Writer.");
			throw new ExportException(L10n.getString("Error.WriterNotCreated"));
		}
		
		logger.debug("Filename: "+ filename);
		
		// Create a new builder according to the selected format. 
		Builder builder;
		if(format.getDescription().equals(L10n.getString("Format.CSV")))
			builder = new CSVBuilder(writer, projections);
		else if(format.getDescription().equals(L10n.getString("Format.DC")))                        
			builder = new DarwinCoreBuilder(writer);
		else if(format.getDescription().equals(L10n.getString("Format.ABCD"))) 
			builder = new ABCDBuilder(writer);
		else if(format.getDescription().equals(L10n.getString("Format.XML")))                        
			builder = new XMLBuilder2(projections, writer);                       
		else if(format.getDescription().equals(L10n.getString("Format.PlantloreNative")))                        
			builder = new XMLBuilder2(writer);                       
		else {
			builder = new TrainingBuilder(projections);
			writer.close();
			(new java.io.File(filename)).delete();
		}
		
		if( format.ignoreDead() )
			query.addRestriction(RESTR_EQ, Deletable.DELETED, null, 0, null);

		// Create the task!
		ExportTask2 t;
		if( useProjections )
			t =new ExportTask2(db, query, writer, builder, selection, projections, rootTable);
		else
			t = new ExportTask2(db, query, writer, builder, selection);
		t.ignoreDead( format.ignoreDead() );
		
		// Reset variables.
		query = null;
		projections = null;
		selection = null;
		format = null;
		filename = null;
		useProjections = false;
		rootTable = Occurrence.class;
		
		return t;
	}
	
	
		
	/**
	 * @return The list of file formats this Export Manager can handle.
	 */
	public FileFormat[] getFileFormats() {
		return filters.clone();
	}


}
