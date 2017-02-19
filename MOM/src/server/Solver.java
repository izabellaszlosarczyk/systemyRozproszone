package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
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

public class Solver implements Runnable {
	private Context jndiContext;
	
	private Task task;

	private HashMap<String, TopicPublisher> publishers;
	private HashMap<String, TopicSession> sessionTopics;
	private HashMap<String, Topic> topics;
	
	
	public Solver(HashMap<String, TopicPublisher> publishers, HashMap<String, TopicSession> sessionTopics, HashMap<String, Topic> topics, Task task) {
		this.publishers = publishers;
		this.sessionTopics = sessionTopics;
		this.topics = topics;
		this.task = task;
	}

	@Override
	public void run() {
		task = taskCompute(task);
		sendTask(task);
	}
	
	public void sendTask(Task task) {
		TopicPublisher publisher = this.publishers.get(task.getType().toString());
		TopicSession session = this.sessionTopics.get(task.getType().toString());
		Topic topic = this.topics.get(task.getType().toString());
		try {
			ObjectMessage message = session.createObjectMessage();
			message.setObject(task);
			System.out.println("Publishing task from server to topic: " + task.getType().toString());
			publisher.publish(topic, message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Task taskCompute(Task task){
		int[] tmp = new int[2];
		tmp[0] = Integer.parseInt(task.getFirstNumber());
		tmp[1] = Integer.parseInt(task.getSecondNumber());
		switch(task.getType()) {
		case MULTIPLICATION:
			task.setResult(new Integer(tmp[0]*tmp[1]).toString());
			break;
		case ADDITION:
			task.setResult(new Integer(tmp[0]+tmp[1]).toString());
			break;
		case SUBSTRACTION:
			task.setResult(new Integer(tmp[0]-tmp[1]).toString());
			break;
		case DIVISION:
			task.setResult(new Integer(tmp[0]/tmp[1]).toString());
			break;
		case EXPONENTATION:
			task.setResult(new Integer(tmp[0]*tmp[0]).toString());
			break;
		case ROOT:
			task.setResult(new Float(Math.sqrt(tmp[0])).toString());
			break;
		}
		
		return task;
	}
	
	public void setTask(Task task){
		this.task = task;
	}
	public Task getTask(){
		return this.task;
	}
 	
}
