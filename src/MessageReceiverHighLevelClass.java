import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.Exchange.DeleteOk;


public class MessageReceiverHighLevelClass {
	
	static ReceivingGUI window;
	static JAXBContext jaxbContext_XMLRootClass;
    static Marshaller marsh_XMLRootClass;
    static JAXBContext jaxbContext_XMLMessageRootClass;
    static Marshaller marsh_XMLMessageRootClass;
    static HighLevelClass.XMLRootClass systemObject = null;
    static MessageSenderHighlevelClass.XMLMessageRootClass MessageObject = null;
    
    static List <Exchange> ExchangeList;
    static List <Queue> QueueList;
    static String QueueMessage;
    static String ServerIP;
    static String XMLFileName;
    
	static ConnectionFactory factory;
	static Connection connection;
	static Channel channel;
	static QueueingConsumer consumer;
	static boolean showNextMessage;
	static int numMessages;
	static int messageCounter;
    
    public static boolean OpenConnectionToServer()
	{
		try {
			factory = new ConnectionFactory();
			factory.setHost(ServerIP);
			connection = factory.newConnection();
			channel = connection.createChannel();
			return true;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"ERROR occured when creating new connection/channel", "Error", JOptionPane.ERROR_MESSAGE);
			CloseConnectionToServer();
			return false;
		}
	}
    
    public static void CloseConnectionToServer()
	{
		try {
			channel.close();
			connection.close();
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null,"Error in closing connection", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
    
    public static void ReceiveMessage(String queueName) throws IOException
	{
		 factory = new ConnectionFactory();
	     factory.setHost("localhost");
	     
	     if(OpenConnectionToServer())
			{
		 	    AMQP.Queue.DeclareOk QDecAck = channel.queueDeclarePassive(queueName);
		 	    numMessages = QDecAck.getMessageCount();
		 	    System.out.println("Msg count: " + numMessages);
		 	    
	    	 	consumer = new QueueingConsumer(channel);
		 	    channel.basicConsume(queueName, true, consumer);
		 	    
			 	   messageCounter = 1;
			 	   try {   
			        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			        String message = new String(delivery.getBody());
			        String routingKey = delivery.getEnvelope().getRoutingKey();
			        UnmarshalMessage(message);
			        System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
				   } catch (Exception e) { e.printStackTrace();}
			}
	}
    
    public static void GetNextMessage()
    {
    	if (messageCounter++ < numMessages) 
	 	{
	 		try {   
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            String routingKey = delivery.getEnvelope().getRoutingKey();
            UnmarshalMessage(message);
            System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
	 		} catch (Exception e) { e.printStackTrace();}
	 	}
    }
    
    public static void UnmarshalMessage(String xmlMessage)
    {
    	ByteArrayInputStream input = new ByteArrayInputStream (xmlMessage.getBytes());
    	MessageObject = null;
    	
    	try {
    		jaxbContext_XMLMessageRootClass = JAXBContext.newInstance(MessageSenderHighlevelClass.XMLMessageRootClass.class);
	    	MessageObject = (MessageSenderHighlevelClass.XMLMessageRootClass) jaxbContext_XMLMessageRootClass.createUnmarshaller().unmarshal(input);
	    } catch (Exception ex)
	    {
	    	System.out.println("Exception:");
	    	MessageObject = new MessageSenderHighlevelClass.XMLMessageRootClass();
	    }
	    System.out.println(" [x] Received " + MessageObject.InfoList.get(0));
	    
	    window.DisplayMessageContents(MessageObject);
    }
    
    public static List <Queue> GetQueueList()
	{
		return QueueList;
	}
    
    public static void SetShowNextMessage()
    {
    	showNextMessage = true;
    }
    
    public static void CloseApplication()
	{
		window.CloseWindow();
		CloseConnectionToServer();
		System.exit(0);
	}

	public static void main(String[] args) throws Exception {
		jaxbContext_XMLRootClass = JAXBContext.newInstance(HighLevelClass.XMLRootClass.class);
	  
	    //String XMLFileName = JOptionPane.showInputDialog("Enter filename to load from:");
		String XMLFileName = "system5.xml";
	    try {
	    	systemObject = (HighLevelClass.XMLRootClass) jaxbContext_XMLRootClass.createUnmarshaller().unmarshal(new FileInputStream(XMLFileName));
	    } catch (Exception ex)
	    {
	    	systemObject = new HighLevelClass.XMLRootClass();
	    }
	    

	    ServerIP = systemObject.ServeIP;
	    ExchangeList = systemObject.ExchangeList;
	    QueueList = systemObject.QueueList;
	    System.out.println(systemObject.ExchangeList.size());
	    System.out.println(systemObject.QueueList.size());
		System.out.println(ServerIP);
		
		window = new ReceivingGUI();
		//window.open();
		window.start();
		
		
	}
}
