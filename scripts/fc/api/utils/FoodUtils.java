package scripts.fc.api.utils;

import org.tribot.api2007.Inventory;

import scripts.fc.api.items.FCItem;
import scripts.fc.framework.quest.BankBool;

public class FoodUtils {
	public static FCItem generateOptionalFoodFCItemToWithdraw(int id, int minAmt, int optimalAmt) {
		if(Inventory.getCount(id) <= minAmt)
		{
			int foodInBank = BankBool.bankObserver.getCount(id);
			if(foodInBank == 0)
				foodInBank = minAmt;
			
			return new FCItem(foodInBank > optimalAmt ? optimalAmt : foodInBank, false, id);
		}
		
		return new FCItem(minAmt, false, id);
	}
}
