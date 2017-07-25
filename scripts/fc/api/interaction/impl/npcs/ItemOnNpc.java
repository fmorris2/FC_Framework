package scripts.fc.api.interaction.impl.npcs;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.NpcInteraction;
import scripts.fc.api.mouse.AccurateMouse;

public class ItemOnNpc extends NpcInteraction
{
	private String itemName;
	private int itemId;
	
	public ItemOnNpc(String itemName, String npcName, int searchDistance)
	{
		this("Use", npcName, itemName, searchDistance);
	}
	
	public ItemOnNpc(String action, String name, String itemName, int searchDistance)
	{
		super(action, name, searchDistance);
		this.itemName = itemName;
	}

	@Override
	protected boolean interact()
	{	
		General.println("ItemOnNpc interact()");
		this.name = npc.getDefinition().getName();
		RSItem[] items = itemName == null ? Inventory.find(itemId) : Inventory.find(itemName);
		
		if(items.length > 0)
		{
			if(!Game.isUptext(action + " " + itemName + " ->"))
			{
				General.println("Need to click item...");
				if(!GameTab.open(TABS.INVENTORY))
					return false;
				
				if(items[0].click(action) && !Timing.waitCondition(FCConditions.uptextContains(action + " " + itemName + " ->"), 800))
					return false;
			}
			
			General.println("NPC in ItemOnNpc: " + npc.getID());
			return AccurateMouse.click(npc, "Use");
		}
		
		return false;
	}
	
	@Override
	public boolean hoverEntity()
	{		
		findEntity();
		if(position != null)
		{
			if(!PathFinding.canReach(position, false))
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
