package scripts.fc.api.interaction.impl.items;

import scripts.fc.api.interaction.ItemInteraction;
import scripts.fc.api.items.ItemUtils;

public class ItemOnItem extends ItemInteraction
{

	private String nameTwo;
	
	public ItemOnItem(String action, String nameOne, String nameTwo)
	{
		super(action, nameOne);
		this.nameTwo = nameTwo;
	}
	

	@Override
	protected boolean interact()
	{
		return ItemUtils.useItemOnItem(name, nameTwo);
	}

}
