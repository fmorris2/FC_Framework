package scripts.fc.api.skills;

import org.tribot.api2007.WebWalking;

import scripts.fc.api.generic.FCConditions;

public abstract class BasicGatheringLocation<T> extends GatheringLocation<T>
{
	private final int CENTER_TILE_THRESH = 8;
	@Override
	public boolean goTo()
	{
		return WebWalking.walkTo(centerTile.getPosition(), FCConditions.withinDistanceOfTile(centerTile, 
				(getRadius() > CENTER_TILE_THRESH ? CENTER_TILE_THRESH : getRadius() - 1)), 600);
	}
	
	@Override
	public boolean isDepositBox()
	{
		return false;
	}

	@Override
	public boolean goToBank()
	{
		if(isInBank())
			return true;
		
		return WebWalking.walkToBank();
	}

	@Override
	public boolean hasRequirements()
	{
		return true;
	}
}
