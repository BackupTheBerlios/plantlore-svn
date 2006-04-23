package net.sf.plantlore.client.export;

import java.io.IOException;
import java.util.HashSet;

import net.sf.plantlore.common.record.Record;


public abstract class RecordWalkBuilder implements Builder {
	
	private Template tmp;
	
		
	public RecordWalkBuilder(Template tmp) {
		this.tmp = tmp;
	}
	
	
	
	public void writeRecord(Record[] args) throws IOException {
		for(Record record : args) dealWith( record );
	}
	
	private String methodName(String column) {
		StringBuilder s = new StringBuilder("get" + column);
		s.setCharAt(3, Character.toUpperCase(s.charAt(3)));
		return s.toString();
	}
	
	protected void dealWith(Record record) throws IOException {
		Class table = Template.whichTable( record );
		if( table == null ||  !tmp.isSetTableD( table ) ) return;
		
		String[] foreignKeys = record.getForeignKeys(); // I love inheritance ;)
		String[] allColumns = record.getColumns();
		
		HashSet<String> fk = new HashSet<String>(20);
		if( foreignKeys != null ) for(String key : foreignKeys) fk.add( key );
		
		for(String column : allColumns) {
			if( fk.contains(column) || !tmp.isSet(table, column) ) continue; // skip foreign keys and unwanted columns
			try {
				Object value = table.getMethod( methodName(column), new Class[0] ).invoke( record, new Object[0] );
				wc(table, column, value);
			} catch(Exception e) {}
		}
	}
	
	
	protected abstract void wc(Class table, String column, Object value) throws IOException;
		
		
	
	public void makeHeader() throws IOException {}
	public void makeFooter() throws IOException {}
	public void startNewRecord() throws IOException {}
	public void finishRecord() throws IOException {}
}
