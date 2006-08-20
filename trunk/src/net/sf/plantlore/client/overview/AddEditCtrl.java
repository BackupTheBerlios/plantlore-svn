/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

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
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import net.sf.plantlore.client.*;

import net.sf.plantlore.client.checklist.ChecklistCtrl;
import net.sf.plantlore.client.checklist.ChecklistView;
import net.sf.plantlore.common.AutoComboBox;
import net.sf.plantlore.common.AutoTextArea;
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
    
    //--------------MODELS AND VIEWS THIS CONTROLLER CREATES-----------------
    private ChecklistView checklistView;
    
    
    /** Creates a new instance of AddEditCtrl */
    public AddEditCtrl(AddEdit model, AddEditView view, boolean edit) {
        this.inEditMode = edit;
        this.inAddMode = ! edit;
        this.model = model;
        this.view = view;
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());                

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
        view.altitudeTextField.addFocusListener(new AltitudeListener());
        view.longitudeTextField.addFocusListener(new LongitudeListener());
        view.latitudeTextField.addFocusListener(new LatitudeListener());
        
        //------- TextAreas --------        
        view.taxonTextArea.addPropertyChangeListener(AutoTextArea.UPDATE_LIST_OF_PLANTS, new TaxonAreaListener());
        view.descriptionArea.addFocusListener(new PlaceAreaListener());
        view.locationNoteArea.addFocusListener(new LocationAreaListener());
        view.occurrenceNoteArea.addFocusListener(new OccurrenceAreaListener());

        //------- Spinners --------
        view.yearSpinner.addChangeListener(new YearListener());
        view.monthChooser.addPropertyChangeListener("month", new MonthChangeListener());
        view.timeTextField.addFocusListener(new TimeListener());
        
        //------- RadioButtons --------
//        view.WGS84Button.addActionListener(new CoordinateSystemListener());
//        view.S42Button.addActionListener(new CoordinateSystemListener());
//        view.SJTSKButton.addActionListener(new CoordinateSystemListener());
        
        //------- Buttons --------
        view.extendedButton.addMouseListener(new ExtendedButtonListener());
        view.okButton.addMouseListener(new OkButtonListener());
        view.cancelButton.addMouseListener(new CancelButtonListener());
        view.checklistButton.setAction(new ChecklistAction());
        
        view.clearLocationButton.setAction(new ClearLocationAction());
        view.clearOccurrenceButton.setAction(new ClearOccurrenceAction());
        
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
           
            if (command.equals("phytCountryCombo") && (c.getSelectedItem() instanceof String))
                model.setPhytCountry((String) c.getSelectedItem());

            if (command.equals("sourceCombo") && (c.getSelectedItem() instanceof String))
                model.setSource((String) c.getSelectedItem());
           
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

    class AltitudeListener implements FocusListener {
        NumberFormat nf = NumberFormat.getNumberInstance( L10n.getCurrentLocale() );
        NumberFormat nfUS = NumberFormat.getNumberInstance( Locale.US );

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField tf = (JTextField) e.getSource();
            if (tf.getText().trim().equals("")) {
                model.setAltitude(null);
                return;
            }
            
            Double value;
            try {
                value = nf.parse(tf.getText()).doubleValue();
                String s = nf.format(value);
                if (!s.equals(tf.getText().trim()))
                    throw new ParseException("Parsing could stop at decimal point. Take a closer look.",0);
                model.setAltitude(value);
            } catch (ParseException ex) {
                try {
                    value = nfUS.parse(tf.getText()).doubleValue();
                    model.setAltitude(value);
                    tf.setText(nf.format(value)); //print the number using proper decimal point
                } catch (ParseException ex2) {
                    if (model.getAltitude() != null) {
                        tf.setText(""+nf.format(model.getAltitude()));
                    } else
                        tf.setText("");
                    return;
                }
            }
        }
    }//AltitudeListener

    class LatitudeListener implements FocusListener {
        NumberFormat nf = NumberFormat.getNumberInstance( L10n.getCurrentLocale() );
        NumberFormat nfUS = NumberFormat.getNumberInstance( Locale.US );

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField tf = (JTextField) e.getSource();
            if (tf.getText().trim().equals("")) {
                model.setLatitude(null);
                return;
            }
            
            Double value;
            try {
                value = nf.parse(tf.getText()).doubleValue();
                String s = nf.format(value);
                if (!s.equals(tf.getText().trim()))
                    throw new ParseException("Parsing could stop at decimal point. Take a closer look.",0);
                model.setLatitude(value);
            } catch (ParseException ex) {
                try {
                    value = nfUS.parse(tf.getText()).doubleValue();
                    tf.setText(nf.format(value)); //print the number using proper decimal point
                    model.setLatitude(value);                    
                } catch (ParseException ex2) {
                    if (model.getLatitude() != null)
                        tf.setText(""+model.getLatitude());
                    else
                        tf.setText("");
                    return;
                }
            }
        }
    }//LatitudeListener

    class LongitudeListener implements FocusListener {
        NumberFormat nf = NumberFormat.getNumberInstance( L10n.getCurrentLocale() );
        NumberFormat nfUS = NumberFormat.getNumberInstance( Locale.US );
        
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField tf = (JTextField) e.getSource();
            if (tf.getText().trim().equals("")) {
                model.setLongitude(null);
                return;
            }
            
            Double value;
            try {
                value = nf.parse(tf.getText()).doubleValue();
                String s = nf.format(value);
                if (!s.equals(tf.getText().trim()))
                    throw new ParseException("Parsing could stop at decimal point. Take a closer look.",0);
                model.setLongitude(value);
            } catch (ParseException ex) {
                try {
                    value = nfUS.parse(tf.getText()).doubleValue();
                    tf.setText(nf.format(value)); //print the number using proper decimal point
                    model.setLongitude(value);                    
                } catch (ParseException ex2) {
                    if (model.getLongitude() != null)
                        tf.setText(""+model.getLongitude());
                    else
                        tf.setText("");
                    return;
                }
            }
        }
    }//LongitudeListener
    
    class TimeListener implements FocusListener {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        Calendar c = Calendar.getInstance();
        
        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            JTextField tf = (JTextField) e.getSource();
            if (tf.getText().trim().equals("")) {
                model.setTime(null);
                return;
            }
            
            Date value;
            try {
                value = df.parse(tf.getText());
                model.setTime(value);
            } catch (ParseException ex) {
                if (model.getTime() != null) {
                    c.setTime(model.getTime());
                    tf.setText(""+c.get(Calendar.HOUR_OF_DAY)+":"+Calendar.MINUTE);
                } else
                    tf.setText("");
                return;
            }
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
    
    class DayChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            model.setDay((Integer) evt.getNewValue());
        } 
    }
    
    class ExtendedButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            view.switchExtended();
        }
    }//ExtendedButtonListener
    
    class OkButtonListener extends MouseAdapter {
        private String ALL = L10n.getString("AddEdit.All");
        private String JUST_THIS = L10n.getString("AddEdit.JustThis");
        private String CANCEL = L10n.getString("Common.Cancel");
        private String TITLE = L10n.getString("AddEdit.QuestionDialogTitle");
        
        public void mouseClicked(MouseEvent e) {
            int choice=-1;
            try {
                Pair<Boolean,String> check = model.checkData();
                if (!check.getFirst()) {
                    JOptionPane.showMessageDialog(view,check.getSecond());
                    return;
                } 
                if (inEditMode) {
                    Occurrence[] sharedOcc = model.getHabitatSharingOccurrences();
                    if (sharedOcc.length > 1) {
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
                    view.setVisible(false);
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
    
    class CancelButtonListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            System.out.println("Cancel");
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
}

