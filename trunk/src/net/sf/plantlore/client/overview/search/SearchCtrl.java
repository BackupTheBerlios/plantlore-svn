/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview.search;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import net.sf.plantlore.client.*;
import net.sf.plantlore.client.overview.*;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author reimei
 */
public class SearchCtrl {
    private Logger logger;
    private Search model;
    private SearchView view;
    private final static int MAXIMUM_FRACTION_DIGITS = 5;
    private final static int MAXIMUM_INTEGER_DIGITS = 4;
    private final static Color COLOR_INVALID = Color.RED;
    
    /** Creates a new instance of AddEditCtrl */
    public SearchCtrl(Search model, SearchView view) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
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
        view.altitudeTextField.getDocument().addDocumentListener(new AltitudeListener());
        view.longitudeTextField.getDocument().addDocumentListener(new LongitudeListener());
        view.latitudeTextField.getDocument().addDocumentListener(new LatitudeListener());
        
        //------- TextAreas --------        
        view.taxonTextArea.addFocusListener(new TaxonAreaListener());
        view.descriptionArea.addFocusListener(new PlaceAreaListener());
        view.locationNoteArea.addFocusListener(new LocationAreaListener());
        view.occurrenceNoteArea.addFocusListener(new OccurrenceAreaListener());
               
        //------- Buttons --------
        view.extendedButton.setAction(new ExtendedButtonAction());
        view.okButton.setAction(new OkButtonAction());
        view.cancelButton.setAction(new CancelButtonAction());
        
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
           
            if (command.equals("phytCountryCombo"))
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
            Integer i = (Integer) evt.getNewValue();
            if (!i.equals(12))
                model.setMonth(i);
            else
                model.setMonth(null);
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
    class AltitudeListener implements DocumentListener {
        NumberFormat nf = NumberFormat.getNumberInstance( L10n.getCurrentLocale() );
        Color oldColor;

        public AltitudeListener() {
            oldColor = view.altitudeTextField.getForeground();
            nf.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
            nf.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
        }

        public void processEvent(DocumentEvent e) {
            if (e.getDocument().getLength() == 0) {
                model.setAltitude(null);
                return;
            }
            String text = null;
            try { text = e.getDocument().getText(0, e.getDocument().getLength()); } 
            catch (BadLocationException ex) { 
                logger.warn("AltitudeListener in AddEditCtrl: unexpected BadLocationException: "+ex); 
                return;//shouldn't happen, just ignore it }
            }
            
            if (text.trim().equals("")) {
                model.setAltitude(null);
                return;
            }

            Double value;
            try {
                value = nf.parse(text).doubleValue();
                String s = nf.format(value);
                if (!s.equals(text.trim()))
                    throw new ParseException("Parsing could stop at decimal point. Take a closer look.",0);
                model.setAltitude(value);
                view.altitudeTextField.setForeground(oldColor);
            } catch (ParseException ex) {
                    view.altitudeTextField.setForeground(COLOR_INVALID);
                    model.setAltitudeValid(false);
            }
        }

        public void insertUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void removeUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void changedUpdate(DocumentEvent e) {
            processEvent(e);
        }
    }//AltitudeListener

    class LatitudeListener implements DocumentListener {
        NumberFormat nf = NumberFormat.getNumberInstance( L10n.getCurrentLocale() );
        Color oldColor;

        public LatitudeListener() {
            oldColor = view.latitudeTextField.getForeground();
            nf.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
            nf.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
        }

        public void processEvent(DocumentEvent e) {
            System.out.println("LATITUDE old COLOR = "+oldColor);
            if (e.getDocument().getLength() == 0) {
                model.setLatitude(null);
                return;
            }
            String text = null;
            try { text = e.getDocument().getText(0, e.getDocument().getLength()); } 
            catch (BadLocationException ex) { 
                logger.warn("LatitudeListener in AddEditCtrl: unexpected BadLocationException: "+ex); 
                return;//shouldn't happen, just ignore it }
            }
            
            if (text.trim().equals("")) {
                model.setLatitude(null);
                return;
            }
            
            Double value;
            try {
                value = nf.parse(text).doubleValue();
                String s = nf.format(value);
                if (!s.equals(text.trim()))
                    throw new ParseException("Parsing could stop at decimal point. Take a closer look.",0);
                model.setLatitude(value);
                view.latitudeTextField.setForeground(oldColor);
            } catch (ParseException ex) {
                view.latitudeTextField.setForeground(COLOR_INVALID);
                model.setLatitudeValid(false);
            }
        }

        public void insertUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void removeUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void changedUpdate(DocumentEvent e) {
            processEvent(e);
        }
    }//LatitudeListener

    class LongitudeListener implements DocumentListener {
        NumberFormat nf = NumberFormat.getNumberInstance( L10n.getCurrentLocale() );
        Color oldColor;

        public LongitudeListener() {
            oldColor = view.longitudeTextField.getForeground();
            nf.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
            nf.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
        }

        public void processEvent(DocumentEvent e) {
            if (e.getDocument().getLength() == 0) {
                model.setLongitude(null);
                return;
            }
            String text = null;
            try { text = e.getDocument().getText(0, e.getDocument().getLength()); } 
            catch (BadLocationException ex) { 
                logger.warn("LongitudeListener in AddEditCtrl: unexpected BadLocationException: "+ex); 
                return;//shouldn't happen, just ignore it }
            }
            
            if (text.trim().equals("")) {
                model.setLongitude(null);
                return;
            }
            
            Double value;
            try {
                value = nf.parse(text).doubleValue();
                String s = nf.format(value);
                if (!s.equals(text.trim()))
                    throw new ParseException("Parsing could stop at decimal point. Take a closer look.",0);
                model.setLongitude(value);
                view.longitudeTextField.setForeground(oldColor);
            } catch (ParseException ex) {
                model.setLongitudeValid(false);
                view.longitudeTextField.setForeground(COLOR_INVALID);
            }
        }

        public void insertUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void removeUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void changedUpdate(DocumentEvent e) {
            processEvent(e);
        }
    }//LongitudeListener

    
    class ExtendedButtonAction extends AbstractAction {
        public ExtendedButtonAction() {
            putValue(NAME, L10n.getString("Search.Extended"));
            //putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SchedaTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Search.Extended"));
        }
        
        public void actionPerformed(ActionEvent e) {
            view.switchExtended();
        }
    }//ExtendedButtonListener
    
    class OkButtonAction extends AbstractAction {
        public OkButtonAction() {
            putValue(NAME, L10n.getString("Common.Ok"));
            //putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SchedaTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Common.Ok"));
        }
        
        public void actionPerformed(ActionEvent e) {
            Pair<Boolean,String> check = model.checkData();
            if (!check.getFirst()) {
                JOptionPane.showMessageDialog(view,check.getSecond());
                return;
            }
            try {
                
                model.constructQuery();
            } catch (DBLayerException ex) {
                DefaultExceptionHandler.handle(view, ex);
                return;
            } catch (RemoteException ex) {
                DefaultExceptionHandler.handle(view, ex);
                return;
            }
            view.setVisible(false);
        }
    }//OkButtonListener
    
    class CancelButtonAction extends AbstractAction {
        public CancelButtonAction() {
            putValue(NAME, L10n.getString("Common.Cancel"));
            //putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SchedaTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Common.Cancel"));
        }
        
        public void actionPerformed(ActionEvent e) {
            view.setVisible(false);
        }
    }//CancelButtonListener
}

