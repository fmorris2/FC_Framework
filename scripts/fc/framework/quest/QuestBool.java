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
	
	public QuestBool and(QuestBool o)
	{
		QuestBool first = this;
		return new QuestBool(true)
		{
			@Override
			public boolean value()
			{
				return first.validate() && o.validate();
			}		
		};
	}
	
	public QuestBool or(QuestBool o)
	{
		QuestBool first = this;
		return new QuestBool(true)
		{
			@Override
			public boolean value()
			{
				return first.validate() || o.validate();
			}			
		};
	}
	
	public abstract boolean value();
}
