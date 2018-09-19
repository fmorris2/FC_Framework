package scripts.fc.framework.grand_exchange;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.banking.listening.FCBankObserver;
import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.grand_exchange.FCGrandExchange;
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
	
	private static final Positionable GE_TILE = new RSTile(3164, 3485, 0);
	private static final int GE_DIST_THRESH = 12;
	private static final int GE_BOOTH_ID = 10061;
	private static final int MAX_F2P_INDEX = 2; //Can only use the first 3 GE slots if F2P
	private static final long LAST_OFFER_THRESH = 1200;
	
	private final List<GEOrderItem> ORDER_ITEMS;
	private final FCBankObserver BANK_OBSERVER;
	
	private GEOrder_Status status = GEOrder_Status.GO_TO_GE;
	private long lastOffer;
	private boolean makingSpace;
	
	public GEOrder(final FCBankObserver obs, final List<SingleReqItem> reqItems)
	{
		BANK_OBSERVER = obs;
		ORDER_ITEMS = reqItems.stream().map(reqItem -> new GEOrderItem(reqItem)).collect(Collectors.toList());
		attemptToCombine();
	}
	
	public GEOrder(final FCBankObserver obs, final GEOrderItem... items)
	{
		BANK_OBSERVER = obs;
		ORDER_ITEMS = Arrays.asList(items);
		attemptToCombine();
	}
	
	private void attemptToCombine()
	{
		for(int i = 0; i < ORDER_ITEMS.size(); i++)
		{
			for(int z = i + 1; z < ORDER_ITEMS.size(); z++)
			{
				final GEOrderItem one = ORDER_ITEMS.get(i);
				final GEOrderItem two = ORDER_ITEMS.get(z);
				if(two.ID == one.ID)
				{
					General.println("GEOrder: Able to combine " + one + " with " + two);
					ORDER_ITEMS.set(i, new GEOrderItem(one.ID, (one.AMT + two.AMT), GEOrderItem.combineGatherMissions(one, two)));
					ORDER_ITEMS.remove(z);
				}
			}
		}
	}
	
	public void execute()
	{
		//first, check if we're done with the order
		if(ORDER_ITEMS.stream().allMatch(o -> o.isPurchased()))
			finishOrder();
		//then, check if we have enough gold on account for at least one item
		else if(!isWaitingToCollect() && BANK_OBSERVER.hasCheckedBank && (getTotalGpOnAccount() < getMinGpNeeded()) && GrandExchange.close())
		{
			General.println("Player does not have enough gold to complete GE order! Resorting to gather missions for unbought items...");
			status = GEOrder_Status.FAILED;
		}
		else if(!isInGe()) //then, if we aren't in the GE, go there
			goToGe();
		else if((Inventory.getCount(995) == 0 && needsToOfferItems()) || Inventory.getCount(995) < getMinGpNeeded()) //if we need to withdraw gold
			withdrawGold();
		else //lastly, we're at the GE, so now we handle the buying process
			handleGeLogic();
	}
	
	private void finishOrder()
	{
		General.println("[GEOrder] Finish order");
		final int invSpace = Inventory.getAll().length;
		
		if(GrandExchange.getWindowState() != null)
			GrandExchange.close();
		else if((Banking.isBankScreenOpen() || Banking.openBank()) && (Inventory.getAll().length == 0 
				|| (Banking.depositAll() > 0 && Timing.waitCondition(FCConditions.inventoryChanged(invSpace), 3200))))
			status = GEOrder_Status.SUCCESS;
	}
	
	public static boolean isInGe()
	{
		return Player.getPosition().distanceTo(GE_TILE) < GE_DIST_THRESH;
	}
	
	private void goToGe()
	{
		status = GEOrder_Status.GO_TO_GE;
		Travel.webWalkTo(GE_TILE, FCConditions.withinDistanceOfTile(GE_TILE, GE_DIST_THRESH));
	}
	
	public static boolean travelToGe()
	{
		return Travel.webWalkTo(GE_TILE, FCConditions.withinDistanceOfTile(GE_TILE, GE_DIST_THRESH));
	}
	
	private void handleGeLogic()
	{
		status = GEOrder_Status.BUY_ITEMS;
		
		final boolean GE_WINDOW_OPEN = GrandExchange.getWindowState() != null;
		if(!GE_WINDOW_OPEN && InterfaceUtils.isQuestInterfaceUp())
		{
			General.println("[GEOrder] Closing quest interface");
			InterfaceUtils.closeQuestInterface();
		}
		else if(!GE_WINDOW_OPEN && InterfaceUtils.isQuestJournalGuideUp())
		{
			General.println("[GEOrder] Closing quest journal guide interface");
			InterfaceUtils.closeQuestJournalGuide();
		}
		else if(makingSpace)
			makeSpace();
		else if(Banking.isBankScreenOpen())
			Banking.close();
		else if(GrandExchange.getWindowState() == null)
			openGe();
		else
			buyItems();
	}
	
	private void buyItems()
	{
		General.println("[GEOrder] Buy items");
		if(needsToOfferItems() && hasEmptySlot())
			offerItems();
		else if(shouldCollectItems())
			collectItems();
		else if(shouldModifyPrice())
			modifyPrice();
		else
			General.println("Waiting for transactions to complete...");
	}
	
	private boolean shouldModifyPrice()
	{
		if(Timing.timeFromMark(lastOffer) < LAST_OFFER_THRESH)
			return false;
		
		return getUnsoldOffers().length > 0;
	}
	
	private void modifyPrice()
	{
		General.println("Modifying prices...");
		Arrays.stream(getUnsoldOffers())
			.forEach(unsold -> {final GEOrderItem orderItem = getOrderItemForOffer(unsold); if(unsold.click("Abort offer") && orderItem != null){orderItem.resubmit();}});
	}
	
	private boolean needsToMakeSpaceToCollect()
	{
		return 28 - Inventory.getAll().length < getCompletedOffersStream().count();
	}
	
	private void collectItems()
	{
		General.println("Collecting items...");
		
		if(Timing.timeFromMark(lastOffer) < LAST_OFFER_THRESH)
			General.sleep(1200, 2400);
		else if(needsToMakeSpaceToCollect())
		{
			makingSpace = true;
			makeSpace();
			return;
		}
		else if(Banking.isBankScreenOpen())
			Banking.close();
		else if(GrandExchange.getWindowState() == null)
			openGe();
		else
		{	
			final RSInterface collectButton = InterfaceUtils.findContainingText("Collect");
			
			if(collectButton != null && Clicking.click(collectButton))
			{
				getOfferedItemsStream()
				.filter(c -> getCompletedOffersStream().anyMatch(o -> c.ID == o.getItemID() && c.AMT == o.getQuantity()))
				.forEach(purchased -> {General.println("Successfully purchased " + purchased); purchased.setPurchased(true);});
				
				FCTiming.waitCondition(() -> !shouldCollectItems(), 2400);
			}
		}
	}
	
	private void makeSpace()
	{
		General.println("Making space to collect items...");
		final int goldInInv = Inventory.getCount(995);
		final int goldInBank = BANK_OBSERVER.getCount(995);
		final int invSpace = Inventory.getAll().length;
		
		if(GrandExchange.getWindowState() != null)
			GrandExchange.close();
		else if((Banking.isBankScreenOpen() || Banking.openBank()) && (Inventory.getAll().length == 0 
				|| (Banking.depositAll() > 0 && Timing.waitCondition(FCConditions.inventoryChanged(invSpace), 3200))))
		{
			if(goldInInv > 0)
				FCTiming.waitCondition(() -> BANK_OBSERVER.getCount(995) > goldInBank, 2400);
			
			General.println("Successfully made space to collect items...");
			makingSpace = false;
		}
	}
	
	private GEOrderItem getOrderItemForOffer(final RSGEOffer offer)
	{
		return ORDER_ITEMS.stream().filter(i -> offer.getItemID() == i.ID && offer.getQuantity() == i.AMT).findFirst().orElse(null);
	}
	
	private boolean shouldCollectItems()
	{
		return getAccessibleOffers().anyMatch(o -> o.getStatus() == STATUS.CANCELLED || o.getStatus() == STATUS.COMPLETED);
	}
	
	private RSGEOffer[] getUnsoldOffers()
	{
		return getAccessibleOffers()
				.filter(o -> o.getStatus() == STATUS.IN_PROGRESS && o.getTransferredAmount() == 0 
					&& getOrderItemForOffer(o) != null && getOrderItemForOffer(o).getResubmitPrice() <= getTotalGpOnAccount())
				.toArray(RSGEOffer[]::new);
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
		final GEOrderItem toOffer = ORDER_ITEMS.stream().filter(i -> !i.isOffered() && Inventory.getCount(995) >= i.getPrice()).findAny().get();
		General.println("Putting in buy offer for item " + toOffer);
		
		final long oldEmptyOffers = getEmptyOffers();
		if(FCGrandExchange.offer(toOffer.NAME.trim(), toOffer.getPricePer(), toOffer.AMT, false)
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
			
		final EntityInteraction inter = General.random(0, 1) == 0 
				? new ClickNpc("Exchange", "Grand Exchange Clerk", 15) : new ClickObject("Exchange", GE_BOOTH_ID, 15);
				
		if(inter.execute())
			FCTiming.waitCondition(() -> GrandExchange.getWindowState() != null, 3000);
	}
	
	private void closeCollectionBox()
	{
		final RSInterface collectionBox = Interfaces.get(COLLECTION_BOX_MASTER, COLLECTION_BOX_CHILD);
		final RSInterface closeButton = collectionBox == null ? null : collectionBox.getChild(COLLECTION_BOX_COMP);
		if(closeButton != null)
			Clicking.click(closeButton);	
	}
	
	/**
	 * Open bank if necessary and withdraw all gold in it
	 */
	private void withdrawGold()
	{
		if(Timing.timeFromMark(lastOffer) < LAST_OFFER_THRESH)
			return;
		
		if(shouldCollectItems())
		{
			collectItems();
			return;
		}
		
		status = GEOrder_Status.WITHDRAW_GP;
		if(GrandExchange.getWindowState() != null)
			GrandExchange.close();
		else if(!Banking.isBankScreenOpen())
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
		return ORDER_ITEMS.stream().filter(item -> !item.isPurchased() && item.getGatherMissions() != null)
				.flatMap(item -> Arrays.stream(item.getGatherMissions())).toArray(Mission[]::new);
	}
	
	/**
	 * Find the minimum amount of gold needed to purchase one item
	 * 
	 * @return the price of the lowest-cost item in our order items list
	 */
	public int getMinGpNeeded()
	{
		final Optional<GEOrderItem> optional = ORDER_ITEMS.stream().filter(i -> !i.isOffered()).min((one, two) -> one.getPrice() - two.getPrice());
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
