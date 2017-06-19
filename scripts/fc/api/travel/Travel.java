package scripts.fc.api.travel;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;

public class Travel
{
	public static boolean travelTo(Positionable p)
	{
		return WebWalking.walkTo(p);
	}
}
