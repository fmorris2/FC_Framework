package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class Rimmington extends GatheringLocation<LogType>
{

	private static final Positionable BANK_TILE = new RSTile(3043, 3236, 0);
	
	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.YEW);
	}

	@Override
	public String getName()
	{
		return "Rimmington";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(2938, 3230, 0);
	}

	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.inAreaCondition(area), 600);
	}

	@Override
	public boolean isDepositBox()
	{
		return true;
	}

	@Override
	public boolean goToBank()
	{
		return WebWalking.walkTo(BANK_TILE);
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

}
