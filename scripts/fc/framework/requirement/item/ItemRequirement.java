package scripts.fc.framework.requirement.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSItem;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.travel.Travel;
import scripts.fc.api.wrappers.FCTiming;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.mission.impl.GEMission;
import scripts.fc.framework.requirement.Requirement;
import scripts.fc.framework.script.FCMissionScript;

public abstract class ItemRequirement extends Requirement
{
	public static Map<Integer, Integer> satisfiedReqs = new HashMap<>();
	
	protected List<ReqItem> reqItems = new ArrayList<>(Arrays.asList(getReqItems()));
	private boolean hasCheckedInv;
	private boolean hasCheckedEquipment;
	private boolean hasCheckedBank;
	
	public abstract ReqItem[] getReqItems();
	
	public ItemRequirement(FCMissionScript script)
	{
		super(script);
		General.println("CHECKING FOR ITEM REQUIREMENT");
	}
	
	@Override
	public void checkReqs()
	{
		if(Login.getLoginState() == STATE.INGAME)
		{
			if(!hasCheckedInv && !reqItems.isEmpty())
			{
				checkReqs(Inventory.getAll());
				hasCheckedInv = true;
			}
			else if(!hasCheckedEquipment && !reqItems.isEmpty())
			{
				checkReqs(Equipment.getItems());
				hasCheckedEquipment = true;
			}
			else if(!hasCheckedBank && !reqItems.isEmpty())
				checkBank();
			else //needs to gather items
				gatherItems();
		}
	}
	
	private void gatherItems()
	{	
		addPreReqs();
		hasCheckedReqs = true;
	}
	
	private void checkBank()
	{
		if(script.BANK_OBSERVER.getItemArray().length == 0
				&& !Banking.isInBank() && Travel.walkToBank() && !FCTiming.waitCondition(() -> Banking.isInBank(), 4000))
			WebWalking.walkToBank();
		else
		{
			RSItem[] cache = script.BANK_OBSERVER.getItemArray();
			
			if(Banking.isBankScreenOpen() || cache.length > 0)
			{
				checkReqs(cache.length > 0 ? cache : Banking.getAll());
				hasCheckedBank = true;
			}
			else
			{
				if(GrandExchange.getWindowState() != null)
					GrandExchange.close();
				
				if(Banking.openBank())
					Timing.waitCondition(FCConditions.BANK_LOADED_CONDITION, 4500);
			}
		}
	}
	
	private void checkReqs(RSItem[] items)
	{
		for(ReqItem req : reqItems)
			req.check(items);
		
		checkForReqs();
	}
	
	private void checkForReqs()
	{
		for(int i = reqItems.size() - 1; i >= 0; i--)
		{
			ReqItem req = reqItems.get(i);
			
			if(req.isSatisfied())
			{
				General.println("req " + req + " SATISFIED");
				
				//if this req was satisfied by having a combined version of multiple items, don't add the single items to satisfiedReqs
				if(!req.getSingleReqItems().stream().filter(it -> it.needsItem()).allMatch(item -> item.isSatisfied()))
					req.getSingleReqItems().stream().forEach(item -> satisfiedReqs.put(item.getId(), satisfiedReqs.getOrDefault(item.getId(), 0) + 1));
				
				reqItems.remove(i);
			}
		}
	}
	
	private void addPreReqs()
	{
		if(reqItems.isEmpty()) //no need to add any pre reqs if reqItems is empty
			return;
		
		List<SingleReqItem> geOrder = new ArrayList<>();
		List<Mission> mustBeGatheredItems = new ArrayList<>();
		
		for(ReqItem req : reqItems)
		{
			for(SingleReqItem r : req.getSingleReqItems())
			{
				General.println("Add prereqs for " + r + ", r.getAmt(): " + r.getAmt() + ", r.getPlayerAmt(): " + r.getPlayerAmt());
				int amtNeeded = r.getAmt() - (r.getPlayerAmt() < 0 ? 0 : r.getPlayerAmt());
				if(!r.needsItem() || amtNeeded <= 0)
					continue;
				
				General.println("Player does not have item requirement: " + req);
				if(r.shouldUseGE())
				{
					if(r.getAmt() - r.getPlayerAmt() <= 0)
						continue;
					
					General.println("Will attempt to use GE for req " + r);
					General.println("Needs to purchase " + r.getId() + "x" + amtNeeded);
					geOrder.add(new SingleReqItem(r, (r.getAmt() - r.getPlayerAmt())));
				}
				else if(!r.isFutureReq)
				{
					General.println("Will attempt to gather req " + r + " manually");
					Mission[] preReqMissions = r.getPreReqMissions();
					if(preReqMissions == null)
						cannotContinue = true;
					else
						mustBeGatheredItems.addAll(Arrays.asList(preReqMissions));
				}
			}
		}
	
		if(!geOrder.isEmpty())
			missions.add(new GEMission(script, geOrder));
		
		missions.addAll(mustBeGatheredItems);
		satisfiedReqs.clear();
	}
	
	public List<ReqItem> getSetReqItems()
	{
		return reqItems;
	}
}
