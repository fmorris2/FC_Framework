package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.skills.BasicGatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class DraynorVillage extends BasicGatheringLocation<LogType>
{
	private static final int REQ_COMBAT_LEVEL = 15;
	
	@Override
	public String getName()
	{
		return "Draynor Village";
	}

	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.WILLOW);
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3088, 3233, 0);
	}

	@Override
	public int getRadius()
	{
		return 10;
	}
	
	@Override
	public boolean hasRequirements()
	{
		RSPlayer p = Player.getRSPlayer();
		return p != null && p.getCombatLevel() >= REQ_COMBAT_LEVEL;
	}

}
