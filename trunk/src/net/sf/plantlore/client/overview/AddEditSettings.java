/*
 * AddEditSettings.java
 *
 * Created on 25. srpen 2006, 15:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.overview;

import java.util.EnumSet;
import java.util.Set;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * @author fraktalek
 */
public class AddEditSettings {
    Logger logger = Logger.getLogger(AddEditSettings.class.getPackage().getName());
    Set<NullableField> enabledFields = EnumSet.allOf(NullableField.class);
    Set<NullableField> disabledFields = EnumSet.noneOf(NullableField.class);
    AddEditView addEditView;
    
    /** Creates a new instance of AddEditSettings */
    public AddEditSettings(AddEditView addEditView) {
        assert addEditView != null;
        this.addEditView = addEditView;
    }
    
    public enum NullableField {
        QUADRANT,
        COUNTRY,
        PLACE_DESCRIPTION,
        LOCATION_NOTE,
        GPS,
        DATE,
        TIME,
        SOURCE,
        PUBLICATION,
        HERBARIUM,
        OCCURRENCE_NOTE;
                
        public String toString() {
            return L10n.getString("AddEdit.Field."+super.toString());
        }    
    }//enum NullableField

        
        public void setEnabled(NullableField field, boolean enabled) {
            switch (field) {
                case QUADRANT:
                    addEditView.quadrantLabel.setEnabled(enabled);
                    addEditView.quadrantTextField.setEnabled(enabled);
                    break;
                case COUNTRY:
                    addEditView.phytCountryCombo.setEnabled(enabled);
                    addEditView.countryLabel.setEnabled(enabled);
                    break;
                case PLACE_DESCRIPTION:
                    addEditView.placeDescriptionLabel.setEnabled(enabled);
                    addEditView.descriptionArea.setEnabled(enabled);
                    break;
                case LOCATION_NOTE:
                    addEditView.locationNoteArea.setEnabled(enabled);
                    addEditView.locationNoteLabel.setEnabled(enabled);
                    break;
                case GPS:
                    addEditView.gpsChangeButton.setEnabled(enabled);
                    addEditView.gpsLoadButton.setEnabled(enabled);
                    addEditView.gpsShowButton.setEnabled(enabled);
                    addEditView.gpsSymbolLabel.setEnabled(enabled);
                    addEditView.gpsSymbolTextField.setEnabled(enabled);
                    addEditView.gpsTableLabel.setEnabled(enabled);
                    addEditView.altitudeLabel.setEnabled(enabled);
                    addEditView.altitudeTextField.setEnabled(enabled);
                    addEditView.latitudeLabel.setEnabled(enabled);
                    addEditView.latitudeTextField.setEnabled(enabled);
                    addEditView.longitudeLabel.setEnabled(enabled);
                    addEditView.longitudeTextField.setEnabled(enabled);
                    addEditView.coordinateSystemLabel.setEnabled(enabled);
                    break;
                case DATE:
                    addEditView.dayLabel.setEnabled(enabled);
                    addEditView.dayTextField.setEnabled(enabled);
                    addEditView.monthChooser.setEnabled(enabled);
                    addEditView.monthLabel.setEnabled(enabled);
                    break;
                case TIME:
                    addEditView.timeTextField.setEnabled(enabled);
                    addEditView.timeLabel.setEnabled(enabled);
                    break;
                case SOURCE:
                    addEditView.sourceCombo.setEnabled(enabled);
                    addEditView.sourceLabel.setEnabled(enabled);
                    break;
                case PUBLICATION:
                    addEditView.publicationCombo.setEnabled(enabled);
                    addEditView.publicationLabel.setEnabled(enabled);
                    break;
                case HERBARIUM:
                    addEditView.herbariumLabel.setEnabled(enabled);
                    addEditView.herbariumTextField.setEnabled(enabled);
                    break;
                case OCCURRENCE_NOTE:
                    addEditView.occurrenceNoteArea.setEnabled(enabled);
                    addEditView.occurrenceNoteLabel.setEnabled(enabled);
                    addEditView.occurrenceTable.setEnabled(enabled);
                    break;
            }
        }//setEnabled()
        
    public Set<NullableField> getEnabled() {
        return enabledFields;
    }
    
    public Set<NullableField> getDisabled() {
        return disabledFields;
    }
    
    public void enable(NullableField field) {
        if (field == null) {
            logger.warn("AddEditSettings: trying to do enable(null)!!!");
            return;
        }
        
        if (enabledFields.contains(field)) {
            logger.warn("AddEditSettings: trying to enable already enabled field "+field);
            return;
        }
        
        logger.debug("Disabling "+field);
        setEnabled(field,true);
        enabledFields.add(field);
        disabledFields.remove(field);
    }
    
    public void disable(NullableField field) {
        if (field == null) {
            logger.warn("AddEditSettings: trying to do disable(null)!!!");
            return;
        }
        
        if (disabledFields.contains(field)) {
            logger.warn("AddEditSettings: trying to disable already disabled field "+field);
            return;
        }
        
        logger.debug("Disabling "+field);
        setEnabled(field,false);
        disabledFields.add(field);
        enabledFields.remove(field);
    }
    
    public static void main(String[] args) {
        for (NullableField nf : NullableField.values())
            System.out.println(nf);
    }
}


