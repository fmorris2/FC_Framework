package scripts.fc.api.interaction.impl.objects;

import org.tribot.api.Timing;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.ObjectInteraction;
import scripts.fc.api.mouse.AccurateMouse;

public class ItemOnObject extends ObjectInteraction
{
	private String itemName;
	private int itemId;
	
	public ItemOnObject(String action, String name, String itemName, int searchDistance)
	{
		super(action, name, searchDistance);
		this.itemName = itemName;
	}
	
	public ItemOnObject(String action, int id, String itemName, int searchDistance)
	{
		super(action, id, searchDistance);
		this.itemName = itemName;
	}
	
	public ItemOnObject(String action, RSObject object, int itemId)
	{
		super(action, object);
		this.itemId = itemId;
	}

	@Override
	protected boolean interact()
	{
		this.name = object.getDefinition().getName();
		RSItem[] items = itemName == null ? Inventory.find(itemId) : Inventory.find(itemName);
		
		if(items.length > 0)
		{
			if(!Game.isUptext(action + " " + itemName + " ->"))
			{
				if(items[0].click(action) && !Timing.waitCondition(FCConditions.uptextContains(action + " " + itemName + " -> " + name), 800))
					return false;
			}
			
			return AccurateMouse.click(object, "Use");
			//return DynamicClicking.clickRSObject(object, "Use " + itemName + " -> " + name);
		}
		
		return false;
	}
	
	@Override
	public boolean hoverEntity()
	{			
		findEntity();
		if(position != null)
		{
			if(!PathFinding.canReach(position, this instanceof ObjectInteraction))
				return false;
			
			if(Player.getPosition().distanceTo(position) < DISTANCE_THRESHOLD)
			{
				if(!position.getPosition().isOnScreen())
					Camera.turnToTile(position);
				
				RSItem[] items = itemName == null ? Inventory.find(itemId) : Inventory.find(itemName);
				if(items.length > 0 && !Game.isUptext(action + " "+ itemName + " -> "))
					items[0].click("Use");
						
				return entity.hover() && Game.isUptext(action + " " + itemName + " -> ");
			}
		}
		
		return false;
	}

}
