/*
 * SynonymSearch.java
 *
 * Created on 23. říjen 2006, 16:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.synonyms;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.log4j.Logger;

import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

/**
 *
 * @author yaa
 */
public class PlantSearch extends Observable {

	private Logger logger = Logger.getLogger(getClass().getPackage().getName());
	
    private Plant[] plants;
    private static String[] names = { Plant.TAXON, Plant.CZECHNAME, Plant.SYNONYMS };
    private ArrayList<Pair<String,Plant>> results;
    private DBLayer database;


	public PlantSearch(DBLayer database) {
		this.database = database;
	}
    
//    public void setPlants(Plant[] plants) {
//        this.plants = plants;
//    }
    
    public void loadPlantsFromDatabase() {
    	// The good thing is that the database layer we have here is only a proxy 
    	// and when we get notified that it is necessary to reload the plants from the database
    	// the "correct" database layer is already there (i.e. we do not have to ask anyone for it).
    	
    	// Erase the old plants.
    	plants = new Plant[] {};
    	SelectQuery plantQuery = null;
    	try {
    		// Try to retrieve the plants from the database.
    		plantQuery = database.createQuery(Plant.class);
    		plantQuery.addOrder(PlantloreConstants.DIRECT_ASC, Plant.TAXON);
    		Integer resultId = database.executeQuery(plantQuery);
    		int resultCount = database.getNumRows(resultId);
    		plants = new Plant[resultCount]; // Actually a non-trivial amount of memory!
    		
    		for(int i = 0; i < resultCount; i++)
    			// This ~(Plant)((Object[])db.more()[0])[0]~ shit should have been fixed a very long time ago!
    			// In fact, the whole database layer should have been rewritten properly in the first place!
    			plants[i] = (Plant)((Object[])database.more( resultId, i, i )[0])[0];
    		
    	} catch(Exception e) {
    		// This better not happen...
    		logger.error("Unable to retrieve plants from the database! " + e);
    		plants = new Plant[] {};
    	} finally {
    		if(plantQuery != null)
    			try { 	database.closeQuery(plantQuery); } catch(Exception ex) {/* At least I tried.. */}
    	}
    }
    
    @SuppressWarnings("unchecked")
	public Pair<String,Plant>[] getResults() {
        if(results == null)
            return null;
        return (Pair<String,Plant>[])results.toArray();
    }
    
    
    public void findPlants(String byName) {
        if(byName == null || "".equals(byName))
            return;
        
        String byNameLC = byName.toLowerCase();
        results = new ArrayList<Pair<String,Plant>>();
        for(Plant p : plants) {
            for(String column : names) {
                Object t = p.getValue( column );
                if( !(t instanceof String) )
                    continue;
                String name = (String) t;
                if(name.toLowerCase().contains(byNameLC)) {
                    results.add( new Pair<String,Plant>(p.toString() + " ("+column+" = "+byName+")", p) );
                    break;
                }
            }
        }
        setChanged();
        notifyObservers();
    }
    
}
