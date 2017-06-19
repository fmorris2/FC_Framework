package scripts.fc.api.interaction.impl.grounditems;

import org.tribot.api.DynamicClicking;

import scripts.fc.api.interaction.GroundItemInteraction;
import scripts.fc.api.mouse.AccurateMouse;

public class PickUpGroundItem extends GroundItemInteraction
{

	public PickUpGroundItem(String name)
	{
		super("Take", name);
	}

	@Override
	protected boolean interact()
	{
		return AccurateMouse.click(groundItem, "Take " + name);
		//return DynamicClicking.clickRSGroundItem(groundItem, "Take " + name);
	}

}
