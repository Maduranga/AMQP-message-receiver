import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class Exchange {
	Exchange() {}
	
	@XmlAttribute (name="Name")
	protected String ExchangeName;
	@XmlAttribute (name="Outgoing_Bindings_count")
	int OutgoingBindingsCount = 0;
	@XmlAttribute (name="Incoming_Bindings_count")
	int IncomingBindingsCount = 0;
	
	
	@XmlElement (name="Bindings")
	List <String> BindingsList = new ArrayList<String>();
	
	@XmlElement (name="Bindings_Exchanges")
	List <String> BoundExchangeList = new ArrayList<String>();
	
	@XmlElement (name="Bindings_Routing_keys")
	List <String> BoundExchangeKeys = new ArrayList<String>();
	
	public Exchange(String exchName)
	{
		ExchangeName = exchName;
	}
	
	public void AddBinding(String destinationName, String routingKey)
	{
		BoundExchangeList.add(destinationName);
		BoundExchangeKeys.add(routingKey);
		OutgoingBindingsCount++;
	}
	
	
	public void RemoveBinding(String destinationName)
	{
		if(BoundExchangeList.contains(destinationName))
		{
			int idx = BoundExchangeList.indexOf(destinationName);
			BoundExchangeList.remove(idx);
			BoundExchangeKeys.remove(idx);
			OutgoingBindingsCount--;
		}
	}
	
	public void IncreaseIncomingBindingsCount ()
	{
		IncomingBindingsCount++;
	}
	public void DecreaseIncomingBindingsCount ()
	{
		IncomingBindingsCount--;
	}
	
	public String toString()
	{
		return ExchangeName;
	}
	
	public List<String> GetBoundExchangesList()
	{
		return BoundExchangeList;
	}
	
	public boolean equals(Object obj)
	{
		if(this.toString().equals(obj.toString()))
			return true;
		else
			return false;
	}
	

}
