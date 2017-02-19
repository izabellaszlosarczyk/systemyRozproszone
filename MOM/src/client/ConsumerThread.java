package client;

import java.io.IOException;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class ConsumerThread extends Thread {
	
	private String jmsProviderUrl;
	private String jndiContext;
	private String topicName; 
	private String threadId;

	public ConsumerThread(String jmsProviderUrl, String jndiContext, String topicName, String threadId) {
		this.jmsProviderUrl = jmsProviderUrl;
		this.jndiContext = jndiContext;
		this.topicName = topicName;
		this.threadId = threadId; 
	}
	@Override
	public void run() {
		System.out.println("Creating consumer: " + threadId);
		try {
			Consumer consumer = new Consumer(this.jmsProviderUrl, this.jndiContext, this.topicName, this.threadId);
			consumer.start();
		} catch (NamingException | JMSException | IOException e) {
			e.printStackTrace();
		}

	}

}
