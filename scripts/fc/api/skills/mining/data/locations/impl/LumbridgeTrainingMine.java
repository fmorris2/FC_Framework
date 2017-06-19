package scripts.fc.api.skills.mining.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.mining.data.RockType;
import scripts.fc.api.skills.mining.data.locations.MiningLocation;

public class LumbridgeTrainingMine extends MiningLocation
{
	private static final long serialVersionUID = -8273930490222748641L;

	@Override
	public List<RockType> getSupported()
	{
		return Arrays.asList(RockType.COPPER, RockType.TIN);
	}

	@Override
	public String getName()
	{
		return "Lumbridge Training";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3227, 3147, 0);
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
		return 10;
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

}
