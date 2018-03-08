package scripts.fc.framework.requirement.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tribot.api2007.types.RSItem;

public class CombinedReqItem extends ReqItem
{
	private ReqItem one, two;
	private Type type;
	
	public CombinedReqItem(ReqItem one, ReqItem two, Type t)
	{
		this.one = one;
		this.two = two;
		this.type = t;
	}

	@Override
	public void check(RSItem[] items)
	{
		one.check(items);
		two.check(items);
	}

	@Override
	public boolean isSatisfied()
	{
		if(!checkBools()) //our bools say we don't need this requirement, so mark it as satisfied
			return true;
		
		if(type == Type.OR) {
			return one.isSatisfied() || two.isSatisfied();
		}
		
		List<SingleReqItem> reducedNeeds = getReducedNeededReqItems();
		return reducedNeeds.stream()
					.allMatch(req -> req.isSatisfied());
	}
	
	private List<SingleReqItem> getReducedNeededReqItems() {
		Map<Integer, SingleReqItem> itemMap = new HashMap<>();
		for(SingleReqItem i : one.getSingleReqItems()) {
			if(i.checkBools()) {
				itemMap.put(i.getId(), i);
			}
		}
		
		for(SingleReqItem i : two.getSingleReqItems()) {
			if(i.checkBools()) {
				if(itemMap.containsKey(i.getId())) {
					itemMap.get(i.getId()).addToAmt(i.getAmt());
				} else {
					itemMap.put(i.getId(), i);
				}
			}
		}
		
		return itemMap.values().stream()
					.collect(Collectors.toList());
	}
	
	@Override
	public List<SingleReqItem> getSingleReqItems()
	{
		ReqItem[] items = {one, two};
		
		List<SingleReqItem> singleReqItems = new ArrayList<>();
		
		Arrays.stream(items).forEach(i ->
		{
			if(i instanceof SingleReqItem)
				singleReqItems.add((SingleReqItem)i);
			else
				singleReqItems.addAll(((CombinedReqItem)(i)).getSingleReqItems());
		});
			
		return singleReqItems;
	}
	
	public enum Type
	{
		OR,
		AND;
	}
	
	public String toString()
	{
		return "[" + one + " " + type + " " + two + "]";
	}
}
