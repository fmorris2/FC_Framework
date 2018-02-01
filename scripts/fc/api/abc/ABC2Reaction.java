package scripts.fc.api.abc;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCProperties;

import scripts.fc.framework.data.Vars;

public class ABC2Reaction
{
	private boolean hasStarted, isFixed, underAttack;
	private long startTime = -1, estimatedWait;
	
	public ABC2Reaction(boolean isFixed, long estimatedWait)
	{
		this.isFixed = isFixed;
		this.estimatedWait = estimatedWait;
	}
	
	public ABC2Reaction underAttack()
	{
		this.underAttack = true;
		return this;
	}
	
	public void start()
	{
		if(startTime == -1)
		{
			General.println("[ABC2]: Tracking reaction...");
			startTime = Timing.currentTimeMillis();
			setProfile(estimatedWait);
		}
		else if(abc2().performTimedActions())
		{	
			General.println("[ABC2]: timed actions");
		}
		
		hasStarted = true;
	}
	
	public boolean react()
	{
		if(hasStarted && startTime != -1)
		{
			setProfile(getWaitTime());
			long reactionTime = abc2().generateReactionTime();
			General.println("[ABC2]: Performing reaction wait of " + reactionTime + "ms");
			abc2().sleep(reactionTime);
			startTime = -1;
			hasStarted = false;
			return true;
		}
		
		return false;
	}
	
	private void setProfile(long waitTime)
	{
		ABCProperties props = Vars.get().get("abc2Props", new ABCProperties());
		props.setWaitingTime(((Long)(getWaitTime())).intValue());
		props.setWaitingFixed(isFixed);
		props.setUnderAttack(underAttack);
		abc2().generateTrackers();
	}
	
	private long getWaitTime()
	{
		return Timing.timeFromMark(startTime);
	}
	
	private PersistantABCUtil abc2()
	{
		return Vars.get().get("abc2");
	}
}
