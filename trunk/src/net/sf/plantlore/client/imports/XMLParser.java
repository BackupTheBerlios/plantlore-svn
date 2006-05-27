/*
 * XMLParser.java
 *
 * Created on 24. květen 2006, 12:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.imports;

import java.io.File;
import java.util.List;
import net.sf.plantlore.common.exception.ParserException;
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
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Lada Oberreiterova
 */
public class XMLParser implements Parser {

    private Logger logger;
    private Document document;
    private String file;
    private Integer currentNumberOcc = 0;
    private Integer currentNumberAuthor = 0;
    private List occurrenceList;
    private List authorsList;
    
    private Node occNode = null;
    private Node authorsNode = null;
    private Node autOccNode = null;
    private Node autNode = null;
    private Node habNode = null; 
    private Node metadataNode = null;
    private Node planNode = null;
    private Node pubNode = null;
    private Node terrNode = null;
    private Node villNode = null;
    private Node phytNode = null;
    
    private Occurrence occurrence;
    private AuthorOccurrence authorOccurrence;
    private Author author;
    private Metadata metadata;
    private Plant plant;
    private Publication publication;
    private Habitat habitat;
    private Village village;
    private Territory territory;
    private Phytochorion phytochorion;
    
    /** Creates a new instance of XMLParser */
    public XMLParser(String fileName) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());                  
        this.file = fileName;               
    }

    public void initialize() throws ParserException {
        SAXReader reader = new SAXReader();        
        File fXML = new File(file);
        try {            
            this.document = reader.read(fXML);
        } catch (DocumentException ex) {
            throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
        }        
        occurrenceList = document.selectNodes("//occurrence");
        setNumberOfOccurrences(occurrenceList.size());        
    }

    public void cleanup() {
    }

    public boolean hasNextRecord() {        
        return getNumberOfOccurrences() > 0;
    }

    public Parser.Action fetchNextRecord() {
        return Parser.Action.UNKNOWN;
    }

    public Record getNextPart(Class table) throws ParserException {        
        if (table == Occurrence.class) {
            //OCC_NODE
            setOccurrenceNode(occurrenceList);            
            List habList = occNode.selectNodes("habitat");
            List metadataList = occNode.selectNodes("metadata");
            List plantList =   occNode.selectNodes("plant");
            List pubList = occNode.selectNodes("publication");  
            setHabitatNode(habList);
            setMetadataNode(metadataList);
            setPlantNode(plantList);
            setPubNode(pubList);
                       
            //HAB_NODE
            List terrList =  habNode.selectNodes("territory");
            List villList = habNode.selectNodes("village");
            List phyList = habNode.selectNodes("phytochorion");
            setTerrNode(terrList);
            setVillNode(villList);
            setPhyNode(phyList);
                        
            //set list of author`s node
            authorsList = occNode.selectNodes("authors");
            setNumberOfAuthors(authorsList.size());
            
            //create Records       
            occurrence = new Occurrence();            
            habitat = new Habitat();
            metadata = new Metadata();
            publication = new Publication();
            plant = new Plant();
            village = new Village();
            territory = new Territory();
            phytochorion = new Phytochorion();
            
            //set FK of Records
            occurrence.setHabitat(habitat);
            occurrence.setPlant(plant);
            occurrence.setPublication(publication);
            occurrence.setMetadata(metadata);
            habitat.setTerritory(territory);
            habitat.setPhytochorion(phytochorion);
            habitat.setNearestVillage(village);            
            
            //load data to record
            part((Record)occurrence, occNode);
            part((Record)habitat, habNode);
            part((Record)metadata, metadataNode);
            part((Record)plant, planNode);
            part((Record)publication, pubNode);
            part((Record)territory, terrNode);
            part((Record)village, villNode);
            part((Record)phytochorion, phytNode);                                          
            
            return occurrence;
        } else if (table == AuthorOccurrence.class) {
            
            //create NODE                    
            setAuthorsNode(authorsList);            
            //List with author`s node
            List autList = authorsNode.selectNodes("author");
            //Set author node
            setAutNode(autList);            
            //List with authorOccurrence
            List autOccList = autNode.selectNodes("authorOccurrence");
            //Set authorOccurrence node
            setAutOccNode(autOccList);
            //create Records
            authorOccurrence = new AuthorOccurrence();
            author = new Author();
            //set FK of Records
            authorOccurrence.setAuthor(author);
            authorOccurrence.setOccurrence(occurrence);
                                  
            part((Record)author, autNode);
            part((Record)authorOccurrence, autOccNode);
            
            return authorOccurrence;
        }        
        return null;
    }
    
     public void part(Record record, Node node){
        if(record == null || node == null) return;        
        // Build this part of the record.
        for( String property : record.getProperties() )                                                 
        	record.setValue(property, node.valueOf(property));
    }

     
    public boolean hasNextPart(Class table) {
    	if(table == AuthorOccurrence.class) 
    		return getNumberOfAuthors() > 0;
    	else 
    		return false;
    }

    public Parser.Action intentedFor() {
        return Parser.Action.UNKNOWN;
    }
   
    //FOR TESTING
   public static void main(String[] args) {
       XMLParser xmlParser = new XMLParser("c:/Documents and Settings/Lada/Dokumenty/test.xml");       
        try {
            System.out.println("test");
            xmlParser.initialize();
            while (xmlParser.hasNextRecord()) {
                Occurrence occMain = (Occurrence) xmlParser.getNextPart(Occurrence.class);
                //Test of output
                System.out.println("1:" + occMain.getHabitat().getCountry());                
                while (xmlParser.hasNextPart(AuthorOccurrence.class)) {
                    AuthorOccurrence aoMain =  (AuthorOccurrence) xmlParser.getNextPart(AuthorOccurrence.class);
                    //Test of output
                    System.out.println("2: "+ aoMain.getAuthor().getWholeName());
                }                
            }
        } catch (ParserException ex) {
            ex.printStackTrace();
        }       
   }

    /**
     *  Set count of no process occurrence record in file.
     *  @param i count of occurrence record for processing
     */
    private void setNumberOfOccurrences(int i) {
        this.currentNumberOcc = i;
    }

    /**
     * Get count of no process occurrence record in file
     *
     */
    private int getNumberOfOccurrences() {
        return this.currentNumberOcc;
    }
 
    /**
     *  Set count of no process author record in file.
     *  @param i count of author record for processing
     */
     private void setNumberOfAuthors(int i) {
        this.currentNumberAuthor = i;
    }

     /**
     * Get count of no process author record in file
     *
     */
    private int getNumberOfAuthors() {
        return this.currentNumberAuthor;
    }

    /**
     *  
     */
    private void setOccurrenceNode(List occurrenceList) {
        int num = getNumberOfOccurrences();
        setNumberOfOccurrences(num - 1);   
        this.occNode = (Node)occurrenceList.get(occurrenceList.size()- num); 
    }

    private void setHabitatNode(List habList) 
    throws ParserException {
       if (habList.size() <= 0) {
           logger.error("Bad XML file - element HABITAT doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
           
       } else     
            habNode = (Node) habList.get(0);               
    }

    private void setMetadataNode(List metadataList) 
     throws ParserException {
       if (metadataList.size() <= 0) {
           logger.error("Bad XML file - element METADATA doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
           metadataNode = (Node) metadataList.get(0);
    }

    private void setPlantNode(List plantList) 
     throws ParserException {
       if (plantList.size() <= 0) {
           logger.error("Bad XML file - element PLANT doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            planNode = (Node) plantList.get(0);
    }

    private void setPubNode(List pubList)  
    throws ParserException {
       if (pubList.size() <= 0) {
           logger.error("Bad XML file - element PUBLICATION doesn`t exist. Elements for all NOT NULL column must exist.");
          throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            pubNode = (Node) pubList.get(0);
    }      

    private void setTerrNode(List terrList) 
     throws ParserException {
       if (terrList.size() <= 0) {
           logger.error("Bad XML file - element TERRITORY doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            terrNode = (Node) terrList.get(0);
    }

    private void setVillNode(List villList) 
     throws ParserException {
       if (villList.size() <= 0) {
           logger.error("Bad XML file - element VILLAGE doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            villNode = (Node) villList.get(0);
    }

    private void setPhyNode(List phyList) 
     throws ParserException {
       if (phyList.size() <= 0) {
           logger.error("Bad XML file - element PHYTOCHORION doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            phytNode = (Node) phyList.get(0);        
    }

    private void setAuthorsNode(List authorsList) 
    throws ParserException {
       if (authorsList.size() <= 0) {
           logger.error("Bad XML file - element AUTHORS doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else {
         int num = getNumberOfAuthors();
         setNumberOfAuthors(num - 1);
         Node node =(Node) authorsList.get(authorsList.size() - num);
         this.authorsNode = node;        
       }
    }

    private void setAutNode(List autList) 
    throws ParserException {
       if (autList.size() <= 0) {
           logger.error("Bad XML file - element AUTHOR doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            autNode = (Node) autList.get(0);        
    }         

    private void setAutOccNode(List autOccList) 
    throws ParserException {
       if (autOccList.size() <= 0) {
           logger.error("Bad XML file - element AUTHOROCCURRENCE doesn`t exist. Elements for all NOT NULL column must exist.");
           throw new ParserException(L10n.getString("Error.IncorrectXMLFile"));            
            
       } else
            autOccNode = (Node) autOccList.get(0);        
    }
}
