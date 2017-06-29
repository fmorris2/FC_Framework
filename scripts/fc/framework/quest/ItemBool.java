package scripts.fc.framework.quest;

import org.tribot.api2007.Inventory;

public class ItemBool extends QuestBool
{
	private String itemName;
	private int itemId, amt;
	
	public ItemBool(int itemId, int amt, boolean normal)
	{
		super(normal);
		this.itemId = itemId;
		this.amt = amt;
	}
	
	public ItemBool(String itemName, int amt, boolean normal)
	{
		super(normal);
		this.itemName = itemName;
		this.amt = amt;
	}

	@Override
	public boolean value()
	{
		return itemName == null ? Inventory.getCount(itemId) >= amt : Inventory.getCount(itemName) >= amt;
	}

}
