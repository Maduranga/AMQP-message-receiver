import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.Exchange.DeleteOk;

public class HighLevelClass {

	//static SystemInterface window;
    static JAXBContext jaxbContext;
    static Marshaller marsh;
    static XMLRootClass systemObject = null;
        
	static ConnectionFactory factory;
	static Connection connection;
	static Channel channel;
	
@XmlRootElement	
@XmlAccessorType(XmlAccessType.FIELD)
public static class XMLRootClass {
	
	@XmlElement (name="ServerIP")
	static String ServeIP = "localhost";
	
	@XmlElement (name="Filename")
	static String XMLFileName = "system2.xml";
	
	@XmlElement (name="Exchange")
	static List <Exchange> ExchangeList = new ArrayList <Exchange>();
	
	@XmlElement (name="Queue")
	static List <Queue> QueueList = new ArrayList <Queue>();
	

	/*public static void AddExchange(String exchangeName)
	{
		Exchange newExchange = new Exchange(exchangeName);
		if(ExchangeList.contains(newExchange))
		{
			window.ShowInStatusText("Exchange " + exchangeName + " is already declared");
		}
		else
		{
			if(OpenConnectionToServer())
			{
				try {
					AMQP.Exchange.DeclareOk ExDecAck = channel.exchangeDeclare(exchangeName, "direct", true);
					window.ShowInStatusText("Exchange " + exchangeName + " added");
					ExchangeList.add(newExchange);
					window.UpdateAllLists(ExchangeList, QueueList);
					
				} catch (Exception ex) {
					window.ShowInStatusText("ERROR in adding new exchange");
				}
				CloseConnectionToServer();
			}

		}
	}
	
	public static void RemoveExchange(String exchangeName)
	{
		if(OpenConnectionToServer())
		{
			try {
				AMQP.Exchange.DeleteOk ExDelAck = channel.exchangeDelete(exchangeName);
			
				Exchange removingExchange = ExchangeList.get(ExchangeList.indexOf(new Exchange(exchangeName)));
				
				Iterator <Exchange> ExchangeIterator = ExchangeList.iterator();
				while(ExchangeIterator.hasNext())
					ExchangeIterator.next().RemoveBinding(exchangeName);
				
				Iterator <Queue> QueueIterator = QueueList.iterator();
				while(QueueIterator.hasNext())
					QueueIterator.next().RemoveBinding(exchangeName);
				
				List <String> BoundExchangeList = removingExchange.GetBoundExchangesList();
				Iterator<String> bindingsMapIter = BoundExchangeList.iterator();
				while(bindingsMapIter.hasNext())
				{
					String boundExchnageName = (String) bindingsMapIter.next();
					Exchange boundExchange = ExchangeList.get(ExchangeList.indexOf(new Exchange(boundExchnageName)));
					boundExchange.DecreaseIncomingBindingsCount();
				}
			
				ExchangeList.remove(removingExchange);
				window.ShowInStatusText("Exchange " + exchangeName + " deleted.");
				window.UpdateAllLists(ExchangeList, QueueList);
		
		} catch (Exception ex) {
			window.ShowInStatusText("ERROR. Exchange not deleted.");
		}
		
		CloseConnectionToServer();
		}
	}
	
	public static void AddQueue(String queueName)
	{
		Queue newQueue = new Queue(queueName);
		
		if(QueueList.contains(newQueue))
		{
			window.ShowInStatusText("Queue " + newQueue + " is already declared");
		}
		else
		{
			if(OpenConnectionToServer())
			{
				try {
					AMQP.Queue.DeclareOk QDecAck = channel.queueDeclare(queueName, true, false, false, null);
					window.ShowInStatusText("Queue " + QDecAck.getQueue() + " added");
					QueueList.add(newQueue);
					window.UpdateAllLists(ExchangeList, QueueList);
					
				} catch (Exception ex) {
					window.ShowInStatusText("ERROR in adding new queue");
				}
				CloseConnectionToServer();
			}
		}
	}
	
	public static void RemoveQueue(String queueName)
	{
		window.ShowInStatusText("Call to remove Queue \n");
		if(OpenConnectionToServer())
		{
			try {
				AMQP.Queue.DeleteOk QDelAck = channel.queueDelete(queueName);
				
				Queue removingQueue = QueueList.get(QueueList.indexOf(new Queue(queueName)));
				
				List <String> BoundExchangeList = removingQueue.GetBoundExchangesList();
				Iterator<String> bindingsMapIter = BoundExchangeList.iterator();
				while(bindingsMapIter.hasNext())
				{
					String boundExchnageName = (String) bindingsMapIter.next();
					window.ShowInStatusText("Bound exchange: " + boundExchnageName);
					Exchange boundExchange = ExchangeList.get(ExchangeList.indexOf(new Exchange(boundExchnageName)));
					boundExchange.DecreaseIncomingBindingsCount();
				}
				
				QueueList.remove(removingQueue);
				window.ShowInStatusText("Queue " + queueName + " deleted.");
				window.UpdateAllLists(ExchangeList, QueueList);
				
			} catch (Exception ex) {
				window.ShowInStatusText("ERROR. Queue not deleted.");
			}
			
			CloseConnectionToServer();
			}
	}
	
	public static void BindExchangeToExchange(String targetExchangeName, String destinationExchangeName, String routingKey)
	{		
		if(OpenConnectionToServer())
		{
			try {
				channel.exchangeBind(targetExchangeName, destinationExchangeName, routingKey, null);
				
				int exchIdx = ExchangeList.indexOf(new Exchange(targetExchangeName));
				Exchange targetExchangeObj = ExchangeList.get(exchIdx);
				targetExchangeObj.AddBinding(destinationExchangeName, routingKey);
				
				exchIdx = ExchangeList.indexOf(new Exchange(destinationExchangeName));
				Exchange destinatioonExchangeObj = ExchangeList.get(exchIdx);
				destinatioonExchangeObj.IncreaseIncomingBindingsCount();
				
				window.ShowInStatusText(targetExchangeName + " ----> " + destinationExchangeName);
				window.ShowInStatusText("Routing key: " + routingKey);
				
			} catch (Exception ex) {
				window.ShowInStatusText("Error when binding");
			}
			CloseConnectionToServer();
		}
	}
	
	public static void BindQueueToExchange(String targetQueueName, String destinationExchangeName, String routingKey)
	{
		if(OpenConnectionToServer())
		{
			try {
				channel.queueBind(targetQueueName, destinationExchangeName, routingKey, null);
		
				int exchIdx = QueueList.indexOf(new Queue(targetQueueName));
				Queue targetQueueObj = QueueList.get(exchIdx);
				targetQueueObj.AddBinding(destinationExchangeName, routingKey);
				
				exchIdx = ExchangeList.indexOf(new Exchange(destinationExchangeName));
				Exchange destinatioonExchangeObj = ExchangeList.get(exchIdx);
				destinatioonExchangeObj.IncreaseIncomingBindingsCount();
				
				window.ShowInStatusText(targetQueueName + " ----> " + destinationExchangeName);
				window.ShowInStatusText("Routing key: " + routingKey);
			} catch (Exception ex) {
				window.ShowInStatusText("Error when binding");
			}
			CloseConnectionToServer();
		}
	}
	
	public static void DeleteRedundantExchangesAndQueues()
	{
		Iterator <Exchange> ExchangeIterator = ExchangeList.iterator();
		while(ExchangeIterator.hasNext())
		{
			Exchange tempExchObj = ExchangeIterator.next();
			if(tempExchObj.OutgoingBindingsCount==0 && tempExchObj.IncomingBindingsCount==0)
				RemoveExchange(tempExchObj.toString());
		}
		
		Iterator <Queue> QueueIterator = QueueList.iterator();
		while(QueueIterator.hasNext())
		{
			Queue tempQueObj = QueueIterator.next();
			if(tempQueObj.OutgoingBindingsCount==0 && tempQueObj.IncomingBindingsCount==0)
				RemoveQueue(tempQueObj.toString());
		}
	}
	
}
	
public static List <Exchange> GetExchangeList()
{
	return XMLRootClass.ExchangeList;
}

public static List<Queue> GetQueueList()
{
	return XMLRootClass.QueueList;
}

public static void SetFileName(String Filename)
{
	XMLRootClass.XMLFileName = Filename;
}

public static String GetFileName()
{
	return XMLRootClass.XMLFileName;
}

public static void SaveChanges()
{
	try {
		marsh.marshal(systemObject,new FileOutputStream(XMLRootClass.XMLFileName));
	} catch (Exception ex)
	{
		System.out.println("Exception!!!");
	}
}

public static void CloseApplication()
{
	window.CloseWindow();
	System.exit(0);
}

public static boolean OpenConnectionToServer()
{
	try {
		factory = new ConnectionFactory();
		//factory.setHost("localhost");
		factory.setHost(window.GetServerIP());
		connection = factory.newConnection();
		channel = connection.createChannel();
		window.ShowInStatusText("Connection to server is successful");
		return true;
	}
	catch (Exception ex) {
		window.ShowInStatusText("ERROR occured when creating new connection/channel");
		return false;
	}
}

public static void CloseConnectionToServer()
{
	try {
		channel.close();
		connection.close();
		window.ShowInStatusText("Closed connection to server");
	}
	catch (Exception ex) {
		window.ShowInStatusText("ERROR occured when closing connection/channel");
	}
}


public static void main(String[] args) throws Exception { 
    jaxbContext = JAXBContext.newInstance(XMLRootClass.class);
    marsh = jaxbContext.createMarshaller();
    marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    
    XMLRootClass.XMLFileName = JOptionPane.showInputDialog("Enter filename to load from:");
    System.out.println(XMLRootClass.XMLFileName);
    
    window = new SystemInterface();

    try {
    	systemObject = (XMLRootClass) jaxbContext.createUnmarshaller().unmarshal(new FileInputStream(XMLRootClass.XMLFileName));
    } catch (Exception ex)
    {
    	systemObject = new XMLRootClass();
    }
    
	window.open();
	*/
}

}
