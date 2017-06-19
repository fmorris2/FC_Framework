package scripts.fc.api.skills.fishing.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.skills.BasicGatheringLocation;
import scripts.fc.api.skills.fishing.FishType;

public class BarbarianVillage extends BasicGatheringLocation<FishType>
{
	@Override
	public String getName()
	{
		return "Barbarian Village";
	}

	@Override
	public List<FishType> getSupported()
	{
		return Arrays.asList(FishType.TROUT, FishType.SALMON);
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3105, 3434, 0);
	}

	@Override
	public int getRadius()
	{
		return 15;
	}

}
