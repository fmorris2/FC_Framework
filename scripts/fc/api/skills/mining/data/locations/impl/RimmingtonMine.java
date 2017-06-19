package scripts.fc.api.skills.mining.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.mining.data.RockType;
import scripts.fc.api.skills.mining.data.locations.MiningLocation;

public class RimmingtonMine extends MiningLocation
{
	private static final long serialVersionUID = 7281297825898593125L;
	
	private static final Positionable DEPOSIT_BOX_TILE = new RSTile(3045, 3235, 0);
	private static final RSArea DEPOSIT_BOX_AREA = new RSArea(DEPOSIT_BOX_TILE, 7);

	@Override
	public List<RockType> getSupported()
	{
		return Arrays.asList(RockType.TIN, RockType.COPPER, RockType.CLAY, RockType.IRON, RockType.GOLD);
	}

	@Override
	public String getName()
	{
		return "Rimmington";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(2977, 3239, 0);
	}

	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.inAreaCondition(area), 600);
	}

	@Override
	public boolean goToBank()
	{
		return WebWalking.walkTo(DEPOSIT_BOX_TILE, FCConditions.inAreaCondition(DEPOSIT_BOX_AREA), 600);
	}

	@Override
	public boolean hasRequirements()
	{
		return true;
	}

	@Override
	public int getRadius()
	{
		return 15;
	}

	@Override
	public boolean isDepositBox()
	{
		return true;
	}

}
