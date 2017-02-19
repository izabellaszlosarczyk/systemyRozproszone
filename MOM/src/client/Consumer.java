package client;

import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import common.Task;

public class Consumer implements MessageListener {
	private static final String JNDI_CONTEXT_FACTORY_CLASS_NAME = "org.exolab.jms.jndi.InitialContextFactory";
	private static final String DEFAULT_JMS_PROVIDER_URL = "tcp://localhost:3035/";
	private static final String DEFAULT_INCOMING_MESSAGES_TOPIC_NAME = "topic1";
	private static final String TOPIC_SUBSCRIPTION_NAME = "sub1";
	
	private Context jndiContext;

	private TopicConnectionFactory topicConnectionFactory;
	private Topic incomingMessagesTopic;

	private TopicConnection connection;
	private TopicSession session;
	private TopicSubscriber subscriber;

	public Consumer() throws NamingException, JMSException {
		this(JNDI_CONTEXT_FACTORY_CLASS_NAME, DEFAULT_JMS_PROVIDER_URL, DEFAULT_INCOMING_MESSAGES_TOPIC_NAME, TOPIC_SUBSCRIPTION_NAME);
	}
	

	public Consumer(String providerUrl, String jndiContext, String topicName, String name) throws NamingException, JMSException {
		initializeJndiContext(jndiContext, providerUrl);
		initializeAdministrativeObjects(topicName);
		initializeJmsClientObjects(name);
	}

	private void initializeJndiContext(String jndiContextTmp, String providerUrl) throws NamingException {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, jndiContextTmp);
		props.put(Context.PROVIDER_URL, providerUrl);
		jndiContext = new InitialContext(props);
		System.out.println("JNDI context initialized!");
	}

	private void initializeAdministrativeObjects(String incomingMessagesTopicName) throws NamingException {
		// ConnectionFactory
		topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");
		// Destination
		incomingMessagesTopic = (Topic) jndiContext.lookup(incomingMessagesTopicName);
		System.out.println("JMS administrative objects (ConnectionFactory, Destinations) initialized!");
	}
	
	private void initializeJmsClientObjects(String name) throws JMSException {
		connection = topicConnectionFactory.createTopicConnection();
		session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); // false - non-transactional, AUTO_ACKNOWLEDGE - messages acknowledged after receive() method returns
		subscriber = session.createDurableSubscriber(incomingMessagesTopic, name);
		System.out.println("JMS client objects (Session, MessageConsumer) initialized!");
		subscriber.setMessageListener(this);
	}

	public void start() throws JMSException, IOException {
		connection.start();
		System.out.println("Connection started - receiving messages possible!");
	}
	
	public void stop() {
        // close the connection
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException exception) {
                exception.printStackTrace();
            }
        }
        
		// close the context
        if (jndiContext != null) {
            try {
            	jndiContext.close();
            } catch (NamingException exception) {
                exception.printStackTrace();
            }
        }
	}

	@Override
	public void onMessage(Message msg) {
		try {
			ObjectMessage tmp = (ObjectMessage) msg;
			Task task = (Task)tmp.getObject();
			System.out.println("Odebrałem task: " + task.getFirstNumber() + " " + task.getType() + " " + task.getSecondNumber() + ". Wynik: " + task.getResult());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
