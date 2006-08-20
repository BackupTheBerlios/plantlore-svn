package net.sf.plantlore.client.occurrenceimport;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.PlantloreConstants.Table;
import net.sf.plantlore.common.exception.*;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;

public class OccurrenceImportTask extends Task implements RecordProcessor {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	private OccurrenceParser parser;
	private int count;
	private DBLayerUtils dbutils;
	
	private DBLayerException fakeException = new DBLayerException();
	
	private static Table[] TABLES_TO_UPDATE = new PlantloreConstants.Table[] { 
		Table.AUTHOR, Table.AUTHOROCCURRENCE, Table.HABITAT,
		Table.METADATA, Table.OCCURRENCE, Table.PUBLICATION };
	
	
	public OccurrenceImportTask(DBLayer db, OccurrenceParser parser) {
		this.parser = parser;
		parser.setRecordProcessor( this );
		dbutils = new DBLayerUtils(db);
	}

	@Override
	public Object task() throws IOException, SAXException {
		count = 0;
		
		try {
			parser.startParsing();
		} catch(SAXException e) {
			if(e.getException() != fakeException)
				throw e;
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
	 * 
	 */
	@Override
	public void proceed() {
		// Empty implementation. Just to make sure no one will try to resurrect this operation.
	}

	
	public void processRecord(AuthorOccurrence... aos) 
	throws DBLayerException, RemoteException {
		
		if( isCanceled() )
			throw fakeException;
		
		count++;
		setStatusMessage(L10n.getFormattedString("Import.RecordsImported", count));
		try {
			
			if(aos == null || aos.length == 0)
				throw new DBLayerException(L10n.getString("Error.IncompleteRecord"));
				
			dbutils.processRecord(aos[0].getOccurrence(), aos);
			
		} catch(DBLayerException e) {
			logger.error("Unable to process record No. "+count+". Here's why " + e.getMessage());
			if( e.isReconnectNecessary() )
				throw e;
			setStatusMessage(e.getMessage());
		} catch(RemoteException e) {
			logger.error("Unable to process record No. "+count+". Here's why " + e.getMessage());
			throw e;
		}
	}
	
	

}
