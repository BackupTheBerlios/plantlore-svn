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
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

/**
 *
 * @author kaimu
 */
public class PlantSearch extends Observable {

	private Logger logger = Logger.getLogger(getClass().getPackage().getName());
	
    private Plant[] plants = new Plant[] {};
    private static String[] names = { Plant.TAXON, Plant.CZECHNAME, Plant.SYNONYMS };
    private Pair<String,Plant>[] results;
    private DBLayer database;


	public PlantSearch(DBLayer database) {
		this.database = database;
	}
    

	
	public Task createReloadPlantsFromDatabaseTask() {
		return new Task("Load plants from the database.") {
			@Override
			public Object task() throws Exception {
				// The good thing is that the database layer we have here is only a proxy 
				// and when we get notified that it is necessary to reload the plants from the database
				// the "correct" database layer is already there (i.e. we do not have to ask anyone for it).

				// Erase the old plants.
				plants = new Plant[] {};
				SelectQuery plantQuery = null;
				try {
					logger.debug("Preparing for plants retrieval...");
					setStatusMessage(L10n.getString("PlantSearch.ObtainPlants"));
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
				return null;
			}
		};
    }
    
    @SuppressWarnings("unchecked")
	public Pair<String,Plant>[] getResults() {
        return results;
    }
    
    
    @SuppressWarnings("unchecked")
	public void findPlants(String byName) {
        if(byName == null || "".equals(byName))
            return;
        
        if(plants == null || plants.length == 0) {
        	logger.error("No plants available!!");
        	return;
        }
        
        String byNameLC = byName.toLowerCase();
        ArrayList<Pair<String,Plant>> preResults = new ArrayList<Pair<String,Plant>>();
        for(Plant p : plants) {
            for(String column : names) {
            	if(p == null)
            		continue;
                Object t = p.getValue( column );
                if( !(t instanceof String) )
                    continue;
                String name = (String) t;
                if(name.toLowerCase().contains(byNameLC)) {
                    preResults.add( 
                    		new Pair<String,Plant>(
                    				p.getTaxon() + (column.equals(Plant.TAXON) ? "" : " ("+name+")"), 
                    				p) 
                    );
                    break;
                }
            }
        }
        
        results = new Pair[preResults.size()];
        int i = 0;
        for( Pair<String, Plant> p : preResults ) { 
        	results[i] = p;
        	i++;
        }
        
        setChanged();
        notifyObservers();
    }
    
}
