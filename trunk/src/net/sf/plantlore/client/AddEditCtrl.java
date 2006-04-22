/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import net.sf.plantlore.common.Pair;

/**
 *
 * @author reimei
 */
public class AddEditCtrl {
    private boolean inEditMode = false;
    private boolean inAddMode = true;
    private AddEdit model;
    private AddEditView view;
    
    /** Creates a new instance of AddEditCtrl */
    public AddEditCtrl(AddEdit model, AddEditView view, boolean edit) {
        this.inEditMode = edit;
        this.inAddMode = ! edit;
        this.model = model;
        this.view = view;
        
        view.authorComboBox.addActionListener(new ComboListener());
        view.townComboBox.addActionListener(new ComboListener());
        view.territoryNameCombo.addActionListener(new ComboListener());
        view.phytNameCombo.addActionListener(new ComboListener());
        view.phytCodeCombo.addActionListener(new ComboListener());
        view.phytCountryCombo.addActionListener(new ComboListener());
        view.sourceCombo.addActionListener(new ComboListener());
        view.publicationCombo.addActionListener(new ComboListener());
        view.projectCombo.addActionListener(new ComboListener());
        
        view.taxonTextArea.addFocusListener(new TaxonAreaListener());
        view.descriptionArea.addFocusListener(new PlaceAreaListener());
        view.locationNoteArea.addFocusListener(new LocationAreaListener());
        view.occurrenceNoteArea.addFocusListener(new OccurrenceAreaListener());
    }
    
    class ComboListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox c = (JComboBox)e.getSource();
            String command = e.getActionCommand();
            if (command.equals("authorComboBox"))
                model.setAuthor((Pair<String, Integer>) c.getSelectedItem());
                
            if (command.equals("townComboBox"))
                model.setVillage((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("territoryNameCombo"))
                model.setTerritoryName((Pair<String, Integer>) c.getSelectedItem());

            if (command.equals("phytNameCombo"))
                model.setPhytName((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("phytCodeCombo"))
                model.setPhytCode((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("phytCountryCombo"))
                model.setPhytCountry((String) c.getSelectedItem());

            if (command.equals("sourceCombo"))
                model.setSource((String) c.getSelectedItem());
           
            if (command.equals("publicationCombo"))
                model.setPublication((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("projectCombo"))
                model.setProject((Pair<String, Integer>) c.getSelectedItem());
           
        }
        
    }//class ComboListener
    
    class TaxonAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextArea ta = (JTextArea) e.getSource();
            model.setTaxon(ta.getText());
        }
    }//taxonAreaListener
    
    class PlaceAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextArea ta = (JTextArea) e.getSource();
            model.setLocalityDescription(ta.getText());
        }
    }//placeAreaListener

    class LocationAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextArea ta = (JTextArea) e.getSource();
            model.setHabitatNote(ta.getText());
        }
    }//LocationAreaListener

    class OccurrenceAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextArea ta = (JTextArea) e.getSource();
            model.setOccurrenceNote(ta.getText());
        }
    }//LocationAreaListener
}
