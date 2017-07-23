package scripts.fc.bodyguard.bodyguard_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scripts.fc.bodyguard.bodyguard_client.Bodyguard;

public class BodyguardServer
{
	public static final String HOST_NAME = "127.0.0.1";//"bodyguard.vikingsoftware.org";
	public static final int PORT = 43594;
	
	public static List<Bodyguard> bodyguards = Collections.synchronizedList(new ArrayList<>());
	
	public static void main(String[] args)
	{
		try (ServerSocket server = new ServerSocket(PORT))
		{
			System.out.println("Running socket server on port " + PORT);
			while(true)
			{
				new BodyguardThread(server.accept()).start();
			}
		} 
		catch (IOException e)
		{
			System.err.println("Could not listen on port " + PORT);
			System.exit(-1);
		}
	}
}
