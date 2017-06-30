package scripts.fc.framework.requirement.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api2007.types.RSItem;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.mission.impl.OneTaskMission;
import scripts.fc.framework.task.Task;

public class SingleReqItem extends ReqItem
{
	public int playerAmt;
	
	private int id;
	private int amt;
	private boolean needsItem;
	
	private SingleReqItem(int id, int amt, boolean useGE, boolean needsItem)
	{
		this.id = id;
		this.amt = amt;
		shouldUseGE = useGE;
	}
	
	public SingleReqItem(SingleReqItem old, int amt)
	{
		this.id = old.id;
		this.shouldUseGE = old.shouldUseGE;
		this.needsItem = old.needsItem;
		this.preReqMissions = old.preReqMissions;
		this.amt = amt;
	}
	
	public SingleReqItem(int id, int amt, boolean useGE, boolean needsItem, Mission... preReqMissions)
	{
		this(id, amt, useGE, needsItem);
		this.preReqMissions = preReqMissions;
	}
	
	public SingleReqItem(int id, int amt, boolean useGE, boolean needsItem, Task task)
	{
		this(id, amt, useGE, needsItem);
		preReqMissions = task == null ? null : new Mission[]{new OneTaskMission(task, "Pre req mission: ("+id+"x"+amt+")")};
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getAmt()
	{
		return amt;
	}
	
	public int getPlayerAmt()
	{
		return playerAmt;
	}
	
	public boolean needsItem()
	{
		return needsItem;
	}
	
	@Override
	public boolean isSatisfied()
	{
		return amt <= playerAmt;
	}

	@Override
	public void check(RSItem[] items)
	{
		for(RSItem i : items)
			if(i.getID() == id || i.getID() == id + 1)
				playerAmt += i.getStack();
	}
	
	@Override
	public List<SingleReqItem> getSingleReqItems()
	{
		return new ArrayList<>(Arrays.asList(this));
	}
	
	public String toString()
	{
		return "("+id+"x"+amt+")";
	}
}
