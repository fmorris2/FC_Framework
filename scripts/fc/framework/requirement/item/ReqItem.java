package scripts.fc.framework.requirement.item;

import org.tribot.api2007.types.RSItem;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.mission.impl.OneTaskMission;
import scripts.fc.framework.task.Task;

public class ReqItem
{
	public int playerAmt;
	
	private int id;
	private int amt;
	private boolean shouldUseGE; //if we should use the GE... if we don't have enough gp it will resort to using either the prereq missions or worker
	private Mission[] preReqMissions;
	
	private ReqItem(int id, int amt, boolean useGE)
	{
		this.id = id;
		this.amt = amt;
		shouldUseGE = useGE;
	}
	
	public ReqItem(int id, int amt, boolean useGE, Mission... preReqMissions)
	{
		this(id, amt, useGE);
		this.preReqMissions = preReqMissions;
	}
	
	public ReqItem(int id, int amt, boolean useGE, Task task)
	{
		this(id, amt, useGE);
		preReqMissions = new Mission[]{new OneTaskMission(task, "Pre req mission: ("+id+"x"+amt+")")};
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getAmt()
	{
		return amt;
	}
	
	public boolean isSatisfied()
	{
		return amt <= playerAmt;
	}
	
	public Mission[] getPreReqMissions()
	{
		return preReqMissions;
	}
	
	public boolean shouldUseGE()
	{
		return shouldUseGE;
	}
	
	public void check(RSItem[] items)
	{
		for(RSItem i : items)
			if(i.getID() == id || i.getID() == id + 1)
				playerAmt += i.getStack();
	}
	
	public String toString()
	{
		return "("+id+"x"+amt+")";
	}
}
