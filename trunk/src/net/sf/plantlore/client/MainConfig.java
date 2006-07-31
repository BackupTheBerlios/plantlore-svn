/*
 * MainConfigParser.java
 *
 * Created on 13. květen 2006, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.client.login.Login;
import net.sf.plantlore.server.RMIServer;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author fraktalek
 */
public class MainConfig {
    private Logger logger;
    private Document document;
    private String file;
    private ArrayList<Column> columns = null;
    private ArrayList<DBInfo> dbinfos = null;
    private DBInfo selected = null;
    private boolean selectAutomatically = false; 
    
    /** Creates a new instance of MainConfigParser */
    public MainConfig(String file) throws DocumentException {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.file = file;
        document = DocumentHelper.createDocument();
        document.addElement("config");
    }
    
    public static void createEmptyConfig(String file) throws IOException {
        Document doc = DocumentHelper.createDocument();
        doc.addElement("config");
        FileWriter out = new FileWriter(file);
        doc.write(out);
        out.close();
    }
    
    public Document load() throws DocumentException {
        SAXReader reader = new SAXReader();
        logger.info("Loading main configuration file "+file);
        File fajl = new File(file);
        Document document = reader.read(fajl);
        this.document = document;
        return document;        
    }
    
    public Document load(String file) throws DocumentException {
        this.file = file;
        return load();
    }
    
    public ArrayList<Column> getColumns() {
        ArrayList<Column> columns = new ArrayList<Column>();
        
        List columnList = document.selectNodes("//config/overview/columns/column");
        Iterator it = columnList.iterator();
        while (it.hasNext()) {
            Node n = (Node)it.next();
            String text = n.getText();
            Column c = null;
            try {
                Column.Type ct = Column.Type.valueOf(text);
                c = new Column(ct);
            } catch(IllegalArgumentException e) {
                logger.warn("The config file "+file+" is corrupted: "+text+" is not allowed column type!");
            }
            if (c != null) {
                Number preferredSize = n.numberValueOf("@preferredSize");
                if (!preferredSize.toString().equals("NaN")) //FIXME: better way to recognize a NaN?
                    c.setPreferredSize(preferredSize.intValue());
                columns.add(c);
            }
        }
        return columns;
    }
    
    public ArrayList<DBInfo> getDBinfos() {
    	ArrayList<DBInfo> result = new ArrayList<DBInfo>();
    	
    	String alias, host, databaseType, databaseIdentifier, 
    	databaseParameter, masterUser, masterPassword;
    	String[] users;
    	int port, databasePort;
    	
    	int unnamedDatabase = 0;
    	selectAutomatically = false;
    	
    	Node login = document.selectSingleNode("/config/login");
    	if(login != null && login instanceof Element) {
    		Attribute attr = ((Element)login).attribute("auto");
    		if( attr != null && "true".equalsIgnoreCase(attr.getValue()) )
    			selectAutomatically = true;
    	}
    	
    	List columnList = document.selectNodes("//config/login/triplet");
    	Iterator it = columnList.iterator();
    	while (it.hasNext()) {
    		
    		alias = host = databaseType = databaseIdentifier = 
    			databaseParameter = masterUser = masterPassword = "";
    		port = RMIServer.DEFAULT_PORT; databasePort = -1;
    		
    		Node n = (Node)it.next();
    		
    		
    		// Obtain the basic characteristic.
    		alias = n.valueOf("alias");
    		host = n.valueOf("host");
    		Number portNumber = n.numberValueOf("port");
    		port = ( portNumber == null ? RMIServer.DEFAULT_PORT : portNumber.intValue() );
    		
    		// The database info.
    		Node database = n.selectSingleNode("database");
    		if(database != null) {
    			databaseType = database.valueOf("engine");
    			databaseIdentifier = database.valueOf("identifier");
    			Number databasePortNumber = database.numberValueOf("port");
    			databasePort = ( databasePortNumber == null ? -1 : databasePortNumber.intValue() );
    			databaseParameter = database.valueOf("parameter");
    			masterUser = database.valueOf("masteruser");
    			masterPassword = database.valueOf("masterpassword");
    		}
    		else
    			continue; // incomplete record (databaseType, databasePort, databaseIdentifier are obligatory).
    		
    		if(databasePort < 0)
    			continue; // corrupted record (databasePort is obligatory).
    		
    		if(alias == null || alias.length() == 0) {
    			unnamedDatabase++;
    			alias = "Database " + unnamedDatabase;
    		}
    		
    		// The list of stored users. 
    		List userList = n.selectNodes("user");
    		users = new String[ Login.MAX_NAMES /*userList.size()*/ ];
    		Node user = null;
    		Iterator it2 = userList.iterator(); 
    		int i = 0;
    		while (it2.hasNext() && i < Login.MAX_NAMES) {
    			user = (Node) it2.next();
    			users[i] = user.getText();
    			i++;
    		}
    		
    		// Create a new DBInfo ~ triplet.
    		DBInfo dbi = new DBInfo(
    				alias, host, port, 
    				databaseType, databasePort, databaseIdentifier, databaseParameter, 
    				users, masterUser, masterPassword );
    		
    		if( n instanceof Element ) {
    			Attribute attr = ((Element)n).attribute("selected");
    			if(attr != null && "true".equalsIgnoreCase(attr.getValue()) ) {
    				selected = dbi;
    				System.out.println("SELECTED = " + dbi);
    			}
    		}
    		
    		result.add(dbi);
    	}
    	return result;
    }
    
    public boolean getSelectAutomatically() {
    	return selectAutomatically;
    }
    
    
    public DBInfo getSelected() {
    	return selected;
    }
    
    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }
    
    public void setDBInfos(ArrayList<DBInfo> dbinfos, DBInfo selected) {
        this.dbinfos = dbinfos;
        this.selected = selected;
    }
    
    public void setSelectAutomatically(boolean automatically) {
    	this.selectAutomatically = automatically;
    }
    
    private void storeColumns() {
        Node colsNode = document.selectSingleNode("//config/overview/columns");
        if (colsNode != null)
            colsNode.detach();
        org.dom4j.Element root = document.getRootElement();
        Iterator it = root.elementIterator("overview");
        org.dom4j.Element overview;
        if (it.hasNext())
            overview = (Element) it.next();
        else {
            logger.warn("Main config was missing overview section. Adding it.");
            overview = root.addElement("overview");
        }
        Element e = overview.addElement("columns");
        
        for (Column column : columns) {
            e.addElement("column").addAttribute("preferredSize",""+column.getPreferredSize()).setText(column.type.toString());
        }        
    }
    
    private void storeDBInfos() {
        Node colsNode = document.selectSingleNode("//config/login");
        if (colsNode != null)
            colsNode.detach();
        org.dom4j.Element root = document.getRootElement();
        org.dom4j.Element login = root.addElement("login");
        
        // Store the "Select Automatically" option.
        login.addAttribute("auto", Boolean.toString(selectAutomatically));
        
        for (int i = 0; i < dbinfos.size(); i++) {
            DBInfo dbi = dbinfos.get(i);
            
            // Server settings.
            Element triplet = login.addElement("triplet");
            
            if(dbi == selected)
            	triplet.addAttribute("selected", "true");
            
            if (dbi.getAlias() != null)
                triplet.addElement("alias").setText(dbi.getAlias());
            if (dbi.getHost() != null)
                triplet.addElement("host").setText(dbi.getHost());
            if(dbi.getPort() > 0)
            	triplet.addElement("port").setText("" + dbi.getPort());
            
            // Database settings.
            Element database = triplet.addElement("database");
            if (dbi.getDatabaseType() != null)
                database.addElement("engine").setText(dbi.getDatabaseType());
            if(dbi.getDatabaseIdentifier() != null)
            	database.addElement("identifier").setText(dbi.getDatabaseIdentifier());
            if(dbi.getDatabasePort() > 0)
            	database.addElement("port").setText("" + dbi.getDatabasePort());
            if(dbi.getDatabaseParameter() != null)
            	database.addElement("parameter").setText(dbi.getDatabaseParameter());
            if(dbi.getMasterUser() != null)
            	database.addElement("masteruser").setText(dbi.getMasterUser());
            if(dbi.getMasterPassword() != null)
            	database.addElement("masterpassword").setText(dbi.getMasterPassword());
            
            // Regular users of the database.
            String[] usrs = dbi.getUsers();
            for (int u = 0; u < usrs.length; u++) {
                if (usrs[u] != null)
                    triplet.addElement("user").setText(usrs[u]);
            }
        }                
    }
    
    
    public void save() throws IOException {
        if (columns != null)
            storeColumns();
        if (dbinfos != null)
            storeDBInfos();
        
        File f = new File(file);
        if (!f.exists())
            f.createNewFile();
            
        FileOutputStream out = new FileOutputStream(f);
        
        // Pretty print the document to System.out
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( out, format );
        writer.write( document );
        
    }
}

