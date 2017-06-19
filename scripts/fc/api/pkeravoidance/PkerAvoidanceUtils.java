package scripts.fc.api.pkeravoidance;

import java.util.Arrays;
import java.util.List;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;

import scripts.fc.api.utils.Utils;

public class PkerAvoidanceUtils
{
	public static final Filter<RSPlayer> CAN_ATTACK_FILTER = canAttackFilter();
	public static final Filter<RSPlayer> NOT_RUNNING_ORBS_FILTER = notRunningOrbsFilter();
	
	private static Filter<RSPlayer> canAttackFilter()
	{
		return new Filter<RSPlayer>()
		{
			@Override
			public boolean accept(RSPlayer o)
			{
				final int WILDY_LEVEL = Utils.getWildyLevel();
				
				return Math.abs(Player.getRSPlayer().getCombatLevel() - o.getCombatLevel()) <= WILDY_LEVEL;
			}	
		};
	}
	
	private static Filter<RSPlayer> notRunningOrbsFilter()
	{
		final String[] SAFE_WEAPONS = {"Staff of air", "Smoke battlestaff"};
		
		return new Filter<RSPlayer>()
		{
			@Override
			public boolean accept(RSPlayer o)
			{
				return !o.equals(Player.getRSPlayer()) && /*!hasSafeWep(o, SAFE_WEAPONS) && */o.getSkullIcon() == 0;
			}	
		};
	}
	
	private static boolean hasSafeWep(RSPlayer o, String[] safeWeps)
	{	
		RSPlayerDefinition def = o.getDefinition();
		final List<String> SAFE_WEPS = Arrays.asList(safeWeps);
		
		if(def != null)
		{
			for(RSItem i : def.getEquipment())
			{
				RSItemDefinition itemDef = i.getDefinition();
				
				if(itemDef != null && SAFE_WEPS.contains(itemDef.getName()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
