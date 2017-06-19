package scripts.fc.api.interaction;

import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import scripts.fc.api.wrappers.FCTiming;

public abstract class ItemInteraction extends EntityInteraction
{
	public ItemInteraction(String action, String name)
	{
		super(action, name);
	}

	protected RSItem item;
	
	@Override
	protected abstract boolean interact();

	@Override
	protected void findEntity()
	{
		RSItem[] items = null;
		
		if(id <= 0)
			items = Inventory.find(Filters.Items.nameEquals(name));
		else
			items = Inventory.find(id);
		
		if(items.length > 0)
		{
			item = items[0];
			entity = items[0];
		}
	}
	
	@Override
	protected void prepareInteraction()
	{}
	
	@Override
	public boolean hoverEntity()
	{
		findEntity();
		
		if(item != null)
			return item.click("Use") && FCTiming.waitCondition(() -> Game.isUptext("Use " + name + "->"), 1500);
		
		return false;
	}
}
