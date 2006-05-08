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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import net.sf.plantlore.common.AutoComboBox;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.record.Occurrence;

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
        view.timeFormattedTextField.addPropertyChangeListener("value",new TimeListener());
        
        //------- TextAreas --------        
        view.taxonTextArea.addFocusListener(new TaxonAreaListener());
        view.descriptionArea.addFocusListener(new PlaceAreaListener());
        view.locationNoteArea.addFocusListener(new LocationAreaListener());
        view.occurrenceNoteArea.addFocusListener(new OccurrenceAreaListener());

        //------- Spinners --------
        view.yearSpinner.addChangeListener(new YearListener());
        view.monthSpinner.addChangeListener(new MonthListener());
        view.daySpinner.addChangeListener(new DayListener());
        
        //------- RadioButtons --------
        view.WGS84Button.addActionListener(new CoordinateSystemListener());
        view.S42Button.addActionListener(new CoordinateSystemListener());
        view.SJTSKButton.addActionListener(new CoordinateSystemListener());
        
        //------- Buttons --------
        view.extendedButton.addMouseListener(new ExtendedButtonListener());
        view.okButton.addMouseListener(new OkButtonListener());
        view.cancelButton.addMouseListener(new CancelButtonListener());
        view.helpButton.addMouseListener(new HelpButtonListener());
    }
    
    class CommonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox c = null;
            
            c  = (JComboBox) e.getSource();
            
            String command = e.getActionCommand();
            
            //------- ComboBoxes --------            
//            if (command.equals("authorComboBox"))
//                model.setAuthor((Pair<String, Integer>) c.getSelectedItem());
                
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
        
    }//class CommonActionListener
    
    class CoordinateSystemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("WGS84"))
                model.setCoordinateSystem(AddEdit.WGS84);
            
            if (command.equals("S42"))
                model.setCoordinateSystem(AddEdit.S42);
            
            if (command.equals("SJTSK"))
                model.setCoordinateSystem(AddEdit.SJTSK);            
        }
    }//CoordinateSystemListener
    
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
    class DayListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSpinner s = (JSpinner) e.getSource();
            model.setDay((Integer)s.getValue());
        }
    }//DayListener
    
    class YearListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSpinner s = (JSpinner) e.getSource();
            model.setYear((Integer)s.getValue());
        }
    }//YearListener
    
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

    class TimeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            model.setTime((Date)evt.getNewValue());
        }        
    }//AltitudeListener
    
    class ExtendedButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            view.switchExtended();
        }
    }//ExtendedButtonListener
    
    class OkButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            int choice=-1;
            Pair<Boolean,String> check = model.checkData();
            if (!check.getFirst()) {
                JOptionPane.showMessageDialog(view,check.getSecond());
                return;
            } 
            if (inEditMode) {
                Occurrence[] sharedOcc = model.getHabitatSharingOccurrences();
                if (sharedOcc.length > 1) {
                    Object[] options = {"All","Just this","Cancel"};
                    choice = JOptionPane.showOptionDialog(view, 
                            "This plant's habitat is shared by "+sharedOcc.length+" other occurrences. \nDo you want to edit all the plants or just this one?",
                            "Multiple plants share the same habitat",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);
                    System.out.println("User selected "+options[choice]);
                    switch (choice) {
                        case 0:
                            model.storeRecord(true);
                            view.setVisible(false);
                            break;
                        case 1:
                            model.storeRecord(false);
                            view.setVisible(false);
                            break;
                        case 2:
                        default:                        
                            //we'll do nothing and leave the AddEdit dialog visible
                    }
                } else {
                        model.storeRecord(true);
                        view.setVisible(false);                    
                }
            } else {//inAddMode
                model.storeRecord(true);
                view.setVisible(false);
            }
        }
    }//OkButtonListener
    
    class CancelButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            System.out.println("Cancel");
            view.setVisible(false);
        }
    }//CancelButtonListener

    class HelpButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            System.out.println("Help");
        }
    }//HelpButtonListener
    
}

