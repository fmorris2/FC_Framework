package scripts.fc.api.skills.woodcutting.utils;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.fc.api.skills.woodcutting.data.Hatchet;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class WcUtils
{	
	public static Hatchet getHatchetOnCharacter()
	{
		for(int i = Hatchet.values().length - 1; i >= 0; i--)
		{
			Hatchet h = Hatchet.values()[i];
			
			if(Skills.getActualLevel(SKILLS.WOODCUTTING) >= h.getWcLvl() && (Inventory.getCount(h.getId()) > 0 || Equipment.isEquipped(h.getId())))
				return h;
		
		}
		
		return null;
	}
	
	public static LogType getCurrentLogType(LogType target)
	{
		LogType appropriate = LogType.getAppropriate();
		
		if(appropriate.ordinal() >= target.ordinal())
			return target;
		
		return appropriate;
	}
}
