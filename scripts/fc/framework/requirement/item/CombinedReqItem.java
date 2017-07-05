package scripts.fc.framework.requirement.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		
		return type == Type.OR ? one.isSatisfied() || two.isSatisfied() : one.isSatisfied() && two.isSatisfied();
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
