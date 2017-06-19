package scripts.fc.api.skills.fishing.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.skills.BasicGatheringLocation;
import scripts.fc.api.skills.fishing.FishType;

public class DraynorVillage extends BasicGatheringLocation<FishType>
{
	private static final int REQ_COMBAT_LEVEL = 15;
	
	@Override
	public String getName()
	{
		return "Draynor Village";
	}

	@Override
	public List<FishType> getSupported()
	{
		return Arrays.asList(FishType.SHRIMPS, FishType.ANCHOVIES);
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3087, 3228, 0);
	}

	@Override
	public int getRadius()
	{
		return 15;
	}
	
	@Override
	public boolean hasRequirements()
	{
		RSPlayer p = Player.getRSPlayer();
		return p != null && p.getCombatLevel() >= REQ_COMBAT_LEVEL;
	}

}
