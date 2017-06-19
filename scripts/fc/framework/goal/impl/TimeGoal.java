package scripts.fc.framework.goal.impl;

import org.tribot.api.Timing;

import scripts.fc.framework.goal.Goal;

public class TimeGoal implements Goal
{
	private static final long serialVersionUID = -8154557185443540913L;
	
	private long startTime;
	private long timeAmount;
	
	public TimeGoal(long timeAmount)
	{
		this.startTime = Timing.currentTimeMillis();
		this.timeAmount = timeAmount;
	}

	@Override
	public boolean hasReached()
	{
		return Timing.timeFromMark(startTime) >= timeAmount;
	}

	@Override
	public String getCompletionMessage()
	{
		return "Goal Complete: Execute mission for " + Timing.msToString(timeAmount);
	}

	@Override
	public String getName()
	{
		return "Time goal: " + Timing.msToString(Timing.timeFromMark(startTime + timeAmount));
	}
	
	public String toString()
	{
		return "Time goal: " + Timing.msToString(timeAmount);
	}
	
	public void reset()
	{
		startTime = Timing.currentTimeMillis();
	}

}
