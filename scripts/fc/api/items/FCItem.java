package scripts.fc.api.items;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;

import scripts.fc.api.generic.FCConditions;

public class FCItem
{
	private final int MAX_CLICKING_AMT = 4;
	
	private String[] names = null;
	private int[] ids = null;
	private int amt;
	private boolean isStackable, isRequired = true;
	private Filter<RSItem> itemFilter;
	
	public FCItem(int amt, boolean isStackable, int... ids)
	{
		this.ids = ids;
		this.amt = amt;
		this.isStackable = isStackable;
		this.itemFilter = itemFilter();
	}
	
	public FCItem(int amt, boolean isStackable, String... names)
	{
		this.names = names;
		this.amt = amt;
		this.isStackable = isStackable;
		this.itemFilter = itemFilter();
	}
	
	public FCItem(int amt, boolean isStackable, boolean isRequired, int... ids)
	{
		this(amt, isStackable, ids);
		this.isRequired = isRequired;
	}
	
	public boolean isRequired()
	{
		return isRequired;
	}
	
	public int[] getIds()
	{
		return ids;
	}
	
	public int getAmt()
	{
		return amt;
	}
	
	public boolean isStackable()
	{
		return isStackable;
	}
	
	public String[] getNames()
	{
		return names;
	}
	
	public boolean deposit(int amt)
	{
		return Banking.depositItem(getRSItem(true), amt);
	}
	
	public boolean withdraw(int amt)
	{
		RSItem item = getRSItem(false);
		
		if(item == null)
			return false;
		
		if(amt <= MAX_CLICKING_AMT && amt != 0)
		{	
			for(int i = 0; i < amt; i++)
			{
				final int INV_SIZE = Inventory.getAll().length;
				if(Clicking.click(item))
					Timing.waitCondition(FCConditions.inventoryChanged(INV_SIZE), 3500);
			}
			
			return getInvCount(isStackable) >= amt;
		}
		
		return Banking.withdrawItem(item, amt);
	}
	
	public int getWithdrawAmt()
	{
		final int NEEDED_AMT = amt - getInvCount(isStackable);	
		return NEEDED_AMT < 0 ? 0 : NEEDED_AMT;
	}
	
	public RSItem getRSItem(boolean inInventory)
	{
		if(names != null)
		{
			RSItem[] items = !inInventory ? Banking.find(itemFilter) : Inventory.find(itemFilter);
			return items.length == 0 ? null : items[0];
		}
		
		RSItem[] items = !inInventory ? Banking.find(itemFilter) : Inventory.find(itemFilter);
		
		return items.length == 0 ? null : items[0];
	}
	
	public boolean isEquipped()
	{
		return Equipment.isEquipped(itemFilter);
	}
	
	public void equip()
	{
		ItemUtils.equipItem(itemFilter);
	}
	
	public int getRequiredInvSpace()
	{
		return isStackable ? 1 : amt;
	}
	
	public boolean hasOverflow()
	{
		return getInvCount(false) > getRequiredInvSpace();
	}
	
	public int getInvCount(boolean stackIncluded)
	{
		
		final int COUNT = getCount();
		
		if(isStackable && !stackIncluded && COUNT > 1)
			return 1;
		
		return COUNT;
	}
	
	public int getBankCount()
	{
		RSItem[] items = Banking.find(itemFilter);
		return items.length == 0 ? 0 : items[0].getStack();
	}
	
	private int getCount()
	{
		int count = 0;
		RSItem[] items = Inventory.find(itemFilter);
		
		for(RSItem i : items)
			count += i.getStack();
		
		return count;
	}
	
	public boolean equals(int id, String name)
	{
		if(names == null)
		{
			for(int i : ids)
				if(id == i)
					return true;
		}
		else
		{
			for(String real : names)
				if(name.contains(real))
					return true;
		}
		
		return false;
	}
	
	private Filter<RSItem> itemFilter()
	{
		return names == null ? Filters.Items.idEquals(ids) : Filters.Items.nameContains(names);
	}
}
