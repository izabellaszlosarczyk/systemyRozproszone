package pl.edu.agh.iisg.rozprochy.gra;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

public class Server {
	static IGame gameImpl;
	static String portRmi;
	
	public static void main( String[] args ) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		try {
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("config.properties");
			prop.load(input);
			String registryName = prop.getProperty("rmiRegistry");
			String gameRemoteName = prop.getProperty("gameRemoteName");
			String port = prop.getProperty("port");
			String url = registryName + ":" + port + "/" + gameRemoteName;
			
	        Registry registry = LocateRegistry.createRegistry(new Integer(port));
			gameImpl = new GameImpl();
			IGame game = (IGame) UnicastRemoteObject.exportObject(gameImpl, 0);
			Naming.rebind(url, game);
			System.out.println("SERVER STARTED");
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
}