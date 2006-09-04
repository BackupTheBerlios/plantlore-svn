/*
 * DarwinCoreBuilder.java
 *
 * Created on 19. květen 2006, 23:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.export.builders;


import java.io.IOException;
import java.io.Writer;
import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 *  DarwinCore2 builder
 *  
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class DarwinCoreBuilder implements Builder {
     
    private Writer outputWriter; 
    private XMLWriter xmlWriter; 
    private Element recordElement;         
    private Occurrence occurrence = null;  
    private AuthorOccurrence authorOccurrence = null;
    
    /** 
     * Creates a new instance of DarwinCoreBuilder 
     * This builder receives records (holder objects from the database)
     * decomposes them, mapping them to Darwin Core 2 and stores it in the 
     * specified XML file. 
     *
     * @param writer	The writer that will create the file.     
     */
    public DarwinCoreBuilder(Writer writer) {      
    	outputWriter = writer;
    }
    
    /**
     * Generate the header of this format.
     */
    public void header() 
    throws IOException {
    	outputWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	outputWriter.write("\n<recordSet>");
    	
    	OutputFormat format = OutputFormat.createPrettyPrint();
        xmlWriter = new XMLWriter( outputWriter, format );
    }

    /**
     * Generate the footer of this format.
     */
    public void footer() 
    throws IOException {
    	System.out.println("ABCD - footer.");
    	outputWriter.write("\n</recordSet>");
        xmlWriter.close();
    }       
    
    /**
     * Begin a new record.
     */
    public void startRecord() throws IOException {      	 
    	recordElement = DocumentHelper.createElement("record");       
    }

    /**
     * Finish processing of the record.
     */
    public void finishRecord() throws IOException {      	       
    	xmlWriter.write(recordElement);
    }
   
    /**
     * Build part of the whole record.
     */
    public void part(Record record) throws IOException {
        if(record == null) return;        
        Class table = record.getClass();
        if(table == Occurrence.class) {
            //set occurrence and generate XML
            occurrence = (Occurrence)record;
            outputDarwinCore();
        } else if (table == AuthorOccurrence.class) {
            authorOccurrence = (AuthorOccurrence)record;
            outputAuthors();
        }     
    }

    /**
     * Decompose the given <code>occurrence</code>, generate XML document for Darwin Core and mapping data of occurrence.     
     */
    private void outputDarwinCore() {              
        recordElement.addElement("institutionCode").setText(occurrence.getMetadata().getOwnerOrganizationAbbrevNN());
        recordElement.addElement("collectionCode").setText(occurrence.getMetadata().getDataSetTitle());
        recordElement.addElement("basisOfRecord").setText(occurrence.getMetadata().getRecordBasisNN());
        recordElement.addElement("dateLastModified").setText(occurrence.getMetadata().getDateModified().toString());
        
        recordElement.addElement("genus").setText(occurrence.getPlant().getGenusNN());
        recordElement.addElement("identifiedBy").setText(occurrence.getPlant().getScientificNameAuthorNN());
        recordElement.addElement("scientificName").setText(occurrence.getPlant().getTaxon());
        recordElement.addElement("specificNameAuthorYear").setText(occurrence.getPlant().getScientificNameAuthorNN());
        
        recordElement.addElement("catalogNumber").setText(occurrence.getId().toString());
        recordElement.addElement("dayCollected").setText(occurrence.getDayCollectedNN().toString());
        recordElement.addElement("monthCollected").setText(occurrence.getMonthCollectedNN().toString());
        recordElement.addElement("yearCollected").setText(occurrence.getYearCollected().toString());
        recordElement.addElement("timeOfDay").setText(occurrence.getTimeCollectedNN().toString());
        recordElement.addElement("notes").setText(occurrence.getNoteNN());
        
        recordElement.addElement("country").setText(occurrence.getHabitat().getCountryNN());
        recordElement.addElement("county").setText(occurrence.getHabitat().getNearestVillage().getName());
        recordElement.addElement("stateProvince").setText(occurrence.getHabitat().getTerritory().getName());
        recordElement.addElement("locality").setText(occurrence.getHabitat().getDescriptionNN());
        recordElement.addElement("latitude").setText(occurrence.getHabitat().getLatitudeNN().toString());
        recordElement.addElement("longitude").setText(occurrence.getHabitat().getLongitudeNN().toString());
        recordElement.addElement("minimumElevation").setText(occurrence.getHabitat().getAltitudeNN().toString());
  
    }

    /**
     * Create elements for author of occurrence.
     */
    private void outputAuthors() {        
        recordElement.addElement("collector").setText(authorOccurrence.getAuthor().getWholeName());
    }           
}
