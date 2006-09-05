/*
 * HabitatTreeCtrl.java
 *
 * Created on 12. srpen 2006, 13:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview.tree;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.DefaultReconnectDialog;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.PostTaskAction;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/** The HabitatTree Controller
 *
 * @author fraktalek
 */
public class HabitatTreeCtrl implements TreeExpansionListener, TreeSelectionListener {    
    Logger logger = Logger.getLogger(HabitatTreeCtrl.class.getPackage().getName());
    HabitatTree model;
    HabitatTreeView view;
    
    private boolean showButtonText = false;
    
    /** Creates a new instance of HabitatTreeCtrl */
    public HabitatTreeCtrl(HabitatTree model, HabitatTreeView view) {
        this.model = model;
        this.view = view;
        view.habitatTree.addTreeExpansionListener(this);
        view.habitatTree.addTreeSelectionListener(this);
        view.habitatTree.addMouseListener(new PopupMouseAdapter());
        view.searchButton.setAction(new SearchAction());
        view.searchMenuItem.setAction(new SearchItemAction());
        view.refreshButton.setAction(new RefreshAction());
        view.refreshMenuItem.setAction(new RefreshItemAction());
        view.addMenuItem.setAction(new AddItemAction());
    }

    /** Expansion event handler.
     *
     * If the node expanded is of type PHYTOCHORION then this method makes the model
     * load habitats into the expanded node and removes the fake node.
     *
     */
   public void treeExpanded(TreeExpansionEvent event) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
        final NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
        logger.debug("HabitatTree: EXPANDED: "+nodeInfo+" ("+nodeInfo.getType()+")");
        switch (nodeInfo.getType()) {
            case PHYTOCHORION:
                try {
                    node.remove((DefaultMutableTreeNode)nodeInfo.getObject());
                    nodeInfo.setObject(null);
                } catch (IllegalArgumentException ex) {
                    logger.error("HabitatTree node "+node+" didn't contain a fakeNode although it should have.");
                    ex.printStackTrace();
                }


                Task task = new Task() {
                    @Override
                    public Object task() throws DBLayerException, RemoteException {
                        setStatusMessage(L10n.getString("Overview.Tree.LoadingHabitats"));
                        model.addHabitats(node,nodeInfo.getId());
                        return null;
                    }
                };
                task.setPostTaskAction(new PostTaskAction() {
                    public void afterStopped(Object value) {
                        model.getTreeModel().nodeStructureChanged(node);                        
                    }                    
                });
                Dispatcher.getDispatcher().dispatch(task, view, false);
                /*
                DefaultProgressBar dpb = new DefaultProgressBar(task,view,true) {
                    @Override
                    public void afterStopping() {
                        model.getTreeModel().nodeStructureChanged(node);                        
                    }
                };
                
                task.start(); */
        }
    }

   /** Collapsed event handler.
    *
    * If the collapsed node is of type PHYTOCHORION then removes all its child nodes and
    * creates one child fake node (and stores the fake node also to the node's NodeInfo object).
    *
    */
   public void treeCollapsed(TreeExpansionEvent event) {
       DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
       NodeInfo nodeInfo = (NodeInfo) node.getUserObject();
       logger.debug("HabitatTree: COLLAPSED: "+nodeInfo+" ("+nodeInfo.getType()+")");
       switch (nodeInfo.getType()) {
           case PHYTOCHORION:
               node.removeAllChildren();
               model.getTreeModel().nodeStructureChanged(node);
               DefaultMutableTreeNode fakeNode = model.createFakeNode();
               nodeInfo.setObject(fakeNode);
               model.getTreeModel().insertNodeInto(fakeNode, node, 0);
       }
   }
    
   /** Selection handler.
    *
    */
   public void valueChanged(TreeSelectionEvent e) {
       JTree tree = (JTree) e.getSource();
       DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
       
       if (node == null) return;
       
       Object obj = node.getUserObject();
       NodeInfo nodeInfo;
       if (!(obj instanceof NodeInfo))
           return;
       nodeInfo = (NodeInfo) node.getUserObject();
       logger.debug("HabitatTree: valueChanged event on node "+nodeInfo+" ("+nodeInfo.getType()+", "+nodeInfo.getId()+") with "+node.getChildCount()+" children.");
       switch (nodeInfo.getType()) {
           case HABITAT:
               model.setSelectedNode(node);
               break;
           default:
               model.setSelectedNode(null);
       }
   }//valueChanged()
   
    class SearchAction extends AbstractAction {
            public SearchAction() {
                    if (showButtonText)
                            putValue(NAME, L10n.getString("Overview.Tree.Search"));
                    putValue(SMALL_ICON, Resource.createIcon("/toolbarButtonGraphics/general/Search24.gif"));
                    putValue(SHORT_DESCRIPTION, L10n.getString("Overview.Tree.SearchTT"));
            }

            public void actionPerformed(ActionEvent actionEvent) {
                model.search();
            }
    }//SearchAction
   
    class RefreshAction extends AbstractAction {
            public RefreshAction() {
                    if (showButtonText)
                            putValue(NAME, L10n.getString("Overview.Tree.Refresh"));
                    putValue(SMALL_ICON, Resource.createIcon("/toolbarButtonGraphics/general/Refresh24.gif"));
                    putValue(SHORT_DESCRIPTION, L10n.getString("Overview.Tree.RefreshTT"));
            }

            public void actionPerformed(ActionEvent actionEvent) {
                    Task task = model.reload();
                    Dispatcher.getDispatcher().dispatch(task, view, false);
            }
    }//RefreshAction
   
    class SearchItemAction extends AbstractAction {
            public SearchItemAction() {
                    putValue(NAME, L10n.getString("Overview.Tree.Search"));
//                    putValue(SMALL_ICON, Resource.createIcon("/toolbarButtonGraphics/general/Search24.gif"));
                    putValue(SHORT_DESCRIPTION, L10n.getString("Overview.Tree.SearchTT"));
            }

            public void actionPerformed(ActionEvent actionEvent) {
                model.search();
            }
    }//SearchAction
   
    class RefreshItemAction extends AbstractAction {
            public RefreshItemAction() {
                    putValue(NAME, L10n.getString("Overview.Tree.Refresh"));
//                    putValue(SMALL_ICON, Resource.createIcon("/toolbarButtonGraphics/general/Refresh24.gif"));
                    putValue(SHORT_DESCRIPTION, L10n.getString("Overview.Tree.RefreshTT"));
            }

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    model.reload();
                } catch (Exception ex) {
                    DefaultExceptionHandler.handle(view,ex);
                }
            }
    }//RefreshAction
    
    class AddItemAction extends AbstractAction {
        public AddItemAction() {
            putValue(NAME, L10n.getString("Overview.Tree.Add"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Overview.Tree.AddTT"));            
        }
        
        public void actionPerformed(ActionEvent ae) {
            model.add();
        }
    }//AddItemAction

    class PopupMouseAdapter extends MouseAdapter {
         public void mousePressed(MouseEvent e) {
             int selRow = view.habitatTree.getRowForLocation(e.getX(), e.getY());
             TreePath selPath = view.habitatTree.getPathForLocation(e.getX(), e.getY());
             DefaultMutableTreeNode node = selPath == null ? null : (DefaultMutableTreeNode)selPath.getLastPathComponent();
             switch (e.getButton()) {
                 case MouseEvent.BUTTON3: //right button
                     //if(selRow != -1) {
                         if (e.isPopupTrigger()) {
                             view.popupMenu.show(view.habitatTree,e.getX(),e.getY());
                         }
                     //}
                     break;
                 case MouseEvent.BUTTON1://left button
                     if (e.getClickCount() == 2) { //double-click
                         if (node != null && ((NodeInfo)node.getUserObject()).getType().equals(NodeInfo.NodeType.HABITAT))
                             model.search();
                     }
                     break;
             }//switch
         }        
    }//PopupMouseAdapter

    
}//class HabitatTreeCtrl



