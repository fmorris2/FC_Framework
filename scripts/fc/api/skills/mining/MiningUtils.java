package scripts.fc.api.skills.mining;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;

import scripts.fc.api.skills.mining.data.Pickaxe;
import scripts.fc.api.skills.mining.data.RockType;

public class MiningUtils
{	
	/*
	 * This variable represents the max distance a prospective rock can be away from
	 * the player, if we're currently waiting on another to respawn. It doesn't make sense to walk to a
	 * far away rock just because it's available, when the current rock will respawn in a second or two.
	 */
	private static final int LOW_ROCK_DIST_THRESHOLD = 4;
	private static final int MIN_ROCKS_IN_CLUSTER = 2;
	
	private static RockType BEST_LOW_LVL_ROCK = RockType.IRON; //THIS ROCK AND ALL ROCKS BELOW IT ARE "LOW LEVEL ROCKS"
	
	public static Pickaxe getBestUsablePick(boolean isCheckingBank)
	{
		Pickaxe[] picks = Pickaxe.values();
		
		for(int i = picks.length - 1; i >= 0; i--)
		{
			Pickaxe pick = picks[i];
			if(pick.getMiningLevel() <= Skills.getActualLevel(SKILLS.MINING) //Mining level check
					&& ((Inventory.getCount(pick.getItemId()) > 0 || Equipment.getCount(pick.getItemId()) > 0) //Inventory check
							|| (isCheckingBank && Banking.find(pick.getItemId()).length > 0))) //Bank check
				return pick;
		}
		
		return null;
	}
	
	public static Pickaxe currentAppropriatePick()
	{
		Pickaxe[] picks = Pickaxe.values();
		
		for(int i = picks.length - 1; i >= 0; i--)
			if(picks[i].getMiningLevel() <= Skills.getActualLevel(SKILLS.MINING))
				return picks[i];
		
		return Pickaxe.IRON;
	}
	
	public static Filter<RSObject> rockFilter(RockType rock, RSObject[] previousRocks)
	{
		return new Filter<RSObject>()
		{
			@Override
			public boolean accept(RSObject o)
			{
				if(rock.getIds().contains(o.getID()))
					return !isTooFarAway(rock, o, previousRocks);
				else if(isColorMatch(o, rock))
				{
					rock.getIds().add(o.getID());
					
					return !isTooFarAway(rock, o, previousRocks);
				}
				
				return false;
			}
			
		}.combine(Filters.Objects.nameEquals("Rocks"), true);
	}
	
	private static boolean isTooFarAway(RockType rock, RSObject o, RSObject[] previousRocks)
	{
		return previousRocks.length >= MIN_ROCKS_IN_CLUSTER && rock.ordinal() <= BEST_LOW_LVL_ROCK.ordinal() 
				&& previousRocks[0].getPosition().distanceTo(o) > LOW_ROCK_DIST_THRESHOLD;
	}
	
	private static boolean isColorMatch(RSObject o, RockType rock)
	{
		for(short color : o.getDefinition().getModifiedColors())
		{
			for(int i : rock.getColors())
			{
				if(color == i)
					return true;
			}
		}
		
		return false;
	}
}
