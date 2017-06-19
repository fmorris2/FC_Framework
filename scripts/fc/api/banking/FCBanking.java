package scripts.fc.api.banking;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSItem;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.impl.objects.ClickObject;
import scripts.fc.api.items.FCItem;
import scripts.fc.api.items.FCItemList;

public class FCBanking
{	
	private final static String DEPOSIT_BOX_NAME = "Bank deposit box";
	
	public static boolean withdraw(FCItemList itemList)
	{
		if(itemList == null)
			return false;
		
		if(itemList.hasListInInv())
			return true;
		
		final int TOTAL_IN_INV = itemList.getTotalInInventory();
		final int TOTAL_ITEMS_NEEDED = itemList.getRemainingCountNeeded();
		final int ERRONEOUS_ITEMS = itemList.getErroneousItemsInInv();
		
		General.println("TOTAL IN INV: " + TOTAL_IN_INV);
		General.println("ERRONEOUS_ITEMS: " + ERRONEOUS_ITEMS);
		General.println("TOTAL_ITEMS_NEEDED: " + TOTAL_ITEMS_NEEDED);
		
		if(TOTAL_IN_INV + TOTAL_ITEMS_NEEDED + ERRONEOUS_ITEMS > 28) //DOES NOT HAVE ENOUGH SPACE
			makeSpace(itemList);
		else //HAS ENOUGH SPACE
			return itemList.withdraw();
		
		return false;
	}
	
	private static void makeSpace(FCItemList itemList)
	{
		//TODO DEPOSIT INV IF FASTER
		//General.println("MAKE SPACE");
		for(RSItem invItem : Inventory.getAll())
		{
			if(Inventory.getCount(invItem.getID()) == 0)
				continue;
			
			final int ID = invItem.getID();
			final String NAME = invItem.getDefinition().getName();
			
			final FCItem LIST_ITEM = itemList.find(ID, NAME);
			
			if(LIST_ITEM != null)
			{
				if(LIST_ITEM.hasOverflow())
					makeSpaceListItem(LIST_ITEM);
			}
			else
				depositItem(invItem.getID(), 0);
					
		}
	}
	
	public static void depositItem(int id, int count)
	{
		final int INV_COUNT = Inventory.getCount(id);
		final int INV_SIZE = Inventory.getAll().length;
		boolean success = false;
		
		if(INV_COUNT == 1 && Clicking.click(Inventory.find(id)[0])) //LENGTH CHECK NOT NECESSARY
			success = true;
		else if(Banking.deposit(count, id))
			success = true;
				
		
		if(success)
			Timing.waitCondition(FCConditions.inventoryChanged(INV_SIZE), 3500);
	}
	
	private static void makeSpaceListItem(FCItem listItem)
	{
		final int OVERFLOW = listItem.getInvCount(listItem.isStackable()) - listItem.getRequiredInvSpace();
		final int INV_SIZE = Inventory.getAll().length;
		
		//General.println("makeSpaceListItem: ");
		//General.println("OVERFLOW: " + OVERFLOW);
		//General.println("INV_SIZE: " + INV_SIZE);
		
		if(OVERFLOW > 0 && listItem.deposit(OVERFLOW))
			Timing.waitCondition(FCConditions.inventoryChanged(INV_SIZE), 3500);
	}
	
	public static boolean isNearDepositBox(int distance)
	{
		return Objects.find(distance, DEPOSIT_BOX_NAME).length > 0;
	}
	
	public static boolean openDepositBox(int distance)
	{
		General.println("Open deposit box");
		return new ClickObject("Deposit", DEPOSIT_BOX_NAME, distance).execute() && Timing.waitCondition(FCConditions.DEPOSIT_BOX_OPEN_CONDITION, 3500);
	}
}
