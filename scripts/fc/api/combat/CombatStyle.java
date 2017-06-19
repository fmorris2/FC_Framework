package scripts.fc.api.combat;

import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.types.RSItem;

public enum CombatStyle
{
	ATTACK,
	STRENGTH,
	DEFENCE,
	CONTROLLED;
	
	public static CombatStyle getCurrentStyle()
	{
		switch(Combat.getSelectedStyleIndex())
		{
		case 0:
			return ATTACK;
		case 1:
			return STRENGTH;
		case 2:
			return CONTROLLED;
		case 3:
			return DEFENCE;
		}
		
		return ATTACK;	
	}
	
	public static void selectStyle(CombatStyle style)
	{
		RSItem wep = Equipment.getItem(SLOTS.WEAPON);
		String wepName = wep == null ? null : wep.getDefinition().getName();
		
		if(wepName == null) //Unarmed
			selectSpecialStyle(style);
		else
			selectNormalStyle(style);
	}
	
	private static void selectSpecialStyle(CombatStyle style)
	{
		switch(style)
		{
			case ATTACK:
				Combat.selectIndex(0);
				break;
			case STRENGTH:
				Combat.selectIndex(1);
				break;
			case DEFENCE:
				Combat.selectIndex(2);
				break;
			default:
			break;
		}
	}
	
	private static void selectNormalStyle(CombatStyle style)
	{
		switch(style)
		{
			case ATTACK:
				Combat.selectIndex(0);
				break;
			case STRENGTH:
				Combat.selectIndex(1);
				break;
			case CONTROLLED:
				Combat.selectIndex(2);
				break;
			case DEFENCE:
				Combat.selectIndex(3);
				break;
			default:
				break;	
		}
	}
}
