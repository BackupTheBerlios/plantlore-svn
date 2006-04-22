package net.sf.plantlore.client.export;

import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;

public class MetaBuilder implements Builder {
	
	public void makeHeader() {}
	
	public void makeFooter() {}
	
	public void startNewRecord() {}
	
	public void finishRecord() {}
	
	public void writePartialRecord(Author arg) {}
	
	public void writePartialRecord(AuthorOccurrence arg) {}
	
	public void writePartialRecord(Habitat arg) {}
	
	public void writePartialRecord(Metadata arg) {}
	
	public void writePartialRecord(Occurrence arg) {}
	
	public void writePartialRecord(Phytochorion arg) {}
	
	public void writePartialRecord(Plant arg) {}
	
	public void writePartialRecord(Publication arg) {}
	
	public void writePartialRecord(Territory arg) {}

	public void writePartialRecord(Village arg) {}
	
}
