package com.sam.jms.queue;

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
import org.json.JSONObject;

import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;

public class Publisher {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ConnectionFactory connFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
		try {
			Connection connection = connFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			//creates queue if not not existing else uses the existing gone
			Destination destination = session.createQueue("demo");
			
			MessageProducer messageProducer = session.createProducer(destination);
			
			
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("from_date", "01-Jan-2021");
			jsonObj.put("to_date", "31-Dec-2021");
			jsonObj.put("email", "pqr@gmail.com");
			jsonObj.put("type", "excel");
			
			List<String> messages = Arrays.asList("Eureka","Docker","Kubernetes","Zuul","Hysterix","spring","microservices",jsonObj.toString());
			
			for(String msg:messages) {
				TextMessage textMessage = session.createTextMessage(msg);
				messageProducer.send(textMessage);
				System.out.println("Message sent >> "+msg);
			}
			
			session.close();
			connection.close();
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
