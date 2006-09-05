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

/**
 * The Unified Table Parser is capable of parsing
 * a file in the XML file format. It can detect the type of records stored in the file
 * and the operation that should be performed with that record.
 * <br/> 
 * Typicall format of the file looks like this:
 * <br/>
 * <pre>
 * &lt;villages&gt;  // The Root element is used to determine the immutable table.
 *  &lt;add&gt;
 *   &lt;nearestvillage&gt;
 *     &lt;name&gt;Třebíč&lt;/name&gt;
 *   &lt;/nearestvillage&gt;
 *  &lt;/add&gt;
 *  
 *  &lt;delete&gt;
 *   &lt;nearestvillage&gt;
 *     &lt;name&gt;Železný Brod&lt;/name&gt;
 *   &lt;/nearestvillage&gt;  
 *  &lt;delete&gt;
 *  
 *  &lt;update&gt; // Records in this branch must go in pairs &lt;original, replacement&gt;
 *   &lt;nearestvillage&gt;
 *     &lt;name&gt;Kšice&lt;/name&gt;
 *   &lt;/nearestvillage&gt;  
 *   &lt;nearestvillage&gt;
 *     &lt;name&gt;Košice&lt;/name&gt;
 *   &lt;/nearestvillage&gt;
 *  &lt;update&gt;
 * &lt;/villages&gt;
 * </pre>
 * <br/>
 * The same goes for plants, metadata, phytochoria, and territories - you just have to
 * change the root element and the content of those three branches.
 * <br/>
 * The Unified Table Parser uses DOM to process the input. 
 * It may require a huge amount of memory, however the purpose of the Table Import
 * is to allow the User to make some minor changes to the (otherwise) immutable tables.
 * It is not expected that the number of villages (nearest bigger seats), known plants,
 * phytochoria, or territories will grow or change extremely fast. 
 * The Table Import is meant for occasional changes and updates of a few records.
 *  
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-10
 * @version 1.0
 *
 */
public class UnifiedTableParser implements TableParser {
	
	private Document document;
	private DataHolder data = new DataHolder();
	
	private int numberOfRecords = -1;
	private Class rootTable;
	private Iterator recIterator;
	private SAXReader saxReader;
	
	private Reader reader;
	
	private static Map<String, Class> tables = new Hashtable<String, Class>(10);
	
	/**
	 * The key to the immutable tables.
	 */
	static {
		tables.put( "plants", Plant.class);
		tables.put( "metadata", Metadata.class);
		tables.put( "villages", NearestVillage.class);
		tables.put( "phytochoria", Phytochorion.class);
		tables.put( "territories", Territory.class);
	}
	
	/**
	 * Create a new Unified Table Parser that will read from the supplied Reader.
	 * 
	 * @param reader	The input containing the  records.
	 */
	public UnifiedTableParser(Reader reader) {
		this.reader = reader;
	}
	
	/**
	 * Initialize the reader and detect the immutable table into which the records in the file belong.
	 */
	public Class initialize() throws ParserException {
		try {
        	saxReader = new SAXReader();
        	
        	System.out.println("ABOUT TO PARSER THE DOCUMENT WITH DOM4J");
            document = saxReader.read( reader );
            System.out.println("COMPLETED.");
            
            Node root = document.getRootElement();
            if(root == null)
            	throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));
            rootTable = tables.get( root.getName().toLowerCase() );
            if(rootTable == null)
            	throw new ParserException(L10n.getFormattedString("Error.UnsupportedTable", rootTable.getSimpleName()));
            
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
        
        return rootTable;
	}
	
	/**
	 * @return True if there are more records in the file.
	 */
	public boolean hasNext() {
		if(recIterator == null)
			return false;
		return recIterator.hasNext();
	}

	/**
	 * @return Another record from the file.
	 */
	public DataHolder getNext() 
	throws ParserException {
		if(recIterator == null)
			return null;

		try {
			data.record = (Record)rootTable.newInstance();
			
			Node node = (Node) recIterator.next();
			reconstruct( data.record, node );
			Node parent = node.getParent();
			String name = (parent == null) ? "add" : parent.getName().toLowerCase();
			node.detach();
			
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
				node.detach();
			}
		} catch(IllegalAccessException e) {
			throw new ParserException(L10n.getString("Error.Internal"));
		}  catch(InstantiationException e) {
			throw new ParserException(L10n.getString("Error.Internal"));
		}
		
		
		return data;
	}

	/**
	 * @return The number of records in the file.
	 */
	public int getNumberOfRecords() {
		return numberOfRecords;
	}
	
	/**
	 * Reconstruct the record from the XML element.
	 * 
	 * @param part	The record to be recontructed.
	 * @param node	The node storing the information about the record.
	 */
    private void reconstruct(Record part, Node node) {
    	if(part == null || node == null)
    		return;
    	for(String property : part.getProperties()) {
    		String value = node.valueOf(property.toLowerCase());
    		if("".equals(value)) value = null;
    		part.setValue(property, value);
    	}
    }

    
	public void cleanup() {
		recIterator = null;
		saxReader.resetHandlers();
		document.clearContent();
		document = null;
		data = null;
		saxReader = null;
		try {
			reader.close();
		} catch(Exception e) {}
	}
     
}
