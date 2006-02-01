package hibernatetest;

import org.hibernate.*;

/**
 *
 * @author Kovo
 */
public class UserMain {
    
    /** Creates a new instance of UserMain */
    public static void main(String[] args) throws HibernateException {        
        if (args.length > 0) {
            new UserManagement(args[0]);
        } else {
            System.out.println("Not enough parameters");
            System.out.println("Available parameters: insert, select");
        }
    }
    
}
