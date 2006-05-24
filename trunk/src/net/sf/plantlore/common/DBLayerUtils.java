/*
 * TempClass.java
 *
 * Created on 26. duben 2006, 10:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import java.rmi.RemoteException;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.server.HibernateDBLayer;
import org.apache.log4j.Logger;

/** Class offering convenience methods for DBLayer.
 *
 * @author reimei
 */
public class DBLayerUtils {
    DBLayer db;
    Logger logger;
    
    /** Creates a new instance of TempClass */
    public DBLayerUtils(DBLayer db) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        
        this.db = db;
    }
    
    /** Gets an object according to it's id.
     *
     * @param id id of the row
     * @param c class of the object
     * @return Record Object of type c with id id.
     * @return null in case an exception is thrown or no row with that id exists
     */
    public Record getObjectFor(int id, Class c) {
        logger.debug("Looking up "+c.getName()+" object in the database for id "+id);
        //FIXME:
        try {
            SelectQuery sq = db.createQuery(c);
            sq.addRestriction(PlantloreConstants.RESTR_EQ,"id",null,id,null);
            int resultid = db.executeQuery(sq);
            int resultCount = db.getNumRows(resultid);
            if (resultCount == 0)
                return null;
            Object[] results = db.more(resultid, 0, 0);
            Object[] tmp = (Object[]) results[0];
            return (Record)tmp[0];
            
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public AuthorOccurrence[] getAuthorsOf(Occurrence o) {
        AuthorOccurrence[] authorResults = null;
        //FIXME:
        try {
            SelectQuery sq = db.createQuery(AuthorOccurrence.class);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
            sq.addRestriction(PlantloreConstants.RESTR_NE,AuthorOccurrence.DELETED,null,1,null);
            int resultid = db.executeQuery(sq);
            int resultCount = db.getNumRows(resultid);
            authorResults = new AuthorOccurrence[resultCount];
            Object[] results = db.more(resultid, 0, resultCount-1);
            Object[] tmp;
            AuthorOccurrence ao;
            for (int i = 0; i < resultCount; i++) {
                tmp = (Object[]) results[i];
                ao = (AuthorOccurrence)tmp[0];
                authorResults[i] = ao;
            }
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return authorResults;
    }
    
    public static void main(String[] args) throws DBLayerException, RemoteException {
        DBLayer db = new HibernateDBLayer();
        db.initialize("jdbc:firebirdsql:localhost/3050:/mnt/data/temp/plantloreHIBdataUTF.fdb","sysdba","masterkey");
        DBLayerUtils dlu = new DBLayerUtils(db);
        Author a = (Author)dlu.getObjectFor(1,Author.class);
        System.out.println("class "+a.getWholeName());
        Occurrence o = (Occurrence)dlu.getObjectFor(1,Occurrence.class);
        System.out.println("nalez "+o.getPlant().getTaxon());
    }
    
}
