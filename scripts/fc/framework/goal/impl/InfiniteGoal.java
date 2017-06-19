package scripts.fc.framework.goal.impl;

import scripts.fc.framework.goal.Goal;

public class InfiniteGoal implements Goal
{
	private static final long serialVersionUID = -2927611079897991487L;

	@Override
	public boolean hasReached()
	{
		return false;
	}

	@Override
	public String getCompletionMessage()
	{
		return "This will never be reached";
	}

	@Override
	public String getName()
	{
		return "Infinite";
	}
	
	public String toString()
	{
		return getName();
	}
}
