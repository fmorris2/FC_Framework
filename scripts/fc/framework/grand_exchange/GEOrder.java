package scripts.fc.framework.grand_exchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.requirement.item.ReqItem;

public class GEOrder
{
	public final GEOrder_Status STATUS = GEOrder_Status.IN_PROGRESS;
	private final List<GEOrderItem> ORDER_ITEMS;
	
	public GEOrder(List<ReqItem> reqItems)
	{
		ORDER_ITEMS = new ArrayList<>();
		for(ReqItem i : reqItems)
			ORDER_ITEMS.add(new GEOrderItem(i));	
	}
	
	public GEOrder(GEOrderItem... items)
	{
		ORDER_ITEMS = Arrays.asList(items);
	}
	
	public void execute()
	{
		
	}
		
	/**
	 * Compile gather missions for order items which we failed to purchase
	 * 
	 * @return array of gather missions for order items which we failed to purchase
	 */
	public Mission[] getGatherMissions()
	{
		return ORDER_ITEMS.stream().filter(item -> !item.isPurchased())
				.flatMap(missions -> Arrays.stream(missions.getGatherMissions())).toArray(Mission[]::new);
	}
}
