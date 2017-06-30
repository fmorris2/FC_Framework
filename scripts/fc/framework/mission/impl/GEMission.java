package scripts.fc.framework.mission.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.tribot.api.General;

import scripts.fc.framework.grand_exchange.GEOrder;
import scripts.fc.framework.grand_exchange.GEOrder_Status;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.SingleReqItem;
import scripts.fc.framework.script.FCMissionScript;

public class GEMission implements Mission
{
	private GEOrder order;
	private boolean isDone;
	private FCMissionScript script;
	
	public GEMission(FCMissionScript script, List<SingleReqItem> reqItems)
	{
		this.script = script;
		order = new GEOrder(script.BANK_OBSERVER, reqItems);
	}
	
	@Override
	public boolean hasReachedEndingCondition()
	{
		return isDone;
	}

	@Override
	public String getMissionName()
	{
		return "GE Mission";
	}

	@Override
	public String getCurrentTaskName()
	{
		return "GE Order: " + order.getStatus();
	}

	@Override
	public String getEndingMessage()
	{
		return "GE Mission has ended";
	}

	@Override
	public String[] getMissionSpecificPaint()
	{
		return new String[]{};
	}

	@Override
	public void execute()
	{
		if(order.getStatus() == GEOrder_Status.SUCCESS)
			isDone = true;
		else if(order.getStatus() == GEOrder_Status.FAILED) //have gather missions we need to execute
		{
			General.println("Failed to purchase all items from GE. Initializing gather missions...");
			Mission[] gatherMissions = order.getGatherMissions();
			if(gatherMissions == null || gatherMissions.length == 0 || Arrays.stream(gatherMissions).anyMatch(m -> m == null))
			{
				stopScript();
				return;
			}
			
			for(Mission m : gatherMissions)
			{
				General.println("Adding pre req gather mission: " + m.getMissionName()); 
				((LinkedList<Mission>)script.getSetMissions()).addFirst(m);
			}
			
			isDone = true;
		}
		else 
			order.execute();
	}
	
	private void stopScript()
	{
		General.println("Could not purchase mandatory item from GE, and we don't have a gather mission for it! Ending script...");
		script.setIsRunning(false);
	}

	@Override
	public void resetStatistics()
	{}

}
