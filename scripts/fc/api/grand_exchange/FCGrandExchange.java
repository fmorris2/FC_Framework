package scripts.fc.api.grand_exchange;

import java.util.Arrays;
import java.util.stream.Stream;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.GrandExchange.WINDOW_STATE;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSInterface;

import scripts.fc.api.utils.InterfaceUtils;
import scripts.fc.api.wrappers.FCTiming;

public class FCGrandExchange
{
	private static final int MAX_F2P_INDEX = 2; //Can only use the first 3 GE slots if F2P
	private static final int GE_MASTER = 465;
	private static final int[] SLOTS = {7,8,9,10,11,12,13,14};
	private static final int BUY_COMP = 0, SELL_COMP = 1;
	private static final int BUY_OFFER_ITEM_SELECTION_MASTER = 162, BUY_OFFER_ITEM_SELECTION_COMP = 42;
	
	public static boolean offer(String name, int price, int quantity, boolean sell)
	{
		//check if we need to get to main selection window
		if(GrandExchange.getWindowState() != WINDOW_STATE.SELECTION_WINDOW && !GrandExchange.goToSelectionWindow(true))
			return false;
		
		//check if we have an empty offer and we clicked the offer
		RSGEOffer emptyOffer = getEmptyOffer();
		if(emptyOffer == null || !clickOffer(emptyOffer, sell))
			return false;
		
		return !sell ? buyItem(name, price, quantity) : sellItem(name, price, quantity);
	}
	
	private static boolean buyItem(String name, int price, int quantity)
	{
		RSInterface typeInter = InterfaceUtils.findContainingText("What would you like to buy?");
		if(typeInter == null)
			return false;
		
		//wait for type interface to appear
		General.sleep(600, 1200);

		Keyboard.typeString(name);
		
		//wait for items to populate
		if(!FCTiming.waitCondition(() -> getItemSelection(name) != null, 1800))
			return false;
		
		RSInterface item = getItemSelection(name);
		
		if(item == null || !Clicking.click(item))
			return false;
	
		//wait for offer to load up
		if(!FCTiming.waitCondition(() -> GrandExchange.getItemID() > 0, 1200))
			return false;
		
		//set price & quantity & confirm offer
		return (General.random(0, 1) == 0 ?
				GrandExchange.setPrice(price) && (quantity == 1 || GrandExchange.setQuantity(quantity))
				: (quantity == 1 || GrandExchange.setQuantity(quantity)) && GrandExchange.setPrice(price))
				&& GrandExchange.confirmOffer();
	}
	
	private static RSInterface getItemSelection(String name)
	{
		RSInterface itemSelection = Interfaces.get(BUY_OFFER_ITEM_SELECTION_MASTER, BUY_OFFER_ITEM_SELECTION_COMP);
		RSInterface[] children = itemSelection == null ? null : itemSelection.getChildren();
		return children == null ? null 
				: Arrays.stream(itemSelection.getChildren())
					.filter(i -> {if(i == null) return false; String text = i.getText(); return text != null && text.equalsIgnoreCase(name);})
					.findFirst().orElse(null);
	}
	
	private static boolean sellItem(String name, int price, int quantity)
	{
		return false;
	}
	
	private static boolean clickOffer(RSGEOffer offer, boolean sell)
	{
		RSInterface slot = Interfaces.get(GE_MASTER, SLOTS[offer.getIndex()]);
		RSInterface offerButton = slot == null ? null : slot.getChild(sell ? SELL_COMP : BUY_COMP);
		if(offerButton != null)
			return Clicking.click(offerButton) && FCTiming.waitCondition(
					() -> GrandExchange.getWindowState() == WINDOW_STATE.NEW_OFFER_WINDOW 
							&& InterfaceUtils.findContainingText("What would you like to buy?") != null, 2400);
		
		return false;
	}
	
	
	/**
	 * Get a stream of the accessible GE offers (F2P offers and Members offers if on a member world)
	 * 
	 * @return a stream of the accessible GE offers
	 */
	private static Stream<RSGEOffer> getAccessibleOffers()
	{
		return Arrays.stream(GrandExchange.getOffers()).filter(o -> o.getIndex() <= MAX_F2P_INDEX || WorldHopper.isMembers(WorldHopper.getWorld()));
	}
	
	private static RSGEOffer getEmptyOffer()
	{
		return getAccessibleOffers().filter(o -> o.getStatus() == STATUS.EMPTY).findFirst().orElse(null);
	}
}
