package net.sf.plantlore.client.export;

import java.io.IOException;

import net.sf.plantlore.client.export.Template;
import net.sf.plantlore.common.record.*;


/**
 * MetaBuilder. Hard-wired database scheme = changes of the database model will
 * affect this class directly (the code will have to be changed accordingly).
 * Fast, but not flexible (other permutations of columns aren't possible).
 * <br/>
 * MetaBuilder is a partial implementation of the Builder interface
 * and implements the most annoying and always-repeating parts:
 * the traversal through all tables and their columns.
 * For each column, that has to be exported, 
 * the <code>w()</code> method is called.
 * <br/>
 * <code>w() </code> is the only method that has to be implemented;
 * it says how the [table, column, value] should be written to the output.
 * 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 * @see net.sf.plantlore.client.export.MetaBuilder#w(Class, String, Object)
 * @see net.sf.plantlore.client.export.RecordWalkBuilder
 */
public abstract class MetaBuilder implements Builder {
	
	/** 
	 * The particular template that is used while building the output.
	 * @see net.sf.plantlore.client.export.Template
	 */ 
	private Template tmp;
	
	/**
	 * "Create" a new MetaBuilder. Since MetaBuilder is an abstract class,
	 * you cannot instantiate it.
	 * 
	 * @param tmp The template that is used to decide, whether a particular
	 * 		table interests us or not.
	 * @see net.sf.plantlore.client.export.Template
	 */
	public MetaBuilder(Template tmp) {
		this.tmp = tmp;
	}
	
	
	/**
	 * An implementation of the Builder::writeRecord().
	 * It completely traverses the record starting with <code>args[0]</code>
	 * as a root table. It traverses all tables that are seleted in the Template. 
	 */
	public void writeRecord(Record[] args) throws IOException {
		Record r = args[0];
		if(r instanceof Author) process( (Author)r );
		else if(r instanceof AuthorOccurrence) process( (AuthorOccurrence)r );
		else if(r instanceof Habitat) process( (Habitat)r );
		else if(r instanceof Metadata) process( (Metadata)r );
		else if(r instanceof Occurrence) process( (Occurrence)r );
		else if(r instanceof Phytochorion) process( (Phytochorion)r );
		else if(r instanceof Plant) process( (Plant)r );
		else if(r instanceof Publication) process( (Publication)r );
		else if(r instanceof Territory) process( (Territory)r );
		else if(r instanceof Village) process( (Village)r );
		else /* ERROR! */;
	}

	/** Process the table of Authors. */
	protected void process(Author a) throws IOException {
		if (!tmp.isSetTableD(Author.class))
			return; // prevent recursion!
		w(Author.class, Author.WHOLENAME, a.getWholeName());
		w(Author.class, Author.ORGANIZATION, a.getOrganization());
		w(Author.class, Author.ADDRESS, a.getAddress());
		w(Author.class, Author.EMAIL, a.getEmail());
		w(Author.class, Author.PHONENUMBER, a.getPhoneNumber());
		w(Author.class, Author.URL, a.getUrl());
		w(Author.class, Author.ROLE, a.getRole());
		w(Author.class, Author.NOTE, a.getNote());
	}

	/** Process the table of AuthorsOccurences. */
	protected void process(AuthorOccurrence a) throws IOException {
		if (!tmp.isSetTableD(AuthorOccurrence.class))
			return; // prevent recursion!
		process(a.getAuthor());
		process(a.getOccurrence());
		w(AuthorOccurrence.class, AuthorOccurrence.ROLE, a.getRole());
		w(AuthorOccurrence.class, AuthorOccurrence.RESULTREVISION, a
				.getResultRevision());
	}

	/** Process the table of Habitats. */
	protected void process(Habitat a) throws IOException {
		if (!tmp.isSetTableD(Habitat.class))
			return; // prevent recursion!
		process(a.getTerritory());
		process(a.getPhytochorion());
		process(a.getNearestVillage());
		w(Habitat.class, Habitat.DESCRIPTION, a.getDescription());
		w(Habitat.class, Habitat.COUNTRY, a.getCountry());
		w(Habitat.class, Habitat.NOTE, a.getNote());
		w(Habitat.class, Habitat.QUADRANT, a.getQuadrant());
		w(Habitat.class, Habitat.ALTITUDE, a.getAltitude());
		w(Habitat.class, Habitat.LATITUDE, a.getLatitude());
		w(Habitat.class, Habitat.LONGITUDE, a.getLongitude());
	}

	/** Process the table of Metadata. */
	protected void process(Metadata a) throws IOException {
		if (!tmp.isSetTableD(Metadata.class))
			return; // prevent recursion!
		w(Metadata.class, Metadata.CONTENTCONTACTNAME, a
				.getContentContactName());
		w(Metadata.class, Metadata.CONTENTCONTACTADDRESS, a
				.getContentContactAddress());
		w(Metadata.class, Metadata.CONTENTCONTACTEMAIL, a
				.getContentContactEmail());
		w(Metadata.class, Metadata.DATASETTITLE, a.getDataSetTitle());
		w(Metadata.class, Metadata.DATASETDETAILS, a.getDataSetDetails());
		w(Metadata.class, Metadata.DATECREATE, a.getDateCreate());
		w(Metadata.class, Metadata.DATEMODIFIED, a.getDateModified());
		w(Metadata.class, Metadata.OWNERORGANIZATIONABBREV, a
				.getOwnerOrganizationAbbrev());
		w(Metadata.class, Metadata.RECORDBASIS, a.getRecordBasis());
		w(Metadata.class, Metadata.SOURCEINSTITUTIONID, a
				.getSourceInstitutionId());
		w(Metadata.class, Metadata.SOURCEID, a.getSourceId());
		w(Metadata.class, Metadata.TECHNICALCONTACTNAME, a
				.getTechnicalContactName());
		w(Metadata.class, Metadata.TECHNICALCONTACTADDRESS, a
				.getTechnicalContactAddress());
		w(Metadata.class, Metadata.TECHNICALCONTACTEMAIL, a
				.getTechnicalContactEmail());
		w(Metadata.class, Metadata.VERSIONPLANTSFILE, a.getVersionPlantsFile());
	}

	/** Process the table of Occurences. */
	protected void process(Occurrence a) throws IOException {
		if (!tmp.isSetTableD(Occurrence.class))
			return; // prevent recursion!
		process(a.getPlant());
		process(a.getHabitat());
		process(a.getMetadata());
		process(a.getPublication());
		w(Occurrence.class, Occurrence.UNITIDDB, a.getUnitIdDb());
		w(Occurrence.class, Occurrence.UNITVALUE, a.getUnitValue());
		w(Occurrence.class, Occurrence.DAYCOLLECTED, a.getDayCollected());
		w(Occurrence.class, Occurrence.MONTHCOLLECTED, a.getMonthCollected());
		w(Occurrence.class, Occurrence.YEARCOLLECTED, a.getYearCollected());
		w(Occurrence.class, Occurrence.TIMECOLLECTED, a.getTimeCollected());
		w(Occurrence.class, Occurrence.ISODATETIMEBEGIN, a
				.getIsoDateTimeBegin());
		w(Occurrence.class, Occurrence.DATASOURCE, a.getDataSource());
		w(Occurrence.class, Occurrence.HERBARIUM, a.getHerbarium());
		w(Occurrence.class, Occurrence.NOTE, a.getNote());
		w(Occurrence.class, Occurrence.CREATEDWHEN, a.getCreatedWhen());
		w(Occurrence.class, Occurrence.UPDATEDWHEN, a.getUpdatedWhen());
	}

	/** Process the table of Phytochoria. */
	protected void process(Phytochorion a) throws IOException {
		if (!tmp.isSetTableD(Phytochorion.class))
			return; // prevent recursion!
		w(Phytochorion.class, Phytochorion.NAME, a.getName());
		w(Phytochorion.class, Phytochorion.CODE, a.getCode());
	}

	/** Process the table of Plants. */
	protected void process(Plant a) throws IOException {
		if (!tmp.isSetTableD(Plant.class))
			return; // prevent recursion!
		w(Plant.class, Plant.TAXON, a.getTaxon());
		w(Plant.class, Plant.SCIENTIFICNAMEAUTHOR, a.getScientificNameAuthor());
		w(Plant.class, Plant.CZECHNAME, a.getCzechName());
		w(Plant.class, Plant.GENUS, a.getGenus());
		w(Plant.class, Plant.SPECIES, a.getSpecies());
		w(Plant.class, Plant.SYNONYMS, a.getSynonyms());
		w(Plant.class, Plant.SURVEYTAXID, a.getSurveyTaxId());
		w(Plant.class, Plant.NOTE, a.getNote());
	}

	/** Process the table of Publications. */
	protected void process(Publication a) throws IOException {
		if (!tmp.isSetTableD(Publication.class))
			return; // prevent recursion!
		w(Publication.class, Publication.COLLECTIONNAME, a.getCollectionName());
		w(Publication.class, Publication.COLLECTIONYEARPUBLICATION, a
				.getCollectionYearPublication());
		w(Publication.class, Publication.JOURNALNAME, a.getJournalName());
		w(Publication.class, Publication.JOURNALAUTHORNAME, a
				.getJournalAuthorName());
		w(Publication.class, Publication.NOTE, a.getNote());
		w(Publication.class, Publication.REFERENCECITATION, a
				.getReferenceCitation());
		w(Publication.class, Publication.REFERENCEDETAIL, a
				.getReferenceDetail());
		w(Publication.class, Publication.URL, a.getUrl());
	}

	/** Process the table of Territories. */
	protected void process(Territory a) throws IOException {
		if (!tmp.isSetTableD(Territory.class))
			return; // prevent recursion!
		w(Territory.class, Territory.NAME, a.getName());
	}

	/** Process the table of Villages. */
	protected void process(Village a) throws IOException {
		if (!tmp.isSetTableD(Village.class))
			return; // prevent recursion!
		w(Village.class, Village.NAME, a.getName());
	}

	/**
	 * Write down the [table, column, value] to the output in a particular format.
	 * It is up to the subclass to specify this method. 
	 * 
	 * @param table	The currently processed table.
	 * @param column	The currently considered column of the table.
	 * @param value	The particular value contained in that column.
	 * @throws IOException	when an IO error occurs.
	 */
	protected abstract void w(Class table, String column, Object value)
		throws IOException;

	
	/** Empty implementation (does nothing). */
	public void makeHeader() throws IOException {}
	
	/** Empty implementation (does nothing). */
	public void makeFooter() throws IOException {}
	
	/** Empty implementation (does nothing). */
	public void startNewRecord() throws IOException {}
	
	/** Empty implementation (does nothing). */
	public void finishRecord() throws IOException {}

}
