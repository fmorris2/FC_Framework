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
	
	private final List<RSItem> bankCache =  new ArrayList<>();
	
	public FCBankObserver()
	{
		start();
	}
	
	public void clear() {
		hasCheckedBank = false;
		bankCache.clear();
	}
	
	@Override
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
						final RSItem[] bank = Banking.getAll();
						bankCache.clear();
						bankCache.addAll(Arrays.asList(bank));
						hasCheckedBank = true;
					}
				}
				
				sleep(CYCLE_TIME);
			}
			catch(final Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean containsItem(final Filter<RSItem> filter)
	{
		return bankCache.stream().anyMatch(i -> filter.accept(i) && MEMBERS_FILTER.accept(i));
	}
	
	public boolean containsItem(final int id, final int amt)
	{
		return bankCache.stream().anyMatch(i -> i.getID() == id && i.getStack() >= amt && MEMBERS_FILTER.accept(i));
	}
	
	public boolean containsItem(final String name, final int amt)
	{
		return bankCache.stream().anyMatch(i -> 
		{
			final RSItemDefinition def = i.getDefinition(); 
			return def != null && def.getName().equals(name) && i.getStack() >= amt && MEMBERS_FILTER.accept(i);
		});
	}
	
	private Filter<RSItem> membersFilter()
	{
		return new Filter<RSItem>()
		{
			@Override
			public boolean accept(final RSItem i)
			{
				return !i.getDefinition().isMembersOnly() || WorldHopper.isMembers(WorldHopper.getWorld());
			}
		};
	}
	
	public RSItem getItem(final Filter<RSItem> filter)
	{
		return bankCache.stream().filter(i -> filter.accept(i)).findFirst().orElse(null);
	}
	
	public int getCount(final int id)
	{
		final Optional<RSItem> opt = bankCache.stream().filter(i -> i.getID() == id).findFirst();
		return opt.isPresent() ? opt.get().getStack() : 0;
	}
	
	public RSItem[] getItemArray()
	{
		return bankCache.toArray(new RSItem[bankCache.size()]);
	}
	
}
