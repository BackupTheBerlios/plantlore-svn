/*
 * NodeInfo.java
 *
 * Created on 13. srpen 2006, 17:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview.tree;

/** Class for holding information about nodes.
 *
 * Each node is supposed to represent a row in a database table, the primary key cId is
 * given to the constructor and can be retrieved using <code>getId()</code> method.
 *
 * Each node has an associated type NodeType that informs the user about the node level.
 * Because all the nodes on a given level are of the same type.
 *
 */
public class NodeInfo {
    public enum NodeType {TERRITORY, PHYTOCHORION, HABITAT, ROOT, FAKE};    
    String name;
    Integer id;
    int childCount;
    NodeType type;
    Object object;
    
    /** Creates an instance of NodeInfo class.
     *
     * @param name used in <code>toString()</code> method
     * @param id cId of the represented database row / hibernate object
     * @param childCount used in <code>toString()</code> method to inform user about the number of descendants
     *
     */
    public NodeInfo(NodeType type, String name, Integer id, int childCount) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.childCount = childCount;        
    }
    
    public String toString() {
        if (childCount > 0)
            return name + " ("+childCount+")";
        else
            return name;
    }
    
    public Integer getId() {
        return id;
    }
    
    public NodeType getType() {
        return type;
    }
    
    public int getChildCount() {
        return childCount;
    }
    
    /** The NodeInfo object can hold some arbitrary object using this method.
     *
     */
    public void setObject(Object obj) {
        this.object = obj;
    }
    
    /** Returns the object previously set by <code>setObject()</code> method.
     *
     */
    public Object getObject() {
        return object;
    }
    
    /** Two NodeInfo objects are equal iff they are of the same type
     * and their ids are equal.
     *
     * Instances of other classes than NodeInfo are never equal to NodeInfo.
     *
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof NodeInfo))
            return false;
        NodeInfo ni = (NodeInfo) obj;
        return ni.getType().equals(getType()) && ni.getId().equals(getId());
    }
}

