package chat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleHelper {
	
	private static Properties properties = new Properties();
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private static InputStream in;
	private static Logger logger = LoggerFactory.getLogger(ConsoleHelper.class);
	
	public static String message(String request)
	{
		try{
			in = ConsoleHelper.class.getClassLoader().getResourceAsStream("phrases.properties");
			properties.load(in);
			return new String(properties.getProperty(request));
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static String readLine()
	{
		String s = null;
		try {
			s = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	public static void writeMessage(String message)
	{
		System.out.println(message);
	}
	
	public static int readInt()
	{
		while(true)
		{
			try {
				int x = Integer.parseInt(reader.readLine());
				return x;
			} catch (NumberFormatException | IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void log(String logMessage, String className, String type)
	{
		switch(type)
		{
			case("info"):
				logger.info(className + " for " + logMessage);
				break;
			case("error"):
				logger.error(className + " for " + logMessage);
				break;
		}
	}

}
