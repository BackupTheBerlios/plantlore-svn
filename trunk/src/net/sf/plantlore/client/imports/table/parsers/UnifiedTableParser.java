package net.sf.plantlore.client.tableimport.parsers;

import java.io.Reader;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import net.sf.plantlore.client.tableimport.DataHolder;
import net.sf.plantlore.client.tableimport.TableParser;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;

public class UnifiedTableParser implements TableParser {
	
	private Document document;
	private DataHolder data = new DataHolder();
	
	private int numberOfRecords = -1;
	private Class rootTable;
	private Iterator recIterator;
	
	private Reader reader;
	
	private static Map<String, Class> tables = new Hashtable<String, Class>(10);
	
	static {
		tables.put( "plants", Plant.class);
		tables.put( "metadata", Metadata.class);
		tables.put( "villages", Village.class);
		tables.put( "phytochoria", Phytochorion.class);
		tables.put( "territories", Territory.class);
	}
	
	
	public UnifiedTableParser(Reader reader) {
		this.reader = reader;
	}
	
	
	public void initialize() throws ParserException {
		try {
        	SAXReader saxReader = new SAXReader();
            document = saxReader.read( reader );
            
            Node root = document.getRootElement();
            if(root == null)
            	throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));
            rootTable = tables.get( root.getName().toLowerCase() );
            if(rootTable == null)
            	throw new ParserException("Error.UnsupportedTable");
            
            List nodes = root.selectNodes("//"+rootTable.getSimpleName().toLowerCase());
            if( nodes != null) {
            	numberOfRecords = nodes.size();
            	recIterator = nodes.iterator();
            }
            else
            	throw new ParserException(L10n.getString("Error.EmptyXMLFile"));
			
		} catch( OutOfMemoryError er ) {
			throw new ParserException(L10n.getString("Error.OutOfMemory"));			
        } catch (DocumentException e) {
        	throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));
		} 
	}
	

	public boolean hasNext() {
		if(recIterator == null)
			return false;
		return recIterator.hasNext();
	}

	public DataHolder getNext() 
	throws ParserException {
		if(recIterator == null)
			return null;

		try {
			data.record = (Record)rootTable.newInstance();
			
			Node node = (Node) recIterator.next();
			reconstruct( data.record, node );
			node = node.getParent();
			String name = (node == null) ? "add" : node.getName().toLowerCase();
			
			if( name.startsWith("add") )
				data.action = Action.INSERT;
			else if( name.startsWith("del") )
				data.action = Action.DELETE;
			else if( name.startsWith("upd")) {
				data.action = Action.UPDATE;
				node = (Node) recIterator.next();
				if(node == null)
					throw new ParserException(L10n.getString("Error.MissingUpdateRecord"));
				data.replacement = (Record)rootTable.newInstance();
				reconstruct( data.replacement, node );
			}
		} catch(Exception e) {
			throw new ParserException(L10n.getString("Import.PartialyCorruptedRecord"));
		}
		
		return data;
	}

	
	public int getNumberOfRecords() {
		return numberOfRecords;
	}
	
	
    private void reconstruct(Record part, Node node) {
    	if(part == null || node == null)
    		return;
    	for(String property : part.getProperties()) {
    		String value = node.valueOf(property.toLowerCase());
    		if("".equals(value)) value = null;
    		part.setValue(property, value);
    	}
    }
     
}
