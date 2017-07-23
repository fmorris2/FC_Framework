package scripts.fc.bodyguard.bodyguard_client.commands;

import java.io.Serializable;

public class RemoveBodyguard implements Serializable
{
	private static final long serialVersionUID = 7738566522070443985L;
	
	public final String NAME;
	
	public RemoveBodyguard(String name)
	{
		NAME = name;
	}

}
