package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import chat.ConsoleHelper;
import chat.util.Connection;
import chat.util.Message;
import chat.util.Type;

public class Server {

	private ServerSocket serverSocket;
	private Map<String, Connection> handlerMap = new HashMap<>();
	
	public Server()
	{
		try {
			this.serverSocket = new ServerSocket(8080);
			serverMainLoop();
			
		} catch (IOException e) {
			ConsoleHelper.log("Server socet exception: " + e.getMessage(), getClass().getName(), "error");
		}
	}
	void serverMainLoop()
	{
		while(true)
		{
			Socket socket;
			try {
				socket = serverSocket.accept();
				ConsoleHelper.log("Connection accepted ", getClass().getName(), "info");
				Handler handler = new Handler(socket);
				handler.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	class Handler extends Thread
	{
		private Socket socket;
		private String hName;
		public Handler()
		{
			
		}
		public Handler(Socket socket)
		{
			this.socket = socket;
		}
		@Override
		public void run() {
			try(Connection connection = new Connection(socket)){
			while(true)
			{
					Message message = connection.recieve();
					if((message.getType()).equals(Type.INCOMING))
					{
						while(true)
						{
							if(isFreeName(message.getSender()))
							{
								setHName(message.getSender());
								handlerMap.put(message.getSender(), connection);
								ConsoleHelper.log("user " + message.getSender() + " added", getClass().getName(), "info");
								connection.send(new Message(Type.WELCOME, "Server", 
										String.format(ConsoleHelper.message("welcome"), message.getSender())));
								break;
							}
							else 
							{
								connection.send(new Message(Type.ERROR, "Server",
										String.format(ConsoleHelper.message("wrongname"), message.getSender())));
								message = connection.recieve();
							}
						}
					}
					if((message.getType()).equals(Type.PUBLIC))
					{
						System.out.println("Message recieved");
						broadCast(message);
					}
					if((message.getType()).equals(Type.PRIVATE))
					{
						sendPrivate(message);
					}
					if((message.getType()).equals(Type.EXIT))
						connection.close();
					System.out.println(handlerMap.size());
				}
			}
			catch(Exception e)
			{
				System.out.println("!Connection");
			}
		}
		private void broadCast(Message message)
		{
			for(Map.Entry<String, Connection> entry : handlerMap.entrySet())
			{
				entry.getValue().send(message);
				System.out.println("Message sent");
			}
		}
		private void sendPrivate(Message message)
		{
			handlerMap.get(message.getAddres()).send(message);
		}
		private boolean isFreeName(String name)
		{
			for(Map.Entry<String, Connection> entry : handlerMap.entrySet())
			{
				if(entry.getKey().equalsIgnoreCase(name))
					return false;
			}
			return true;
		}
		public void setHName(String name) {
			this.hName = name;
		}
		
		public String getHName()
		{
			return hName;
		}
		
	}
}
