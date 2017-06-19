package scripts.fc.framework.quest;

public abstract class QuestBool
{
	boolean normal;
	
	public QuestBool(boolean normal)
	{
		this.normal = normal;
	}
	
	public boolean validate()
	{
		if(normal)
			return value();
		
		return !value();
	}
	public abstract boolean value();
}
