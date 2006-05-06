package net.sf.plantlore.common.exception;

import net.sf.plantlore.common.record.Record;

public class ImportException extends PlantloreException {
	
	private Record problematicRecord;

	public ImportException() { super(); }
	
	public ImportException(String message) { super(message); }
	
	public ImportException(String message, Record problematicRecord) {
		super(message);
		this.problematicRecord = problematicRecord; 
	}
	
	public Record getProblematicRecord() {
		return problematicRecord;
	}

}
