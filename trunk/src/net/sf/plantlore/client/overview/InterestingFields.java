package net.sf.plantlore.client.overview;

import net.sf.plantlore.common.Pair;

/**
 * The holder object of most important fields in the dialog whose state should be stored.
 * The list of currently selected authors is not saved because that component may be a subject to change.
 *
 * This class must cannot be an inner class because then that inner class would become
 * an object of serialization as well!
 *
 * @author kaimu
 */
public class InterestingFields implements java.io.Serializable {
    public Pair<String, Integer> territory, phytochorion, town, publication, project;
    public String description, locationNote, occurrenceNote, latitude, longitude, altitude, country, time, day, quadrant, herbarium, source;
    public Integer month, year;
}