package com.sam.jms.queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONObject;

public class Consumer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ConnectionFactory connFactory = new ActiveMQConnectionFactory("admin","admin","tcp://localhost:61616");
		try {
			Connection connection = connFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			
			Destination destination = session.createQueue("demo");
			
			MessageConsumer messageConsumer = session.createConsumer(destination);

			messageConsumer.setMessageListener(new MessageListener(){
				public void onMessage(Message message) {
					TextMessage tm = (TextMessage)message;
					try {
						
						try {
						JSONObject obj = new JSONObject(tm.getText());
						System.out.println("JSON Object >>> "+obj.toMap());
						}catch(Exception ex) {
							System.out.println("<< Not a JSON object >");
						}
						System.out.println(tm.getText());
						tm.acknowledge();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			});
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
