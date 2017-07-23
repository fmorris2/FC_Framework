package scripts.fc.bodyguard.bodyguard_server;

import java.io.Serializable;

public class BodyguardResponse implements Serializable
{
	private static final long serialVersionUID = 4308232407069222222L;
	
	private ResponseStatus status;
	
	public BodyguardResponse(ResponseStatus status)
	{
		this.status = status;
	}
	
	public ResponseStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(ResponseStatus s)
	{
		status = s;
	}
	
	public String toString()
	{
		return "Status: " + status;
	}
}
