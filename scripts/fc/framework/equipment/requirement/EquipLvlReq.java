package scripts.fc.framework.equipment.requirement;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

public class EquipLvlReq implements WieldEquipmentRequirement {
	
	final SKILLS SKILL;
	final int LVL;
	
	public EquipLvlReq(SKILLS skill, int lvl) {
		SKILL = skill;
		LVL = lvl;
	}
	
	@Override
	public boolean canWield() {
		return Skills.getActualLevel(SKILL) >= LVL;
	}

}
