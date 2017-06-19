package scripts.fc.api.skills.mining.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.mining.data.RockType;
import scripts.fc.api.skills.mining.data.locations.MiningLocation;

public class AlKharidMine extends MiningLocation
{
	private static final long serialVersionUID = 7270341397422978498L;

	@Override
	public List<RockType> getSupported()
	{
		return Arrays.asList(RockType.TIN, RockType.COPPER, RockType.IRON, RockType.SILVER, RockType.COAL, RockType.GOLD,
				RockType.MITHRIL, RockType.ADAMANTITE);
	}

	@Override
	public String getName()
	{
		return "Al-Kharid";
	}

	@Override
	public Positionable centerTile()
	{
		//3298 3297
		return new RSTile(3298, 3297, 0);
	}

	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.inAreaCondition(area), 600);
	}

	@Override
	public boolean goToBank()
	{
		return WebWalking.walkToBank();
	}

	@Override
	public boolean hasRequirements()
	{
		RSPlayer player = Player.getRSPlayer();
		
		return player == null ? false : player.getCombatLevel() >= 29;
	}

	@Override
	public int getRadius()
	{
		return 25;
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

}
