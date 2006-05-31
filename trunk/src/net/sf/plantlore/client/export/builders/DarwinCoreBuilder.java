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
import java.util.Date;
import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Lada
 */
public class DarwinCoreBuilder implements Builder {

    private Document document;   
    private Writer outputWriter;        
    private Element root;
    private Element actualRecordElement;       
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
        document = DocumentHelper.createDocument(); 
        this.outputWriter = writer;
        document.addElement("recordSet");
        root = document.getRootElement();
    }

    /** Empty. */
    public void header() throws IOException {                
    }

    /*          
     * Generate the footer of this format.
     * Save data to XML file in DarwinCore Schema.
     */
    public void footer() throws IOException {        
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlwriter = new XMLWriter( outputWriter, format );
        xmlwriter.write( document );
        xmlwriter.close();
    }

    /** Empty. */
    public void startRecord() throws IOException {
    }

    /*
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

    /** Empty. */
    public void finishRecord() throws IOException {
    }

    /*
     * Decompose the given <code>occurrence</code>, generate XML document for Darwin Core and mapping data of occurrence.     
     */
    private void outputDarwinCore() {      
        Element record = document.getRootElement().addElement("record");
        setActualREcordElement(record);                
               
        record.addElement("institutionCode").setText(occurrence.getMetadata().getOwnerOrganizationAbbrevNN());
        record.addElement("collectionCode").setText(occurrence.getMetadata().getDataSetTitle());
        record.addElement("basisOfRecord").setText(occurrence.getMetadata().getRecordBasisNN());
        record.addElement("dateLastModified").setText(occurrence.getMetadata().getDateModified().toString());
        
        record.addElement("genus").setText(occurrence.getPlant().getGenusNN());
        record.addElement("identifiedBy").setText(occurrence.getPlant().getScientificNameAuthor());
        record.addElement("scientificName").setText(occurrence.getPlant().getTaxon());
        record.addElement("specificNameAuthorYear").setText(occurrence.getPlant().getScientificNameAuthor());
        
        record.addElement("catalogNumber").setText(occurrence.getId().toString());
        record.addElement("dayCollected").setText(occurrence.getDayCollectedNN().toString());
        record.addElement("monthCollected").setText(occurrence.getMonthCollectedNN().toString());
        record.addElement("yearCollected").setText(occurrence.getYearCollected().toString());
        record.addElement("timeOfDay").setText(occurrence.getTimeCollectedNN().toString());
        record.addElement("notes").setText(occurrence.getNoteNN());
        
        record.addElement("country").setText(occurrence.getHabitat().getCountryNN());
        record.addElement("county").setText(occurrence.getHabitat().getNearestVillage().getName());
        record.addElement("stateProvince").setText(occurrence.getHabitat().getTerritory().getName());
        record.addElement("locality").setText(occurrence.getHabitat().getDescriptionNN());
        record.addElement("latitude").setText(occurrence.getHabitat().getLatitudeNN().toString());
        record.addElement("longitude").setText(occurrence.getHabitat().getLongitudeNN().toString());
        record.addElement("minimumElevation").setText(occurrence.getHabitat().getAltitudeNN().toString());
  
    }

    private void outputAuthors() {
        Element record = getActualRecordElement();
        record.addElement("collector").setText(authorOccurrence.getAuthor().getWholeName());
    }
    
   public void setActualREcordElement(Element recordElement) {
        this.actualRecordElement = recordElement;
    }
    
    public Element getActualRecordElement() {
        return this.actualRecordElement;
    }        
}
