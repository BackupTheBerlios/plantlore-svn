package net.sf.plantlore.client.occurrenceimport;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.PlantloreConstants.Table;
import net.sf.plantlore.common.exception.*;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;

/**
 * The Occurrence Import task instructs the Parser to start parsing the file
 * and every time a record is reconstructed, it is processed here (via a callback).
 * The record is processed using the <code>DBLayerUtils.processRecord()</code>.
 * <br/>
 * The task can be cancelled.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-14
 * @version 1.0
 * 
 *  @see net.sf.plantlore.common.DBLayerUtils#processRecord(Occurrence, AuthorOccurrence[])
 */
public class OccurrenceImportTask extends Task implements RecordProcessor {
	
	private Logger logger = Logger.getLogger(OccurrenceImportTask.class.getPackage().getName());
	
	private OccurrenceParser parser;
	private int count, rejected;
	private DBLayerUtils dbutils;
	
	private DBLayerException canceledByUser = new DBLayerException(L10n.getString("Import.CanceledByUser"));
	
	/**
	 * The list of tables that can be modified during the import
	 * (therefore the application must reload the content of those tables). 
	 */
	private static Table[] TABLES_TO_UPDATE = new Table[] { 
		Table.AUTHOR, 
		Table.AUTHOROCCURRENCE, 
		Table.HABITAT,
		Table.METADATA, 
		Table.OCCURRENCE, 
		Table.PUBLICATION };
	
	private static Set<Integer> IGNORE_ERRORS = new HashSet<Integer>(Arrays.asList(
			DBLayerException.ERROR_UNSPECIFIED,
			DBLayerException.ERROR_DELETE, 
			DBLayerException.ERROR_SAVE,
			DBLayerException.ERROR_UPDATE,
			DBLayerException.ERROR_RIGHTS));
	
	/**
	 * Create a new Occurrence Import task. 
	 * 
	 * @param db		The database layer mediating the access to the database.
	 * @param parser	The parser that is capable of reconstructing the records stored in some file.
	 */
	public OccurrenceImportTask(DBLayer db, OccurrenceParser parser) {
		this.parser = parser;
		parser.setRecordProcessor( this );
		dbutils = new DBLayerUtils(db);
	}

	@Override
	public Object task() throws Exception {
		count = rejected = 0;
		try {
			parser.startParsing();
		} catch(SAXException e) {
			// Some exceptions may be wrapped in the SAXException
			// because the handler cannot throw anything else :/
			if(e.getException() != null)
				throw e.getException();
		} finally {
			setStatusMessage(L10n.getString("Import.UpdatingEnvironment"));
			setChanged();
			notifyObservers( TABLES_TO_UPDATE );
		}
		
		setStatusMessage(L10n.getFormattedString("Import.RecordsProcessed", count, rejected));
		
		fireStopped(null);
		return null;
	}
	
	/**
	 * Import is a very delicate procedure, it should not be restarted.
	 */
	@Override
	public void proceed() {
		// Empty implementation. Just to make sure no one will try to resurrect this operation.
	}

	/**
	 * Store the record supplied by the parser into the database. 
	 * All author-occurrences should share the same occurrence.
	 * <br/>
	 * Some erros may be ignored (such as insufficient access rights)
	 * i.e. the whole import procedure will not fail, just the problematic 
	 * record will be rejected. 
	 * <br/>
	 * The record is processed using the <code>DBLayerUtils.processRecord()</code>.
	 * 
	 * @see net.sf.plantlore.common.DBLayerUtils#processRecord(Occurrence, AuthorOccurrence[])
	 */
	public void processRecord(AuthorOccurrence... aos) 
	throws DBLayerException, RemoteException {
		if( isCanceled() ) 
			throw canceledByUser;
		setStatusMessage(L10n.getFormattedString("Import.RecordsProcessed", count, rejected));
		count++;
		try {
			if(aos == null || aos.length == 0) {
				logger.error("The occurrence record is either corrupted or incomplete. It will be skipped.");
				throw new DBLayerException(L10n.getString("Error.CorruptedRecord"));
			}
			dbutils.processRecord(aos[0].getOccurrence(), aos);
		} 
		catch(DBLayerException e) {
			if( IGNORE_ERRORS.contains(e.getErrorCode()) )
				//setStatusMessage( e.getMessage() );
				rejected++;
			else
				throw e;
		}
	}
}
