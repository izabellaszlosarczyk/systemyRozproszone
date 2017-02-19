package server;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class ServerMain {
	
	public static void main(String[] args) throws IOException {
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream("config.properties");
		prop.load(input);
		String jmsProviderUrl = prop.getProperty("jmsProviderUrl");
		String queueName = prop.getProperty("queueName");
		String jndiContext = prop.getProperty("jndiContext");
		String tmp = prop.getProperty("numberOfSolvers");
		int numberOfSolvers = Integer.parseInt(tmp);
		
		Server server = null;
		try {
			server = new Server(jmsProviderUrl, queueName, numberOfSolvers , jndiContext);
			//server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (server != null) {
			//server.stop();
		}
	}

}
