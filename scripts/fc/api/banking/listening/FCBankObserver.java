package scripts.fc.api.banking.listening;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.fc.api.generic.FCConditions;

public class FCBankObserver extends Thread
{
	private final int CYCLE_TIME = 50;
	private final Filter<RSItem> MEMBERS_FILTER = membersFilter();
	
	public boolean isRunning = true;
	public boolean hasCheckedBank = false;
	
	private List<RSItem> bankCache =  new ArrayList<>();
	
	public FCBankObserver()
	{
		start();
	}
	
	public void run()
	{
		while(isRunning)
		{
			try
			{
				if(Banking.isBankScreenOpen() && FCConditions.BANK_LOADED_CONDITION.active())
				{
					synchronized(this)
					{
						RSItem[] bank = Banking.getAll();
						bankCache.clear();
						bankCache.addAll(Arrays.asList(bank));
						hasCheckedBank = true;
					}
				}
				
				sleep(CYCLE_TIME);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean containsItem(Filter<RSItem> filter)
	{
		return bankCache.stream().anyMatch(i -> filter.accept(i) && MEMBERS_FILTER.accept(i));
	}
	
	public boolean containsItem(int id, int amt)
	{
		return bankCache.stream().anyMatch(i -> i.getID() == id && i.getStack() >= amt && MEMBERS_FILTER.accept(i));
	}
	
	public boolean containsItem(String name, int amt)
	{
		return bankCache.stream().anyMatch(i -> 
		{
			RSItemDefinition def = i.getDefinition(); 
			return def != null && def.getName().equals(name) && i.getStack() >= amt && MEMBERS_FILTER.accept(i);
		});
	}
	
	private Filter<RSItem> membersFilter()
	{
		return new Filter<RSItem>()
		{
			@Override
			public boolean accept(RSItem i)
			{
				return !i.getDefinition().isMembersOnly() || WorldHopper.isMembers(WorldHopper.getWorld());
			}
		};
	}
	
	public RSItem getItem(Filter<RSItem> filter)
	{
		return bankCache.stream().filter(i -> filter.accept(i)).findFirst().orElse(null);
	}
	
	public int getCount(int id)
	{
		Optional<RSItem> opt = bankCache.stream().filter(i -> i.getID() == id).findFirst();
		return opt.isPresent() ? opt.get().getStack() : 0;
	}
	
	public RSItem[] getItemArray()
	{
		return bankCache.toArray(new RSItem[bankCache.size()]);
	}
	
}
