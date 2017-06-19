package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class VarrockCastle extends GatheringLocation<LogType>
{
	private static final Positionable BANK_TILE = new RSTile(3167, 3490, 0);
	
	private DPathNavigator dPath;
	
	public VarrockCastle()
	{
		dPath = new DPathNavigator();
	}
	
	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.YEW);
	}

	@Override
	public String getName()
	{
		return "Varrock Castle";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3212, 3501, 0);
	}

	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.inAreaCondition(area), 600);
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

	@Override
	public boolean goToBank()
	{
		return dPath.traverse(BANK_TILE) || WebWalking.walkToBank();
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

}
