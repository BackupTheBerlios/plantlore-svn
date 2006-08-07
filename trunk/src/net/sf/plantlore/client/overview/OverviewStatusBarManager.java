/*
 * OverviewStatusBarManager.java
 *
 * Created on 28. květen 2006, 23:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.util.Observable;
import javax.swing.JLabel;
import net.sf.plantlore.client.*;
import net.sf.plantlore.common.StatusBarManager;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author fraktalek
 */
public class OverviewStatusBarManager extends StatusBarManager {
    
    /** Creates a new instance of OverviewStatusBarManager */
    public OverviewStatusBarManager(JLabel statusLabel) {
        super(statusLabel);
    }

    public void update(Observable o, Object arg)  {
        if (arg instanceof String) {
            String msg = (String)arg;
            
            if (msg.equals("LOADING_PLANTS")) {
                display(L10n.getString("Message.LoadingPlants"));
            }
            
            if (msg.equals("LOADING_AUTHORS")) {
                display(L10n.getString("Message.LoadingAuthors"));
            }
            
            if (msg.equals("LOADING_AUTHOR_ROLES")) {
                display(L10n.getString("Message.LoadingAuthorRoles"));
            }
            
            if (msg.equals("LOADING_VILLAGES")) {
                display(L10n.getString("Message.LoadingVillages"));
            }

            if (msg.equals("LOADING_TERRITORIES")) {
                display(L10n.getString("Message.LoadingTerritories"));
            }
            
            if (msg.equals("LOADING_PHYTOCHORIA")) {
                display(L10n.getString("Message.LoadingPhytochoria"));
            }
            
            if (msg.equals("LOADING_SOURCES")) {
                display(L10n.getString("Message.LoadingSources"));
            }
            
            if (msg.equals("LOADING_PUBLICATIONS")) {
                display(L10n.getString("Message.LoadingPublications"));
            }
            
            if (msg.equals("LOADING_PROJECTS")) {
                display(L10n.getString("Message.LoadingProjects"));
            }
            
            if (msg.equals("LOADING_OVERVIEW_DATA")) {
                display(L10n.getString("Message.LoadingOverviewData"));
            }
            
            if (msg.equals("LOADED")) {
                displayDefaultText();
            }
        }
    }
    
}
