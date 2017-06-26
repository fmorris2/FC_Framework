package scripts.fc.framework.grand_exchange;

import scripts.fc.api.items.ItemUtils;
import scripts.fc.api.utils.PriceUtils;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.ReqItem;

public class GEOrderItem
{
	private static final double BUY_MODIFIER = 1.15; //we'll put in offers for 15% over market price
	
	public final int ID, AMT;
	public final String NAME;
	private final int GE_PRICE_PER;
	
	private Mission[] gatherMissions; //in case we've failed to buy this item from the GE
	private boolean isPurchased, isOffered;
	
	public GEOrderItem(int id, int amt)
	{
		ID = id;
		AMT = amt;
		GE_PRICE_PER = PriceUtils.getPrice(ID);
		NAME = ItemUtils.getName(ID);
	}
	
	public GEOrderItem(ReqItem i)
	{
		this(i.getId(), i.getAmt());
		gatherMissions = i.getPreReqMissions();
	}
	
	public int getPrice()
	{
		return getPricePer() * AMT;
	}
	
	public int getPricePer()
	{
		return (int)Math.ceil(GE_PRICE_PER * BUY_MODIFIER);
	}
	
	public void setPurchased(boolean b)
	{
		isPurchased = b;
	}
	
	public void setOffered(boolean b)
	{
		isOffered = b;
	}
	
	public boolean isPurchased()
	{
		return isPurchased;
	}
	
	public boolean isOffered()
	{
		return isOffered;
	}
	
	public Mission[] getGatherMissions()
	{
		return gatherMissions;
	}
	
	public String toString()
	{
		return "("+ID+"x"+AMT+")";
	}
}
