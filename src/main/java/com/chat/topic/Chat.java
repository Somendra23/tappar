package com.chat.topic;

import java.io.*;
import java.util.Enumeration;

import javax.jms.*;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.naming.*;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Chat implements javax.jms.MessageListener{
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;

    /* Constructor used to Initialize Chat */
    public Chat(String topicFactory, String topicName, String username) 
        throws Exception {
    	
    	// Obtain a JNDI connection using the jndi.properties file
        ActiveMQConnectionFactory ctx = new ActiveMQConnectionFactory("admin","admin","tcp://localhost:61616");

        // Look up a JMS connection factory
//        TopicConnectionFactory conFactory = 
//        	(TopicConnectionFactory)ctx.lookup(topicFactory);

        // Create a JMS connection
        TopicConnection connection = ctx.createTopicConnection();

        // Create two JMS session objects
        TopicSession pubSession = connection.createTopicSession(
        	false, Session.AUTO_ACKNOWLEDGE);     
        
        TopicSession subSession = connection.createTopicSession(
        	false, Session.AUTO_ACKNOWLEDGE);

        // Look up a JMS topic
        //Topic chatTopic = (Topic)ctx.lookup(topicName);
        Topic chatTopic = pubSession.createTopic(topicName);

        // Create a JMS publisher and subscriber
        TopicPublisher publisher = 
            pubSession.createPublisher(chatTopic);
        
        //javax.jms.DeliveryMode
        publisher.setDeliveryMode(javax.jms.DeliveryMode.PERSISTENT);
        //Message expiration in millisecond
        publisher.setTimeToLive(360000);
        //JMS Priority
        publisher.setPriority(9);
        
        TopicSubscriber subscriber = 
            subSession.createSubscriber(chatTopic, null, true);

        // Set a JMS message listener
        subscriber.setMessageListener(this);

        // Intialize the Chat application variables
        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        this.username = username;

        // Start the JMS connection; allows messages to be delivered
        connection.start( );
    }

    /* Receive Messages From Topic Subscriber */
    public void onMessage(Message message) {
        try {
            
        	Enumeration propNames = message.getPropertyNames();
        	while(propNames.hasMoreElements()) {
        		String name = (String)propNames.nextElement();
        		Object value = message.getObjectProperty(name);
        		System.out.println(" Key ="+name+" Value ="+value);
        	}
        	
        	
        	TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText( );
            System.out.println(text);
            //Topic or queue
            System.out.println("JMSDestination : "+message.getJMSDestination());
            //PERSISTENT OR NONPERSISTENT
            System.out.println("JMSDeliveryMode : "+message.getJMSDeliveryMode());
            //Time when message was received by message vendor not when it was delivered
            System.out.println("JMStimestamp : "+message.getJMSTimestamp());
            System.out.println("JMStimestamp : "+message.getJMSExpiration());
            // 0-4 are gradations of normal priority
            // 5-9 are gradations of expedited priority
            System.out.println("JMS Priority : "+ message.getJMSPriority());
            
            System.out.println("JMSCorrelationID : "+message.getJMSCorrelationID());
            System.out.println("JMSMessageID : "+message.getJMSMessageID());
            
            
            
        } catch (JMSException jmse){ jmse.printStackTrace( ); }
    }

    /* Create and Send Message Using Publisher */
    protected void writeMessage(String text) throws JMSException {
        TextMessage message = pubSession.createTextMessage( );
        message.setText(username+": "+text);
        //set application specific properties in the message by the provider
        message.setStringProperty("userName", "Somendra Sharma");
        message.setStringProperty("city", "Boston");
        
        
        publisher.publish(message);
    }

    /* Close the JMS Connection */
    public void close( ) throws JMSException {
        connection.close( );
    }

    /* Run the Chat Client */
    public static void main(String [] args){
        try{
            if (args.length!=3)
                System.out.println("Factory, Topic, or username missing");

            // args[0]=topicFactory; args[1]=topicName; args[2]=username
            Chat chat = new Chat(args[0],args[1],args[2]);

            // Read from command line
            BufferedReader commandLine = new 
              java.io.BufferedReader(new InputStreamReader(System.in));

            // Loop until the word "exit" is typed
            while(true){
                String s = commandLine.readLine( );
                if (s.equalsIgnoreCase("exit")){
                    chat.close( ); // close down connection
                    System.exit(0);// exit program
                } else 
                    chat.writeMessage(s);
            }
        } catch (Exception e){ e.printStackTrace( ); }
    }
}