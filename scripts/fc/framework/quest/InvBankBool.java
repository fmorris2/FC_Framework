package scripts.fc.framework.quest;

import java.util.Arrays;

import org.tribot.api2007.Banking;
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
		boolean inInv = Inventory.getCount(id) >= amt;
		
		if(!((type == TYPE.IN_ONE || type == TYPE.NOT_IN_EITHER) && inInv) && !BankBool.bankObserver.hasCheckedBank)
			BankBool.checkBank(BankBool.bankObserver);
		
		boolean inBank = BankBool.bankObserver.containsItem(id, amt)
				|| (Banking.isBankScreenOpen() && Arrays.stream(Banking.getAll()).allMatch(i -> i.getID() == id && i.getStack() >= amt));
		
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
