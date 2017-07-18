package scripts.fc.api.interaction.impl.items;

import org.tribot.api2007.Banking;

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
		if(Banking.isBankScreenOpen())
			Banking.close();
		
		return ItemUtils.useItemOnItem(name, nameTwo);
	}

}
