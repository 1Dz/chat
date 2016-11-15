package chat.Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
				if(s.equalsIgnoreCase("exit"))
				{
					connection.send(new Message(Type.EXIT, null, null));
					connection.close();
					ifConnected = false;
				}
				else if(s.startsWith("private:"))
				{
					String[] temp = s.split("%");
					sendPrivate(temp[2], temp[1]);
				}
				else if(s.startsWith("file%"))
				{
					String[] temp = s.split("%");
					System.out.println(temp[0] + temp[1] + temp[2]);
					sendFile(temp[2], temp[1]);
				}
				else 
					{
						connection.send(new Message(Type.PUBLIC, this.name, s));
						System.out.println("Message sent");
					}
				
			}
		}
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	private void sendFile(String path, String adress)
	{
		try {
			System.out.println(path + " --- " + adress);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
			byte[] outByte = new byte[bis.available()];
			bis.read(outByte);
			connection.send(new Message(Type.PRIVATE, this.name, adress, outByte));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPrivate(String message, String adress)
	{
		connection.send(new Message(Type.PRIVATE, this.name, adress, message));
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
		private void incomingToServ()
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
//					message = connection.recieve();
//					usersList = (List) message.getMess();
					notifyConnectionStatusChanged(true);
					break;
				}
				if(message.getType().equals(Type.ERROR))
					ConsoleHelper.writeMessage(ConsoleHelper.message("wrongname"));
			}
		}
		private void clientMainLoop()
		{
			while(true)
			{
				Message message = connection.recieve();
				if(message.getType().equals(Type.PRIVATE))
				{
					ConsoleHelper.writeMessage("PRIVATE from: " + message.getSender() + ": " + message.getMess());
				}
				else if(message.getType().equals(Type.FILE))
				{
					ConsoleHelper.writeMessage("FILE from: " + message.getSender() + ". Enter path to save");
					String temp = ConsoleHelper.readLine();
					recieveFile(message, temp);
				}
				else if(message.getType().equals(Type.PUBLIC))
				{
					ConsoleHelper.writeMessage(message.getSender() + " : " + message.getMess());
				}
			}
		}
		protected void notifyConnectionStatusChanged(boolean clientConnected) {


            Client.this.ifConnected = clientConnected;

            synchronized (Client.this) {
                Client.this.notify();
            }
		}
		
		private void recieveFile(Message message, String path)
		{
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
				
				bos.write((byte[]) message.getMess());
				bos.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
