package net.sf.plantlore.server;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;


/**
 * Temporary solution.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 30.3.2006
 * @version beta not tested
 */
public class Plantlore {
	
	// Probably some more sophisticated method for obtaining a password from the user :)
	public static String getPassword() {
		return "poweroverwhelming";
	}

	/**
	 * 
	 * @param args Controlling the server from the command line:
	 * 		start|stop|who|kick=id [host] [port]
	 */
	public static void main(String[] args) {
		
		int m = args.length, port = RMIServer.DEFAULT_PORT, id = -1;
		String command = "start", host = null;
		
		// Parse the command line.
		if(m >= 1) command = args[0];
		if(m >= 2) try { int c = Integer.parseInt(args[1]); port = c; }
			catch(NumberFormatException e) { host = args[1]; }
		if(m >= 3) port = Integer.parseInt(args[2]);
			
		if(command.startsWith("kick")) {
			id = Integer.parseInt(command.substring(5));
			command = "kick";
		}
		 
		// The server interface.		
		Server server = null ;
		
		// Take the appropriate action.
		try {
			if (command.equalsIgnoreCase("start")) {
				try { server = new RMIServer(port); } catch (Exception e) { System.err.println(e); server.stop(true); }
			} 
			else {
				Registry registry = LocateRegistry.getRegistry(host, port);
				Guard guard = (Guard) registry.lookup(Guard.ID);
				server = guard.certify(getPassword());
				if(server == null) { System.err.println("<!> No server on that port."); return; }
				if(command.equalsIgnoreCase("stop")) server.stop(true);
				else {
					Collection<ConnectionInfo> clients = server.getClients();
					int c = 0;
					if(command.equalsIgnoreCase("who")) {
						for(ConnectionInfo info : clients) System.out.println("(" + c++ + ") " + info);
					}
					else if(command.equalsIgnoreCase("kick")) {
						for(ConnectionInfo info : clients) if(c++ == id) { server.disconnect(info); break; }
					}
					else System.err.println("<?> Unknown command.");
				}
					
			}
		} catch(Exception e) { System.err.println(e); }
		
	}

}
