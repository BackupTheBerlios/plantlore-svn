package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.client.export.Projection;
import net.sf.plantlore.common.record.*;

/**
 * An improved version of the previous XMLBuilder. 
 * This XMLBuilder is capable of creating files of virtually any size.
 * The builder uses the Dom4j to create just one Occurrence element
 * at a time; that element is written down when another Occurrence
 * record arrives to be processed. 
 * <br/>
 * This way, the creation of an element is handled by the Dom4j 
 * (all those necessary conversions 
 * of <code>&gt;</code> to <code>&amp;gt;</code> etc.)
 * <br/>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-07-21
 *
 */
public class XMLBuilder2 implements Builder {
	
    private Writer outputWriter;
    private XMLWriter xmlWriter;
    private Element element;
    protected Projection template;
    
    /**
     * Create a new XML Builder.
     * The builder receives records (holder objects from the database)
     * decomposes them, creates an XML node, and stores it in the specified
     * file.
     * <br/>
     * The template holds the set of important attributes (columns) of the record
     * that will be exported.
     * 
     * @param template	Description of important attributes of  the whole record. 
     * @param writer	The writer that will create the file.
     * @see net.sf.plantlore.client.export.Projection
     */
    public XMLBuilder2(Projection template, Writer writer) {
        this.outputWriter = writer;
        this.template = template;
    }
    
    /**
     * Create a new XML Builder.
     * The builder receives records (holder objects from the database)
     * decomposes them, creates an XML node, and stores it in the specified
     * file.
     * <br/>
     * Every attribute (column) of the whole record will be exported.
     * 
     * @param writer	The writer that will create the file.
     * @see net.sf.plantlore.client.export.Projection
     */
    public XMLBuilder2(Writer writer) {
    	this(new Projection().setEverything(), writer);
    }
    
       
    /**
     * Generate the header of this format.
     */
    public void header() 
    throws IOException {
    	outputWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	outputWriter.write("\n<occurrences>\n");
    	
    	OutputFormat format = OutputFormat.createPrettyPrint();
        xmlWriter = new XMLWriter( outputWriter, format );
    }
    
    /**
     * Generate the footer of this format.
     */
    public void footer() 
    throws IOException {
    	outputWriter.write("</occurrences>\n");
        xmlWriter.close();
    }
    
    /**
     * Begin a new record.
     */
    public void startRecord() 
    throws IOException {
    	element = null;
    }
    
    /**
     * Finish processing of the record.
     */
    public void finishRecord() 
    throws IOException {
    	xmlWriter.write( element );
    }
    

    /**
     * Build part of the whole record.
     */
    public void part(Record record) 
    throws IOException {
    	decompose( element, record);
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
    	
    	Element current;
    	if( father == null )
    		current = element = DocumentHelper.createElement(table.getSimpleName().toLowerCase());
    	else
    		current = father.addElement(table.getSimpleName().toLowerCase());
    	
    	boolean hasAtLeastOneProperty = false;
    	
    	for( String property : record.getProperties() )
    		if( template.isSet(table, property) ) {
    			Object value = record.getValue(property);
    			if( value != null && value.toString().length() > 0 ) {
    				current.addElement(property.toLowerCase()).setText(value.toString());
    				hasAtLeastOneProperty = true;
    			}
    		}
    	
    	// Decompose all subrecords of this record.
    	for(String key : record.getForeignKeys())
    		hasAtLeastOneProperty =  decompose( current, (Record) record.getValue(key) ) || hasAtLeastOneProperty;
    	
    	if( !hasAtLeastOneProperty )
    		current.detach();
    	
    	return hasAtLeastOneProperty;
    }

}
