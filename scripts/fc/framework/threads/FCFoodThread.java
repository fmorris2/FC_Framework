package scripts.fc.framework.threads;

import java.util.Arrays;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Combat;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.items.FCItem;
import scripts.fc.api.utils.Utils;
import scripts.fc.framework.data.Vars;

public class FCFoodThread extends Thread
{
	private static final int CYCLE_TIME = 100;
	
	private final int MIN_PERC, MAX_PERC, ATTEMPT_THRESH = 5;
	private final FCItem[] ITEMS;
	private int nextEatPerc;
	
	public FCFoodThread(int minPerc, int maxPerc, FCItem... items)
	{
		super("FC Food Thread");
		MIN_PERC = minPerc;
		MAX_PERC = maxPerc;
		ITEMS = items;
		nextEatPerc = getNextEatPerc();
	}
	
	public void run()
	{
		while(Vars.get().get("isRunning", true))
		{	
			try
			{
				if(Login.getLoginState() == STATE.INGAME && Combat.getHPRatio() <= nextEatPerc)
					eat();
				
				Thread.sleep(CYCLE_TIME);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void eat()
	{
		RSItem food = Arrays.stream(ITEMS)
				.map(i -> i.getRSItem(true))
				.filter(i -> i != null)
				.findFirst().orElse(null);
		
		if(food != null)
		{
			RSItemDefinition def = food.getDefinition();
			String name = def == null ? null : def.getName();
			General.println("[FCFoodThread] Eating " + (name != null ? name : food.getID()));
			int oldInvCount = Inventory.getAll().length;
			Utils.getMainScriptThread().suspend();
			Utils.getMouseMovementThread().interrupt();
			try
			{
				int attempts = 0;
				while(attempts < ATTEMPT_THRESH)
				{
					if(GameTab.open(TABS.INVENTORY) && Clicking.click(food) 
							&& Timing.waitCondition(FCConditions.inventoryChanged(oldInvCount), 1200))
					{
						General.println("[FCFoodThread] Sucessfully ate " + (name != null ? name : food.getID()));
						nextEatPerc = getNextEatPerc();
						break;
					}
				
				attempts++;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				Utils.getMainScriptThread().resume();
			}
		}
	}
	
	private int getNextEatPerc()
	{
		return General.random(MIN_PERC, MAX_PERC);
	}
}
