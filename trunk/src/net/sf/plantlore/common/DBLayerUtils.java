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
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.server.HibernateDBLayer;

/** Class offering convenience methods for DBLayer.
 *
 * @author reimei
 */
public class DBLayerUtils {
    DBLayer db;
    
    /** Creates a new instance of TempClass */
    public DBLayerUtils(DBLayer db) {
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
        //FIXME:
        try {
            SelectQuery sq = db.createQuery(c);
            sq.addRestriction(PlantloreConstants.RESTR_EQ,"id",null,id,null);
            int resultid = db.executeQuery(sq);
            int resultCount = db.getNumRows(resultid);
            if (resultCount == 0)
                return null;
            Object[] results = db.more(resultid, 1, 1);
            Object[] tmp = (Object[]) results[0];
            return (Record)tmp[0];
            
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        return null;
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
