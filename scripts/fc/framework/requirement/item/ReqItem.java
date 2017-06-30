package scripts.fc.framework.requirement.item;

import java.util.List;

import org.tribot.api2007.types.RSItem;

import scripts.fc.framework.mission.Mission;

public abstract class ReqItem
{
	protected boolean shouldUseGE; //if we should use the GE... if we don't have enough gp it will resort to using either the prereq missions or worker
	protected Mission[] preReqMissions;
	
	
	public Mission[] getPreReqMissions()
	{
		return preReqMissions;
	}
	
	public boolean shouldUseGE()
	{
		return shouldUseGE;
	}
	
	public abstract void check(RSItem[] items);
	public abstract boolean isSatisfied();
	public abstract List<SingleReqItem> getSingleReqItems();
}
