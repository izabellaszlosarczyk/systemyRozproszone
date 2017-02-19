package client;

import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import common.Task;

public class Producer {
	
	private static final String JNDI_CONTEXT_FACTORY_CLASS_NAME = "org.exolab.jms.jndi.InitialContextFactory";
	private static final String DEFAULT_JMS_PROVIDER_URL = "tcp://localhost:3035/";
	private static final String DEFAULT_OUTGOING_MESSAGES_QUEUE_NAME = "queue1";
	private static final int MESSAGES_PER_PRODUCER = 20;
	private Context jndiContext;
	
	private QueueConnectionFactory queueConnectionFactory;
	private Queue outgoingMessagesQueue;

	private QueueConnection connection;
	private QueueSession session;
	private QueueSender sender;
	
	public Producer() throws NamingException, JMSException {
		this(DEFAULT_JMS_PROVIDER_URL, DEFAULT_OUTGOING_MESSAGES_QUEUE_NAME, MESSAGES_PER_PRODUCER, JNDI_CONTEXT_FACTORY_CLASS_NAME);
	}
	
	public Producer(String providerUrl, String outgoingMessagesQueueName, int messagePerProducer, String jndiContext) throws NamingException, JMSException {
		initializeJndiContext(providerUrl, jndiContext);
		initializeAdministrativeObjects(outgoingMessagesQueueName);
		initializeJmsClientObjects();
	}
	
	private void initializeJndiContext(String providerUrl, String jndiContextTmp) throws NamingException {
		// JNDI Context
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, jndiContextTmp);
		props.put(Context.PROVIDER_URL, providerUrl);
		jndiContext = new InitialContext(props);
		System.out.println("JNDI context initialized!");
	}

	private void initializeAdministrativeObjects(String outgoingMessagesQueueName) throws NamingException {
		// ConnectionFactory
		queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");
		// Destination
		outgoingMessagesQueue = (Queue) jndiContext.lookup(outgoingMessagesQueueName);
		System.out.println("JMS administrative objects (ConnectionFactory, Destinations) initialized!");
	}
	
	private void initializeJmsClientObjects() throws JMSException {
		connection = queueConnectionFactory.createQueueConnection();
		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		sender = session.createSender(outgoingMessagesQueue);
		System.out.println("JMS client objects (Session, MessageConsumer) initialized!");
	}

	public void start( int messagePerProducer) throws JMSException, IOException {
		connection.start();
		System.out.println("Connection started - sendind messages possible!");
		
		for (int i = 0; i < messagePerProducer; i = i + 1){
			ObjectMessage m =  session.createObjectMessage();
			Task task = new Task();
			m.setObject(task);
			System.out.println("Wysylam wiadomosc:" + task.toString());
			sender.send(m);
		}
	}
	
	public void stop() {
        if (jndiContext != null) {
            try {
            	jndiContext.close();
            } catch (NamingException exception) {
                exception.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException exception) {
                exception.printStackTrace();
            }
        }
	}
	
}
