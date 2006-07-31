package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;

import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Projection;
import net.sf.plantlore.common.record.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 * XML Builder.
 *
 * @author Lada Oberreiterová
 * @author Erik Kratochvíl
 */
public class XMLBuilder extends AbstractBuilder {

    private Document document;
    private Writer outputWriter;
    private Element occurrence;
    
    /**
     * Create a new XML Builder.
     * The builder receives records (holder objects from the database)
     * decomposes them, creates an XML tree, and stores it in the specified
     * file.
     * <br/>
     * The template holds the set of important attributes (columns) of the record
     * that will be exported.
     * 
     * @param template	Description of important attributes of  the whole record. 
     * @param writer	The writer that will create the file.
     * @see net.sf.plantlore.client.export.Projection
     */
    public XMLBuilder(Projection template, Writer writer) {
    	super(template);
        document = DocumentHelper.createDocument();
        document.addElement("occurrences");
        this.outputWriter = writer;
        this.template = template;
    }
    
    /**
     * Create a new XML Builder.
     * The builder receives records (holder objects from the database)
     * decomposes them, creates an XML tree, and stores it in the specified
     * file.
     * <br/>
     * Every attribute (column) of the whole record will be exported.
     * 
     * @param writer	The writer that will create the file.
     * @see net.sf.plantlore.client.export.Projection
     */
    public XMLBuilder(Writer writer) {
    	this(new Projection().setEverything(), writer);
    }
    
    /**
     * Generate the footer of this format.
     */
    @Override
    public void footer() throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlwriter = new XMLWriter( outputWriter, format );
        xmlwriter.write( document );
        xmlwriter.close();
    }

    /**
     * Build part of the whole record.
     */
    @Override
    public void part(Record record) 
    throws IOException {
    	// One Occurrence can have many AuthorOccurrences [AOs]. 
    	// All those AOs are in their own special node <authors></authors>.
    	decompose( (record instanceof AuthorOccurrence) ? occurrence : document.getRootElement(), record);
    }
    
    /**
     * Decompose the given <code>record</code> and build the XML tree appropriately.
     *  
     * @param father	Father element of the currently processed <code>record</code>.
     * @param record	Part of the whole record corresponding to a certain table in the database.
     */
    protected boolean decompose(Element father, Record record) 
    throws IOException {
    	if(record == null) 
    		return false;
    	
    	Class table = record.getClass();
    	Element current = father.addElement(table.getSimpleName().toLowerCase());
    	// Every occurrence may have 0..N associated AuthorOccurrences.
    	if(record instanceof Occurrence) 
    		occurrence = current;
    	
    	boolean hasAtLeastOneProperty = false;
    	
    	for( String property : record.getProperties() )
    		if( template.isSet(table, property) ) {
    			Object value = record.getValue(property);
    			current.addElement(property.toLowerCase()).setText(value == null ? "" : value.toString());
    			hasAtLeastOneProperty = true;
    		}
    	
    	// Decompose all subrecords of this record.
    	for(String key : record.getForeignKeys()) {
    		hasAtLeastOneProperty =  decompose( current, (Record) record.getValue(key) ) || hasAtLeastOneProperty;
    	}
    	
    	if( !hasAtLeastOneProperty )
    		current.detach();
    	
    	return hasAtLeastOneProperty;
    }

	@Override
	protected void output(Class table, String column, Object value) throws IOException {
		// I do not need this method.
	}

}
