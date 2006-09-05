/*
 * HabitatTree.java
 *
 * Created on 7. srpen 2006, 12:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview.tree;

import java.rmi.RemoteException;
import java.util.Observable;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.NearestVillage;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class HabitatTree extends Observable {
    Logger logger = Logger.getLogger(HabitatTree.class.getPackage().getName());
    
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode rootNode;

    DBLayer dblayer;
    
    private DefaultMutableTreeNode selectedNode;
    
    /** Creates a new instance of HabitatTree */
    public HabitatTree() {
        //even the rootNode must contain NodeInfo user object not only String so that we can rely on the fact that EACH node contains NodeInfo
        rootNode = new DefaultMutableTreeNode(new NodeInfo(NodeInfo.NodeType.ROOT,L10n.getString("Overview.Tree.Habitats"),-1,-1));
        treeModel = new DefaultTreeModel(rootNode);
    }
    
    
    public void setDBLayer(DBLayer dblayer) {
        this.dblayer = dblayer;
    }
    
    public Task loadData() {
        final HabitatTree model = this;
        return new Task() {
            public Object task() throws DBLayerException, RemoteException {
                SelectQuery subQuery = dblayer.createSubQuery(Habitat.class,"h");
                subQuery.addProjection(PlantloreConstants.PROJ_PROPERTY,"h." + Habitat.TERRITORY);
                subQuery.addRestriction(PlantloreConstants.RESTR_EQ,"h."+Habitat.DELETED,null,0,null);

                SelectQuery query = dblayer.createQuery(Territory.class);
                query.addRestriction(PlantloreConstants.SUBQUERY_IN,Territory.ID,null,subQuery,null);

                int resultid = dblayer.executeQuery(query);
                int resultsCount = dblayer.getNumRows(resultid);
                if (resultsCount <= 0) {
                    dblayer.closeQuery(query);
                    return null;
                }
                Object[] records = (Object[]) dblayer.more(resultid, 0,resultsCount - 1);
                dblayer.closeQuery(query);
                Territory territory;
                DefaultMutableTreeNode node;
                for (Object record : records) {
                    territory = (Territory) ((Object [])record)[0];
                    node = new DefaultMutableTreeNode(new NodeInfo(NodeInfo.NodeType.TERRITORY,territory.getName(),territory.getId(),getPhythochoriaCount(territory)));
                    rootNode.add(node);
                    addPhythochoria(node,territory);
                }

                model.setChanged();
                model.notifyObservers("LOADED_DATA");
                return null;
            }
        };//return new Task;
    }
    
    public int getPhythochoriaCount(Territory t) throws RemoteException, DBLayerException {
        SelectQuery query = dblayer.createQuery(Habitat.class);
        query.addRestriction(PlantloreConstants.RESTR_EQ,Habitat.TERRITORY,null,t,null);
        query.addProjection(PlantloreConstants.PROJ_COUNT_DISTINCT,Habitat.PHYTOCHORION);
        int resultid = dblayer.executeQuery(query);
        int rowCount = dblayer.getNumRows(resultid);
        
        assert rowCount == 1;
        
        Object[] o = dblayer.more(resultid, 0, 0);
        dblayer.closeQuery(query);
        return (Integer)((Object[])(o[0]))[0];
    }
    
    public int getOccurrenceCount(Integer habitatId) throws RemoteException, DBLayerException {
        SelectQuery query = dblayer.createQuery(Occurrence.class);
        query.createAlias(Occurrence.HABITAT,"h");
        query.addRestriction(PlantloreConstants.RESTR_EQ,"h."+Habitat.ID,null,habitatId,null);
        query.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.DELETED,null,0,null);
        query.addProjection(PlantloreConstants.PROJ_COUNT_DISTINCT,Occurrence.ID);
        int resultid = dblayer.executeQuery(query);
        int rowCount = dblayer.getNumRows(resultid);
        
        assert rowCount == 1;
        
        Object[] o = dblayer.more(resultid, 0, 0);
        dblayer.closeQuery(query);
        return (Integer)((Object[])(o[0]))[0];
    }
    
    protected void addPhythochoria(DefaultMutableTreeNode territoryNode, Territory t) throws DBLayerException, RemoteException {
        SelectQuery query = dblayer.createQuery(Habitat.class);
        query.addRestriction(PlantloreConstants.RESTR_EQ,Habitat.TERRITORY,null,t,null);
        query.addProjection(PlantloreConstants.PROJ_DISTINCT,Habitat.PHYTOCHORION);
        int resultid = dblayer.executeQuery(query);
        int rowCount = dblayer.getNumRows(resultid);
                
        Object[] records = dblayer.more(resultid, 0, rowCount - 1);
        dblayer.closeQuery(query);
        DefaultMutableTreeNode node;
        NodeInfo nodeInfo;
        DefaultMutableTreeNode fakeNode;
        for (Object record : records) {
            Phytochorion phyt = (Phytochorion)((Object[])record)[0];
            nodeInfo = new NodeInfo(NodeInfo.NodeType.PHYTOCHORION,phyt.getName(),phyt.getId(),-1);
            node = new DefaultMutableTreeNode(nodeInfo);
            treeModel.insertNodeInto(node,territoryNode,0);//territoryNode.add(node);
            fakeNode = createFakeNode();
            nodeInfo.setObject(fakeNode);
            treeModel.insertNodeInto(fakeNode,node,0);//we want the phytochoria nodes displayed as having children
        }
        treeModel.nodeStructureChanged(territoryNode);
    }
    
    /**We don't want to load habitats into each phytochorion node in advance we want to load them on demand
      but we also want to display the phytochorion node as having children so we first put the fake node into
      each phytochorion node and then on Expanded event remove it and load the habitats */
    protected DefaultMutableTreeNode createFakeNode() {
         return new DefaultMutableTreeNode(new NodeInfo(NodeInfo.NodeType.FAKE, "", -256, -1));        
    }
    
    protected void addHabitats(DefaultMutableTreeNode phytNode, int phytochorionId) throws DBLayerException, RemoteException {
        DBLayerUtils dlu = new DBLayerUtils(dblayer);
        
        SelectQuery query = dblayer.createQuery(Habitat.class);
        query.addRestriction(PlantloreConstants.RESTR_EQ, Habitat.PHYTOCHORION, null, dlu.getObjectFor(phytochorionId, Phytochorion.class),null);
        query.addProjection(PlantloreConstants.PROJ_PROPERTY, Habitat.ID);
        query.addProjection(PlantloreConstants.PROJ_PROPERTY, Habitat.DESCRIPTION);
        query.addProjection(PlantloreConstants.PROJ_PROPERTY, Habitat.NEARESTVILLAGE);
        query.addProjection(PlantloreConstants.PROJ_PROPERTY, Habitat.QUADRANT);

        int resultid = dblayer.executeQuery(query);
        int rowCount = dblayer.getNumRows(resultid);
                
        Object[] records = dblayer.more(resultid, 0, rowCount - 1);
        dblayer.closeQuery(query);
        DefaultMutableTreeNode node;
        for (Object record : records) {
            Object[] obj = (Object[])record;
            String name = ((NearestVillage)obj[2]).getName() + " - " + obj[1] + " (quadrant " + obj[3] + ")";
            name = "("+getOccurrenceCount((Integer)obj[0])+") "+name;
            node = new DefaultMutableTreeNode(new NodeInfo(NodeInfo.NodeType.HABITAT,name,(Integer)obj[0],-1));
            treeModel.insertNodeInto(node, phytNode,0);
            //phytNode.add(node);
        }
    }
    
    public void setSelectedNode(DefaultMutableTreeNode node) {
        this.selectedNode = node;
        logger.debug("HabitatTree: selected node "+(node==null ? "null" : node.getUserObject()));
    }
    
    /** Notify observers about a new requirement for search (overview update).
     *
     * Sends the <code>NodeInfo</code> object of the selected node to the observer.
     *
     */
    public void search() {
        if (selectedNode != null) {
            setChanged();
            notifyObservers(new Pair<String,NodeInfo>("SEARCH",(NodeInfo)selectedNode.getUserObject()));
        }
    }
    
    /** Reloads the whole tree.
     *
     * Removes all children of the <code>rootNode</code> and then calls <code>loadData()</code>
     *
     */
    public Task reload() {
        rootNode.removeAllChildren();
        return loadData();
    }
    
    /** Invokes the Add dialog on given habitat.
     *
     */
    public void add() {
        if (selectedNode != null) {
            setChanged();
            notifyObservers(new Pair<String,NodeInfo>("ADD",(NodeInfo)selectedNode.getUserObject()));
        }
    }
    
    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }



}//class HabitatTree

