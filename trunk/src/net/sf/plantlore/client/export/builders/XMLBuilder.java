package net.sf.plantlore.client.export.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Template;
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
    private String filename;
    private Element authors;
    
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
     * @param filename	The name of the file where the output should be saved.
     * @see net.sf.plantlore.client.export.Template
     */
    public XMLBuilder(Template template, String filename) {
    	super(template);
        document = DocumentHelper.createDocument();
        document.addElement("occurrences");
        this.filename = filename;
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
     * @param filename	The name of the file where the output should be saved.
     * @see net.sf.plantlore.client.export.Template
     */
    public XMLBuilder(String fileName) {
    	this(new Template().setEverything(), fileName);
    }
    
    /**
     * Generate the footer of this format.
     */
    @Override
    public void footer() throws IOException {
        File fXML= new File(filename);
        if (!fXML.exists()) fXML.createNewFile();
        
        FileOutputStream out = new FileOutputStream(fXML);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( out, format );
        writer.write( document );
        writer.close();
    }

    /**
     * Build part of the whole record.
     */
    @Override
    public void part(Record record) 
    throws IOException {
    	// One Occurrence can have many AuthorOccurrences [AOs]. 
    	// All those AOs are in their own special node <authors></authors>.
    	decompose( (record instanceof AuthorOccurrence) ? authors : document.getRootElement(), record);
    }
    
    /**
     * Decompose the given <code>record</code> and build the XML tree appropriately.
     *  
     * @param father	Father element of the currently processed <code>record</code>.
     * @param record	Part of the whole record corresponding to a certain table in the database.
     */
    protected void decompose(Element father, Record record) 
    throws IOException {
    	if(record == null) return;
    	Element current = father.addElement(record.getClass().getSimpleName().toLowerCase());
    	// Every occurrence may have 0..N associated AuthorOccurrences.
    	if(record instanceof Occurrence) 
    		authors = current.addElement("authors");
    	
    	for( String property : record.getProperties() ) {
    		String value = record.getValue(property) == null ? "" : record.getValue(property).toString();
    		current.addElement(property.toLowerCase()).setText(value);
    	}
    	// Decompose all subrecords of this record.
    	for(String key : record.getForeignKeys())
    		decompose( current, (Record) record.getValue(key) );
    }

	@Override
	protected void output(Class table, String column, Object value) throws IOException {}

}
