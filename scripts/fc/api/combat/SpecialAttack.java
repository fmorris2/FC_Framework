package scripts.fc.api.combat;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSItem;

public class SpecialAttack 
{
	
	public static void activate()
	{
		openTab();
		setSpec();	
	}
	
	public static int getSpecPercent() 
 	{
        return  (Game.getSetting(300)/10);
    }
 	
 	public static void setSpec() 
 	{
        if (!specEnabled()) 
        {
            Mouse.clickBox(579, 419, 700, 428, 1);
            General.sleep(1000, 1125);
        }
    }
 	
 	public static boolean weildingSpecWep(int specwep) 
 	{
		for (RSItem i : Interfaces.get(387, 28).getItems()) 
		{
			if(i.getID() == specwep)
				return true;
		}
		return false;
	}
 	
 	public static void openTab() {
        if (!GameTab.getOpen().equals(GameTab.TABS.COMBAT)) 
        {
        	if(!GameTab.open(TABS.COMBAT))
        	{
        		Keyboard.pressFunctionKey(1);
        	}
        }
    }
 	
 	public static boolean specEnabled() 
 	{
        if (Game.getSetting(301) == 1) 
        {
            return true;
        }
        return false;
    }
}
