package scripts.fc.framework.quest;

public class QuestState
{
	private QuestBool[] bools;
	
	public QuestState(QuestBool... bools)
	{
		this.bools = bools;
	}
	
	public QuestBool[] getBools()
	{
		return bools;
	}
	
	public boolean validate()
	{
		for(QuestBool bool : bools)
			if(!bool.validate())
				return false;
		
		return true;
	}
}
