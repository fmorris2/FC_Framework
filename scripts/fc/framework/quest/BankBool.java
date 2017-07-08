package scripts.fc.framework.quest;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.WebWalking;

import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.api.travel.Travel;
import scripts.fc.api.wrappers.FCTiming;

public class BankBool extends QuestBool
{
	public static FCBankObserver bankObserver;
	
	private int id, amt;
	
	public BankBool(int id, int amt, boolean normal)
	{
		super(normal);
		this.id = id;
		this.amt = amt;
	}

	@Override
	public boolean value()
	{
		if(!bankObserver.hasCheckedBank)
			checkBank(bankObserver);
		
		return bankObserver.containsItem(id, amt);
	}
	
	public static void checkBank(FCBankObserver bankObserver)
	{
		while(!bankObserver.hasCheckedBank)
		{
			General.println("Going to check bank...");
			
			if(!Banking.isInBank())
			{
				if(Travel.walkToBank() && !FCTiming.waitCondition(() -> Banking.isInBank(), 4000))
					WebWalking.walkToBank();
			}
			else if(!Banking.isBankScreenOpen())
				Banking.openBank();
			
			General.sleep(1000);
		}
			
	}

}
