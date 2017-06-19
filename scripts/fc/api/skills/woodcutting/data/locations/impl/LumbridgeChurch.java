package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.skills.BasicGatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class LumbridgeChurch extends BasicGatheringLocation<LogType>
{

	@Override
	public String getName()
	{
		return "Lumbridge Church";
	}

	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.YEW);
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3248, 3201, 0);
	}

	@Override
	public int getRadius()
	{
		return 6;
	}

}
