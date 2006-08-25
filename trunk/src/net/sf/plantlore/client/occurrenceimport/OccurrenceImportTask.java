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

public class OccurrenceImportTask extends Task implements RecordProcessor {
	
	private Logger logger = Logger.getLogger(OccurrenceImportTask.class.getPackage().getName());
	
	private OccurrenceParser parser;
	private int count;
	private DBLayerUtils dbutils;
	
	private DBLayerException canceledByUser = new DBLayerException(L10n.getString("Import.CanceledByUser"));
	
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
	
	
	public OccurrenceImportTask(DBLayer db, OccurrenceParser parser) {
		this.parser = parser;
		parser.setRecordProcessor( this );
		dbutils = new DBLayerUtils(db);
	}

	@Override
	public Object task() throws Exception {
		count = 0;
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

	
	public void processRecord(AuthorOccurrence... aos) 
	throws DBLayerException, RemoteException {
		if( isCanceled() ) 
			throw canceledByUser;
		count++;
		setStatusMessage(L10n.getFormattedString("Import.RecordsProcessed", count));
		try {
			if(aos == null || aos.length == 0) {
				logger.error("The occurrence record is either corrupted or incomplete. It will be skipped.");
				throw new DBLayerException(L10n.getString("Error.CorruptedRecord"));
			}
			dbutils.processRecord(aos[0].getOccurrence(), aos);
		} 
		catch(DBLayerException e) {
			if( IGNORE_ERRORS.contains(e.getErrorCode()) )
				setStatusMessage( e.getMessage() );
			else
				throw e;
		}
	}
}
