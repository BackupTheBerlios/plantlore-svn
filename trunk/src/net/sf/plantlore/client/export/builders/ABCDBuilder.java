package net.sf.plantlore.client.export.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.client.export.Template;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * ABCD Builder 
 *
 * @author Lada Oberreiterová
 * @since 18.5.2006
 * @version
 *
 */
public class ABCDBuilder implements Builder {
    
    private Logger logger;

    private Document document;
    private File fXML;
    private String file = "ABCD_2_06.xml";    
    private String projectTitle = "";
    private Element root;
    private Element actualUnitsElement;   
    private Element actualAgentsElement;
    private Occurrence occurrence = null;  
    private AuthorOccurrence authorOccurrence = null;
    
    
    /** Creates a new instance of ABCDBuilder */
    public ABCDBuilder(String file) {                
        logger = Logger.getLogger(this.getClass().getPackage().getName());          
        document = DocumentHelper.createDocument();        
        this.file = file;
    }
    
    
    public void header() throws IOException {
	logger.debug("ABCD Builder - header.");	
        document.addElement("dataSets");
        root = document.getRootElement();
    }

    /*
     * Create new XML file for saved data.
     * Save data into XML file in ABCD Schema.
     */
    public void footer() throws IOException {
        System.out.println("Training Builder disengaged.");
        
        fXML= new File(file);
        if (!fXML.exists()) fXML.createNewFile();
        
        // Pretty print the document to System.out        
        FileOutputStream out = new FileOutputStream(fXML);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( out, format );
        writer.write( document );
    }

    public void startRecord() throws IOException {
            logger.debug("ABCD Builder - startRecord.");            
    }

    public void finishRecord() throws IOException {
            logger.debug("ABCD Builder - finishRecord.");            
    }

    /**
     * Only for testing
     */
    public void output(Class table, String column, Object value) throws IOException {
            System.out.println("   " + table.getSimpleName() + "." + column + " = " + value);
    }
    
    /**
     * For eache occurrence generate XML elements and save value.
     */
    public void outputABCD() {
        if (occurrence == null ) return;
        String projectName = occurrence.getMetadata().getDataSetTitle();
        if (!projectTitle.equals(projectName)) {
            projectTitle = projectName;
            generateDataSet();
        }
        Element unit = getActualUnitsElement().addElement("Unit");
        Element gathering = unit.addElement("gathering");
        Element herbariumUnit = unit.addElement("herbariumUnit");
        Element identifications = unit.addElement("identifications");
        Element unitReferences = unit.addElement("unitReferences");
        
        Element agents = gathering.addElement("agents");
        Element altitude = gathering.addElement("altitude");
        Element country = gathering.addElement("country");
        Element dateTime = gathering.addElement("dateTime");
        Element project = gathering.addElement("project");
        Element siteCoordinateSets = gathering.addElement("siteCoordinateSets");
                
        Element measurementOrFactAtomised = altitude.addElement("measurementOrFactAtomised");
        Element siteCoordinates = siteCoordinateSets.addElement("siteCoordintates");
        Element coordinatesLatLong = siteCoordinates.addElement("coordinatesLatLong");
        Element unitReference = unitReferences.addElement("unitReference");
        
        Element identification = identifications.addElement("identification");
        Element result = identification.addElement("result");
        Element taxonIdentified = result.addElement("taxonIdentified");
        Element specificName = taxonIdentified.addElement("specificName");
        Element nameAtomised = specificName.addElement("nameAtomised");
        Element bacterial = nameAtomised.addElement("bacterial");               
                        
        //save data
        unit.addElement("dateLastEdit").setText(occurrence.getMetadata().getDateModified().toString());
        unit.addElement("recordBasis").setText(occurrence.getMetadata().getRecordBasis());
        unit.addElement("sourceId").setText(occurrence.getMetadata().getSourceId());
        unit.addElement("sourceInstitutionId").setText(occurrence.getMetadata().getSourceInstitutionId());
        unit.addElement("unitId").setText(occurrence.getId().toString());        
        
        gathering.addElement("localityText").setText(occurrence.getHabitat().getDescription());
        gathering.addElement("notes").setText(occurrence.getHabitat().getNote());
        measurementOrFactAtomised.addElement("lowerValue").setText(occurrence.getHabitat().getAltitude().toString());
        country.addElement("name").setText(occurrence.getHabitat().getCountry());
        dateTime.addElement("isoDateTimeBegin").setText(occurrence.getIsoDateTimeBegin().toString());
        project.addElement("projectTitle").setText(occurrence.getMetadata().getDataSetTitle());
        coordinatesLatLong.addElement("latitudeDecimal").setText(occurrence.getHabitat().getLatitude().toString());
        coordinatesLatLong.addElement("longitudeDecimal").setText(occurrence.getHabitat().getLongitude().toString());
        herbariumUnit.addElement("exsiccatum").setText(occurrence.getHerbarium());
        specificName.addElement("fullSpecificNameString").setText(occurrence.getPlant().getSpecies());
        bacterial.addElement("genusOrMonomial").setText(occurrence.getPlant().getGenus());        
        
        //FIXME: nutno osetrit polozky, co mohou byt NULL - hlavne FK
        unitReference.addElement("citationDetail").setText(occurrence.getPublication().getReferenceDetail());
        unitReference.addElement("titleCitation").setText(occurrence.getPublication().getReferenceCitation());
        unitReference.addElement("url").setText(occurrence.getPublication().getUrl());
        
        //set element AGENTS 
        setActualAgentsElement(agents);
    }

    /**
     * For each project generate elements contains metadata (content and technidal contact person, project descrption).
     *
     */
    public void generateDataSet() {
        Element contentContacts = root.addElement("contentContacts");
        Element technicalContacts = root.addElement("technicalContacts");
        Element metadata = root.addElement("metadata");
        Element units = root.addElement("units");
        
        Element contentContact = contentContacts.addElement("contentContacts");
        Element technicalContact = technicalContacts.addElement("technicalContact");
        Element description = metadata.addElement("description");
        Element owners = metadata.addElement("owners");  //dopsat elementy pro owners      
        Element revisionData = metadata.addElement("revisionData");
        Element representation = description.addElement("representation");
        
        Metadata metadataRecord = occurrence.getMetadata();    
        
        contentContact.addElement("address").setText(metadataRecord.getContentContactAddress());
        contentContact.addElement("email").setText(metadataRecord.getContentContactEmail());
        contentContact.addElement("name").setText(metadataRecord.getContentContactName());
        
        technicalContact.addElement("address").setText(metadataRecord.getTechnicalContactAddress());
        technicalContact.addElement("email").setText(metadataRecord.getTechnicalContactEmail());
        technicalContact.addElement("name").setText(metadataRecord.getTechnicalContactName());                
        
        representation.addElement("title").setText(metadataRecord.getDataSetTitle());
        representation.addElement("details").setText(metadataRecord.getDataSetDetails());
        revisionData.addElement("creators").setText(metadataRecord.getSourceInstitutionId());
        revisionData.addElement("creators").setText(metadataRecord.getSourceId());
        revisionData.addElement("dateCreated").setText(metadataRecord.getDateCreate().toString());
        revisionData.addElement("dateModified").setText(metadataRecord.getDateModified().toString());
        
        setActualUnitsElement(units);
    }
    
     public void outputAuthors() {
        Element agents = getActualAgentElement();
        Element gatheringAgent = agents.addElement("gatheringAgent");
        Element organization = gatheringAgent.addElement("organization");
        Element name = organization.addElement("name");
        Element representation = name.addElement("representation");
        Element person = gatheringAgent.addElement("person");

        //save data
        person.addElement("fullName").setText(authorOccurrence.getAuthor().getWholeName());  
        representation.addElement("text").setText(authorOccurrence.getAuthor().getOrganization());
    }
    
    public void part(Record record) throws IOException {
        if(record == null) return;        
        Class table = record.getClass();
        if(table == Occurrence.class) {
            //set occurrence and generate XML
            occurrence = (Occurrence)record;
            outputABCD();
        } else if (table == AuthorOccurrence.class) {
            authorOccurrence = (AuthorOccurrence)record;
            outputAuthors();
        }               
    }    
    
    public void setActualUnitsElement(Element unitsElement) {
        this.actualUnitsElement = unitsElement;
    }
    
    public Element getActualUnitsElement() {
        return this.actualUnitsElement;
    }

    public void setActualAgentsElement(Element agentsElement){
        this.actualAgentsElement = agentsElement;
    }
    
    public Element getActualAgentElement(){
        return this.actualAgentsElement;
    }
      
}