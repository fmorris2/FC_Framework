package scripts.fc.bodyguard.requester_client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;

import scripts.fc.api.wrappers.SerializablePositionable;
import scripts.fc.bodyguard.bodyguard_server.BodyguardResponse;
import scripts.fc.bodyguard.bodyguard_server.BodyguardServer;

public class BodyguardRequest implements Serializable
{
	private static final long serialVersionUID = 8041399005009389103L;
	
	public final String REQUESTER_NAME;
	public final SerializablePositionable REQUESTER_POS;
	public final int RADIUS;
	public final String[] TARGETS;
	
	private BodyguardResponse response;
	
	public BodyguardRequest(String requesterName, Positionable requesterLocation, int radius, String... bodyguardTargets)
	{
		REQUESTER_NAME = requesterName;
		REQUESTER_POS = new SerializablePositionable(requesterLocation);
		RADIUS = radius;
		TARGETS = bodyguardTargets;
	}
	
	public BodyguardResponse send()
	{
		General.println("Attempting to send bodyguard request: " + this);
		try
		(
			Socket socket = new Socket(BodyguardServer.HOST_NAME, BodyguardServer.PORT);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		)
		{
			out.writeObject(this);
			General.println("Successfully sent bodyguard request to server. Waiting for response...");
			response = (BodyguardResponse)in.readObject();
			General.println("Bodyguard response received! Details: " + response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public BodyguardResponse getResponse()
	{
		return response;
	}
	
	public String toString()
	{
		return "name: " + REQUESTER_NAME + ", pos: " + REQUESTER_POS + ", radius: " + RADIUS; 
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((REQUESTER_NAME == null) ? 0 : REQUESTER_NAME.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BodyguardRequest other = (BodyguardRequest) obj;
		if (REQUESTER_NAME == null)
		{
			if (other.REQUESTER_NAME != null)
				return false;
		} else if (!REQUESTER_NAME.equals(other.REQUESTER_NAME))
			return false;
		return true;
	}
	
	
}
