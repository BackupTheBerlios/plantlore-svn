package net.sf.plantlore.client.imports.parsers;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import net.sf.plantlore.client.imports.AbstractParser;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
//import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


/**
 *
 * @author Lada Oberreiterová
 * @author Erik Kratochvíl
 */
public class XMLParser extends AbstractParser {

    private Document document;
    private int occurrences;
    private Iterator occIterator, aoIterator;
    private Node currentOccurrence;
    
    private AuthorOccurrence ao;
    private Occurrence occ;
   

    
    public XMLParser(Reader reader) {
        super(reader);               
    }
    
    
    public int getNumberOfRecords() {
    	return occurrences;
    }

    @Override
    public void initialize() 
    throws ParserException {
        try {
        	SAXReader saxReader = new SAXReader();
            document = saxReader.read( reader );
            occIterator = document.selectNodes("//occurrence").iterator();
        } catch (Exception ex) {
            throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
        } 
    }


    @Override
    public boolean hasNextRecord() {        
        return occIterator.hasNext();
    }


    @Override
    public Action fetchNextRecord() 
    throws ParserException {
    	currentOccurrence = (Node) occIterator.next();
    	List authors = currentOccurrence.selectNodes(AuthorOccurrence.class.getSimpleName().toLowerCase());
    	if(authors == null || authors.size() <= 0)
    		throw new ParserException("Error.IncompleteRecord");
    	aoIterator = authors.iterator();
    	
    	ao = (AuthorOccurrence) new AuthorOccurrence().createTorso();
    	occ = ao.getOccurrence();
    	reconstruct( occ, currentOccurrence );
    	
        return Action.UNKNOWN;
    }

    
    private void reconstruct(Record part, Node node) {
    	if(part == null || node == null)
    		return;
    	// Retrieve properties.
    	for(String property : part.getProperties()) 
    		part.setValue(property, node.valueOf(property.toLowerCase()));
    	
    	// Reconstruct subrecords as well.
    	List<String> foreignKeys = part.getForeignKeys();
    	if(part instanceof AuthorOccurrence)
    		foreignKeys.remove(AuthorOccurrence.OCCURRENCE); // Occurrence has already been reconstructed.
    	for(String key : foreignKeys) {
    		Node subNode = node.selectSingleNode(key.toLowerCase());
    		if(subNode != null)
    			reconstruct( (Record)part.getValue(key), subNode );
    		else
    			part.setValue(key, null);
    	}
    }
    
    
    @Override
    public Record getNextPart(Class table) 
    throws ParserException {
    	if(table == AuthorOccurrence.class) {
    		Node currentAO = (Node) aoIterator.next();
    		reconstruct( ao, currentAO );
    		return ao;
    	} 
    	else 
    		return occ.findSubrecord(table);
    }
    
    
    @Override
    public boolean hasNextPart(Class table) {
    	if(table == AuthorOccurrence.class && aoIterator.hasNext())
    		return true;
    	else
    		return false;
    }
   
}
