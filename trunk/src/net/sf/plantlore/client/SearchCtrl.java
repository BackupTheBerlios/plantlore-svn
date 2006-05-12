/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

/**
 *
 * @author reimei
 */
public class SearchCtrl {
    private Search model;
    private SearchView view;
    
    /** Creates a new instance of AddEditCtrl */
    public SearchCtrl(Search model, SearchView view) {
        this.model = model;
        this.view = view;
        
        //------- ComboBoxes --------
        view.townComboBox.addActionListener(new CommonActionListener());
        view.territoryNameCombo.addActionListener(new CommonActionListener());
        view.phytNameCombo.addActionListener(new CommonActionListener());
        view.phytCodeCombo.addActionListener(new CommonActionListener());
        view.phytCountryCombo.addActionListener(new CommonActionListener());
        view.sourceCombo.addActionListener(new CommonActionListener());
        view.publicationCombo.addActionListener(new CommonActionListener());
        view.projectCombo.addActionListener(new CommonActionListener());

        //------- TextFields --------        
        view.herbariumTextField.addFocusListener(new HerbariumListener());
        view.quadrantTextField.addFocusListener(new QuadrantListener());
        view.altitudeFormattedTextField.addPropertyChangeListener("value",new AltitudeListener());
        view.longitudeFormattedTextField.addPropertyChangeListener("value",new LongitudeListener());
        view.latitudeFormattedTextField.addPropertyChangeListener("value",new LatitudeListener());
        
        //------- TextAreas --------        
        view.taxonTextArea.addFocusListener(new TaxonAreaListener());
        view.descriptionArea.addFocusListener(new PlaceAreaListener());
        view.locationNoteArea.addFocusListener(new LocationAreaListener());
        view.occurrenceNoteArea.addFocusListener(new OccurrenceAreaListener());
               
        //------- Buttons --------
        view.extendedButton.addMouseListener(new ExtendedButtonListener());
        view.okButton.addMouseListener(new OkButtonListener());
        view.cancelButton.addMouseListener(new CancelButtonListener());
        view.helpButton.addMouseListener(new HelpButtonListener());
        
        //------- Time
        view.monthChooser.addPropertyChangeListener("month",new MonthChangeListener());
        view.fromDateChooser.addPropertyChangeListener(new FromDateChangeListener());
        view.toDateChooser.addPropertyChangeListener(new ToDateChangeListener());
        view.intervalRadioButton.addActionListener(new TimeButtonListener());
        view.monthRadioButton.addActionListener(new TimeButtonListener());
    }
    
    class CommonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox c = null;
            
            c  = (JComboBox) e.getSource();
            
            String command = e.getActionCommand();
            
            //------- ComboBoxes --------            
//            if (command.equals("authorComboBox"))
//                model.setAuthor((Pair<String, Integer>) c.getSelectedItem());
                //! instanceof String because of comboBox default value
            if (command.equals("townComboBox") && !(c.getSelectedItem() instanceof String)) {
                model.setVillage((Pair<String, Integer>) c.getSelectedItem());
            }
           
            if (command.equals("territoryNameCombo") && !(c.getSelectedItem() instanceof String))
                model.setTerritoryName((Pair<String, Integer>) c.getSelectedItem());

            if (command.equals("phytNameCombo") && !(c.getSelectedItem() instanceof String))
                model.setPhytName((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("phytCodeCombo") && !(c.getSelectedItem() instanceof String))
                model.setPhytCode((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("phytCountryCombo") && !(c.getSelectedItem() instanceof String))
                model.setPhytCountry((String) c.getSelectedItem());

            if (command.equals("sourceCombo"))
                model.setSource((String) c.getSelectedItem());
           
            if (command.equals("publicationCombo") && !(c.getSelectedItem() instanceof String))
                model.setPublication((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("projectCombo") && !(c.getSelectedItem() instanceof String))
                model.setProject((Pair<String, Integer>) c.getSelectedItem());
            
            if (command.equals("monthCombo") && !(c.getSelectedItem() instanceof String))
                model.setMonth(view.monthChooser.getMonth());
            
        }
        
    }//class CommonActionListener
        
    class TimeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("INTERVAL")) {
                view.fromLabel.setEnabled(true);
                view.fromDateChooser.setEnabled(true);
                view.toLabel.setEnabled(true);
                view.toDateChooser.setEnabled(true);
                
                view.monthLabel.setEnabled(false);
                view.monthChooser.setEnabled(false);
                model.setTimeChoice(Search.INTERVAL);
            }
            
            if (command.equals("MONTH")) {
                view.fromLabel.setEnabled(false);
                view.fromDateChooser.setEnabled(false);
                view.toLabel.setEnabled(false);
                view.toDateChooser.setEnabled(false);
                
                view.monthLabel.setEnabled(true);
                view.monthChooser.setEnabled(true);
                model.setTimeChoice(Search.MONTH);            
            }
        }
    }//CoordinateSystemListener

    class MonthChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            model.setMonth((Integer) evt.getNewValue());
        } 
    }
    

    class FromDateChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String prop = evt.getPropertyName();
            if (prop.equals("date"))
                model.setFromDate((Date) evt.getNewValue());
            
            if (prop.equals("value")) {
                JTextFieldDateEditor d = (JTextFieldDateEditor)evt.getSource();
                JTextField tf = (JTextField)d.getUiComponent();
                if (d.getText() == null || d.getText().equals(""))
                    model.setFromDate(null);
            }
        } 
    }

    class ToDateChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String prop = evt.getPropertyName();
            if (prop.equals("date"))
                model.setToDate((Date) evt.getNewValue());
            
            if (prop.equals("value")) {
                JTextFieldDateEditor d = (JTextFieldDateEditor)evt.getSource();
                JTextField tf = (JTextField)d.getUiComponent();
                if (d.getText() == null || d.getText().equals(""))
                    model.setToDate(null);
            }
        } 
    }

    class TaxonAreaListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            ArrayList<String> taxonList = new ArrayList<String>();
            AutoTextArea ta = (AutoTextArea) e.getSource();
            int lineCount = ta.getLineCount();
            for (int i=0; i < lineCount; i++) {
                String tmp = ta.getLine(i);
                if (tmp.length() > 1) //omit empty lines
                    taxonList.add(tmp);
            }
            model.setTaxons(taxonList);
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
    }//OccurrenceAreaListener
    
    class HerbariumListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField tf = (JTextField) e.getSource();
            model.setHerbarium(tf.getText());
        }
    }//HerbariumListener
    
    class QuadrantListener implements FocusListener {
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField tf = (JTextField) e.getSource();
            model.setQuadrant(tf.getText());
        }
    }//QuadrantListener
    
    class MonthListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSpinner s = (JSpinner) e.getSource();
            model.setMonth((Integer)s.getValue());
        }
    }//MonthListener
    
    
    class AltitudeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null)
                model.setAltitude(((Number)evt.getNewValue()).doubleValue());
        }        
    }//AltitudeListener

    class LongitudeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null)
                model.setLongitude(((Number)evt.getNewValue()).doubleValue());
        }        
    }//LongitudeListener

    class LatitudeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null)
                model.setLatitude(((Number)evt.getNewValue()).doubleValue());
        }        
    }//LatitudeListener

    
    class ExtendedButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            view.switchExtended();
        }
    }//ExtendedButtonListener
    
    class OkButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Pair<Boolean,String> check = model.checkData();
            if (!check.getFirst()) {
                JOptionPane.showMessageDialog(view,check.getSecond());
                return;
            }
            
            System.out.println("Would create a query!");
            model.constructQuery();
            view.setVisible(false);
        }
    }//OkButtonListener
    
    class CancelButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            view.setVisible(false);
        }
    }//CancelButtonListener

    class HelpButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            System.out.println("Help");
        }
    }//HelpButtonListener
    
}

