package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.skills.BasicGatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class NorthDraynor extends BasicGatheringLocation<LogType>
{

	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.NORMAL);
	}

	@Override
	public String getName()
	{
		return "North Draynor";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3080, 3269, 0);
	}

	@Override
	public int getRadius()
	{
		return 12;
	}

}
