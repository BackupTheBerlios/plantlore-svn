/*
 * XMLBuilder.java
 *
 * Created on 22. květen 2006, 9:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.export.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.client.export.Template;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 *
 * @author Lada Oberreiterova
 * 
 */
public class XMLBuilder implements Builder {
    
    private Logger logger;

    private Template template;
    private Document document;
    private File fXML;
    private String file = "XMLexport.xml";        
    private Element root;
    private Element occurrence;       
    private Element habitat;
    private Element authors;
    private Element author;
    private Element metadata;
    private Element publication;
    private Element plant;
    private Element phytochorion;
    private int authorActual;
    private int authorCount = 0;

    
    /** Creates a new instance of XMLBuilder */
    public XMLBuilder(Template template, String fileName) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());          
        document = DocumentHelper.createDocument(); 
        this.file = fileName;
        this.template = template;
    }
    
    public void header() throws IOException {
        logger.debug("XML Builder - header.");	
        document.addElement("occurrences");
        root = document.getRootElement();
    }

    public void footer() throws IOException {
         System.out.println("XML Builder disengaged.");
        
        fXML= new File(file);
        if (!fXML.exists()) fXML.createNewFile();
        
        // Pretty print the document to System.out        
        FileOutputStream out = new FileOutputStream(fXML);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( out, format );
        writer.write( document );
        logger.debug("XML doc: "+ document.toString());
    }

    public void startRecord() throws IOException {        
        occurrence = document.getRootElement().addElement("occurrence");          
        authors = null;    
        author = null;
        habitat = null;
        metadata = null;
        plant = null;
        publication = null;
        phytochorion = null;
    }

    public void part(Record record) throws IOException {
        if(record == null) return;        
        if (record.getClass().equals(AuthorOccurrence.class))
            authorActual = authorCount + 1;
        // Build this part of the record.
        Class table = record.getClass();
        for( String property : record.getProperties() ) 
                if( template.isSet(table, property) )
                        output( table, property, record.getValue(property) );
        // Now look at all children of this record.
        for(String key : record.getForeignKeys()) {
                // And build'em too.
                part( (Record) record.getValue(key) );
        }
    }

    protected void output(Class table, String column, Object value) throws IOException {                        
        
        if (value == null) {
            value = "";
        } else {
            value = value.toString();
        }        
        
        if (table.getSimpleName().equals(Occurrence.class.getSimpleName())) {            
            occurrence.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Habitat.class.getSimpleName())) {
            if (habitat == null)
                habitat = occurrence.addElement("habitat");
            habitat.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Author.class.getSimpleName())) {
            if (authors == null) 
                authors = occurrence.addElement("authors");
            if (authorActual != authorCount) {
                authorCount = authorActual;
                author = authors.addElement("author");
            }
            author.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(AuthorOccurrence.class.getSimpleName())) {
            if (authors == null) 
                authors = occurrence.addElement("authors");
            if (authorActual != authorCount) {
                authorCount = authorActual;
                author = authors.addElement("author");
            }
            author.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Metadata.class.getSimpleName())) {
            if (metadata == null)
                metadata = occurrence.addElement("metadata");
            metadata.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Plant.class.getSimpleName())) {
            if (plant == null)
                plant = occurrence.addElement("plant");
            plant.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Publication.class.getSimpleName())) {
            if (publication == null) 
                publication = occurrence.addElement("publication");
            publication.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Territory.class.getSimpleName())) {
            if (habitat == null) 
                habitat = occurrence.addElement("habitat");
            habitat.addElement("territory").setText((String) value);            
        } else if (table.getSimpleName().equals(Phytochorion.class.getSimpleName())) {
            if (habitat == null)
                habitat = occurrence.addElement("habitat");
            if (phytochorion == null)
                phytochorion = habitat.addElement("phytochorion");
            phytochorion.addElement(column).setText((String) value);
        } else if (table.getSimpleName().equals(Village.class.getSimpleName())) {
            if (habitat == null) 
                habitat = occurrence.addElement("habitat");
            habitat.addElement("village").setText((String) value);
        }
    }
    
    public void finishRecord() throws IOException {
    }
    
}
