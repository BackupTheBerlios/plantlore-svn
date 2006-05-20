package net.sf.plantlore.client.export;

import java.io.Writer;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;


public class ExportTask extends Observable implements Observer {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	private DBLayer dblayer;
	private SelectQuery exportQuery;
	private DefaultDirector director;
	private Writer writer;
	private Thread execution, monitor;
	private boolean exportInProgress = false, aborted = false;
	private int results;
	
	
	public ExportTask(DBLayer dblayer, SelectQuery query, DefaultDirector director, Writer writer, int results) {
		this.dblayer = dblayer; this.exportQuery = query; this.director = director; this.writer = writer; this.results = results;
		director.addObserver(this);
	}
	
	public boolean isExportInProgress() {
		return exportInProgress;
	}
	
	public boolean isAborted() {
		return aborted;
	}
	
	public int getNumberOfExported() {
		return director.exportedRecords();
	}
	
	public int getNumberOfResults() {
		return results;
	}
	
	public void abort() {
		aborted = true; exportInProgress = false;
		director.abort();
		setChanged(); notifyObservers(this);
	}
	
	public void execute() {
		if(execution == null) {
			execution = new Thread( director, "Export" );
			exportInProgress = true;
			execution.start();
			
			monitor = new Thread(new Runnable() {
				public void run() {
					// Sleep until the thread is really dead.
					while( true ) try {
						execution.join(); 
						break;
					} catch(InterruptedException e) {}
					// Perform the final cleanup.
					exportInProgress = false;
					try {
						writer.close();
					} catch(Exception e) {}
					try {
						dblayer.closeQuery( exportQuery );
					} catch(Exception e) {}
					
					logger.debug("Environment cleaned up.");
					setChanged(); notifyObservers(this);
					deleteObservers();
				}
			}, "ExportMonitor");
			monitor.start();
		}
	}

	// Re-send notifications.
	public void update(Observable source, Object arg) {
		setChanged(); notifyObservers(arg);
	}
}