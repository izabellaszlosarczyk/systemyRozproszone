package admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

import common.Task.TypeOfTask;

public class AdminMain {
	public static void main(String[] args) throws IOException {
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream("config.properties");
		prop.load(input);
		String jmsProviderUrl = prop.getProperty("jmsProviderUrl");
		String queueName = prop.getProperty("queueName");
		//String jndiContext = prop.getProperty("jndiContext");
		//String tmp = prop.getProperty("numberOfSolvers");
		
		TypeOfTask[] typeOfTask = TypeOfTask.values();

		JmsAdminServerIfc admin = null;
	    try {
			admin = AdminConnectionFactory.create(jmsProviderUrl);
			createQueue(admin, queueName);
			for(TypeOfTask task : typeOfTask ) {
				createTopic(admin, task.toString());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	    
	    
	    if(admin != null) {
	    	System.out.println("DONE");
	    	admin.close();
	    }
	}
	
	private static void createTopic(JmsAdminServerIfc admin, String topicName) {
		    try {
				if (!admin.addDestination(topicName, Boolean.FALSE)) {
				    System.err.println("Failed to create topic " + topicName);
				} else {
					System.out.println("Created topic: " + topicName);
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}

	private static void createQueue(JmsAdminServerIfc admin, String queueName) {
	    try {
			if (!admin.addDestination(queueName, Boolean.TRUE)) {
			    System.err.println("Failed to create queue " + queueName);
			} else {
				System.out.println("Created queue: " + queueName);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
