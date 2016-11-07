package chat.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable{
	
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	private final Socket socket;
	
	public Connection(Socket socket) throws IOException
	{
		this.socket = socket;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
	}
	
	public void send(Message message)
	{
		synchronized (out) {
			try {
				out.writeObject(message);
				out.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public Message recieve()
	{
		synchronized (in) {
			try {
				Message message = (Message) in.readObject();
				return message;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	public Socket getSocket() {
		return socket;
	}
	
	public void close()
	{
		try{
			in.close();
			out.close();
			socket.close();
		}
		catch(Exception e)
		{
			
		}
	}
	
}
