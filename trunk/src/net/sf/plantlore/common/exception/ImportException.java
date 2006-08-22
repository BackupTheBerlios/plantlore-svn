package net.sf.plantlore.common.exception;

import net.sf.plantlore.common.record.Record;

public class ImportException extends PlantloreException {
	
	private static final long serialVersionUID = 2006060411008L;
	
	private Record problematicRecord;

	public ImportException() { super(); }
	
	public ImportException(String message) { super(message); }
	
	public ImportException(String message, Throwable exception) { super(message, exception); }
	
	public ImportException(Throwable exception) { super(exception); }
	
	public ImportException(String message, Record problematicRecord) {
		super(message);
		this.problematicRecord = problematicRecord; 
	}
	
	public Record getProblematicRecord() {
		return problematicRecord;
	}

}
