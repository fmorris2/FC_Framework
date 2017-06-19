package scripts.fc.framework.requirement.item;

import org.tribot.api2007.types.RSItem;

import scripts.fc.framework.mission.Mission;

public class ReqItem
{
	public int playerAmt;
	
	private int id;
	private int amt;
	private Mission[] preReqMissions;
	
	public ReqItem(int id, int amt, Mission... preReqMissions)
	{
		this.id = id;
		this.amt = amt;
		this.preReqMissions = preReqMissions;
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
	
	public void check(RSItem[] items)
	{
		for(RSItem i : items)
			if(i.getID() == id)
				playerAmt += i.getStack();
	}
}
