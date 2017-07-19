package scripts.fc.api.abc;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api2007.Combat;

import scripts.fc.framework.data.Vars;

public class ABC2Reaction
{
	private boolean hasStarted, isFixed;
	private long startTime, estimatedWait;
	
	public ABC2Reaction(boolean isFixed, long estimatedWait)
	{
		this.isFixed = isFixed;
		this.estimatedWait = estimatedWait;
	}
	
	public void start()
	{
		if(startTime == -1)
		{
			General.println("[ABC2]: Tracking reaction...");
			startTime = Timing.currentTimeMillis();
			setProfile(estimatedWait);
			abc2().generateTrackers();
		}
		else if(abc2().performTimedActions())
		{	
			General.println("[ABC2]: timed actions");
		}
		
		hasStarted = true;
	}
	
	public void react()
	{
		if(hasStarted && startTime != -1)
		{
			setProfile(getWaitTime());
			long reactionTime = abc2().generateReactionTime();
			General.println("[ABC2]: Performing reaction wait of " + reactionTime + "ms");
			abc2().sleep(reactionTime);
			startTime = -1;
			hasStarted = false;
		}
	}
	
	private void setProfile(long waitTime)
	{
		ABCProperties props = Vars.get().get("abc2Props");
		props.setWaitingTime(((Long)(getWaitTime())).intValue());
		props.setWaitingFixed(isFixed);
		props.setUnderAttack(Combat.isUnderAttack());
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
