import java.io.IOException;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StyledText;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP.Exchange.DeleteOk;


public class ReceivingGUI extends Thread implements SelectionListener {
	
	private Display display;
	private Shell shlSenderWindow;
	private Text senderNameText;
	private Text recepientNameText;
	private Text messageIDText;
	private Button closeButton;
	private StyledText ticketText;
	private List queueList;
	private Button retrieveMessageButton;
	private boolean clicked;
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ReceivingGUI window = new ReceivingGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run()
	{
		open();
	}
	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		shlSenderWindow = new Shell();
		shlSenderWindow.setSize(373, 600);
		shlSenderWindow.setText("Receiver Window");
		
		Label senderNameLabel = new Label(shlSenderWindow, SWT.NONE);
		senderNameLabel.setBounds(23, 146, 111, 17);
		senderNameLabel.setText("Sender Name");
		
		senderNameText = new Text(shlSenderWindow, SWT.BORDER);
		senderNameText.setBounds(155, 136, 183, 27);
		
		Label recepientNameLabel = new Label(shlSenderWindow, SWT.NONE);
		recepientNameLabel.setBounds(23, 182, 111, 17);
		recepientNameLabel.setText("Recepient Name");
		
		recepientNameText = new Text(shlSenderWindow, SWT.BORDER);
		recepientNameText.setBounds(155, 172, 183, 27);
		
		Label TicketIdLabel = new Label(shlSenderWindow, SWT.NONE);
		TicketIdLabel.setBounds(23, 215, 111, 17);
		TicketIdLabel.setText("Ticket ID");
		
		messageIDText = new Text(shlSenderWindow, SWT.BORDER);
		messageIDText.setBounds(155, 205, 183, 27);
		
		Label ticketLabel = new Label(shlSenderWindow, SWT.NONE);
		ticketLabel.setBounds(23, 251, 70, 17);
		ticketLabel.setText("Ticket");
		
		ticketText = new StyledText(shlSenderWindow, SWT.BORDER);
		ticketText.setBounds(10, 287, 347, 183);
		
		closeButton = new Button(shlSenderWindow, SWT.NONE);
		closeButton.setBounds(233, 532, 105, 29);
		closeButton.setText("Close");
		closeButton.addSelectionListener(this);
		
		queueList = new List(shlSenderWindow, SWT.V_SCROLL|SWT.H_SCROLL);
		queueList.setBounds(23, 33, 111, 92);
		
		Label queueToConnectLabel = new Label(shlSenderWindow, SWT.NONE);
		queueToConnectLabel.setBounds(10, 10, 124, 17);
		queueToConnectLabel.setText("Queue to connect");
		
		retrieveMessageButton = new Button(shlSenderWindow, SWT.NONE);
		retrieveMessageButton.setBounds(191, 65, 122, 29);
		retrieveMessageButton.setText("Retrieve Message");
		retrieveMessageButton.addSelectionListener(this);
			
		UpdateExchangeList(MessageReceiverHighLevelClass.GetQueueList());
		
		shlSenderWindow.open();
		shlSenderWindow.layout();
		while (!shlSenderWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}


	public void UpdateExchangeList(java.util.List <Queue> QueueList)
	{
		queueList.removeAll();
		
		for(int idx=0; idx<QueueList.size(); ++idx)
		{
			String exchName = QueueList.get(idx).toString();
	
			queueList.add(exchName);
		}	
	}
	
	public void CloseWindow()
	{
		display.close();
	}
	
	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		
		if(retrieveMessageButton.isFocusControl())
		{
			if(!clicked)
			{
				try {
					MessageReceiverHighLevelClass.ReceiveMessage(queueList.getItem(queueList.getSelectionIndex()));
					retrieveMessageButton.setText("Next Message");
					clicked = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else
				MessageReceiverHighLevelClass.GetNextMessage();
		}
		else if(closeButton.isFocusControl())
		{
			MessageReceiverHighLevelClass.CloseApplication();
		}
	}
	
	
	public void DisplayMessageContents(MessageSenderHighlevelClass.XMLMessageRootClass messageObject)
	{
		senderNameText.setText(messageObject.InfoList.get(0));
		recepientNameText.setText(messageObject.InfoList.get(1));
		messageIDText.setText(messageObject.InfoList.get(2));
		ticketText.setText(messageObject.InfoList.get(3));
	}
}
