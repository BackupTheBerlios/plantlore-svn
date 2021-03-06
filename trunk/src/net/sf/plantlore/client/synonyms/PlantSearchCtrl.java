﻿/*
 * PlantSearchCtrl.java
 *
 * Created on 23. říjen 2006, 17:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.synonyms;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Plant;

/**
 *
 * @author kaimu
 */
public class PlantSearchCtrl {
    
    private PlantSearchView view;
    private PlantSearch model;
    private AutoTextArea plantAbsorber;
    
    /** Creates a new instance of PlantSearchCtrl */
    public PlantSearchCtrl(PlantSearchView searchView, PlantSearch searchModel, AutoTextArea textArea) {
        this.view = searchView;
        this.model = searchModel;
        this.plantAbsorber = textArea;
        
        view.cancel.setAction( new DefaultCancelAction(this.view) );
        
        view.find.setAction( new StandardAction("PlantSearch.Find") {
            public void actionPerformed(ActionEvent e) {
                model.findPlants( view.pattern.getText() );
            }
        });
        
        view.insert.setAction( new StandardAction("PlantSearch.Insert") {
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
                Object value = view.results.getSelectedValue();
                if(value != null)
                    plantAbsorber.addLines(new String[] { ((Pair<String,Plant>)value).getSecond().getTaxon() } );
            }
        });
        
        view.results.addKeyListener(
        		new KeyAdapter() {
        			@Override
        			public void keyTyped(KeyEvent e) {
        				if(e.getKeyChar() == ' ') // The idiot Java does not fill in the keyCode properly!
        					view.insert.doClick();
        			}
        		});
        
        plantAbsorber.addPropertyChangeListener(
				AutoTextArea.ALLOWED_VALUES_CHANGED,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent arg0) {
						Task reloadPlants = model.createReloadPlantsFromDatabaseTask();
						Dispatcher.getDispatcher().dispatch( reloadPlants, view, false );
					}
				});
        
    }
    
}
