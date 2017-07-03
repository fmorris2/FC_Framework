package scripts.fc.api.generic;

import java.util.Arrays;

import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

public class FCFilters
{
	public static Filter<RSObject> isReachable()
	{
		return new Filter<RSObject>()
		{
			@Override
			public boolean accept(RSObject o)
			{
				return PathFinding.canReach(o, true);
			}
		};
	}
	
	public static Filter<RSNPC> isNpcReachable()
	{
		return new Filter<RSNPC>()
		{
			@Override
			public boolean accept(RSNPC o)
			{
				return PathFinding.canReach(o, true);
			}
		};
	}
	
	public static Filter<Positionable> inArea(RSArea a)
	{
		return new Filter<Positionable>()
		{
			public boolean accept(Positionable p)
			{
				return a.contains(p);
			}
		};
	}
	
	public static Filter<RSMenuNode> correlatesTo(String action, Clickable clickable)
	{
		return new Filter<RSMenuNode>()
		{
			@Override
			public boolean accept(RSMenuNode o)
			{
				return o.containsAction(action) && o.correlatesTo(clickable);
			}
		};
	}
	
	public static Filter<RSMenuNode> optionContains(String option)
	{
		return new Filter<RSMenuNode>()
		{
			@Override
			public boolean accept(RSMenuNode node)
			{
				return node.getAction().contains(option);
			}
			
		};
	}
	
	public static Filter<RSInterface> containsAction(String action)
	{
		return new Filter<RSInterface>()
		{
			@Override
			public boolean accept(RSInterface i)
			{
				String[] actions;
				if(i == null || (actions = i.getActions()) == null)
					return false;
				
				return Arrays.stream(actions).anyMatch(a -> a != null && a.equalsIgnoreCase(action));
			}
		};
	}
	
}
