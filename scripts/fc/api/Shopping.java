package scripts.fc.api;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

public class Shopping {
	/*
	 * @author TehRhio
	 * @description NPC Shop API
	 */
	
	public static boolean isShopOpen(){
		return Interfaces.get(300, 76) != null;
	}
	public static String getShopName(){
		if (isShopOpen()) {
			return Interfaces.get(300, 76).getText();
		}
		return "";
	}
	public static boolean close(){
		//Close the shop interface
		if (isShopOpen()) {
			return Interfaces.get(300, 91).click(new String[0]);
		}
		return false;
	}
	
	public static RSItem[] getAll(){
		//Return an array of all items in the shop
		if (isShopOpen()) {
			return createRectangles(Interfaces.get(300, 75).getItems());
		}
		return new RSItem[0];
	}
	public static RSItem[] get(String... name){
		//Return an array of items with specific names
		ArrayList<RSItem> items = new ArrayList<RSItem>();
		for (RSItem i : getAll()) {
			RSItemDefinition definition = i.getDefinition();
			if(definition != null
					&& Arrays.asList(name).contains(definition.getName())){
				items.add(i);
			}
		}
		return items.toArray(new RSItem[items.size()]);
	}
	public static RSItem[] get(int... ids){
		//Return an array of items with specific ids
		ArrayList<RSItem> items = new ArrayList<RSItem>();
		for (RSItem i : getAll()) {
			for(int id : ids)
			{
				if(i.getID() == id || i.getID() == id - 1)
				{
					items.add(i);
				}
			}
		}
		return items.toArray(new RSItem[items.size()]);
	}
	public static int getCount(String... name){
		//Return the amount of an item a shop has in-stock
		int i = 0;
		for (RSItem item : get(name)) {
			i += item.getStack();
		}
		return i;
	}
	public static int getCount(int... id){
		//Return the amount of an item a shop has in-stock
		int i = 0;
		for (RSItem item : get(id)) {
			i += item.getStack();
		}
		return i;
	}
	private static RSItem[] createRectangles(RSItem[] items){
		//Convert all items in the array to bank items
		for(RSItem i : items){
			int x = ((int) Math.ceil((i.getIndex()) % 8) * 47) + 80;
			int y = ((int) ((Math.floor(i.getIndex()) / 8) % 5) * 47) + 69;
			i.setArea(new Rectangle(x,y,31,31));
		}
		return items;
	}
	
	public static boolean buy(int count, String... names){
		//Buy an item from the shop
		for(RSItem item : get(names)){
			if(item.getStack() > 0){
				if(count >= 10){
					return item.click("Buy 10");
				}else if(count >= 5){
					return item.click("Buy 5");
				}else{
					return item.click("Buy 1");
				}
			}
		}
		return false;
	}
	public static boolean buy(int count, int... ids){
		//Buy an item from the shop
		for(RSItem item : get(ids)){
			if(item.getStack() > 0){
				if(count >= 10){
					return item.click("Buy 10");
				}else if(count >= 5){
					return item.click("Buy 5");
				}else{
					return item.click("Buy 1");
				}
			}
		}
		return false;
	}
	public static boolean sell(int count, String... names){
		RSItem[] items = Inventory.find(names);
		if(items.length > 0){
			if(count >= 10){
				return items[0].click("Sell 10");
			}else if(count >= 5){
				return items[0].click("Sell 5");
			}else{
				return items[0].click("Sell 1");
			}
		}
		return false;
	}
	public static boolean sell(int count, int id){
		RSItem[] items = Inventory.find(id);
		if(items.length > 0) {
			int amountSold = 0;
			int leftToSell = 0;
			RSItem toSell = items[0];
			while(amountSold < count && Inventory.find(id).length > 0)
			{
				leftToSell = (count - amountSold);
				if(leftToSell >= 10 && toSell.click("Sell 10"))
				{
					amountSold += 10;
				}
				else if(leftToSell >= 5 && toSell.click("Sell 5"))
				{
					amountSold += 5;
				}
				else if(leftToSell >= 1 && toSell.click("Sell 1"))
				{	
					amountSold += 1;
				}
				
				General.sleep(General.random(600, 800));
			}
		}
		return false;
	}
}