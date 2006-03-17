package net.sf.plantlore.client;


import net.sf.plantlore.middleware.*;

public class Test {

	public static void main(String[] args) {
		try {
			
			DBLayerFactory factory = new RMIDBLayerFactory();
			DBLayer db = factory.create();
			
			db.initialize();
			
			factory.destroy(db);
			
			
			// Be ugly: while(1) factory.create(); // flooding
					
		} catch(Exception e) {}
	}

}
