package net.sf.plantlore.client.export.builders;

import java.io.IOException;

import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Template;

/**
 * A training extension of the AbstractBuilder.
 * Convenient if you wish to see the results immediately
 * on the default output.
 * <br/>
 * The output has the following form:<br/>
 * <pre>
 * &lt;157&gt;
 *   Author.WholeName = Erik Kratochvíl
 *   Author.Email = discontinuum@gmail.com
 *   Plant.Taxon = Gagea pratensis (Pers.) Dumort. 
 *   Plant.CzechName = ostružiník měkký
 * &lt;/157&gt;
 * </pre>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-27
 */
public class TrainingBuilder extends AbstractBuilder {
	
	private int i = 0;
	
	public TrainingBuilder(Template template) {
		super(template);
	}
 
	public void header() throws IOException {
		System.out.println("Training Builder engaged.");		
	}
  
	public void footer() throws IOException {
		System.out.println("Training Builder disengaged.");
	}
 
	public void startRecord() throws IOException {
		System.out.println(" <" + i + ">");
	}

	public void finishRecord() throws IOException {
		System.out.println(" </" + i + ">");
		i++;
	}
	
	protected void output(Class table, String column, Object value) throws IOException {
		System.out.println("   " + table.getSimpleName() + "." + column + " = " + value);
	}

}
