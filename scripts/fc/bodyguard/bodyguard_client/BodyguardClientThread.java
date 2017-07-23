package scripts.fc.bodyguard.bodyguard_client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.tribot.api.General;

import scripts.fc.bodyguard.bodyguard_server.BodyguardServer;

public class BodyguardClientThread extends Thread
{
	private Bodyguard bodyguard;
	
	public BodyguardClientThread(Bodyguard bodyguard)
	{
		this.bodyguard = bodyguard;
	}
	
	@Override
	public void run()
	{
		try
		(
			Socket socket = new Socket(BodyguardServer.HOST_NAME, BodyguardServer.PORT);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		)
		{
			out.writeObject(bodyguard);
			General.println("Successfully sent data to server... Waiting for first update!");
			while(true)
			{
				try
				{
					Object obj = in.readObject();
					if(obj instanceof Bodyguard)
					{
						Bodyguard updated = (Bodyguard)obj;
						int old = bodyguard.REQUESTS.size();
						updated.REQUESTS.stream()
							.filter(r -> !bodyguard.REQUESTS.contains(r))
							.forEach(r -> bodyguard.REQUESTS.add(r));
						
						General.println("Bodyguard orders updated! " + (bodyguard.REQUESTS.size() - old) + " requests have been added.");
					}
					sleep(400);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
