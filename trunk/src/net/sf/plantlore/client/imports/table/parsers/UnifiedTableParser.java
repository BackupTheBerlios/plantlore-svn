package net.sf.plantlore.client.imports.table.parsers;

import java.io.Reader;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import net.sf.plantlore.client.imports.table.DataHolder;
import net.sf.plantlore.client.imports.table.TableParser;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;

public class UnifiedTableParser implements TableParser {
	
	private Document document;
	private Record current, replacement;
	private DataHolder data = new DataHolder();
	
	private int numberOfRecords = -1;
	private Class rootTable;
	private Iterator recIterator;
	
	private static Map<String, Class> tables = new Hashtable<String, Class>(10);
	
	static {
		tables.put( "plants", Plant.class);
		tables.put( "metadata", Metadata.class);
		tables.put( "villages", Village.class);
		tables.put( "phytochoria", Phytochorion.class);
		tables.put( "territories", Territory.class);
	}
	
	
	public UnifiedTableParser(Reader reader, Class table) 
	throws ParserException {
		try {
        	SAXReader saxReader = new SAXReader();
            document = saxReader.read( reader );
            
            Node root = document.getRootElement();
            if(root == null)
            	throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));
            Class guess = tables.get( root.getName().toLowerCase() );
            if(table != guess && guess != null)
            	table = guess;
            
            List nodes = root.selectNodes("//"+table.getSimpleName().toLowerCase());
            if( nodes != null) {
            	numberOfRecords = nodes.size();
            	recIterator = nodes.iterator();
            }
            else
            	throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));
            
            current = (Record)table.newInstance();
			replacement = (Record)table.newInstance();
			data.record = current;
			data.replacement = replacement;
			
			rootTable = table;
        } catch (Exception ex) {
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
		
		Node node = (Node) recIterator.next();
		reconstruct( current, node );
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
				throw new ParserException("Error.MissingUpdateRecord");
			reconstruct( replacement, node );
		}
		
		return data;
	}

	
	public int getNumberOfRecords() {
		return numberOfRecords;
	}
	
	public Class getRootTable() {
		return rootTable;
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
    
    
    
//    public static void main(String[] args) 
//    throws java.io.IOException, ParserException {
//    	
//    	TableParser p = new UnifiedTableParser(
//    			new java.io.BufferedReader(
//    					new java.io.InputStreamReader(new java.io.FileInputStream("c:/documents and settings/yaa/dokumenty/plantlore/tables/territories.xml"),
//    					"UTF-8")),
//    			Territory.class
//    	);
//    	
//    	System.out.println(p.getNumberOfRecords()+"\n-----------------------------------");
//    	
//    	while( p.hasNext() ) {
//    		RecordData d = p.getNext();
//    		System.out.print(d.action+"  ("+d.record);
//    		if(d.action == Action.UPDATE)
//    			System.out.print(" -> "+d.replacement);
//    		System.out.println(")\n-----------------------------------");
//    	}
//    }
    
}
