package scripts.fc.framework.mission.impl;

import java.util.List;

import org.tribot.api.General;

import scripts.fc.framework.grand_exchange.GEOrder;
import scripts.fc.framework.grand_exchange.GEOrder_Status;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.ReqItem;

public class GEMission implements Mission
{
	private GEOrder order;
	private boolean isDone;
	
	public GEMission(List<ReqItem> reqItems)
	{
		order = new GEOrder(reqItems);
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
		return "GE Order: " + order.STATUS;
	}

	@Override
	public String getEndingMessage()
	{
		return "GE Mission has ended";
	}

	@Override
	public String[] getMissionSpecificPaint()
	{
		return null;
	}

	@Override
	public void execute()
	{
		if(order.STATUS == GEOrder_Status.IN_PROGRESS)
			order.execute();
		else if(order.STATUS == GEOrder_Status.SUCCESS)
			isDone = true;
		else //have gather missions we need to execute
		{
			General.println("Failed to purchase all items from GE. Initializing gather missions...");
		}
	}

	@Override
	public void resetStatistics()
	{}

}
