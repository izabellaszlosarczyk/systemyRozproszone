package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import common.Task;
import common.Task.TypeOfTask;

public class Server implements MessageListener{

	private static final String JNDI_CONTEXT_FACTORY_CLASS_NAME = "org.exolab.jms.jndi.InitialContextFactory";
	private static final String DEFAULT_JMS_PROVIDER_URL = "tcp://localhost:3035/";
	private static final String DEFAULT_INCOMING_MESSAGES_QUEUE_NAME = "queue1";
	private static final int NUMBER_OF_SOLVERS = 12;

	private Context jndiContext;
	
	private QueueConnectionFactory queueConnectionFactory;
	private Queue incomingMessagesQueue;
	private QueueConnection connection;
	private QueueSession session;
	private QueueReceiver receiver;

	//--------------------------------------------------------------------------------------
	//MULTIPLICATION, ADDITION, SUBSTRACTION, DIVISION, EXPONENTATION, ROOT
	private HashMap<String, TopicConnectionFactory> topicConnectionFactories = new HashMap<String, TopicConnectionFactory>();
	private HashMap<String, Topic> incomingMessagesTopics = new HashMap<String, Topic>();
	private HashMap<String, TopicConnection> connectionTopics = new HashMap<String, TopicConnection>();
	private HashMap<String, TopicSession> sessionTopics = new HashMap<String, TopicSession>();
	private HashMap<String, TopicPublisher> publishers = new HashMap<String, TopicPublisher>();

	//--------------------------------------------------------------------------------------
	
	private ExecutorService executor;

	/************** Initialization BEGIN ******************************/
	public Server() throws NamingException, JMSException {
		this(DEFAULT_JMS_PROVIDER_URL, DEFAULT_INCOMING_MESSAGES_QUEUE_NAME, NUMBER_OF_SOLVERS,JNDI_CONTEXT_FACTORY_CLASS_NAME );
	}
	
	public Server(String providerUrl, String incomingMessagesQueueName, int numberOfSolvers, String jndiContext) throws NamingException, JMSException {
		initializeJndiContext(providerUrl, jndiContext);
		initializeAdministrativeObjects(incomingMessagesQueueName, jndiContext);
		TypeOfTask[] topicsTmp = TypeOfTask.values();
		for (int k = 0; k < 6; k = k + 1){
			initializeAdministrativeObjectsTopic(topicsTmp[k].toString(), jndiContext);
		}
		initializeJmsClientObjects(jndiContext);
		
		executor = Executors.newFixedThreadPool(numberOfSolvers);
	}

	private void initializeAdministrativeObjectsTopic(String incomingMessagesTopic, String  jndiContextTmp) {
		try {
			TopicConnectionFactory connectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");
			topicConnectionFactories.put(incomingMessagesTopic, connectionFactory);
			
			Topic topic = (Topic) jndiContext.lookup(incomingMessagesTopic);
			incomingMessagesTopics.put(incomingMessagesTopic, topic);
			System.out.println("JMS administrative objects (ConnectionFactory, Destinations) initialized!");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initializeJndiContext(String providerUrl, String  jndiContextTmp) throws NamingException {
		// JNDI Context
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY,  jndiContextTmp);
		props.put(Context.PROVIDER_URL, providerUrl);
		jndiContext = new InitialContext(props);
		System.out.println("JNDI context initialized!");
	}

	private void initializeAdministrativeObjects(String incomingMessagesQueueName, String jndiContextTmp) throws NamingException {
		// ConnectionFactory
		queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");
		// Destination
		incomingMessagesQueue = (Queue) jndiContext.lookup(incomingMessagesQueueName);
		System.out.println("JMS administrative objects (ConnectionFactory, Destinations) initialized!");
	}
	
	private void initializeJmsClientObjects(String jndiContextTmp) throws JMSException {
		connection = queueConnectionFactory.createQueueConnection();
		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE); // false - non-transactional, AUTO_ACKNOWLEDGE - messages acknowledged after receive() method returns
		receiver = session.createReceiver(incomingMessagesQueue);
		System.out.println("JMS client objects (Session, MessageConsumer) initialized!");
		receiver.setMessageListener(this);	
		
		TypeOfTask[] topicsTmp = TypeOfTask.values();
		for (int k = 0; k < 6; k = k + 1){
			initializeAdministrativeObjectsTopic(topicsTmp[k].toString(), jndiContextTmp);
			connectionTopics.put(topicsTmp[k].toString(), (topicConnectionFactories.get(topicsTmp[k].toString())).createTopicConnection());
			sessionTopics.put(topicsTmp[k].toString(), (connectionTopics.get(topicsTmp[k].toString())).createTopicSession(false, Session.AUTO_ACKNOWLEDGE));
			publishers.put(topicsTmp[k].toString(), sessionTopics.get(topicsTmp[k].toString()).createPublisher(incomingMessagesTopics.get(topicsTmp[k].toString())));
			System.out.println("JMS client objects (Session, MessageConsumer) initialized!");
			//receiver.setMessageListener(this);	
		}
	}
	
	
	public void start() throws JMSException, IOException {
		connection.start();
		TypeOfTask[] topicsTmp = TypeOfTask.values();
		for (int k = 0; k < 6; k = k + 1){
			connectionTopics.get(topicsTmp[k].toString()).start();
		}
		System.out.println("Connection started - receiving messages possible!");
	}
	
	public void stop() {
		TypeOfTask[] topicsTmp = TypeOfTask.values();
        if (connection != null) {
            try {
                connection.close();
        		for (int k = 0; k < 6; k = k + 1){
        			connectionTopics.get(topicsTmp[k].toString()).close();
        		}
            } catch (JMSException exception) {
                exception.printStackTrace();
            }
        }
        if (jndiContext != null) {
            try {
            	jndiContext.close();
            } catch (NamingException exception) {
                exception.printStackTrace();
            }
        }
	}
	
	@Override
	public void onMessage(Message mes) {
		try {
			ObjectMessage tmp = (ObjectMessage)mes;
			Task task = (Task)tmp.getObject();
			System.out.println("Got message from queue: " + task.toString());
            executor.execute(new Solver(publishers, sessionTopics, incomingMessagesTopics, task));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	
}
