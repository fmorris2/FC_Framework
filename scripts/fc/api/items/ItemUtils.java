package scripts.fc.api.items;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.fc.api.generic.FCConditions;

public class ItemUtils
{
	public static boolean useItemOnItem(String firstName, String secondName)
	{
		RSItem[] first = Inventory.find(firstName);
		RSItem[] second = Inventory.find(secondName);
		
		if(first.length > 0 && second.length > 0)
		{
			String upText = Game.getUptext();
			
			if(upText == null || !upText.contains(firstName))
			{
				if(first[0].click("Use") && Timing.waitCondition(FCConditions.uptextContains(firstName), 3500))
					return Clicking.click(second[0]);
			}
			
			return Clicking.click(second[0]);
		}
		
		return false;
	}
	
	public static boolean equipItem(String name)
	{
		if(GameTab.open(TABS.INVENTORY))
		{
			RSItem[] items = Inventory.find(name);
			equip(items);
		}
		
		return Equipment.isEquipped(name);
	}
	
	public static boolean equipItem(Filter<RSItem> filter)
	{
		if(GameTab.open(TABS.INVENTORY))
		{
			RSItem[] items = Inventory.find(filter);
			equip(items);
		}
		
		return Equipment.isEquipped(filter);
	}
	
	public static boolean equipItem(int id)
	{
		if(GameTab.open(TABS.INVENTORY))
		{
			RSItem[] items = Inventory.find(id);
			equip(items);
		}
		
		return Equipment.isEquipped(id);
	}
	
	private static void equip(RSItem[] items)
	{
		if(items.length > 0)
		{
			RSItemDefinition def = items[0].getDefinition();
			
			if(def != null && def.getActions().length > 1 && Clicking.click(items[0]))
				Timing.waitCondition(FCConditions.isEquipped(items[0].getID()), 3500);
		}
	}
	
	public static String getName(RSItem i)
	{
		RSItemDefinition def = i.getDefinition();
		return def == null ? "" : def.getName();
	}
	
	public static String getName(int id)
	{
		RSItemDefinition def = RSItemDefinition.get(id);
		if(def == null)
			return null;
		
		return def.getName();
	}
}
