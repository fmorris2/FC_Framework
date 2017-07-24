package scripts.fc.bodyguard.bodyguard_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.tribot.api2007.types.RSTile;

import scripts.fc.bodyguard.bodyguard_client.Bodyguard;
import scripts.fc.bodyguard.bodyguard_client.commands.RemoveBodyguard;
import scripts.fc.bodyguard.requester_client.BodyguardRequest;

public class BodyguardServerThread extends Thread
{
	private Socket socket = null;
	
	public BodyguardServerThread(Socket socket)
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
				writeAndFlush(out, createResponse(request));
			}
			socket.close();
		} 
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("BodyguardThread ended!");
	}
	
	private void handleBodyguard(Bodyguard guard, ObjectOutputStream out, ObjectInputStream in)
	{
		int oldSize = -1;
		try
		{
			while(true)
			{
				writeAndFlush(out, new Integer(0));
				synchronized(BodyguardServer.bodyguards)
				{
					Bodyguard updated = new Bodyguard(BodyguardServer.bodyguards.get(BodyguardServer.bodyguards.indexOf(guard)));
					if(oldSize != updated.REQUESTS.size())
					{
						System.out.println("Bodyguard " + updated + " requests have changed! Updating client...");
						System.out.println("Updated size: " + updated.REQUESTS.size());
						writeAndFlush(out, updated);
						oldSize = updated.REQUESTS.size();
					}
				}
				sleep(500);
			}
		}
		catch(Exception e)
		{
			System.out.println("Socket has been closed for bodyguard: " + guard);
		}
		
		synchronized(BodyguardServer.bodyguards)
		{
			BodyguardServer.bodyguards.remove(guard);
			System.out.println("Bodyguard removed: " + guard);
			System.out.println("There are now " + BodyguardServer.bodyguards.size() + " registered bodyguards.");
		}	
	}
	
	private void writeAndFlush(ObjectOutputStream out, Object o) throws IOException
	{
		out.reset();
		out.writeObject(o);
		out.flush();
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
					.filter(b -> b.HOME_AREA.contains(requestPos))
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
				{
					guard.REQUESTS.add(request);
					System.out.println("Added request for guard " + guard);
					System.out.println("Requests size: " + guard.REQUESTS.size());
				}
			}
		}
		
		return response;
	}
}
