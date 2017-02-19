package client;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import common.Task.TypeOfTask;

public class ConsumerMain {
	
	
	public static void main(String[] args) throws IOException {
		Properties prop = new Properties();
		FileInputStream input = null;
		try {
			input = new FileInputStream("config.properties");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		prop.load(input);
		String jmsProviderUrl = prop.getProperty("jmsProviderUrl");
		String jndiContext = prop.getProperty("jndiContext");
		String numberOfConsumers = prop.getProperty("numberOfConsumers");
		int numberOfC = Integer.parseInt(numberOfConsumers);
		ConsumerThread consumer = null;
		Random rm = new Random();
		for (TypeOfTask task: TypeOfTask.values()){
			int max =  Math.abs((rm.nextInt()%5 + 1)%numberOfC);
			for (int i = 0; i < max; i = i + 1){
				String threadId= task.toString()+Integer.toString(i);
				try {
					System.out.println("tworze consumera numer:"+ i);
					consumer = new ConsumerThread(jmsProviderUrl, jndiContext, task.toString(), threadId);
					consumer.start();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			numberOfC = numberOfC - max;
		}
	}
	
}

