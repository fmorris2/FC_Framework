package scripts.fc.framework.quest;

public class InvBankBool extends QuestBool
{
	QuestBool bool;
	public InvBankBool(int id, int amt, boolean andRelationship, boolean normal)
	{
		super(normal);
		bool = andRelationship ? new ItemBool(id, amt, normal).and(new BankBool(id, amt, normal), normal)
					: new ItemBool(id, amt, normal).or(new BankBool(id, amt, normal), normal);
	}

	@Override
	public boolean value()
	{
		return bool.value();
	}
	
}
