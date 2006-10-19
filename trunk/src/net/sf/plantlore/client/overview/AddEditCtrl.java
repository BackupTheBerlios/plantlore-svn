/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import com.toedter.calendar.JCalendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import net.sf.plantlore.client.authors.AuthorManagerView;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DialogSwitcher;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.PostTaskAction;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;
import org.apache.log4j.Logger;

import net.sf.plantlore.client.checklist.ChecklistCtrl;
import net.sf.plantlore.client.checklist.ChecklistView;
import net.sf.plantlore.common.AutoComboBoxNG3;
import net.sf.plantlore.common.AutoTextArea;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author reimei
 *@author kaimu
 */
public class AddEditCtrl {
    private Logger logger;
    private boolean inEditMode = false;
    private boolean inAddMode = true;
    private AddEdit model;
    private AddEditView view;
    private AuthorManagerView authView;
    private TransformationChangeView transformationView;
    public final static int MAXIMUM_FRACTION_DIGITS = 3;
    public final static int MAXIMUM_INTEGER_DIGITS = 9;
    private final static Color COLOR_INVALID = Color.RED;
    
    private RememberDefaultValuesAction defaults = new RememberDefaultValuesAction();
    
    //--------------MODELS AND VIEWS THIS CONTROLLER CREATES-----------------
    private ChecklistView checklistView;
    
    
    /** 
     * Creates a new instance of AddEditCtrl 
     *TODO: Make proper Javadoc here!
     * authView will be opened if the User wishes to edit the list of Authors
     */
    public AddEditCtrl(AddEdit model, AddEditView view, boolean edit) {
        this.inEditMode = edit;
        this.inAddMode = ! edit;
        this.model = model;
        this.view = view;
        this.authView = null;
        
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
        view.authButton.setAction(new AuthorManagerAction());
        view.rememberButton.setAction( defaults );
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
    
    
    /**
     * Store the AuthorManagerView that should be opened if the User wants to
     * switch to the Author Manager from this Add/Edit dialog.
     */
    public void setAuthorManager(AuthorManagerView m) {
        this.authView = m;
    }
    
    /**
     * Switch between the Add/Edit dialog and the Author Manager
     *
     */
    class AuthorManagerAction extends AbstractAction {
        
        public AuthorManagerAction() {
            putValue(NAME, L10n.getString("Overview.MenuDataAuthors"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Overview.MenuDataAuthorsTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.MenuDataAuthors"));
        }
        
        public void actionPerformed(ActionEvent isUseless) {
            // Do nothing if the Author manager View is not supplied.
            if( authView != null )
                DialogSwitcher.switchFromTo(view, authView);
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
            nf.setGroupingUsed(false);
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
            nf.setGroupingUsed(false);
        }

        public void processEvent(DocumentEvent e) {
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
            nf.setGroupingUsed(false);
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
                                Task task = model.storeRecord(true);
                                task.setPostTaskAction(new PostTaskAction() {
                                    public void afterStopped(Object value) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                view.setVisible(false);
                                            }
                                        });
                                    }
                                });
                                Dispatcher.getDispatcher().dispatch(task, view, false);
                                break;
                            case 1:
                                task = model.storeRecord(false);
                                task.setPostTaskAction(new PostTaskAction() {
                                    public void afterStopped(Object value) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                view.setVisible(false);
                                            }
                                        });
                                    }
                                });
                                Dispatcher.getDispatcher().dispatch(task, view, false);
                                break;
                            case 2:
                            default:                        
                                //we'll do nothing and leave the AddEdit dialog visible
                        }
                    } else {
                            Task task = model.storeRecord(true);
                            task.setPostTaskAction(new PostTaskAction() {
                                public void afterStopped(Object value) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            view.setVisible(false);
                                        }
                                    });
                                }
                            });
                            Dispatcher.getDispatcher().dispatch(task, view, false);
                    }
                } else {//inAddMode
                    Task task = model.storeRecord(true);
                    /*
                     * The following announcement should be removed (it was a RFE)..
                     *
                    task.setPostTaskAction(new PostTaskAction() {
                        public void afterStopped(Object value) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    JOptionPane.showMessageDialog(view,L10n.getString("AddEdit.AddConfirmation"),L10n.getString("AddEdit.AddConfirmationTitle"),JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }
                    });
                    Dispatcher.getDispatcher().dispatch(task, view, false);
                     *
                     */
                    //view.setVisible(false);
                }
            } catch (RemoteException ex) {
                logger.error("Remote problem: "+ex);
                DefaultExceptionHandler.handle(view, ex);
                return;
            } catch (DBLayerException ex) {
                logger.error("Database problem: "+ex);
                DefaultExceptionHandler.handle(view, ex);
                return;
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
            
            if (view.checkValueCoordinate()) {
                transformationView.setVisible(true);         
                if (model.getIsCancle()) return;
                model.setIsCancle(true);
                view.loadComponetCoordinate();
            }
        }
    }    
    
    
    /**
     * This action stores the default values in the dialog for future revival.
     * The revival is planned after the application is restarted (because there is another mechanism
     * that preserves the currently inserted values in some fields of the dialog - implemented by Jakub Kotowski).
     *
     * @author Erik Kratochvíl
     */
    class RememberDefaultValuesAction extends StandardAction {
        
        private String defaultValuesFileName; {
		String userHome = System.getProperty("user.home"),
		osName = System.getProperty("os.name"),
		plantloreDirName = (osName.equals("Linux") ? "." : "") + net.sf.plantlore.client.Plantlore.PLANTLORE, 
		plantloreConfDir = userHome+File.separator+plantloreDirName;
		
		File plantloreConfDirFile = new File(plantloreConfDir);
		if (!plantloreConfDirFile.exists())
			plantloreConfDirFile.mkdir();
		
		defaultValuesFileName = plantloreConfDir + File.separator + "add-default";
	}
        
        /*
         * The hashtable stores default values for different databases (these databases are
         * recognized by their Unique Identifier).
         *
         * Thus the default values may differ for each database the User works with.
         * It was a necessary step because some of the stored values may not be in the other database at all.
         */
        private Hashtable<String, InterestingFields> storedValues = new Hashtable<String, InterestingFields>(8);
        
        /**
         * Create a new Action that is capable of remembering the list of default values.
         * Also note that when first constructed it restores the state of the dialog to its previous state.
         */
        RememberDefaultValuesAction() {
            super("Add.Remember");
        }

        /**
         * Load the table with default values.
         */
        private void load()
        throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream( new FileInputStream(defaultValuesFileName) );
		storedValues = (Hashtable<String, InterestingFields>) ois.readObject();
		ois.close();
	}
            
        /**
         * Store the table with default values.
         */
        private void save()
        throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(defaultValuesFileName) );
		oos.writeObject( storedValues );
		oos.close();
	}
          
        /**
         * @return null if the string is null or empty (ie. "")
         */
        private String evaluate(String s) {
            if( "".equals(s) ) 
                return null;
            return s;
        }
        
        /**
         * @return null if the object is not a pair or if the object  is null
         */
        private Pair<String, Integer> evaluate(Object o) {
            if( o == null || !(o instanceof Pair) )
                return null;
            return (Pair<String, Integer>) o;
        }
        
        /**
         * Restore the state of the most important fields.
         */
        public void restore(boolean reload) {
            try {
                if(reload)
                    load();
                
                if( storedValues == null )
                    // There are no stored values yet.
                    storedValues = new Hashtable<String, InterestingFields>(8);

                // Look for default values that were saved for the database we are currently working with.
                String databaseID = model.getDatabase().getUniqueDatabaseIdentifier();
                if( databaseID == null)
                    // We are propably not connected to a database yet. This should never happen.
                    return;

                // Obtain the default values.
                InterestingFields defaults = storedValues.get( databaseID );
                if( defaults == null)
                    // No default values stored for this particular database. Never mind.
                    return;

                // Now, do some reviving.
                // The trouble is, that sometimes (mostly with the fields monitored with focus listeners) the model would have to be
                // notified manually. Shame (that Swing doesn't provide a unified interface for some changes..). Let's go!
                if(defaults.territory != null)
                    view.territoryNameCombo.setSelectedItem( defaults.territory );
                if(defaults.phytochorion != null)
                    view.phytNameCombo.setSelectedItem( defaults.phytochorion );
                if(defaults.town != null)
                    view.townComboBox.setSelectedItem( defaults.town );
                if(defaults.project != null)
                    view.projectCombo.setSelectedItem( defaults.project );
                if(defaults.publication != null)
                    view.publicationCombo.setSelectedItem( defaults.publication );

                if(defaults.quadrant != null)
                    view.quadrantTextField.setText( defaults.quadrant );
                if(defaults.country != null)
                    view.phytCountryCombo.setSelectedItem( defaults.country );
                if(defaults.herbarium != null)
                    view.herbariumTextField.setText( defaults.herbarium );
                if(defaults.source != null)
                    view.sourceCombo.setSelectedItem(defaults.source);
                if(defaults.latitude != null)
                    view.latitudeTextField.setText( defaults.latitude );
                if(defaults.altitude != null)
                    view.altitudeTextField.setText( defaults.altitude );
                if(defaults.longitude != null)
                    view.longitudeTextField.setText( defaults.longitude );
                if(defaults.time != null)
                    view.timeTextField.setText( defaults.time );
                if(defaults.month != null)
                    view.monthChooser.setMonth( defaults.month );
                if(defaults.day != null)
                    view.dayTextField.setText( defaults.day );
                if(defaults.year != null)
                    view.yearSpinner.setValue( defaults.year );

                if(defaults.description != null)
                    view.descriptionArea.setText( defaults.description );
                if(defaults.locationNote != null)
                    view.locationNoteArea.setText( defaults.locationNote );
                if(defaults.occurrenceNote != null)
                    view.occurrenceNoteArea.setText( defaults.occurrenceNote );
            } catch (Exception e) {
                logger.error("Unable to restore default values! " + e.getMessage());
            }
                    
        }
        
        /**
         * Remember the state of the most important fields.
         */
        public void remember() {
            try {
                // Gather the information from dialogs.
                String databaseID = model.getDatabase().getUniqueDatabaseIdentifier();
                storedValues.remove( databaseID );
                InterestingFields defaults = new InterestingFields();

                defaults.territory = evaluate( view.territoryNameCombo.getSelectedItem() );
                defaults.phytochorion = evaluate( view.phytNameCombo.getSelectedItem() );
                defaults.town = evaluate( view.townComboBox.getSelectedItem() );
                defaults.publication = evaluate( view.publicationCombo.getSelectedItem() );
                defaults.project = evaluate( view.projectCombo.getSelectedItem() );
                defaults.quadrant = evaluate( view.quadrantTextField.getText() );
                defaults.country = evaluate( (String) view.phytCountryCombo.getSelectedItem() );
                defaults.herbarium = evaluate( view.herbariumTextField.getText() );
                defaults.source = evaluate( (String) view.sourceCombo.getSelectedItem() );
                defaults.latitude = evaluate( view.latitudeTextField.getText() );
                defaults.longitude = evaluate( view.longitudeTextField.getText() );
                defaults.altitude = evaluate( view.altitudeTextField.getText() ); 
                defaults.time = evaluate( view.timeTextField.getText() );
                defaults.day = evaluate( view.dayTextField.getText() );
                defaults.month = view.monthChooser.getMonth();
                defaults.year = (Integer) view.yearSpinner.getValue();
                defaults.description = evaluate( view.descriptionArea.getText() );
                defaults.locationNote = evaluate( view.locationNoteArea.getText() );
                defaults.occurrenceNote = evaluate( view.occurrenceNoteArea.getText() );

                storedValues.put( databaseID, defaults );
                // Store them.
                save(); 
            } catch(Exception e) {
                //logger.error("Unable to remember default values! " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        /**
         * Forget the state of the most important fields.
         */
        public void forget() {
            try {
                String databaseID = model.getDatabase().getUniqueDatabaseIdentifier();
                storedValues.remove( databaseID );
                save();
            } catch(Exception e) {
                logger.error("Unable to forget default values! " + e.getMessage());
            }
        }

        /**
         * Take action = remember the state of fields.
         */
        public void actionPerformed(ActionEvent e) {
            logger.debug("Trying to memorize the default values for this dialog.");
            remember();
        }
    }

        
    
}



