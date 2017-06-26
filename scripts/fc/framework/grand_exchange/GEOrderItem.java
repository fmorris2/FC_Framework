package scripts.fc.framework.grand_exchange;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.ReqItem;

public class GEOrderItem
{
	public final int ID, AMT;
	
	private Mission[] gatherMissions; //in case we've failed to buy this item from the GE
	private boolean isPurchased;
	
	public GEOrderItem(int id, int amt)
	{
		ID = id;
		AMT = amt;
	}
	
	public GEOrderItem(ReqItem i)
	{
		this(i.getId(), i.getAmt());
		gatherMissions = i.getPreReqMissions();
	}
	
	public void setPurchased(boolean b)
	{
		isPurchased = b;
	}
	
	public boolean isPurchased()
	{
		return isPurchased;
	}
	
	public Mission[] getGatherMissions()
	{
		return gatherMissions;
	}
}
