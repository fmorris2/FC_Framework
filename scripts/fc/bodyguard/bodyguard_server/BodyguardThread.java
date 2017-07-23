package scripts.fc.bodyguard.bodyguard_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.tribot.api2007.types.RSTile;

import scripts.fc.bodyguard.bodyguard_client.Bodyguard;
import scripts.fc.bodyguard.bodyguard_client.commands.RemoveBodyguard;
import scripts.fc.bodyguard.requester_client.BodyguardRequest;

public class BodyguardThread extends Thread
{
	private Socket socket = null;
	
	public BodyguardThread(Socket socket)
	{
		super("BodyguardThread");
		this.socket = socket;
	}

	public void run()
	{
		try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());)
		{
			Bodyguard bodyguard;
			BodyguardRequest request;
			Object obj = in.readObject();
			
			if(obj instanceof Bodyguard)
			{
				bodyguard = (Bodyguard)obj;
				registerBodyguard(bodyguard);
				handleBodyguard(bodyguard, out, in);
				System.out.println("Done handling logic for bodyguard: " + bodyguard);
			}
			else if(obj instanceof RemoveBodyguard)
				removeBodyguard((RemoveBodyguard)obj);
			else if(obj instanceof BodyguardRequest)
			{
				request = (BodyguardRequest)obj;
				System.out.println("Bodyguard request received: " + request);
				out.writeObject(createResponse(request));
			}	
			socket.close();
		} 
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleBodyguard(Bodyguard guard, ObjectOutputStream out, ObjectInputStream in)
	{
		int oldSize = -1;
		
		while(true)
		{
			try
			{
				if(oldSize != guard.REQUESTS.size())
				{
					System.out.println("Bodyguard " + guard + " requests have changed! Updating client...");
					out.writeObject(guard);
					oldSize = guard.REQUESTS.size();
				}
				
				sleep(500);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void removeBodyguard(RemoveBodyguard r)
	{
		synchronized(BodyguardServer.bodyguards)
		{
			Bodyguard toRemove = BodyguardServer.bodyguards.stream()
				.filter(b -> b.USERNAME.equals(r.NAME))
				.findFirst().orElse(null);
			
			if(toRemove != null)
				BodyguardServer.bodyguards.remove(toRemove);
		}
	}

	private void registerBodyguard(Bodyguard bodyguard)
	{
		synchronized(BodyguardServer.bodyguards)
		{
			BodyguardServer.bodyguards.remove(bodyguard);
			BodyguardServer.bodyguards.add(bodyguard);
			System.out.println("Registered bodyguard: " + bodyguard);
			System.out.println("There are now " + BodyguardServer.bodyguards.size() + " registered bodyguards.");
		}
	}
	
	private BodyguardResponse createResponse(BodyguardRequest request)
	{
		System.out.println("Creating bodyguard response for request: " + request);
		
		RSTile requestPos = request.REQUESTER_POS.getPosition().getPosition();
		BodyguardResponse response = new BodyguardResponse(ResponseStatus.FAILED);
		
		synchronized(BodyguardServer.bodyguards)
		{
			Bodyguard guard = BodyguardServer.bodyguards.stream()
					.sorted((b1, b2) -> requestPos.distanceTo(b1.HOME_TILE.getPosition()) - requestPos.distanceTo(b2.HOME_TILE.getPosition()))
					.findFirst().orElse(null);
			
			System.out.println("Found closest bodyguard match: " + guard);
			
			if(guard != null)
			{
				if(guard.REQUESTS.isEmpty() || guard.REQUESTS.peek().equals(request))
					response.setStatus(ResponseStatus.IN_PROGRESS);
				else
					response.setStatus(ResponseStatus.IN_QUEUE);
				
				if(!guard.REQUESTS.contains(request))
					guard.REQUESTS.add(request);
			}
		}
		
		return response;
	}
}
