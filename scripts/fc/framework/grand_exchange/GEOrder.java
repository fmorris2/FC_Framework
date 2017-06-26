package scripts.fc.framework.grand_exchange;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.api.travel.Travel;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.ReqItem;

public class GEOrder
{
	private static final RSArea GE_AREA = new RSArea(new RSTile(3158, 3494, 0), new RSTile(3172, 3483, 0));
	
	private final List<GEOrderItem> ORDER_ITEMS;
	private final FCBankObserver BANK_OBSERVER;
	
	private GEOrder_Status status = GEOrder_Status.GO_TO_GE;
	
	public GEOrder(FCBankObserver obs, List<ReqItem> reqItems)
	{
		BANK_OBSERVER = obs;
		ORDER_ITEMS = reqItems.stream().map(reqItem -> new GEOrderItem(reqItem)).collect(Collectors.toList());
	}
	
	public GEOrder(FCBankObserver obs, GEOrderItem... items)
	{
		BANK_OBSERVER = obs;
		ORDER_ITEMS = Arrays.asList(items);
	}
	
	public void execute()
	{
		//first, check if we have enough gold on account for at least one item
		if(BANK_OBSERVER.hasCheckedBank && (getTotalGpOnAccount() < getMinGpNeeded()))
			status = GEOrder_Status.FAILED;
		else if(!isInGe()) //second, if we aren't in the GE, go there
			goToGe();
		else //lastly, we're at the GE, so now we handle the buying process
			handleGeLogic();
	}
	
	private boolean isInGe()
	{
		return GE_AREA.contains(Player.getPosition());
	}
	
	private void goToGe()
	{
		status = GEOrder_Status.GO_TO_GE;
		Travel.webWalkTo(GE_AREA.getRandomTile());
	}
	
	private void handleGeLogic()
	{
		status = GEOrder_Status.BUY_ITEMS;
	}
	

	/**
	 * Compile gather missions for order items which we failed to purchase
	 * 
	 * @return array of gather missions for order items which we failed to purchase
	 */
	public Mission[] getGatherMissions()
	{
		return ORDER_ITEMS.stream().filter(item -> !item.isPurchased())
				.map(item -> Arrays.stream(item.getGatherMissions())).toArray(Mission[]::new);
	}
	
	/**
	 * Find the minimum amount of gold needed to purchase one item
	 * 
	 * @return the price of the lowest-cost item in our order items list
	 */
	public int getMinGpNeeded()
	{
		return ORDER_ITEMS.stream().min((one, two) -> one.getPrice() - two.getPrice()).get().getPrice();
	}
	
	/**
	 * Calculate the total gp needed for this order
	 * 
	 * @return the total gp needed for this order
	 */
	public int getTotalGpNeeded()
	{
		return ORDER_ITEMS.stream().mapToInt(n -> n.getPrice()).sum();
	}
	
	/**
	 * Calculate the total gold on the account.
	 * This includes gold in inventory and bank.
	 * 
	 * @return total gold in inventory and bank
	 */
	public int getTotalGpOnAccount()
	{
		return Inventory.getCount(995) + BANK_OBSERVER.getCount(995);
	}
	
	public GEOrder_Status getStatus()
	{
		return status;
	}
}
