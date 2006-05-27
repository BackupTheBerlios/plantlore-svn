package net.sf.plantlore.client.imports.parsers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import net.sf.plantlore.client.imports.AbstractParser;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
//import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


/**
 *
 * @author Lada Oberreiterova
 */
public class XMLParser extends AbstractParser {

    private Document document;
    private List occurrences;
    private Iterator occIterator, aoIterator;
    private Node currentOccurrence;
    
    private Occurrence occ;
   

    
    public XMLParser(Reader reader) {
        super(reader);               
    }
    
    
    public int getNumberOfRecords() {
    	return occurrences.size();
    }

    
    public void initialize() 
    throws ParserException {
        SAXReader saxReader = new SAXReader();        
        try {            
            document = saxReader.read( reader );
        } catch (DocumentException ex) {
            throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
        } 
        occurrences = document.selectNodes("//occurrence");
        occIterator = occurrences.iterator();
    }

    
    public void cleanup() {}

    
    public boolean hasNextRecord() {        
        return occIterator.hasNext();
    }

    
    public Action fetchNextRecord() 
    throws ParserException {
    	currentOccurrence = (Node) occIterator.next();
    	List authors = currentOccurrence.selectNodes(AuthorOccurrence.class.getSimpleName());
    	
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
    	for(String property : part.getProperties()) {
    		part.setValue(property, node.valueOf(property));
    	}
    	// Reconstruct subrecords as well.
    	List<String> foreignKeys = part.getForeignKeys();
    	if(part instanceof AuthorOccurrence)
    		foreignKeys.remove(AuthorOccurrence.OCCURRENCE); // Occurrence is already reconstructed.
    	for(String key : foreignKeys) {
    		Node subNode = node.selectSingleNode(key);
    		if(subNode != null)
    			reconstruct( (Record)part.getValue(key), subNode );
    		else
    			part.setValue(key, null);
    	}
    }
    
    public Record nextPart(Class table) 
    throws ParserException {
    	if(table == AuthorOccurrence.class) {
    		Node currentAO = (Node) aoIterator.next();
    		reconstruct( ao, currentAO );
    		return ao;
    	} 
    	else 
    		return occ.findSubrecord(table);
    }
    
    
    public boolean hasNextPart(Class table) {
    	if(table == AuthorOccurrence.class && aoIterator.hasNext())
    		return true;
    	else
    		return false;
    }

    
    
    public static void main(String[] args) 
    throws FileNotFoundException {
        XMLParser xmlParser = new XMLParser(new FileReader("c:/Documents and Settings/yaa/Dokumenty/Plantlore/part.OK.xml"));       
         try {
             xmlParser.initialize();
             while (xmlParser.hasNextRecord()) {
            	 xmlParser.fetchNextRecord();
            	 
                 Occurrence occ = (Occurrence) xmlParser.nextPart(Occurrence.class);
                 System.out.println("===========================================");
                 System.out.println(occ);
                 System.out.println("   -------------------------------------------------------");
                 while (xmlParser.hasNextPart(AuthorOccurrence.class)) {
                	 System.out.println("   -------------------------------------------------------");
                	 AuthorOccurrence ao =  (AuthorOccurrence) xmlParser.nextPart(AuthorOccurrence.class);
                	 System.out.println("   " + ao);
                 }  
                 
                 System.out.println("===========================================");
             }
         } catch (ParserException ex) {
             ex.printStackTrace();
         }
    }
}
