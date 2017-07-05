package scripts.fc.framework.quest;

import org.tribot.api2007.Inventory;

public class InvBankBool extends QuestBool
{
	QuestBool bool;
	private TYPE type;
	private int id, amt;
	public InvBankBool(int id, int amt, TYPE type, boolean normal)
	{
		super(normal);
		this.type = type;
		this.id = id;
		this.amt = amt;
	}

	@Override
	public boolean value()
	{
		boolean inBank = !BankBool.bankObserver.hasCheckedBank || BankBool.bankObserver.containsItem(id, amt);
		boolean inInv = Inventory.getCount(id) >= amt;
		
		switch(type)
		{
			case NOT_IN_EITHER:
				return !inBank && !inInv;
			case IN_ONE:
				return inBank || inInv;
			case IN_BOTH:
				return inBank && inInv;
			default:
				return false;
		}
	}
	
	public enum TYPE
	{
		NOT_IN_EITHER,
		IN_ONE,
		IN_BOTH
	}
	
	public String toString()
	{
		return "InvBankBool for ("+id+"x"+amt+"), TYPE: " + type + ": " + validate();
	}
	
}
