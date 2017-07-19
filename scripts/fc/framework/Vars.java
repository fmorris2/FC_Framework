package scripts.fc.framework;

import org.tribot.api.util.ABCUtil;

import scripts.fc.api.abc.PersistantABCUtil;

public class Vars
{
	private static Vars vars = new Vars();
	
	public transient ABCUtil abc = new ABCUtil();
	public transient PersistantABCUtil abc2 = new PersistantABCUtil();

	private Vars()
	{

	}

	public static Vars get()
	{
		return vars;
	}
}
