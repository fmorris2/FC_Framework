package scripts.fc.api.skills.mining.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.mining.data.RockType;
import scripts.fc.api.skills.mining.data.locations.MiningLocation;

public class BarbVillageMine extends MiningLocation
{
	private static final long serialVersionUID = 3141957479419137744L;

	@Override
	public List<RockType> getSupported()
	{
		return Arrays.asList(RockType.COAL, RockType.TIN);
	}

	@Override
	public String getName()
	{
		return "Barbarian Village";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3081, 3421, 0);
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
		return true;
	}

	@Override
	public int getRadius()
	{
		return 7;
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

}
