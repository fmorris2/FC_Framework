package scripts.fc.framework.grand_exchange;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.EntityInteraction;
import scripts.fc.api.interaction.impl.npcs.ClickNpc;
import scripts.fc.api.interaction.impl.objects.ClickObject;
import scripts.fc.api.travel.Travel;
import scripts.fc.api.utils.InterfaceUtils;
import scripts.fc.api.wrappers.FCTiming;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.SingleReqItem;

public class GEOrder
{
	private static final int COLLECTION_BOX_MASTER = 402, COLLECTION_BOX_CHILD = 2, COLLECTION_BOX_COMP = 11;
	
	private static final RSArea GE_AREA = new RSArea(new RSTile(3158, 3494, 0), new RSTile(3172, 3483, 0));
	private static final int GE_BOOTH_ID = 10061;
	private static final int MAX_F2P_INDEX = 2; //Can only use the first 3 GE slots if F2P
	private static final long LAST_OFFER_THRESH = 600;
	
	private final List<GEOrderItem> ORDER_ITEMS;
	private final FCBankObserver BANK_OBSERVER;
	
	private GEOrder_Status status = GEOrder_Status.GO_TO_GE;
	private long lastOffer;
	
	public GEOrder(FCBankObserver obs, List<SingleReqItem> reqItems)
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
		//first, check if we're done with the order
		if(ORDER_ITEMS.stream().allMatch(o -> o.isPurchased()))
			finishOrder();
		//then, check if we have enough gold on account for at least one item
		else if(!isWaitingToCollect() && BANK_OBSERVER.hasCheckedBank && (getTotalGpOnAccount() < getMinGpNeeded()))
		{
			General.println("Player does not have enough gold to complete GE order! Resorting to gather missions for unbought items...");
			status = GEOrder_Status.FAILED;
		}
		else if(!isInGe()) //then, if we aren't in the GE, go there
			goToGe();
		else if(Inventory.getCount(995) < getMinGpNeeded()) //if we need to withdraw gold
			withdrawGold();
		else //lastly, we're at the GE, so now we handle the buying process
			handleGeLogic();
	}
	
	private void finishOrder()
	{
		if(GrandExchange.getWindowState() != null)
			GrandExchange.close();
		else if((Banking.isBankScreenOpen() || Banking.openBank()) && (Inventory.getAll().length == 0 || Banking.depositAll() > 0))
			status = GEOrder_Status.SUCCESS;
	}
	
	private boolean isInGe()
	{
		return GE_AREA.contains(Player.getPosition());
	}
	
	private void goToGe()
	{
		status = GEOrder_Status.GO_TO_GE;
		Travel.webWalkTo(GE_AREA.getRandomTile(), FCConditions.inAreaCondition(GE_AREA));
	}
	
	private void handleGeLogic()
	{
		status = GEOrder_Status.BUY_ITEMS;
		if(Banking.isBankScreenOpen())
			Banking.close();
		else if(GrandExchange.getWindowState() == null)
			openGe();
		else
			buyItems();
	}
	
	private void buyItems()
	{
		if(needsToOfferItems() && hasEmptySlot())
			offerItems();
		else if(shouldCollectItems())
			collectItems();
		else
			General.println("Waiting for transactions to complete...");
	}
	
	private void collectItems()
	{
		General.println("Collecting items...");
		
		if(Timing.timeFromMark(lastOffer) < LAST_OFFER_THRESH)
			General.sleep(1200, 2400);
		
		RSInterface collectButton = InterfaceUtils.findContainingText("Collect");
		
		if(collectButton != null && Clicking.click(collectButton))
		{
			getOfferedItemsStream()
			.filter(c -> getCompletedOffersStream().anyMatch(o -> c.ID == o.getItemID() && c.AMT == o.getQuantity()))
			.forEach(purchased -> {General.println("Successfully purchased " + purchased); purchased.setPurchased(true);});
			
			FCTiming.waitCondition(() -> !shouldCollectItems(), 2400);
		}
	}
	
	private boolean shouldCollectItems()
	{
		return getAccessibleOffers().anyMatch(o -> o.getStatus() == STATUS.CANCELLED || o.getStatus() == STATUS.COMPLETED);
	}
	
	private boolean needsToOfferItems()
	{
		return ORDER_ITEMS.stream().anyMatch(o -> !o.isOffered());
	}
	
	private Stream<GEOrderItem> getOfferedItemsStream()
	{
		return ORDER_ITEMS.stream().filter(i -> i.isOffered() && !i.isPurchased());
	}
	
	private Stream<RSGEOffer> getCompletedOffersStream()
	{
		return getAccessibleOffers().filter(o -> o.getStatus() == STATUS.COMPLETED);
	}
	
	private long getEmptyOffers()
	{
		return getAccessibleOffers().filter(o -> o.getStatus() == STATUS.EMPTY).count();
	}
	
	private void offerItems()
	{
		GEOrderItem toOffer = ORDER_ITEMS.stream().filter(i -> !i.isOffered()).findAny().get();
		General.println("Putting in buy offer for item " + toOffer);
		
		long oldEmptyOffers = getEmptyOffers();
		if(GrandExchange.offer(toOffer.NAME, toOffer.getPricePer(), toOffer.AMT, false)
				&& FCTiming.waitCondition(() -> oldEmptyOffers != getEmptyOffers(), 4000))
		{
			General.println("Successfully put in offer for " + toOffer);
			lastOffer = Timing.currentTimeMillis();
			toOffer.setOffered(true);
		}
	}
	
	private boolean hasEmptySlot()
	{
		return getAccessibleOffers().anyMatch(o -> o.getStatus() == STATUS.EMPTY);
	}
	
	/**
	 * Get a stream of the accessible GE offers (F2P offers and Members offers if on a member world)
	 * 
	 * @return a stream of the accessible GE offers
	 */
	private Stream<RSGEOffer> getAccessibleOffers()
	{
		return Arrays.stream(GrandExchange.getOffers()).filter(o -> o.getIndex() <= MAX_F2P_INDEX || WorldHopper.isMembers(WorldHopper.getWorld()));
	}
	
	/**
	 * Opens the GE using either the clerk or the booth
	 */
	private void openGe()
	{
		//Close collection box if necessary
		closeCollectionBox();
			
		EntityInteraction inter = General.random(0, 1) == 0 
				? new ClickNpc("Exchange", "Grand Exchange Clerk", 15) : new ClickObject("Exchange", GE_BOOTH_ID, 15);
				
		if(inter.execute())
			FCTiming.waitCondition(() -> GrandExchange.getWindowState() != null, 3000);
	}
	
	private void closeCollectionBox()
	{
		RSInterface collectionBox = Interfaces.get(COLLECTION_BOX_MASTER, COLLECTION_BOX_CHILD);
		RSInterface closeButton = collectionBox == null ? null : collectionBox.getChild(COLLECTION_BOX_COMP);
		if(closeButton != null)
			Clicking.click(closeButton);	
	}
	
	/**
	 * Open bank if necessary and withdraw all gold in it
	 */
	private void withdrawGold()
	{
		status = GEOrder_Status.WITHDRAW_GP;
		if(!Banking.isBankScreenOpen())
		{
			if(Banking.openBank())
				Timing.waitCondition(FCConditions.BANK_LOADED_CONDITION, 1200);
		}
		else if(Banking.withdraw(-1, 995))
			FCTiming.waitCondition(() -> Banking.find(995).length == 0, 1200);	
	}
	

	/**
	 * Compile gather missions for order items which we failed to purchase
	 * 
	 * @return array of gather missions for order items which we failed to purchase
	 */
	public Mission[] getGatherMissions()
	{
		return ORDER_ITEMS.stream().filter(item -> !item.isPurchased())
				.flatMap(item -> Arrays.stream(item.getGatherMissions())).toArray(Mission[]::new);
	}
	
	/**
	 * Find the minimum amount of gold needed to purchase one item
	 * 
	 * @return the price of the lowest-cost item in our order items list
	 */
	public int getMinGpNeeded()
	{
		Optional<GEOrderItem> optional = ORDER_ITEMS.stream().filter(i -> !i.isOffered()).min((one, two) -> one.getPrice() - two.getPrice());
		if(optional.isPresent())
			return optional.get().getPrice();
		
		return 0;
	}
	
	/**
	 * Calculate the total gp needed for this order
	 * 
	 * @return the total gp needed for this order
	 */
	public int getTotalGpNeeded()
	{
		return ORDER_ITEMS.stream().filter(i -> !i.isOffered()).mapToInt(n -> n.getPrice()).sum();
	}
	
	/**
	 * Calculate the total gold on the account
	 * This includes gold in inventory and bank
	 *
	 * @return total gold in inventory and bank
	 */
	public int getTotalGpOnAccount()
	{
		return Inventory.getCount(995) + BANK_OBSERVER.getCount(995);
	}
	
	/**
	 * Determines whether or not we have item offers in that we're
	 * waiting to collect
	 * 
	 * @return true if we still have pending offers
	 */
	private boolean isWaitingToCollect()
	{
		return ORDER_ITEMS.stream().anyMatch(i -> i.isOffered() && !i.isPurchased());
	}
	
	public GEOrder_Status getStatus()
	{
		return status;
	}
}
