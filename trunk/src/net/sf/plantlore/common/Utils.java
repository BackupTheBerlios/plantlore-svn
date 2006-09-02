/*
 * Utils.java
 *
 * Created on 1. září 2006, 17:28
 *
 */

package net.sf.plantlore.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.log4j.Logger;

/** A suit of helper methods not fitting anywhere else and of possible use to others.
 *
 * @author fraktalek
 */
public class Utils {
    private static String URL_ENCODING="UTF-8";
    static Logger logger = Logger.getLogger(Utils.class.getPackage().getName());
    
    /** Does nothing */
    public Utils() {
    }
    
    /** Determines whether this class is bundled in and run from a jar file.
     *
     * @return true if run from jar file, false otherwise
     */
    public static boolean isRunningFromJar() {
        java.net.URL url = Utils.class.getResource("Utils.class");
        return url.getProtocol().equals("jar");
    }
    
    /** Determines the name of the jar in which this class is bundled.
     *
     * @return name of the jar file or null if not in jar
     */
    public static String getJarName() {
        java.net.URL url = Utils.class.getResource("Utils.class");
        if (!url.getProtocol().equals("jar"))
            return null;
        
        String jar = url.getFile();
        jar = jar.substring(0,jar.indexOf("jar!")+3);
        jar = jar.substring(jar.lastIndexOf("/")+1);
        return jar;
    }
    
    /** Returns a code base path as needed by RMI.
     *
     * Relies on the fact that this class is bundled in the same jar or directory
     * structure as the rest of the application (the stubs actually).
     *
     * @return path to the code base or null in case of some unexpected problem
     *
     */
    public static String getCodeBasePath() {
        String dir;
        
        //take a class that's in the same place as the rest of the application
        //and get it's location
        java.net.URL url = Utils.class.getResource("Utils.class");
        //now we got something like:
        //"jar:file:/home/fraktalek/cvs/plantlore/dist/plantlore-client.jar!/net/sf/plantlore/common/Utils.class"
        
        try {            
            dir = URLDecoder.decode(url.getPath(),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Utils.java: the URLDecoder complains about unsupported URL encoding: \""+URL_ENCODING+"\"");
            return null;
        }

        //clear off the file name
        dir = dir.substring(0,dir.lastIndexOf("/"));
        if (url.getProtocol().equals("jar")) {
            //remove path to the package and the trailing "!/"
            dir = dir.substring(0,dir.indexOf(Utils.class.getPackage().getName().replaceAll("\\.","/"))-2);
            if (System.getProperty("os.name").equals("windows"))//on windows there's one more slash after file:
                dir = dir.substring(dir.indexOf("file:")+6);//if the url contains path to a jar it begins with "jar:file:/" and getPath() then begins with "file:/"            
            else
                dir = dir.substring(dir.indexOf("file:")+5);//if the url contains path to a jar it begins with "jar:file:/" and getPath() then begins with "file:/"            
        } else {
            //remove the path to the package and a trailing slash        
            dir = dir.substring(0,dir.indexOf(Utils.class.getPackage().getName().replaceAll("\\.","/")));            
        }
        
        return dir;
    }
    
    
    public static void main(String[] args) {
        System.out.println("code base = "+getCodeBasePath());
    }
}

