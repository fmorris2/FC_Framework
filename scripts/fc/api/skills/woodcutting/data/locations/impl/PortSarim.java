package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class PortSarim extends GatheringLocation<LogType>
{
	private static final Positionable BANK_TILE = new RSTile(3043, 3236, 0);
	
	private static DPathNavigator dPath = new DPathNavigator();
	
	@Override
	public List<LogType> getSupported()
	{
		return new ArrayList<>(Arrays.asList(LogType.NORMAL, LogType.OAK, LogType.WILLOW, LogType.YEW));
	}

	@Override
	public String getName()
	{
		return "Port Sarim Willow";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3051, 3264, 0);
	}

	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.withinDistanceOfTile(centerTile, 8), 600);
	}

	@Override
	public boolean isDepositBox()
	{
		return true;
	}

	@Override
	public boolean goToBank()
	{
		return dPath.traverse(BANK_TILE);
	}

	@Override
	public boolean hasRequirements()
	{
		return true;
	}

	@Override
	public int getRadius()
	{
		return 20;
	}

}
