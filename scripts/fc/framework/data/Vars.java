package scripts.fc.framework.data;

import org.tribot.api.util.ABCUtil;

import scripts.fc.api.abc.PersistantABCUtil;

public class Vars
{
	private static Bag instance = new Bag();

	private Vars()
	{

	}

	public static Bag get()
	{
		return instance;
	}
}
