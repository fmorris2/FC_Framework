package scripts.fc.api.skills.mining.data.locations.impl;

import java.util.ArrayList;
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

public class VarrockEastMine extends MiningLocation
{
	private static final long serialVersionUID = -8396891264609368228L;

	@Override
	public List<RockType> getSupported()
	{
		return new ArrayList<>(Arrays.asList(RockType.COPPER, RockType.TIN, RockType.IRON));
	}

	@Override
	public String getName()
	{
		return "Varrock East";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3285, 3365, 0);
	}
	
	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.inAreaCondition(area), 600);
	}

	@Override
	public boolean hasRequirements()
	{
		RSPlayer player = Player.getRSPlayer();
		
		return player == null ? false : player.getCombatLevel() >= 13;
	}

	@Override
	public int getRadius()
	{
		return 8;
	}

	@Override
	public boolean goToBank()
	{
		return WebWalking.walkToBank();
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

}
