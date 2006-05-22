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
import org.apache.log4j.Logger;
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
    Logger logger;
    Document document;
    String file;
    ArrayList<Column> columns = null;
    ArrayList<DBInfo> dbinfos = null;
    
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

        List columnList = document.selectNodes("//config/login/triplet");
        Iterator it = columnList.iterator();
        while (it.hasNext()) {
            Node n = (Node)it.next();
            String alias = n.valueOf("alias"),
                    host = n.valueOf("host"),
                    database = n.valueOf("database");
            Number port = n.numberValueOf("port");

            List userList = n.selectNodes("user");
            String[] users = new String[userList.size()];
            Node user = null;
            Iterator it2 = userList.iterator(); int i = 0;
            while (it2.hasNext()) {
                user = (Node) it2.next();
                users[i] = user.getText();
                i++;
            }
                        
            DBInfo dbi = new DBInfo(alias,host,port.intValue(),database,users);
            result.add(dbi);
        }
        return result;
    }
    
    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }
    
    public void setDBInfos(ArrayList<DBInfo> dbinfos) {
        this.dbinfos = dbinfos;
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
        
        for (int i = 0; i < dbinfos.size(); i++) {
            DBInfo dbi = dbinfos.get(i);
            Element triplet = login.addElement("triplet");
            if (dbi.getAlias() != null)
                triplet.addElement("alias").setText(dbi.getAlias());
            if (dbi.getHost() != null)
                triplet.addElement("host").setText(dbi.getHost());
            if (dbi.getDb() != null)
                triplet.addElement("database").setText(dbi.getDb());
            triplet.addElement("port").setText(""+dbi.getPort());
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

