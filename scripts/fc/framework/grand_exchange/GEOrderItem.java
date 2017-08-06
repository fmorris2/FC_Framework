package scripts.fc.framework.grand_exchange;

import java.util.Arrays;
import java.util.stream.Stream;

import org.tribot.api.General;

import scripts.fc.api.items.ItemUtils;
import scripts.fc.api.utils.PriceUtils;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.SingleReqItem;

public class GEOrderItem
{
	private static final double LOWEST_PRICE_BUY_MODIFIER = 4.00;
	private static final double LOW_PRICE_BUY_MODIFIER = 2.00;//2.00; //we'll put in offers for 100% over market price for low priced items
	private static final double HIGH_PRICE_BUY_MODIFIER = 1.30;//1.30; //we'll put in offers for 30% over market price for high priced items
	private static final double RESUBMIT_STEP = .15; //price will go up a base of 15% every time we resubmit
	private static final double RESUBMIT_STEP_MODIFIER = .10; //resubmit step will go up 10% every time we resubmit 
	private static final int LOW_PRICE_THRESH = 600;
	private static final int LOWEST_PRICE_THRESH = 100;
	
	public final int ID, AMT;
	public final String NAME;
	private final int GE_PRICE_PER;
	
	private Mission[] gatherMissions; //in case we've failed to buy this item from the GE
	private boolean isPurchased, isOffered;
	private double resubmitModifier = 0.0, resubmitStepModifier = 0.0;
	
	public GEOrderItem(int id, int amt)
	{
		ID = id;
		AMT = amt;
		GE_PRICE_PER = PriceUtils.getPrice(ID);
		NAME = ItemUtils.getName(ID);
	}
	
	public GEOrderItem(int id, int amt, Mission[] gatherMissions)
	{
		this(id, amt);
		this.gatherMissions = gatherMissions;
	}
	
	public GEOrderItem(SingleReqItem i)
	{
		this(i.getId(), i.getAmt());
		gatherMissions = i.getPreReqMissions();
	}
	
	public static Mission[] combineGatherMissions(GEOrderItem one, GEOrderItem two)
	{
		if(one.gatherMissions == null)
			return two.gatherMissions;
		else if(two.gatherMissions == null)
			return one.gatherMissions;
		
		return Stream.concat(Arrays.stream(one.gatherMissions), Arrays.stream(two.gatherMissions)).toArray(Mission[]::new);
	}
	
	public void resubmit()
	{
		isOffered = false;
		resubmitModifier += getResubmitStep();
		resubmitStepModifier += RESUBMIT_STEP_MODIFIER;
		General.println("Resubmitting " + this + " with higher price... Resubmit modifier is now " + (resubmitModifier * 100) + "%");
	}
	
	public double getResubmitStep()
	{
		return RESUBMIT_STEP + resubmitStepModifier;
	}
	
	public int getResubmitPrice()
	{
		return getResubmitPricePer() - getPrice();
	}
	
	public int getPrice()
	{
		return getPricePer() * AMT;
	}
	
	public int getPricePer()
	{
		return (int)Math.ceil(GE_PRICE_PER * (getModifier() + resubmitModifier));
	}
	
	private int getResubmitPricePer()
	{
		General.println("Previous price per: " + getPricePer());
		int newPricePer = (int)Math.ceil(GE_PRICE_PER * (getModifier() + (resubmitModifier + getResubmitStep())));
		General.println("New price per: " + newPricePer + ", formula: ceil(" + GE_PRICE_PER + " * " + (getModifier() + (resubmitModifier + getResubmitStep())) + ")");
		return newPricePer;
	}
	
	private double getModifier()
	{
		if(GE_PRICE_PER <= LOWEST_PRICE_THRESH)
			return LOWEST_PRICE_BUY_MODIFIER;
		
		if(GE_PRICE_PER <= LOW_PRICE_THRESH)
			return LOW_PRICE_BUY_MODIFIER;
		
		return HIGH_PRICE_BUY_MODIFIER;
		
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
