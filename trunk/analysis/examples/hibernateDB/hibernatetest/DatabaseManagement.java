package hibernatetest;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import org.hibernate.*;
import org.hibernate.cfg.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *  Class for management of all records in the database. Allows inserting new records into the database
 *  and selecting all the records form DB.
 *
 *  @author Toms Kovarik, tkovarik@gmail.com
 *  @version 0.1; 13.12. 2005 
 * 
 *  @version 0.2; 30.12. 2005 
 */
public class DatabaseManagement {
    /** Configuration file for Hibernate */
    private File configFile;
    /** Hibernate database session */
    private Session session;
           
    /**
     *  Constructor for UserManagement class. Initiates Hibernate, creates Hibernate session and performs
     *  action according to the given parameter
     *  
     *  @param type string representation of action to take. Can be either <pre>insert</pre> or <pre>select</pre>
     */
    public DatabaseManagement(String type, String table) throws HibernateException {        
        // File containing Hibernate configuration
        configFile = new File("hibernate.cfg.xml");
        
        // Fire up Hibernate
        SessionFactory sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
        
        // Open Session
        session = sessionFactory.openSession();        
        // Execute DB query (either insert or select) - depeneds on command line arguments
        if (type.equals("insert")) {
            databaseInsert(table);
        } else {
            userSelect(table);
        }
        // Close session
        session.close();                
    }
    
    /**
     *  Method for inserting new records into the database.
     *
     *  @throws HibernateException
     *  @param table  name of the table has a use for inset
     * 
     */
    public void databaseInsert(String table) throws HibernateException {
			if (table.equals("TAUTHORS")) {				
				authorsInsert();
				System.out.println("END insert into tauthors");
			} else if (table.equals("TUSER")) {				
				userInsert();
				System.out.println("END insert into tuser");
			} else if (table.equals("THABITATS")) {				
				habitatsInsert();
				System.out.println("END insert into thabitats");
			} else if (table.equals("TOCCURRENCES")) {				
				occurencesInsert();
				System.out.println("END insert into toccurrences");
			} else if (table.equals("TAUTHORSOCCURRENCES")) {				
				authorsOccurencesInsert();
				System.out.println("END insert into tauthorsOccurrences");
			} else if (table.equals("TPHYTOCHORIA")) {				
				phytochoriaInsert();
				System.out.println("END insert into tphytochoria");
			} else if (table.equals("TTERRITORIES")) {				
				territoriesInsert();
				System.out.println("END insert into tteritories");
			} else if (table.equals("TVILLAGES")) {				
				villagesInsert();
				System.out.println("END insert into tVillages");
			} else if (table.equals("TPUBLICATIONS")) {				
				publicationsInsert();
				System.out.println("END insert into tpublications");
			} else if (table.equals("TPLANTS")) {				
				plantsInsert();
				System.out.println("END insert into tplants");
			} else if (table.equals("TMETADATA")) {				
				metadataInsert();
				System.out.println("END insert into tmetadata");
			} else if (table.equals("THISTORY")) {				
				historyInsert();
				System.out.println("END insert into thistory");
			}  else if (table.equals("THISTORYCHANGE")) {				
				historyChangeInsert();
				System.out.println("END insert into thistoryChange");
			}  else if (table.equals("THISTORYCOLUMN")) {				
				historyColumnInsert();
				System.out.println("END insert into thistoryColumn");
			}  else if (table.equals("TRIGHT")) {				
				rightInsert();
				System.out.println("END insert into tRight");
			}else {
				System.out.println("Incorrect name of the table");
			}
	
	        
		// [laliluna] clean up (close the session)
		session.close();        
    }
    
    /**
     *  Method for retrieving user records from the database. Prints name and surname of all selected users to console.
     *  
     *  @throws HibernateException
     *  @param table   name of the table has a use for select
     */
    public void userSelect(String table) throws HibernateException {
         // Query using Hibernate Query Language
         String sql = "FROM hibernatetest.User users";
    	 //String sql = " FROM User as users";
		 // Start new DB transaction		  
		 Transaction tx = session.beginTransaction();		
         Query query = session.createQuery(sql);
         // Go through all the selected records and print  name and surname
         for (Iterator it = query.iterate();it.hasNext();) {
            User user = (User) it.next();
            System.out.println("User name: " + user.getFirstName() );                   
            System.out.println("User surname: " + user.getSurname() );
        }
		//	Commit transaction
		tx.commit();  
		//	[laliluna] clean up (close the session)
		session.close();
    } 
    
	/**
	 *  Method for insert new author record into the database 
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void authorsInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tAuthor.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Authors author = new Authors();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
			    if (fields.length != 10)	{
					  System.out.println("Inccorect line - tAuthor.sql");
					  continue;		
			    }
				// Set author data
				author.setFirstName(fields[1]);
				author.setSurname(fields[2]);
				author.setWholeName(fields[3]);
				author.setOrganization(fields[4]);
				author.setRole(fields[5]);
				author.setAddress(fields[6]);				
				author.setEmail(fields[7]);
				author.setURL(fields[8]);
				author.setNote(fields[9]);	
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(author);
				// Commit transaction
				tx.commit();			
			}
			in.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 *  Method for insert new user record into the database 
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void userInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tUser.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", ";  	
		    //Read first line ( info about attributes )
			str = in.readLine();
			
			while ((str = in.readLine()) != null) {						
				// Build a User object
				User user = new User();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 10)	{
					    System.out.println("Inccorect line - tUser.sql");
						continue;		
				}
				// Set user data
			    user.setLogin(fields[1]);
			    user.setFirstName(fields[2]);
			    user.setSurname(fields[3]);
			    user.setEmail(fields[4]);
			    user.setAddress(fields[5]);
			    user.setWhenCreate(new java.util.Date());        
			    user.setWhenDrop(new java.util.Date());	
			    user.setRightId(Integer.parseInt(fields[8]));
			    user.setNote(fields[9]);	
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(user);
				// Commit transaction
				tx.commit();			
			}
			in.close();
		} catch (IOException e) {
		}
	}   
	
	/**
	 *  Method for insert new habitat record into the database 
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void habitatsInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tHabitats.sql"), "UTF8"));			
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Habitats habitat = new Habitats();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 11)	{
					  System.out.println("Inccorect line - tHabitats.sql");
					  continue;		
				}				
				// Set habitat data
				habitat.setTerritoryId(Integer.parseInt(fields[1]));
				habitat.setPhytochoriaId(Integer.parseInt(fields[2]));
				habitat.setQuadrant(fields[3]);
				habitat.setDescription(fields[4]);
				habitat.setNearestVillageId(Integer.parseInt(fields[5]));
				habitat.setCountry(fields[6]);
				habitat.setAltitude(Double.parseDouble(fields[7]));
				habitat.setLatitude(Double.parseDouble(fields[8]));
				habitat.setLongitude(Double.parseDouble(fields[9]));
				habitat.setNote(fields[10]);
					
				habitat.setNote(fields[8]);
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(habitat);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}   
	
	/**
	 *  Method for insert new occurrence record into the database 
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void occurencesInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tOccurrences.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Occurrences occurrence = new Occurrences();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 19)	{
					  System.out.println("Inccorect line - tOccurrences.sql");
					  continue;		
				}				
				// Set occurrences data
				occurrence.setUnitIdDb(fields[1]);
				occurrence.setUnitValue(fields[2]);
				occurrence.setHabitatId(Integer.parseInt(fields[3]));
				occurrence.setPlantId(Integer.parseInt(fields[4]));
				occurrence.setYearCollected(Integer.parseInt(fields[4]));
				occurrence.setMonthCollected(Integer.parseInt(fields[6]));
				occurrence.setDayCollected(Integer.parseInt(fields[7]));
				//occurrence.setTimeCollected(new java.util.Time());
				occurrence.setISODateTimeBegin(new java.util.Date());
				occurrence.setDateSource(fields[10]);
				occurrence.setPublicationsId(Integer.parseInt(fields[11]));	
				occurrence.setHerbarium(fields[12]);
				occurrence.setCreateWhen(new java.util.Date());
				occurrence.setCreateWho(Integer.parseInt(fields[14]));
				occurrence.setUpdateWhen(new java.util.Date());
				occurrence.setUpdateWho(Integer.parseInt(fields[16]));
				occurrence.setNote(fields[17]);
				occurrence.setMetadataId(Integer.parseInt(fields[18]));				
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();				
				// Save User
				session.save(occurrence);				
				// Commit transaction
				tx.commit();						
			}
			in.close(); 
		} catch (IOException e) {
		}
	}   
	
	/**
	 *  Method for join talbes TAUTHORS, TOCCURENCES
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void authorsOccurencesInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tAuthorsOccurrences.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				AuthorsOccurrences authousOccurrence = new AuthorsOccurrences();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 3)	{
					  System.out.println("Inccorect line - tAuthorsOccurrences.sql");
					  continue;		
				}
				// Set authorsOccurences data
				authousOccurrence.setAuthorId(Integer.parseInt(fields[1]));
				authousOccurrence.setOccurrenceId(Integer.parseInt(fields[2]));				
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(authousOccurrence);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}   
	
	/**
	 *  Method for insert new phytochoria record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void phytochoriaInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tPhytochoria.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Phytochoria phytochoria = new Phytochoria();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 3)	{
					  System.out.println("Inccorect line - tPhytochoria.sql");
					  continue;		
				}
				// Set phytochoria data
				phytochoria.setCode(fields[1]);
				phytochoria.setName(fields[2]);				
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(phytochoria);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}
	
	/**
	 *  Method for insert new territory record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void territoriesInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tTerritories.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Territories territory = new Territories();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 2)	{
					  System.out.println("Inccorect line - tTerritories.sql");
					  continue;		
				}
				// Set territory data
				territory.setName(fields[1]);							
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(territory);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}
	
	/**
	 *  Method for insert new villages record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void villagesInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tVillages.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Villages villages = new Villages();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 2)	{
					  System.out.println("Inccorect line - tVillages.sql");
					  continue;		
				}
				// Set territory data
				villages.setName(fields[1]);							
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(villages);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}	
	
	/**
	 *  Method for insert new publication record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void publicationsInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tPublications.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Publications publication = new Publications();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 8)	{
					  System.out.println("Inccorect line - tPublications.sql");
					  continue;		
				}
				// Set publication data
				publication.setCollectionName(fields[1]);
				publication.setCollectionYearPublication(Integer.parseInt(fields[2]));
				publication.setJournalName(fields[3]);
				publication.setJournalAuthorName(fields[4]);
				publication.setReferenceCitation(fields[5]);
				publication.setReferenceDetail(fields[6]);
				publication.setURL(fields[7]);
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(publication);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}
	
	/**
	 *  Method for insert new plant record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void plantsInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tPlants.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Plants plant = new Plants();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 9)	{
					  System.out.println("Inccorect line - tPlants.sql");
					  continue;		
				}
				// Set plant data
				plant.setSurveyTaxId(fields[1]);
				plant.setGenus(fields[2]);
				plant.setScientificNameAuthor(fields[3]);
				plant.setPublishableName(fields[4]);
				plant.setAdoptedName(fields[5]);
				plant.setCzechName(fields[6]);
				plant.setSynonyms(fields[7]);				
				plant.setNote(fields[8]);
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(plant);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}
	
	/**
	 *  Method for insert new metadata record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void metadataInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tMetadata.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Metadata metadata = new Metadata();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 16)	{
					  System.out.println("Inccorect line - tMetadata.sql");
					  continue;		
				}
				// Set metadata data
				metadata.setTechnicalContactName(fields[1]);
				metadata.setTechnicalContactEmail(fields[2]);
				metadata.setTechnicalContactAddress(fields[3]);
				metadata.setContentContactName(fields[4]);
				metadata.setContentContactEmail(fields[5]);
				metadata.setContentContactAddress(fields[6]);
				metadata.setDataSetTitle(fields[7]);
				metadata.setDataSetDetails(fields[8]);
				metadata.setSourceInstitutionId(fields[9]);
				metadata.setSourceId(fields[10]);
				metadata.setOwnerOrganizationAbbrev(fields[11]);
				metadata.setDateCreate(new java.util.Date());
				metadata.setDateModified(new java.util.Date());
				metadata.setLanguage(fields[14]);
				metadata.setRecordBasis(fields[15]);
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(metadata);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}   
	
	/**
	 *  Method for insert new history record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void historyInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tHistory.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				History history = new History();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 5)	{
					  System.out.println("Inccorect line - tHistory.sql");
					  continue;		
				}
				// Set plant data
				history.setColumnId(Integer.parseInt(fields[1]));
				history.setChangeId(Integer.parseInt(fields[2]));
				history.setOldValue(fields[3]);
				history.setNewValue(fields[4]);				
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(history);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}
	
	/**
	 *  Method for insert new historyChange record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void historyChangeInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tHistoryChange.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				HistoryChange historyChange = new HistoryChange();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 6)	{
					  System.out.println("Inccorect line - tHistoryChange.sql");
					  continue;		
				}
				// Set plant data
				historyChange.setOccurrenceId(Integer.parseInt(fields[1]));
				historyChange.setRecordId(Integer.parseInt(fields[2]));
				historyChange.setOperation(fields[3]);
				historyChange.setWhen(new java.util.Date());
				historyChange.setWho(Integer.parseInt(fields[5]));				
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(historyChange);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}	
	
	/**
	 *  Method for insert new historyColumn record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void historyColumnInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tHistoryColumn.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				HistoryColumn historyColumn = new HistoryColumn();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 3)	{
					  System.out.println("Inccorect line - tHistoryColumn.sql");
					  continue;		
				}
				// Set plant data				
				historyColumn.setTableName(fields[1]);
				historyColumn.setColumnName(fields[2]);				
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(historyColumn);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}		
	
	/**
	 *  Method for insert new right record into the database
	 *
	 *  @throws HibernateException
	 * 
	 */
	public void rightInsert() throws HibernateException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("files/utf8/tRight.sql"), "UTF8"));
			// Line of the file
			String str;
			// Delimiter determined by a regular expression instead of a set of characters			 						
			String patternStr = ", "; 
			//Read first line ( info about attributes )
			str = in.readLine(); 	

			while ((str = in.readLine()) != null) {		
				//Build a Authors object
				Right right = new Right();			
				//Read a line in the file, split the string at a delimiter (patternStr), split the string into an array of						
				String[] fields = str.split(patternStr, -1);
				//Check count of item after parse line
				if (fields.length != 8)	{
					  System.out.println("Inccorect line - tRight.sql");
					  System.out.println(fields.length);
					  continue;		
				}
				// Set plant data				
				right.setAdministrator(Integer.parseInt(fields[1]));
				right.setEditAll(Integer.parseInt(fields[2]));
				right.setEditOwen(Integer.parseInt(fields[3]));
				right.setEditGroup(fields[4]);
				right.setUserExport(Integer.parseInt(fields[5]));
				right.setUserImport(Integer.parseInt(fields[6]));
				right.setRole(fields[7]);
				//  Start new DB transaction
				Transaction tx = session.beginTransaction();
				// Save User
				session.save(right);
				// Commit transaction
				tx.commit();			
			}
			in.close(); 
		} catch (IOException e) {
		}
	}			   		
}
