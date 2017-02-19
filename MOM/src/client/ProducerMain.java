package client;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ProducerMain {
	
	
	public static void main(String[] args) throws IOException {
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream("config.properties");
		prop.load(input);
		List<Producer> producers = new ArrayList<Producer>();
		String jmsProviderUrl = prop.getProperty("jmsProviderUrl");
		String queueName = prop.getProperty("queueName");
		String jndiContext = prop.getProperty("jndiContext");
		String tmp = prop.getProperty("numberOfSolvers");
		String numberOfProducers = prop.getProperty("numberOfProducers");
		int numberOfP = Integer.parseInt(numberOfProducers);
		int messagePerProducer = Integer.parseInt(tmp);
		try {
			for (int i =  0; i < numberOfP; i = i + 1){
				producers.add(new Producer(jmsProviderUrl, queueName , messagePerProducer, jndiContext));
				producers.get(i).start(messagePerProducer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (int i =  0; i < numberOfP; i = i + 1){
				if (producers.get(i) != null) {
					System.out.println("koncze dzialanie");
					producers.get(i).stop();
				}
			}
		}
		
	}
}
