package chat.Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import chat.ConsoleHelper;
import chat.util.Connection;
import chat.util.Message;
import chat.util.Type;

public class Client {

	private String name;
	private Connection connection;
	private boolean ifConnected;
	private List<String> usersList;
	
	public Client()
	{
		
	}
	
	public void run()
	{
		SocketHandler socketHandler = new SocketHandler();
		socketHandler.setDaemon(true);
		socketHandler.start();
		try{
			synchronized (this) {
				this.wait();
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		if(ifConnected)
		{
			String s;
			while((s = ConsoleHelper.readLine()) != null)
			{
				connection.send(new Message(Type.PUBLIC, this.name, s));
			}
		}
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	

	class SocketHandler extends Thread
	{

		@Override
		public void run() {
			ConsoleHelper.writeMessage(ConsoleHelper.message("serverInit"));
			
			try {
				String s = ConsoleHelper.readLine();
				int x = ConsoleHelper.readInt();
				Socket socket = new Socket(s, x);
				Client.this.connection = new Connection(socket);
				incomingToServ();
				clientMainLoop();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void incomingToServ()
		{
			ConsoleHelper.writeMessage(ConsoleHelper.message("introduce"));
			String name = ConsoleHelper.readLine();
			Client.this.setName(name);
			
			Message message = new Message(Type.INCOMING, name, null);
			connection.send(message);
			while(true)
			{
				message = connection.recieve();
				if(message.getType().equals(Type.WELCOME)){
					ConsoleHelper.writeMessage(String.format(ConsoleHelper.message("welcome"), Client.this.name));
					message = connection.recieve();
					usersList = (List) message.getMess();
					notifyConnectionStatusChanged(true);
					break;
				}
				if(message.getType().equals(Type.ERROR))
					ConsoleHelper.writeMessage(ConsoleHelper.message("wrongname"));
			}
		}
		public void clientMainLoop()
		{
			while(true)
			{
				Message message = connection.recieve();
				ConsoleHelper.writeMessage(message.getSender() + ": " + message.getMess());
			}
		}
		protected void notifyConnectionStatusChanged(boolean clientConnected) {


            Client.this.ifConnected = clientConnected;

            synchronized (Client.this) {
                Client.this.notify();
            }
}
		
	}
	
}
