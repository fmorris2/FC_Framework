package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class Edgeville extends GatheringLocation<LogType>
{
	private static final Positionable BANK = new RSTile(3093, 3491, 0);
	private static final int DIST_THRESH = 25;
	
	private DPathNavigator dPath;
	
	public Edgeville()
	{
		dPath = new DPathNavigator();
		dPath.setExcludeTiles(new RSTile[]{new RSTile(3097, 3468, 0)});
	}
	
	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.YEW);
	}

	@Override
	public String getName()
	{
		return "Edgeville";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3088, 3475, 0);
	}

	@Override
	public boolean goTo()
	{
		if(Player.getPosition().distanceTo(centerTile) > DIST_THRESH)
			return WebWalking.walkTo(BANK);
		else
			return dPath.traverse(centerTile);
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

	@Override
	public boolean goToBank()
	{
		if(Player.getPosition().distanceTo(centerTile) > DIST_THRESH)
			return WebWalking.walkTo(BANK);
		else
			return dPath.traverse(BANK);
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

}
