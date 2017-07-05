package scripts.fc.framework.requirement.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.mission.impl.OneTaskMission;
import scripts.fc.framework.task.Task;

public class SingleReqItem extends ReqItem
{
	public int playerAmt;
	
	private int id;
	private int amt;
	/*
	 * this is normally set to false for items that are required but are a combined sum of parts
	 * For example, in Goblin Diplomacy, we need blue dyed mail. However, the requirement we use to check
	 * for that is a combined requirement of the Dye + Normal mail, and then the actual dyed mail.
	 * 
	 * If we don't have either, we'll set needsItem to false for the dyed mail, and the framework will ignore it
	 * when we go to gather the required items. Otherwise it would fail because we can't buy it through the GE, and we
	 * also don't have a gather mission for it. But this is not the case for the 2 separate items that form the dyed mail
	 */
	private boolean needsItem;
	
	public SingleReqItem(int id, int amt, boolean useGE, boolean needsItem)
	{
		this.id = id;
		this.amt = amt;
		this.needsItem = needsItem;
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
		int amt = playerAmt - ItemRequirement.satisfiedReqs.getOrDefault(id, 0);
		return amt < 0 ? 0 : amt;
	}
	
	public boolean needsItem()
	{
		return needsItem;
	}
	
	@Override
	public boolean isSatisfied()
	{
		return amt <= getPlayerAmt();
	}

	@Override
	public void check(RSItem[] items)
	{
		for(RSItem i : items)
		{
			RSItemDefinition def = i.getDefinition();
			if(i.getID() == id || (i.getID() == id + 1 && (def != null && def.isNoted())))
				playerAmt += i.getStack();
		}
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
