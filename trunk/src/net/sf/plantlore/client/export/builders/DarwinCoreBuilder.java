/*
 * DarwinCoreBuilder.java
 *
 * Created on 19. květen 2006, 23:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.export.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.log4j.Logger;
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
    
    private Logger logger;

    private Document document;
    private File fXML;
    private String file = "DarwinCore2.xml";        
    private Element root;
    private Element actualRecordElement;       
    private Occurrence occurrence = null;  
    private AuthorOccurrence authorOccurrence = null;
    
    /** Creates a new instance of DarwinCoreBuilder */
    public DarwinCoreBuilder(String file) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());          
        document = DocumentHelper.createDocument(); 
        this.file = file;
    }

    public void header() throws IOException {
        logger.debug("DarwinCore Builder - header.");	
        document.addElement("recordSet");
        root = document.getRootElement();
    }

    public void footer() throws IOException {
        System.out.println("DarwinCore Builder disengaged.");
        
        fXML= new File(file);
        if (!fXML.exists()) fXML.createNewFile();
        
        // Pretty print the document to System.out        
        FileOutputStream out = new FileOutputStream(fXML);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( out, format );
        writer.write( document );
    }

    public void startRecord() throws IOException {
    }

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

    public void finishRecord() throws IOException {
    }

    private void outputDarwinCore() {
        logger.debug("output");
        Element record = document.getRootElement().addElement("record");
        setActualREcordElement(record);                
               
        record.addElement("institutionCode").setText(occurrence.getMetadata().getOwnerOrganizationAbbrev());
        record.addElement("collectionCode").setText(occurrence.getMetadata().getDataSetTitle());
        record.addElement("basisOfRecord").setText(occurrence.getMetadata().getRecordBasis());
        record.addElement("dateLastModified").setText(occurrence.getMetadata().getDateModified().toString());
        
        record.addElement("genus").setText(occurrence.getPlant().getGenus());
        record.addElement("identifiedBy").setText(occurrence.getPlant().getScientificNameAuthor());
        record.addElement("scientificName").setText(occurrence.getPlant().getTaxon());
        record.addElement("specificNameAuthorYear").setText(occurrence.getPlant().getScientificNameAuthor());
        
        record.addElement("catalogNumber").setText(occurrence.getId().toString());
        record.addElement("dayCollected").setText(occurrence.getDayCollected().toString());
        record.addElement("monthCollected").setText(occurrence.getMonthCollected().toString());
        record.addElement("yearCollected").setText(occurrence.getYearCollected().toString());
        record.addElement("timeOfDay").setText(occurrence.getTimeCollected().toString());
        record.addElement("notes").setText(occurrence.getNote());
        
        record.addElement("country").setText(occurrence.getHabitat().getCountry());
        record.addElement("county").setText(occurrence.getHabitat().getNearestVillage().getName());
        record.addElement("stateProvince").setText(occurrence.getHabitat().getTerritory().getName());
        record.addElement("locality").setText(occurrence.getHabitat().getDescription());
        record.addElement("latitude").setText(occurrence.getHabitat().getLatitude().toString());
        record.addElement("longitude").setText(occurrence.getHabitat().getLongitude().toString());
        record.addElement("minimumElevation").setText(occurrence.getHabitat().getAltitude().toString());
  
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
