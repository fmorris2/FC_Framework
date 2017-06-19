package scripts.fc.api.interaction;

import org.tribot.api2007.GroundItems;
import org.tribot.api2007.types.RSGroundItem;

import scripts.fc.api.abc.ABC2Helper;

public abstract class GroundItemInteraction extends EntityInteraction
{
	protected RSGroundItem groundItem;
	
	public GroundItemInteraction(String action, String name)
	{
		super(action, name, 10);
	}

	@Override
	protected void findEntity()
	{
		RSGroundItem[] items = GroundItems.findNearest(name);
		
		if(items.length > 0)
		{
			groundItem = ABC2Helper.shouldUseClosest(abcOne, items) ? items[0] : items[1];
			entity = groundItem;
			position = groundItem;
		}
	}

}
