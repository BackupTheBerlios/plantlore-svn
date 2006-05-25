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
import java.io.IOException;
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
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 *
 * @author Lada
 */
public class XMLParser implements Parser {

    private Logger logger;
    private Document document;
    private File fXML;
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
            //FIXME: pokud bude spatny soubor XML (neuzavrene tagy, nekorektne pouzito &, atd..)
            //spadne, zde s vyjimkou --> odchytavat
            Document doc = reader.read(fXML);
            this.document = doc;
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }        
        occurrenceList = document.selectNodes("//occurrence");
        setNumberOccurrence(occurrenceList.size());        
    }

    public void cleanup() {
    }

    public boolean hasNextRecord() {
        //vrati true pokud existuje jeste <OCCURRENCE></OCCURRENCE>
        if (getNumberOccurrence() > 0)
            return true;
        return false;
    }

    public Parser.Action fetchNextRecord() {
        //FIXME        
        return Parser.Action.UNKNOWN;
    }

    public Record nextPart(Class table) throws ParserException {        
        //vrati objekt
        if (table == Occurrence.class) {
            int num = getNumberOccurrence();
            setNumberOccurrence(num - 1);                       
            occNode = (Node)occurrenceList.get(occurrenceList.size()- num);            
            //Create lists with node. Each list should has only one node. 
            //We have to check not null value of list
            //OCC_NODE
            List habList = occNode.selectNodes("habitat");
            List metadataList = occNode.selectNodes("metadata");
            List plantList =   occNode.selectNodes("plant");
            List pubList = occNode.selectNodes("publication");                                    
            if (habList.size() <= 0) 
                System.out.println("Bad XML file - element HABITAT don`t exist");
            else
                habNode = (Node) habList.get(0);
            if (metadataList.size() <= 0 ) 
                System.out.println("Bad XML file - element METADATA don`t exist");
            else
                metadataNode = (Node) metadataList.get(0);
            if (plantList.size() <= 0) 
                System.out.println("Bad XML file - element PLANT don`t exist");
            else
                planNode = (Node) plantList.get(0);
            if (pubList.size() <= 0 ) 
                System.out.println("Bad XML file - element PUBLICATION don`t exist");
            else
                pubNode = (Node) pubList.get(0);
            //HAB_NODE
            List terrList =  habNode.selectNodes("territory");
            List villList = habNode.selectNodes("village");
            List phyList = habNode.selectNodes("phytochorion");
            if (terrList.size() <= 0) 
                System.out.println("Bad XML file - element TERRITORY don`t exist");
            else                
                terrNode = (Node) terrList.get(0);
            if (villList.size() <= 0) 
                System.out.println("Bad XML file - element VILLAGE don`t exist");
            else
                villNode = (Node) villList.get(0);
            if (phyList.size() <= 0) 
                System.out.println("Bad XML file - element PHYTOCHORION don`t exist");
            else
                phytNode = (Node) phyList.get(0);
            
            //set list of author`s node
            authorsList = occNode.selectNodes("authors");
            setNumberAuthor(authorsList.size());
            //create Records       
            occurrence = new Occurrence();            
            habitat = new Habitat();
            metadata = new Metadata();
            publication = new Publication();
            plant = new Plant();
            village = new Village();
            territory = new Territory();
            phytochorion = new Phytochorion();
            
            try {                
                part((Record)occurrence, occNode);
                part((Record)habitat, habNode);
                part((Record)metadata, metadataNode);
                part((Record)plant, planNode);
                part((Record)publication, pubNode);
                part((Record)territory, terrNode);
                part((Record)village, villNode);
                part((Record)phytochorion, phytNode);
            } catch (IOException ex) {
                ex.printStackTrace();
            }       
            
            //set FK of Records
            occurrence.setHabitat(habitat);
            occurrence.setPlant(plant);
            occurrence.setPublication(publication);
            occurrence.setMetadata(metadata);
            habitat.setTerritory(territory);
            habitat.setPhytochorion(phytochorion);
            habitat.setNearestVillage(village);            
            
            return occurrence;
        } else if (table == AuthorOccurrence.class) {
            //create NODE         
            int num = getNumberAuthor();
            setNumberAuthor(num - 1);
            authorsNode = (Node) authorsList.get(authorsList.size() - num);
            //List with author node
            List autList = authorsNode.selectNodes("author");
            //Set author node
            if (autList.size() <= 0)
                System.out.println("Bad XML file - element AUTHOR don`t exist");
            else
                autNode = (Node) autList.get(0);
            //List with authorOccurrence
            List autOccList = autNode.selectNodes("authorOccurrence");
            //Set authorOccurrence node
            if (autOccList.size() <= 0)
                System.out.println("Bad XML file - element AUTHOROCCURRENCE don`t exist");
            else
                autOccNode = (Node) autOccList.get(0);            
            //create Records
            authorOccurrence = new AuthorOccurrence();
            author = new Author();
            //set FK of Records
            authorOccurrence.setAuthor(author);
            authorOccurrence.setOccurrence(occurrence);
                       
            try {
                part((Record)author, autNode);
                part((Record)authorOccurrence, autOccNode);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            return authorOccurrence;
        }        
        return null;
    }
    
     public void part(Record record, Node node) throws IOException {
        if(record == null) return;        
        if(node == null) return;

        // Build this part of the record.
        Class table = record.getClass();
        for( String property : record.getProperties() ) {                                                
                //set data to record
                record.setValue(property, node.valueOf(property));
            }      
    }

     
    public boolean hasNextPart(Class table) {
        if (getNumberAuthor() > 0) 
            return true;      
        return false;
    }

    public Parser.Action intentedFor() {
        //FIXME
        return Parser.Action.UNKNOWN;
    }
   
    //FOR TESTING
   public static void main(String[] args) {
       XMLParser xmlParser = new XMLParser("c:/Documents and Settings/Lada/Dokumenty/native.xml");       
        try {
            System.out.println("test");
            xmlParser.initialize();
            while (xmlParser.hasNextRecord()) {
                Occurrence occMain = (Occurrence) xmlParser.nextPart(Occurrence.class);
                //Test of output
                System.out.println("1:" + occMain.getHabitat().getCountry());
                System.out.println("2: " + occMain.getHerbarium());
                System.out.println("3: "+ occMain.getPlant().getTaxon());
                while (xmlParser.hasNextPart(AuthorOccurrence.class)) {
                    AuthorOccurrence aoMain =  (AuthorOccurrence) xmlParser.nextPart(AuthorOccurrence.class);
                }                
            }
        } catch (ParserException ex) {
            ex.printStackTrace();
        }       
   }

    /**
     *  Set count of don`t process occurrence record in file.
     *  @param i count of occurrence record for processing
     */
    private void setNumberOccurrence(int i) {
        this.currentNumberOcc = i;
    }

    /**
     * Get count of don`t process occurrence record in file
     *
     */
    private int getNumberOccurrence() {
        return this.currentNumberOcc;
    }
 
    /**
     *  Set count of don`t process author record in file.
     *  @param i count of author record for processing
     */
     private void setNumberAuthor(int i) {
        this.currentNumberAuthor = i;
    }

     /**
     * Get count of don`t process author record in file
     *
     */
    private int getNumberAuthor() {
        return this.currentNumberAuthor;
    }
}
