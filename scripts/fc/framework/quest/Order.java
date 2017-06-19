package scripts.fc.framework.quest;

public enum Order
{
	BEFORE,
	BEFORE_EQUALS,
	EQUALS,
	AFTER_EQUALS,
	AFTER;
	
	public boolean evaluate(int one, int two)
	{
		switch(this)
		{
			case BEFORE:
				return one - two < 0;
			case BEFORE_EQUALS:
				return one - two <= 0;
			case EQUALS:
				return one - two == 0;
			case AFTER_EQUALS:
				return one - two >= 0;
			case AFTER:
				return one - two > 0;
		}
		
		return false;
	}
}
