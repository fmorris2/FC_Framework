package scripts.fc.api.items;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		try
		{
			Pattern pattern = Pattern.compile("Information about '{1}(.*)'{1}");
			URLConnection con = new URL("https://www.runelocus.com/item-details/?item_id="+id).openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while((inputLine = in.readLine()) != null)
			{
				Matcher m = pattern.matcher(inputLine);
				if(m.find())
					return m.group(1);
			}
			
			in.close();		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
