package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;
import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.client.export.Template;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
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
 *
 *
 */
public class ABCDBuilder implements Builder {
        
    private Document document;  
    private Writer outputWriter;  
    private String projectTitle = "";
    private Element root;
    private Element actualUnitsElement;   
    private Element actualAgentsElement;
    private Occurrence occurrence = null;  
    private AuthorOccurrence authorOccurrence = null;
    
    
    /** 
     * Creates a new instance of ABCDBuilder.
     * This builder receives records (holder objects from the database)
     * decomposes them, mapping them to ABCD Schema 2.06 and stores it in the 
     * specified XML file. 
     * <br/>     
     *
     * @param writer	The writer that will create the file.     
     */
    public ABCDBuilder(Writer writer) {                           
        document = DocumentHelper.createDocument();        
        this.outputWriter = writer;
        document.addElement("dataSets");
        root = document.getRootElement();
    }
    
    /** Empty. */ 
    public void header() throws IOException {	        
    }

    /*          
     * Generate the footer of this format.
     * Save data to XML file in ABCD Schema.
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

    /** Empty. */
    public void finishRecord() throws IOException {    
    }
    
    /**     
     *  Decompose the given <code>occurrence</code>, generate XML document for ABCD Schema and mapping data of occurrence.
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
        unit.addElement("recordBasis").setText(occurrence.getMetadata().getRecordBasisNN());
        unit.addElement("sourceId").setText(occurrence.getMetadata().getSourceId());
        unit.addElement("sourceInstitutionId").setText(occurrence.getMetadata().getSourceInstitutionId());
        unit.addElement("unitId").setText(occurrence.getId().toString());        
        
        gathering.addElement("localityText").setText(occurrence.getHabitat().getDescriptionNN());
        gathering.addElement("notes").setText(occurrence.getHabitat().getNoteNN());
        measurementOrFactAtomised.addElement("lowerValue").setText(occurrence.getHabitat().getAltitudeNN().toString());
        country.addElement("name").setText(occurrence.getHabitat().getCountryNN());
        dateTime.addElement("isoDateTimeBegin").setText(occurrence.getIsoDateTimeBeginNN().toString());
        project.addElement("projectTitle").setText(occurrence.getMetadata().getDataSetTitle());
        coordinatesLatLong.addElement("latitudeDecimal").setText(occurrence.getHabitat().getLatitudeNN().toString());
        coordinatesLatLong.addElement("longitudeDecimal").setText(occurrence.getHabitat().getLongitudeNN().toString());
        herbariumUnit.addElement("exsiccatum").setText(occurrence.getHerbariumNN());
        specificName.addElement("fullSpecificNameString").setText(occurrence.getPlant().getSpeciesNN());
        bacterial.addElement("genusOrMonomial").setText(occurrence.getPlant().getGenusNN());        
        
        //FIXME: nutno osetrit polozky, co mohou byt NULL - hlavne FK
        unitReference.addElement("citationDetail").setText(occurrence.getPublication().getReferenceDetailNN());
        unitReference.addElement("titleCitation").setText(occurrence.getPublication().getReferenceCitation());
        unitReference.addElement("url").setText(occurrence.getPublication().getUrlNN());
        
        //set element AGENTS 
        setActualAgentsElement(agents);
    }

    /**
     * For each project create elements contains metadata (content contact person, technidal contact person, project descrption,...).
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
        
        contentContact.addElement("address").setText(metadataRecord.getContentContactAddressNN());
        contentContact.addElement("email").setText(metadataRecord.getContentContactEmailNN());
        contentContact.addElement("name").setText(metadataRecord.getContentContactName());
        
        technicalContact.addElement("address").setText(metadataRecord.getTechnicalContactAddressNN());
        technicalContact.addElement("email").setText(metadataRecord.getTechnicalContactEmailNN());
        technicalContact.addElement("name").setText(metadataRecord.getTechnicalContactName());                
        
        representation.addElement("title").setText(metadataRecord.getDataSetTitle());
        representation.addElement("details").setText(metadataRecord.getDataSetDetailsNN());
        revisionData.addElement("creators").setText(metadataRecord.getSourceInstitutionId());
        revisionData.addElement("creators").setText(metadataRecord.getSourceId());
        revisionData.addElement("dateCreated").setText(metadataRecord.getDateCreate().toString());
        revisionData.addElement("dateModified").setText(metadataRecord.getDateModified().toString());
        
        setActualUnitsElement(units);
    }
    
    /*
     *  Create elements for author of occurrence.     
     */
     public void outputAuthors() {
        Element agents = getActualAgentElement();
        Element gatheringAgent = agents.addElement("gatheringAgent");
        Element organization = gatheringAgent.addElement("organization");
        Element name = organization.addElement("name");
        Element representation = name.addElement("representation");
        Element person = gatheringAgent.addElement("person");

        //save data
        person.addElement("fullName").setText(authorOccurrence.getAuthor().getWholeName());  
        representation.addElement("text").setText(authorOccurrence.getAuthor().getOrganizationNN());
    }
    
     /*
      *  Build part of the whole record.
      */
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