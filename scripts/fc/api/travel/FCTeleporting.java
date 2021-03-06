package scripts.fc.api.travel;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.framework.data.Vars;

public class FCTeleporting
{
	private final static RSArea LUMBRIDGE_SPAWN = new RSArea(new RSTile(3221, 3218, 0), 10);
	private final static int HOME_TELE_THRESH = 30; //30 mins
	
	public static boolean canHomeTele()
	{
		return Timing.timeFromMark(Vars.get().get("lastHomeTele", Timing.currentTimeMillis())) < HOME_TELE_THRESH;
	}
	
	public static boolean homeTeleport()
	{
		final int MAGIC_MASTER = 218;
		final int HOME_TELE = 1;
		
		if(GameTab.open(TABS.MAGIC))
		{
			RSInterface teleInter = Interfaces.get(MAGIC_MASTER, HOME_TELE);
			
			if(teleInter != null && !teleInter.isHidden())
			{
				if(Clicking.click(teleInter))
				{
					Vars.get().addOrUpdate("lastHomeTele", Timing.currentTimeMillis());
					if(Timing.waitCondition(FCConditions.animationChanged(-1), 2000))
						return Timing.waitCondition(FCConditions.inAreaCondition(LUMBRIDGE_SPAWN), 15000);
				}
			}
		}
		
		return false;
	}
}
