package net.sf.plantlore.client.export.builders;

import java.io.IOException;

import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.common.record.Record;

public class TrainingBuilder implements Builder {
	
	private int i = 0;

	public void header() throws IOException {
		System.out.println("Training Builder engaged.");		
	}

	public void footer() throws IOException {
		System.out.println("Training Builder disengaged.");
	}

	public void startRecord() throws IOException {
		System.out.println(" [" + i + "].starts ");
	}

	public void part(Record arg) throws IOException {
		System.out.println("  ▪ " + arg.getClass().getSimpleName() );
	}

	public void part(Record... args) throws IOException {
		for(Record r : args) part( r );
	}

	public void finishRecord() throws IOException {
		System.out.println(" [" + i + "].ends ");
		i++;
	}

}
