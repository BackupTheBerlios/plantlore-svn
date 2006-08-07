/*
 * DetailCtrl.java
 *
 * Created on 2. červen 2006, 12:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview.detail;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import net.sf.plantlore.client.*;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class DetailCtrl {
    private Logger logger = Logger.getLogger(DetailCtrl.class.getPackage().getName());
    Detail model;
    DetailView view;
            
    /** Creates a new instance of DetailCtrl */
    public DetailCtrl(Detail model, DetailView view) {
        this.model = model;
        this.view = view;
                
        view.prevButton.setAction(new PrevAction());
        view.nextButton.setAction(new NextAction());
    }
    
    class NextAction extends AbstractAction {
        public NextAction() {
            putValue(AbstractAction.NAME,L10n.getString("Common.Next"));
            putValue(AbstractAction.SHORT_DESCRIPTION,L10n.getString("Detail.NextTT"));
        }
        public void actionPerformed(ActionEvent e) {
            logger.debug("Next detail.");
            try {
                model.next();
            } catch (DBLayerException ex) {
                JOptionPane.showMessageDialog(view,"Database problem: "+ex);
                ex.printStackTrace();
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(view,"Remote connection problem: "+ex);
                ex.printStackTrace();
            }
        }
        
    }
    
    class PrevAction extends AbstractAction {
        public PrevAction() {
            putValue(AbstractAction.NAME,L10n.getString("Common.Prev"));
            putValue(AbstractAction.SHORT_DESCRIPTION,L10n.getString("Detail.PrevTT"));
        }
        public void actionPerformed(ActionEvent e) {
            logger.debug("Prev detail.");
            try {
                model.prev();
            } catch (DBLayerException ex) {
                JOptionPane.showMessageDialog(view,"Database problem: "+ex);
                ex.printStackTrace();
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(view,"Remote connection problem: "+ex);
                ex.printStackTrace();
            }
        }
        
    }
}
