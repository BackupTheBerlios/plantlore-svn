/*
 * Main.java
 *
 * Created on 22. øíjen 2005, 23:32
 */

package mvcimplementationtest;

/**
 *
 * @author reimei
 */
public class Main {
    ApplicationModel appm;
    ApplicationView appv;
    ApplicationHistoryView appvh;
    ApplicationController appc; 
    
    /** Creates a new instance of Main */
    public Main() {
        appm = new ApplicationModel();
        appv = new ApplicationView(appm);
        appvh = new ApplicationHistoryView(appm);
        appc = new ApplicationController(appm,appv);
        appv.setVisible(true);
        appvh.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Main main = new Main();
    }
    
}
