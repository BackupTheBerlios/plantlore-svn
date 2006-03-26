/**
 * 
 */
package net.sf.plantlore.client.history;

import java.util.Observable;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.client.dblayer.query.Query;
import net.sf.plantlore.client.dblayer.query.SelectQuery;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.common.record.HistoryRecord;

import org.apache.log4j.Logger;


/**
 * @author Lada
 *
 */
public class History extends Observable {

	/** Instance of a logger */
    private Logger logger;

    /** Exception with details about an error */
    private DBLayerException error = null;
    /** Instance of a database management object */
    private DBLayer database;   
    
    //result - searchig history for rocord
    private Result resultHistory;
    private Object[][] historyData;

    /** Name of the table where value was changed*/
	private String tableName;  
	/** Name of the column where value was changed*/
	private String columnName;
	/** Unique value identified record. 
	 * Foring key referenced to table TOCCURRENCES */	
	private int occurrenceId;
	/**Unique value identified record in table where value was changed */
	private int recordId;
	/** Operation whitch was used*/   
	private String operation;
	/** Date and time when the reccord was changed*/
	private java.util.Date when;	
	/** Old value of attribute*/    
	private String oldValue;
	/** New value of attribute*/
	private String newValue;
   /** Name of user who did changed*/
	private String nameUser;

	//Informations about occurrences
	private String namePlant;
	private String nameAuthor;
	private String location;
	
    
    /** Creates a new instance of History 
     *  
     *  v konstruktoru predame jednotlive informace, ktere chceme zobrazit o nalezu
     * */
    public History(DBLayer database, String namePlant, String nameAuthor, String location, int idOcc)
    {
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;	
       
       setOccurrenceId(idOcc);
       setNamePlant(namePlant);
       setNameAuthor(nameAuthor);
       setLocation(location);
	   
	   //dohledani historie zmen pro nalez s id occurrenceId
	   searchHistoryData(occurrenceId);
    }	

    /**
     * Tato funkce dohleda data ulozene v historii pro nalez urceni id 
     * 
     * @param idOccurrence
     */
     
    public void searchHistoryData(Integer idOccurrence)
    {
    	String idOcc = new String();
/*    	
    	 // Create new Select query
        Query query = new SelectQuery();
        query.setType(DBMapping.HISTORYRECORD);
        query.addWhere("occurrenceId","=",idOcc.valueOf(idOccurrence));
        query.addOrderby("when", "ASC");
        
        Result result = null;
        try {
            // Execute query
            result = database.executeQuery(query);        
        } catch (DBLayerException e) {
            // Log and set an error                   
            logger.error("Searching history failed. Unable to execute search query.");
            //setError(e);           
        } finally {                              
            // Save history data
        	logger.debug("Searching history ends successfully");
            saveResult(result);                
        }      
 */
    }
    
    /**
     * 
     * @param result
     */
    public void saveResult(Result result) {
    	this.resultHistory = result;
    	int from = 0;
    	int to = result.getNumRows();
/*    	
    	logger.debug("Retrieving query results.");
        try {
            // Retrieve selected row interval
            Object[] objectHistory = database.more(this.resultHistory, from, to);  
            int countResutl = objectHistory.length;
            logger.debug("Results retrieved. Count: "+ countResutl);
            
            System.out.println(objectHistory);

        } catch (DBLayerException e) {
            // Log and set error in case of an exception
            logger.error("Processing search results failed: "+e.toString());            
        }
  */  	
    }
    
    public Object[][] getData() {
 	    historyData = new Object[][]{{new Boolean(false),"12.01.2006","Lada","village","Stritez", "Slavice"},
		          {new Boolean(false),"12.01.2006","Lada","village","Stritez", "Slavice"}};

    	return this.historyData;
    }
    

    /**
     * Tato funkce bude menit hodnoty v DB (DELETE v historii, zmena v jakekoliv 
     * tabulce pro kterou se zaznamenava historie) - UNDO
     * 
     * bude volana z CTR po stisku klavesy
     * jako parametr dostane seznam oznacenych zmen, ktere se maji vratit
     * 
     */
    public void undo()
    {
    	
    }
    
    /**
     * Tato funkce bude overovat prava, zda dany uzivatel ma
     * pravo provest UNDO - spravne by se uzivateli nevela
     * historie zobrazit pokud nema prava, takze overeni prav
     * musi byt volano jiz v konstruktoru nebo na miste, kde 
     * se vola undo pro dany zaznam, coz by bylo asi nejlogictejsi
     * !!! OVERIT TUTO MYSLENKU 
     * pokud to tak bude, tak po zavolani historie vime, ze je autor nebo ma prava
     * jeste je tu otazka, zda editace od admina zakaze zruseni teto editace neadminem 
     */
    public void checkRight()
    {
    	
    }
    
    /**
     *  overeni, zda nechce vratit starsi zmenu aniz by chtel vratit mladsi
     *  nebo spise, pokud oznaci starsi v tu chvili se mu oznaci i ty mlatsi pro
     *  stejeny atribut
     */
    public void checkCorrectMark()
    {
    	
    }
       
    
    //****************************//
    //****Get and set metods*****//
    //**************************//
    

   public String getNamePlant() {
		  return this.namePlant;
	   }

   public void setNamePlant(String namePlant) {
		  this.namePlant = namePlant;
	}    
   
   public String getNameAuthor() {
		  return this.nameAuthor;
	   }

   public void setNameAuthor(String nameAuthor) {
	  this.nameAuthor = nameAuthor;
   } 
   
   public String getLocation() {
		  return this.location;
	   }

   public void setLocation(String location) {
		  this.location = location;
	}    
   
    /**
	*   Get name of the table where value was changed
	*   @return of the table where value was changed	
	*/
   public String getTableName() {
	  return this.tableName;
   }

   /**
	*   Set name of the table where value was changed
	*   @param tableName string containing of the table where value was changed
	*/
   public void setTableName(String tableName) {
	  this.tableName = tableName;
   }

   /**
   *   Get name of the column where value was changed
   *   @return  name of the column where value was changed
   */
  public String getColumnName() {
	 return this.columnName;
  }

  /**
   *   Set  name of the column where value was changed
   *   @param columnName string containing  name of the column where value was changed
   */
  public void setColumnName(String columnName) {
	 this.columnName = columnName;
  }
	/**
	 *   Get identifier of the occurrence whitch was changed
	 *   @return foreign identifier of the occurrence whitch was changed
	 */
	public int getOccurrenceId() {
	   return this.occurrenceId;
	}

	/**
	 *   Set identifier of the occurrence whitch was changed
	 *   @param occurrenceId identifier of the occurrence whitch was changed
	 */
	public void setOccurrenceId(int occurrenceId) {
	   this.occurrenceId= occurrenceId;
	}	
	
   /**
	 *   Get identifier of the record whitch was changed
	 *   @return identifier of the record whitch was changed
	 */
	public int getRecordId() {
	   return this.recordId;
	}

	/**
	 *   Set identifier of the record whitch was changed
	 *   @param recordId string containing identifier of the record whitch was changed
	 */
	public void setRecordId(int recordId) {
	   this.recordId = recordId;
	}
	  
	/**
	*   Get operation whitch was used
	*   @return operation whitch was used
	*/
	public String getOperation() {
	  return this.operation;
	}

	/**
	*   Set operation whitch was used
	*   @param operation string containing operation whitch was used 
	*/
	public void setOperation(String operation) {
	  this.operation = operation;
	}
	 
	   
	/**
	*   Get date and time when the reccord was changed
	*   @return date and time when the reccord was changed
	*/         
    public java.util.Date getWhen() {
	  return this.when;
    }
 
   /**
	*   Set date and time when the reccord was changed
	*   @param when date and time when the reccord was changed
	*/            
    public void setWhen(java.util.Date when) {
	  this.when = when;
    }     	  
 
	/**
	*   Get name of user who did changed
	*   @return name of user who did changed
	*/
    public String getNameUser() {
	  return this.nameUser;
    }

    /**
	*   Set name of user who did changed
	*   @param nameUser string containing name of user who did changed	
	*/
    public void setNameUser(String nameUser) {
	  this.nameUser = nameUser;
    }  
    
	/**
	*   Get old value of atribute whitch was changed
	*   @return old value of atribute whitch was changed
	*/
    public String getOldValue() {
	  return this.oldValue;
    }

    /**
	*   Set old value of atribute whitch was changed
	*   @param oldValue string containing old value of atribute whitch was changed
	*/
    public void setOldValue(String oldValue) {
	  this.oldValue = oldValue;
    }
	   
	 /**
	*   Get new value of atribute whitch was changed
	*   @return new value of atribute whitch was changed 
	*/
    public String getNewValue() {
	  return this.newValue;
    }

    /**
	*   Set new value of atribute whitch was changed
	*   @param newValue string containing new value of atribute whitch was changed
	*/
    public void setNewValue(String newValue) {
	  this.newValue = newValue;
    }    
}
