package scripts.fc.framework.quest;

import scripts.fc.api.banking.listening.FCBankObserver;

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
		return !bankObserver.hasCheckedBank
					|| bankObserver.containsItem(id, amt);
	}

}
