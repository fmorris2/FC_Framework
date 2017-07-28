package scripts.fc.framework.requirement.item;

import java.util.Arrays;
import java.util.List;

import org.tribot.api2007.types.RSItem;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.quest.QuestBool;

public abstract class ReqItem
{
	protected boolean shouldUseGE; //if we should use the GE... if we don't have enough gp it will resort to using either the prereq missions or worker
	protected QuestBool[] bools;
	protected Mission[] preReqMissions;
	protected boolean isFutureReq;
	
	public ReqItem when(QuestBool... bools)
	{
		this.bools = bools;
		return this;
	}
	
	public CombinedReqItem or(ReqItem other)
	{
		return new CombinedReqItem(this, other, CombinedReqItem.Type.OR);
	}
	
	public CombinedReqItem and(ReqItem other)
	{
		return new CombinedReqItem(this, other, CombinedReqItem.Type.AND);
	}
	
	public Mission[] getPreReqMissions()
	{
		return preReqMissions;
	}
	
	public boolean shouldUseGE()
	{
		return shouldUseGE;
	}
	
	/**
	 * 
	 * @return true if the bool checks passed and we still need this requirement
	 */
	public boolean checkBools()
	{
		//if not all of the required bools for this to execute are validated, we don't need this requirement
		return bools == null || Arrays.stream(bools).allMatch(b -> b.validate());
	}
	
	public void setIsFutureReq(boolean b)
	{
		isFutureReq = b;
	}
	
	public abstract void check(RSItem[] items);
	public abstract boolean isSatisfied();
	public abstract List<SingleReqItem> getSingleReqItems();
}
