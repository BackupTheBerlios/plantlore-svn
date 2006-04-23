package net.sf.plantlore.client.export;

import java.io.IOException;

import net.sf.plantlore.client.export.Template;
import net.sf.plantlore.common.record.*;


/**
 * MetaBuilder. Hard-wired database scheme.
 * Fast, but not flexible (other permutations of columns aren't possible).
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 */



public abstract class MetaBuilder implements Builder {
	
	private Template tmp;
	
	public MetaBuilder(Template tmp) {
		this.tmp = tmp;
	}
	
	
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

	protected void process(Author a) throws IOException {
		if (!tmp.isSetTableD(Author.class))
			return; // prevent recursion!
		wc(Author.class, Author.WHOLENAME, a.getWholeName());
		wc(Author.class, Author.ORGANIZATION, a.getOrganization());
		wc(Author.class, Author.ADDRESS, a.getAddress());
		wc(Author.class, Author.EMAIL, a.getEmail());
		wc(Author.class, Author.PHONENUMBER, a.getPhoneNumber());
		wc(Author.class, Author.URL, a.getUrl());
		wc(Author.class, Author.ROLE, a.getRole());
		wc(Author.class, Author.NOTE, a.getNote());
	}

	protected void process(AuthorOccurrence a) throws IOException {
		if (!tmp.isSetTableD(AuthorOccurrence.class))
			return; // prevent recursion!
		process(a.getAuthor());
		process(a.getOccurrence());
		wc(AuthorOccurrence.class, AuthorOccurrence.ROLE, a.getRole());
		wc(AuthorOccurrence.class, AuthorOccurrence.RESULTREVISION, a
				.getResultRevision());
	}

	protected void process(Habitat a) throws IOException {
		if (!tmp.isSetTableD(Habitat.class))
			return; // prevent recursion!
		process(a.getTerritory());
		process(a.getPhytochorion());
		process(a.getNearestVillage());
		wc(Habitat.class, Habitat.DESCRIPTION, a.getDescription());
		wc(Habitat.class, Habitat.COUNTRY, a.getCountry());
		wc(Habitat.class, Habitat.NOTE, a.getNote());
		wc(Habitat.class, Habitat.QUADRANT, a.getQuadrant());
		wc(Habitat.class, Habitat.ALTITUDE, a.getAltitude());
		wc(Habitat.class, Habitat.LATITUDE, a.getLatitude());
		wc(Habitat.class, Habitat.LONGITUDE, a.getLongitude());
	}

	protected void process(Metadata a) throws IOException {
		if (!tmp.isSetTableD(Metadata.class))
			return; // prevent recursion!
		wc(Metadata.class, Metadata.CONTENTCONTACTNAME, a
				.getContentContactName());
		wc(Metadata.class, Metadata.CONTENTCONTACTADDRESS, a
				.getContentContactAddress());
		wc(Metadata.class, Metadata.CONTENTCONTACTEMAIL, a
				.getContentContactEmail());
		wc(Metadata.class, Metadata.DATASETTITLE, a.getDataSetTitle());
		wc(Metadata.class, Metadata.DATASETDETAILS, a.getDataSetDetails());
		wc(Metadata.class, Metadata.DATECREATE, a.getDateCreate());
		wc(Metadata.class, Metadata.DATEMODIFIED, a.getDateModified());
		wc(Metadata.class, Metadata.OWNERORGANIZATIONABBREV, a
				.getOwnerOrganizationAbbrev());
		wc(Metadata.class, Metadata.RECORDBASIS, a.getRecordBasis());
		wc(Metadata.class, Metadata.SOURCEINSTITUTIONID, a
				.getSourceInstitutionId());
		wc(Metadata.class, Metadata.SOURCEID, a.getSourceId());
		wc(Metadata.class, Metadata.TECHNICALCONTACTNAME, a
				.getTechnicalContactName());
		wc(Metadata.class, Metadata.TECHNICALCONTACTADDRESS, a
				.getTechnicalContactAddress());
		wc(Metadata.class, Metadata.TECHNICALCONTACTEMAIL, a
				.getTechnicalContactEmail());
		wc(Metadata.class, Metadata.VERSIONPLANTSFILE, a.getVersionPlantsFile());
	}

	protected void process(Occurrence a) throws IOException {
		if (!tmp.isSetTableD(Occurrence.class))
			return; // prevent recursion!
		process(a.getPlant());
		process(a.getHabitat());
		process(a.getMetadata());
		process(a.getPublication());
		wc(Occurrence.class, Occurrence.UNITIDDB, a.getUnitIdDb());
		wc(Occurrence.class, Occurrence.UNITVALUE, a.getUnitValue());
		wc(Occurrence.class, Occurrence.DAYCOLLECTED, a.getDayCollected());
		wc(Occurrence.class, Occurrence.MONTHCOLLECTED, a.getMonthCollected());
		wc(Occurrence.class, Occurrence.YEARCOLLECTED, a.getYearCollected());
		wc(Occurrence.class, Occurrence.TIMECOLLECTED, a.getTimeCollected());
		wc(Occurrence.class, Occurrence.ISODATETIMEBEGIN, a
				.getIsoDateTimeBegin());
		wc(Occurrence.class, Occurrence.DATASOURCE, a.getDataSource());
		wc(Occurrence.class, Occurrence.HERBARIUM, a.getHerbarium());
		wc(Occurrence.class, Occurrence.NOTE, a.getNote());
		wc(Occurrence.class, Occurrence.CREATEDWHEN, a.getCreatedWhen());
		wc(Occurrence.class, Occurrence.UPDATEDWHEN, a.getUpdatedWhen());
	}

	protected void process(Phytochorion a) throws IOException {
		if (!tmp.isSetTableD(Phytochorion.class))
			return; // prevent recursion!
		wc(Phytochorion.class, Phytochorion.NAME, a.getName());
		wc(Phytochorion.class, Phytochorion.CODE, a.getCode());
	}

	protected void process(Plant a) throws IOException {
		if (!tmp.isSetTableD(Plant.class))
			return; // prevent recursion!
		wc(Plant.class, Plant.TAXON, a.getTaxon());
		wc(Plant.class, Plant.SCIENTIFICNAMEAUTHOR, a.getScientificNameAuthor());
		wc(Plant.class, Plant.CZECHNAME, a.getCzechName());
		wc(Plant.class, Plant.GENUS, a.getGenus());
		wc(Plant.class, Plant.SPECIES, a.getSpecies());
		wc(Plant.class, Plant.SYNONYMS, a.getSynonyms());
		wc(Plant.class, Plant.SURVEYTAXID, a.getSurveyTaxId());
		wc(Plant.class, Plant.NOTE, a.getNote());
	}

	protected void process(Publication a) throws IOException {
		if (!tmp.isSetTableD(Publication.class))
			return; // prevent recursion!
		wc(Publication.class, Publication.COLLECTIONNAME, a.getCollectionName());
		wc(Publication.class, Publication.COLLECTIONYEARPUBLICATION, a
				.getCollectionYearPublication());
		wc(Publication.class, Publication.JOURNALNAME, a.getJournalName());
		wc(Publication.class, Publication.JOURNALAUTHORNAME, a
				.getJournalAuthorName());
		wc(Publication.class, Publication.NOTE, a.getNote());
		wc(Publication.class, Publication.REFERENCECITATION, a
				.getReferenceCitation());
		wc(Publication.class, Publication.REFERENCEDETAIL, a
				.getReferenceDetail());
		wc(Publication.class, Publication.URL, a.getUrl());
	}

	protected void process(Territory a) throws IOException {
		if (!tmp.isSetTableD(Territory.class))
			return; // prevent recursion!
		wc(Territory.class, Territory.NAME, a.getName());
	}

	protected void process(Village a) throws IOException {
		if (!tmp.isSetTableD(Village.class))
			return; // prevent recursion!
		wc(Village.class, Village.NAME, a.getName());
	}

	/** Write Column! */
	protected abstract void wc(Class table, String column, Object value)
		throws IOException;

	
	
	public void makeHeader() throws IOException {}
	
	public void makeFooter() throws IOException {}
	
	public void startNewRecord() throws IOException {}
	
	public void finishRecord() throws IOException {}

}
