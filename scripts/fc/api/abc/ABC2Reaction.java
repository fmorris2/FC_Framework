package scripts.fc.api.abc;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api2007.Combat;

import scripts.fc.framework.data.Vars;

public class ABC2Reaction
{
	private String name;
	private boolean hasStarted, isFixed;
	private long estimatedWait;
	
	public ABC2Reaction(String name, boolean isFixed, long estimatedWait)
	{
		this.name = name;
		this.isFixed = isFixed;
		this.estimatedWait = estimatedWait;
	}
	
	public void start()
	{
		if(getStartTime() == -1)
		{
			General.println("[ABC2]: Tracking reaction for " + name);
			Vars.get().addOrUpdate(name, Timing.currentTimeMillis());
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
		if(hasStarted && getStartTime() != -1)
		{
			setProfile(getWaitTime());
			long reactionTime = abc2().generateReactionTime();
			General.println("[ABC2]: Performing reaction wait of " + reactionTime + "ms");
			abc2().sleep(reactionTime);
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
		return Timing.timeFromMark(getStartTime());
	}
	
	private long getStartTime()
	{
		return Vars.get().get(name, new Long(-1));
	}
	
	private PersistantABCUtil abc2()
	{
		return Vars.get().get("abc2");
	}
}
