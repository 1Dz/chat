package chat.util;

import java.io.Serializable;

public class Message implements Serializable{

	private Type type;
	private String sender;
	private Object mess;
	private String addres;
	
	public Message()
	{
		
	}
	
	public Message(Type type, String sender, Object mess)
	{
		this.type = type;
		this.sender = sender;
		this.mess = mess;
	}
	
	public Message(Type type, String sender, String addres, Object mess)
	{
		this.type = type;
		this.sender = sender;
		this.addres = addres;
		this.mess = mess;
	}
	
	public Type getType() {
		return type;
	}

	public String getSender() {
		return sender;
	}

	public Object getMess() {
		return mess;
	}
	
	public String getAddres()
	{
		return addres;
	}
}
