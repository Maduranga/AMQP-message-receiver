import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.Exchange.DeleteOk;


//import HighLevelClass.XMLRootClass;


public class MessageSenderHighlevelClass {

	//static SendingGUI window;
	static JAXBContext jaxbContext_XMLRootClass;
    static Marshaller marsh_XMLRootClass;
    static JAXBContext jaxbContext_XMLMessageRootClass;
    static Marshaller marsh_XMLMessageRootClass;
    static HighLevelClass.XMLRootClass systemObject = null;
    static XMLMessageRootClass MessageObject = null;
    
    static List <Exchange> ExchangeList;
    static List <Queue> QueueList;
    static String QueueMessage;
    static String ServerIP;
    static String XMLFileName;
    
	static ConnectionFactory factory;
	static Connection connection;
	static Channel channel;
    
	@XmlRootElement	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class XMLMessageRootClass {
		
		//@XmlElement (name="Sender_name")
		String SenderName;
		
		//@XmlElement (name="Recepient_name")
		static String RecepientName = "";
		
		//@XmlElement (name="Exchange_name")
		static String ExchangeName = "";
		
		//@XmlElement (name="Routing_key")
		static String RoutingKey = "";
		
		//@XmlElement (name="Ticket_ID")
		static String MessageID = "";
		
		//@XmlElement (name="Ticket")
		static String Ticket = "";
		
		@XmlElement (name="InfoList")
		static List <String> InfoList = new ArrayList <String>();
		
		
		/*public static void GenerateMessage()
		{
			SenderName = TicketUI.GetSenderName();
			RecepientName= TicketUI.GetRecepientName();
			MessageID= TicketUI.GetMessageID();
			Ticket= TicketUI.GetTicket();
		}*/
		
	}
	
	
	public static void MessageReady()
	{
		//XMLMessageRootClass.GenerateMessage();
	}
	
	public static String MarshalMessageToString() throws IOException
	{
		try {
			jaxbContext_XMLMessageRootClass = JAXBContext.newInstance(XMLMessageRootClass.class);
			marsh_XMLMessageRootClass = jaxbContext_XMLMessageRootClass.createMarshaller();
			marsh_XMLMessageRootClass.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (Exception e) {
			System.out.println("Error in MarshalMessageToString - 1");
		}
	    
	    //MessageObject = new XMLMessageRootClass();
	    //MessageObject.SenderName = "MesgID";
	    
	    try {
			marsh_XMLMessageRootClass.marshal(MessageObject,new FileOutputStream("message.xml"));
		} catch (Exception ex)
		{
			System.out.println("Error in MarshalMessageToString - 2");
		}	  
		return readFile("message.xml");
	}
	
	public static String readFile(String path) throws IOException 
	{
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
	}
	
	
	public static void BuildMessage(String SenderName, String RecepientName, String MessageID, String Ticket, String ExchangeName, String RoutingKey) 
	{
		MessageObject = new XMLMessageRootClass();
		MessageObject.SenderName = SenderName;
		MessageObject.RecepientName = RecepientName;
		MessageObject.MessageID = MessageID;
		MessageObject.Ticket = Ticket;
		MessageObject.ExchangeName = ExchangeName;
		MessageObject.RoutingKey = RoutingKey;
		
		try {
			QueueMessage = MarshalMessageToString();
			System.out.println(QueueMessage);
		} catch (IOException e) {
			System.out.println("Error in Building message");
		}
		
		if(OpenConnectionToServer())
		{
			try {
				channel.basicPublish(ExchangeName, RoutingKey, null, QueueMessage.getBytes());
				JOptionPane.showMessageDialog(null,"Message published succesfully!", "Success", JOptionPane.OK_OPTION);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"ERROR occured when publishing to server.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			CloseConnectionToServer();
		}
		else
			System.out.println("Connection not succesful");
	}
	
	
	public static List <Exchange> GetExchangeList()
	{
		return ExchangeList;
	}
	
	public static List <Queue> GetQueueList()
	{
		return QueueList;
	}
		
	
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

	public static void CloseApplication()
	{
		//window.CloseWindow();
		//System.exit(0);
	}
	
	
	public static void main(String[] args) throws Exception {
		jaxbContext_XMLRootClass = JAXBContext.newInstance(HighLevelClass.XMLRootClass.class);
	    marsh_XMLRootClass = jaxbContext_XMLRootClass.createMarshaller();
	    marsh_XMLRootClass.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	  
	    XMLFileName = JOptionPane.showInputDialog("Enter filename to load from:");
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
		//System.out.println(MarshalMessageToString());
		
		//window = new SendingGUI();
		//window.open();
		
		
	}

}
