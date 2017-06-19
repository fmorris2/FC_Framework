package scripts.fc.api.skills.woodcutting.data.locations.impl;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;

import scripts.fc.api.skills.GatheringLocation;
import scripts.fc.api.skills.woodcutting.data.LogType;

public class SouthVarrock extends GatheringLocation<LogType>
{
	private static final Positionable[] TREE_TO_BANK = 
	{
			new RSTile(3254, 3367, 0), new RSTile(3259, 3368, 0),
			new RSTile(3264, 3367, 0), new RSTile(3269, 3367, 0),
			new RSTile(3274, 3368, 0), new RSTile(3279, 3371, 0),
			new RSTile(3284, 3371, 0), new RSTile(3289, 3374, 0),
			new RSTile(3290, 3379, 0), new RSTile(3290, 3384, 0),
			new RSTile(3291, 3389, 0), new RSTile(3291, 3394, 0),
			new RSTile(3291, 3399, 0), new RSTile(3291, 3404, 0),
			new RSTile(3291, 3408, 0), new RSTile(3289, 3412, 0),
			new RSTile(3286, 3416, 0), new RSTile(3285, 3421, 0),
			new RSTile(3281, 3422, 0), new RSTile(3278, 3424, 0),
			new RSTile(3275, 3426, 0), new RSTile(3270, 3426, 0),
			new RSTile(3265, 3426, 0), new RSTile(3260, 3427, 0),
			new RSTile(3255, 3427, 0), new RSTile(3254, 3422, 0)
	};
	
	private static final Positionable[] BANK_TO_TREE = Walking.invertPath(TREE_TO_BANK);
	
	@Override
	public List<LogType> getSupported()
	{
		return Arrays.asList(LogType.YEW);
	}

	@Override
	public String getName()
	{
		return "South Varrock";
	}

	@Override
	public Positionable centerTile()
	{
		return new RSTile(3254, 3367, 0);
	}

	@Override
	public boolean goTo()
	{
		if(area.contains(Player.getPosition()))
			return true;
		
		if(!Walking.walkPath(BANK_TO_TREE))
			return WebWalking.walkTo(centerTile);
		
		return true;
	}

	@Override
	public boolean isDepositBox()
	{
		return false;
	}

	@Override
	public boolean goToBank()
	{
		if(!Walking.walkPath(TREE_TO_BANK))
			return WebWalking.walkToBank();
		
		return true;
	}

	@Override
	public boolean hasRequirements()
	{
		return true;
	}

	@Override
	public int getRadius()
	{
		return 6;
	}

}
