package net.sf.plantlore.client.export.builders;

import java.io.IOException;

import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Projection;

/**
 * A training extension of the AbstractBuilder.
 * Convenient if you wish to see the results immediately
 * on the default output.
 * <br/>
 * A sample output:
 * <br/>
 * <pre>
 * &lt;157&gt;
 *   Author.WholeName = Leonhard Euler
 *   Author.Email = euler@koenigsberg.edu
 *   Plant.Taxon = Gagea pratensis (Pers.) Dumort. 
 *   Plant.CzechName = ostružiník měkký
 * &lt;/157&gt;
 * </pre>
 * 
 * @author kaimu
 * @since 2006-04-27
 */
public class TrainingBuilder extends AbstractBuilder {
	
	private int i = 0;
	
	/**
	 * Create a new Training Builder. 
	 * 
	 * @param projections	The list of columns that shall be sent to the output.
	 */
	public TrainingBuilder(Projection projections) {
		super(projections);
	}
 
	/**
	 * Create a header of the output.
	 */
	public void header() throws IOException {
		System.out.println("Training Builder engaged.");		
	}
  
	/**
	 * Create a footer of the output.
	 */
	public void footer() throws IOException {
		System.out.println("Training Builder disengaged.");
	}
 
	/**
	 * Start processing another record.
	 */
	public void startRecord() throws IOException {
		System.out.println(" <" + i + ">");
	}

	/**
	 * Finish processing of the record.
	 */
	public void finishRecord() throws IOException {
		System.out.println(" </" + i + ">");
		i++;
	}
	
	/**
	 * Send a part of the record to the output.
	 */
	protected void output(Class table, String column, Object value) throws IOException {
		System.out.println("   " + table.getSimpleName() + "." + column + " = " + value);
	}

}
