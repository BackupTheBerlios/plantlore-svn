package hibernatetest;

import java.io.*;
import java.util.Iterator;
import org.hibernate.*;
import org.hibernate.cfg.*;

/**
 *  Class for management of user records in the database. Allows inserting new user records into the database
 *  and selecting all the user records form DB.
 *
 *  @author Tomáš Kovaøík, tkovarik@gmail.com
 *  @version 0.1; 13.12. 2005  
 */
public class UserManagement {
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
    public UserManagement(String type) throws HibernateException {        
        // File containing Hibernate configuration
        configFile = new File("hibernate.cfg.xml");
        
        // Fire up Hibernate
        SessionFactory sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
        
        // Open Session
        session = sessionFactory.openSession();        
        // Execute DB query (either insert or select) - depeneds on command line arguments
        if (type.equals("insert")) {
            userInsert();
        } else {
            userSelect();
        }
        // Close session
        session.close();                
    }
    
    /**
     *  Method for inserting new user record into the database.
     *
     *  @throws HibernateException
     */
    public void userInsert() throws HibernateException {
        // Build a User object
        User user = new User();
        // Start new DB transaction
        Transaction tx = session.beginTransaction();
        // Set user data
        user.setLogin("Kovo");
        user.setName("Tomáš");
        user.setSurname("Kovaøík");
        user.setContact("Makovského 1332");
        user.setWhenCreate(new java.util.Date());        
        // Save User
        session.save(user);
        // Commit transaction
        tx.commit();        
    }
    
    /**
     *  Method for retrieving user records from the database. Prints name and surname of all selected users to console.
     *  
     *  @throws HibernateException
     */
    public void userSelect() throws HibernateException {
         // Query using Hibernate Query Language
         String sql = "  FROM User as users";
         Query query = session.createQuery(sql);
         // Go through all the selected records and print  name and surname
         for (Iterator it = query.iterate();it.hasNext();) {
            User user = (User) it.next();
            System.out.println("User name: " + user.getName() );                   
            System.out.println("User surname: " + user.getSurname() );
        }
    }    
}