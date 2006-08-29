/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import com.toedter.calendar.JCalendar;
import net.sf.plantlore.client.*;

import net.sf.plantlore.client.checklist.ChecklistCtrl;
import net.sf.plantlore.client.checklist.ChecklistView;
import net.sf.plantlore.common.AutoComboBox;
import net.sf.plantlore.common.AutoComboBoxNG3;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.DefaultReconnectDialog;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author reimei
 */
public class AddEditCtrl {
    private Logger logger;
    private boolean inEditMode = false;
    private boolean inAddMode = true;
    private AddEdit model;
    private AddEditView view;
    private TransformationChangeView transformationView;
    public final static int MAXIMUM_FRACTION_DIGITS = 8;
    public final static int MAXIMUM_INTEGER_DIGITS = 8;
    private final static Color COLOR_INVALID = Color.RED;
    
    //--------------MODELS AND VIEWS THIS CONTROLLER CREATES-----------------
    private ChecklistView checklistView;
    
    
    /** Creates a new instance of AddEditCtrl */
    public AddEditCtrl(AddEdit model, AddEditView view, boolean edit) {
        this.inEditMode = edit;
        this.inAddMode = ! edit;
        this.model = model;
        this.view = view;
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());                

        new DefaultEscapeKeyPressed(view, new CancelButtonAction());
        
        //------- ComboBoxes --------
        view.townComboBox.addActionListener(new CommonActionListener());
        view.territoryNameCombo.addActionListener(new CommonActionListener());
        view.phytNameCombo.addActionListener(new CommonActionListener());
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
        view.taxonTextArea.addPropertyChangeListener(AutoTextArea.UPDATE_LIST_OF_PLANTS, new TaxonAreaListener());
        view.descriptionArea.addFocusListener(new PlaceAreaListener());
        view.locationNoteArea.addFocusListener(new LocationAreaListener());
        view.occurrenceNoteArea.addFocusListener(new OccurrenceAreaListener());

        //------- Spinners --------
        view.yearSpinner.addChangeListener(new YearListener());
        view.monthChooser.addPropertyChangeListener("month", new MonthChangeListener());
        view.dayTextField.getDocument().addDocumentListener(new DayListener());
        view.timeTextField.getDocument().addDocumentListener(new TimeListener());
                
        //------- RadioButtons --------
//        view.WGS84Button.addActionListener(new CoordinateSystemListener());
//        view.S42Button.addActionListener(new CoordinateSystemListener());
//        view.SJTSKButton.addActionListener(new CoordinateSystemListener());
        
        //------- Buttons --------
        view.okButton.setAction(new OkButtonAction());
        view.cancelButton.setAction(new CancelButtonAction());
        view.checklistButton.setAction(new ChecklistAction());        
        view.clearLocationButton.setAction(new ClearLocationAction());
        view.clearOccurrenceButton.setAction(new ClearOccurrenceAction());
        view.calendarButton.setAction(new CalendarAction());
        view.settingsButton.setAction(new SettingsAction());
        view.gpsChangeButton.setAction(new ChangeCoordinateSystemAction());
//        view.preloadAuthorsCheckBox.addActionListener(new PreloadCheckBox());
    }
    
    
    class ChecklistAction extends AbstractAction {
    	public ChecklistAction() {
    		putValue(NAME, L10n.getString("Checklist.Title")); 
            putValue(SHORT_DESCRIPTION, L10n.getString("Checklist.Description"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Checklist.Title"));
    	}
		public void actionPerformed(ActionEvent isUseless) {
			if(checklistView == null) {
				checklistView = new ChecklistView(view, (AutoTextArea) view.taxonTextArea );
				new ChecklistCtrl( checklistView, (AutoTextArea) view.taxonTextArea );
			}
			checklistView.setVisible(true);			
		}
    }
    
    
    class CommonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox c = null;
            
            c  = (JComboBox) e.getSource();
            
            String command = e.getActionCommand();
            
            
            //------- ComboBoxes --------            
//            if (command.equals("authorComboBox"))
//                model.setAuthor((Pair<String, Integer>) c.getSelectedItem());
             
            //we have to check whether the selected item is of the right type
            //because of the possibility that the choices set to the combobox were null
            //and therefore it by default created only one item with an empty string
            if (command.equals("townComboBox") && (c.getSelectedItem() instanceof Pair))
                model.setVillage((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("territoryNameCombo") && (c.getSelectedItem() instanceof Pair))
                model.setTerritoryName((Pair<String, Integer>) c.getSelectedItem());

            if (command.equals("phytNameCombo") && (c.getSelectedItem() instanceof Pair))
                model.setPhytName((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("phytCodeCombo") && (c.getSelectedItem() instanceof Pair))
                //System.out.println("c.getSelectedItem(): "+c.getSelectedItem());
                model.setPhytCode((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("phytCountryCombo") && (((AutoComboBoxNG3)c).getValue() instanceof String))
                model.setPhytCountry((String) ((AutoComboBoxNG3)c).getValue());

            if (command.equals("sourceCombo") && (((AutoComboBoxNG3)c).getValue() instanceof String))
                model.setSource((String) ((AutoComboBoxNG3)c).getValue());
           
            if (command.equals("publicationCombo") && (c.getSelectedItem() instanceof Pair))
                model.setPublication((Pair<String, Integer>) c.getSelectedItem());
           
            if (command.equals("projectCombo") && (c.getSelectedItem() instanceof Pair))
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
    
    class TaxonAreaListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
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
            model.setHabitatDescription(ta.getText());
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
    
    class YearListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSpinner s = (JSpinner) e.getSource();
            model.setYear((Integer)s.getValue());
        }
    }//YearListener

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
    
    class DayListener implements DocumentListener {
        Color oldColor;
        public DayListener() {
            oldColor = view.dayTextField.getForeground();
        }
        
        public void processEvent(DocumentEvent e) {
            if (e.getDocument().getLength() == 0) {
                model.setDay(null);
                return;
            }
            String text = null;
            try { text = e.getDocument().getText(0, e.getDocument().getLength()); } 
            catch (BadLocationException ex) { 
                logger.warn("DayChangeListener in AddEditCtrl: unexpected BadLocationException: "+ex); 
                return;//shouldn't happen, just ignore it }
            }
            
            if (text.trim().equals("")) {
                model.setDay(null);
                return;
            }

            int day;
            try {
                day = Integer.parseInt(text);
            } catch (Exception ex) {
                model.setDayValid(false);
                view.dayTextField.setForeground(COLOR_INVALID);
                return;
            }
            
            //we can do date validation only after the whole date has been set
            //that means we should do it in AddEdit.storeRecord()
            if (day > 0 && day <= 31) {
                model.setDay(day);
            } else {
                view.dayTextField.setForeground(COLOR_INVALID);
                model.setDayValid(false);
                return;                
            }
            view.dayTextField.setForeground(oldColor);
        }//DayListener
        
        public void insertUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void removeUpdate(DocumentEvent e) {
            processEvent(e);
        }

        public void changedUpdate(DocumentEvent e) {
            processEvent(e);
        }        
    }
    
    class TimeListener implements DocumentListener {
        Color oldColor;
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        Calendar c = Calendar.getInstance();
        
        public TimeListener() {
            oldColor = view.timeTextField.getForeground();
        }
        
        public void processEvent(DocumentEvent e) {
            if (e.getDocument().getLength() == 0) {
                model.setTime(null);
                return;
            }
            String text = null;
            try { text = e.getDocument().getText(0, e.getDocument().getLength()); } 
            catch (BadLocationException ex) { 
                logger.warn("DayChangeListener in AddEditCtrl: unexpected BadLocationException: "+ex); 
                return;//shouldn't happen, just ignore it }
            }
            
            if (text.trim().equals("")) {
                model.setTime(null);
                return;
            }
            
            Date value;
            try {
                value = df.parse(text);
                model.setTime(value);
            } catch (ParseException ex) {
                model.setTimeValid(false);
                view.timeTextField.setForeground(COLOR_INVALID);
                return;
            }
            view.timeTextField.setForeground(oldColor);
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
    }//TimeListener
    
    class MonthChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Integer i = (Integer) evt.getNewValue();
            if (!i.equals(12)) { //there is an empty string at the 12th position in the combobox
                model.setMonth(i);
                //view.dayChooser.setEnabled(true);
            } else {
                model.setMonth(null);
                //view.dayChooser.setEnabled(false);
            }
        } 
    }
    
    class CalendarDayChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Integer i = (Integer) evt.getNewValue();
            //model.setDay(i);
            view.dayTextField.setText(""+i);
        } 
    }

    class CalendarMonthChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Integer i = (Integer) evt.getNewValue();
            //model.setDay(i);
            if (i != null)
                view.monthChooser.setMonth(i);
        } 
    }
    
    class CalendarYearChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Integer i = (Integer) evt.getNewValue();
            //model.setYear(i);
            if (i != null)
                view.yearSpinner.setValue(i);
        } 
    }
    
    class OkButtonAction extends AbstractAction {
        private String ALL = L10n.getString("AddEdit.All");
        private String JUST_THIS = L10n.getString("AddEdit.JustThis");
        private String CANCEL = L10n.getString("Common.Cancel");
        private String TITLE = L10n.getString("AddEdit.QuestionDialogTitle");
        
        public OkButtonAction() {
            putValue(NAME, L10n.getString("Common.Ok"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Common.Ok"));
        }
        
        public void actionPerformed(ActionEvent e) {
            int choice=-1;
            try {
                Pair<Boolean,String> check = model.checkData();
                if (!check.getFirst()) {
                    JOptionPane.showMessageDialog(view,check.getSecond(),L10n.getString("AddEdit.CheckFailedTitle"),JOptionPane.INFORMATION_MESSAGE);
                    return;
                } 
                if (inEditMode) {
                    Occurrence[] sharedOcc = model.getHabitatSharingOccurrences();
                    if (sharedOcc.length > 1 && model.hasHabitatChanged()) //the habitat has changed and is shared by at least two occurrences
                                                                           //we have to ask the user whether he wants to make the change for all occurrences
                                                                           //or to create a new habitat
                    {
                        Object[] arg = {""+(sharedOcc.length-1)};
                        String formattedQuestion = L10n.getFormattedString("AddEdit.OkQuestion",arg);

                        Object[] options = {ALL,JUST_THIS,CANCEL};
                        choice = JOptionPane.showOptionDialog(view, 
                                formattedQuestion,
                                TITLE,
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]);
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
                    JOptionPane.showMessageDialog(view,L10n.getString("AddEdit.AddConfirmation"),L10n.getString("AddEdit.AddConfirmationTitle"),JOptionPane.INFORMATION_MESSAGE);
                    //view.setVisible(false);
                }
            } catch (RemoteException ex) {
                logger.error("Remote problem: "+ex);
                ex.printStackTrace();
                DefaultReconnectDialog.show(view,ex);
            } catch (DBLayerException ex) {
                logger.error("Database problem: "+ex);
                ex.printStackTrace();
                DefaultReconnectDialog.show(view,ex);
            }
        }//mouseClicked
            
    }//OkButtonListener
    
    class CancelButtonAction extends AbstractAction {
        public CancelButtonAction() {
            putValue(NAME, L10n.getString("Common.Cancel"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Common.Cancel"));            
        }
        
        public void actionPerformed(ActionEvent e) {
            logger.info("AddEdit dialog cancelled");
            view.setVisible(false);
        }
    }//CancelButtonListener
    
    class ClearLocationAction extends AbstractAction {
        public ClearLocationAction() {
            putValue(NAME, L10n.getString("AddEdit.ClearLocation"));
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.ClearLocationTT"));                        
        }

        public void actionPerformed(ActionEvent e) {
            model.clearLocation();
        }
    }
    
    class ClearOccurrenceAction extends AbstractAction {
        public ClearOccurrenceAction() {
            putValue(NAME, L10n.getString("AddEdit.ClearOccurrence"));
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.ClearOccurrenceTT"));                        
        }

        public void actionPerformed(ActionEvent e) {
            model.clearOccurrence();
        }
    }
    
    class PreloadCheckBox implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JCheckBox cb = (JCheckBox) e.getSource();
            model.setPreloadAuthorsEnabled(cb.isSelected());
        }        
    }
    
    /**
     *
     */
    class CalendarAction extends AbstractAction {
        JDialog calendarDialog;
        public CalendarAction() {
            putValue(NAME, L10n.getString("AddEdit.Calendar"));
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.CalendarTT"));   
            calendarDialog = new JDialog(view);
            BorderLayout bl = new BorderLayout();
            calendarDialog.setLayout(bl);            
            JCalendar calendar = new JCalendar();
            calendarDialog.add(calendar,BorderLayout.CENTER);
            calendarDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            calendarDialog.pack();
            calendarDialog.setLocationRelativeTo(view);
            DefaultEscapeKeyPressed dekp = new DefaultEscapeKeyPressed(calendarDialog);
            
            calendar.getYearChooser().addPropertyChangeListener("year",new CalendarYearChangeListener());
            calendar.getMonthChooser().addPropertyChangeListener("month",new CalendarMonthChangeListener());
            calendar.getDayChooser().addPropertyChangeListener("day",new CalendarDayChangeListener());
        }
        public void actionPerformed(ActionEvent e) {
            calendarDialog.setVisible(true);
        }
    }//CalendarAction
    
    class SettingsAction extends AbstractAction {
        AddEditSettingsView settingsView;
        
        public SettingsAction() {
            putValue(NAME, L10n.getString("AddEdit.Settings"));
            putValue(SHORT_DESCRIPTION, L10n.getString("AddEdit.SettingsTT"));   
            AddEditSettings sm = new AddEditSettings(view);
            settingsView = new AddEditSettingsView(view,false,sm);
            AddEditSettingsCtrl cm = new AddEditSettingsCtrl(sm,settingsView);
        }
        public void actionPerformed(ActionEvent e) {
            settingsView.setVisible(true);
        }
    }//class SettingsAction
    
    class ChangeCoordinateSystemAction extends AbstractAction {
        
        public ChangeCoordinateSystemAction() {
            putValue(NAME, L10n.getString("Common.GpsChange"));
            transformationView = new TransformationChangeView(view, model, true);
            new TransformationChangeCtrl(model, transformationView); 
        }
        public void actionPerformed(ActionEvent e) {                       
            transformationView.setVisible(true);         
            if (model.getIsCancle()) return;
            model.setIsCancle(true);
            view.loadComponetCoordinate();
        }
    }
}



