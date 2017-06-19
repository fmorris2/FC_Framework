package scripts.fc.api.utils;

import java.awt.Rectangle;

import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.types.RSMenuNode;

public class ChooseOptionUtils
{
	public static Rectangle getArea(String option)
	{
		for(RSMenuNode node : ChooseOption.getMenuNodes())
		{
			if(node == null || !node.containsAction(option))
				continue;
			
			return node.getArea();
		}
		
		return null;
	}
	
	public static Rectangle getArea(String option, Clickable clickable)
	{
		for(RSMenuNode node : ChooseOption.getMenuNodes())
		{
			if(node == null || !node.containsAction(option) || !node.correlatesTo(clickable))
				continue;
			
			return node.getArea();
		}
		
		return null;
	}
}
