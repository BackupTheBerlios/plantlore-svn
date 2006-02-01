package hibernatetest;

import org.hibernate.*;

/**
 *
 * @author Lada
 */
public class DatabaseMain {
    
    /** Creates a new instance of DatabaseMain */
    public static void main(String[] args) throws HibernateException {        
        if (args.length == 2) {
            new DatabaseManagement(args[0],args[1]);
        } else {
            System.out.println("Funkcion DatabaseManagement requirs two parameters");
            System.out.println("Available parameters: insert + name of table, select + name of table");
        }
    }
    
}
