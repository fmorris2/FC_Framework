package scripts.fc.api.skills;

import org.tribot.api2007.Skills.SKILLS;

public class SkillUtils
{
	public static int getTotalLevel()
	{
		int total = 0;
		SKILLS[] skills = SKILLS.values();
		for(SKILLS s : skills)
			total += s.getActualLevel();
		
		return total;
	}
}
