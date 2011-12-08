import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class Queue extends Exchange{

	//@XmlAttribute (name="Name")
	//private String QueueName;
	
	Queue() {}
	
	public Queue(String queueName) {
		//super(null);
		super(queueName);
		//QueueName = queueName;
	}
	
	/*public String toString()
	{
		//return QueueName;
	}*/

}
