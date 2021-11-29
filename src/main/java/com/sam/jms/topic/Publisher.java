package com.sam.jms.topic;

import java.util.Arrays;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {

	public static void main(String[] args) {
		ConnectionFactory connFactory = new ActiveMQConnectionFactory("admin","admin","tcp://localhost:61616");
		
		try {
			Connection connection = connFactory.createConnection();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic("demoTopic");
			
			MessageProducer producer = session.createProducer(destination);
						
			List<String> messages = Arrays.asList("Eureka","Docker","Kubernetes","Zuul","Hysterix","spring","microservices");
			
			for(String msg:messages) {
				TextMessage textMessage = session.createTextMessage(msg);
				producer.send(textMessage);
				System.out.println("Message sent Topic >> "+msg);
			}
			
			
			//System.out.println("<< Message Published On Topic >>");
			session.close();
			connection.close();
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
