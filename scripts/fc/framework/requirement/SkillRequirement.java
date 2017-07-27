package scripts.fc.framework.requirement;

import java.util.Arrays;

import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.script.FCMissionScript;

public class SkillRequirement extends Requirement
{
	private Mission[] missions;
	private SKILLS skill;
	private int req;
	
	public SkillRequirement(FCMissionScript script, SKILLS skill, int req, Mission... missions)
	{
		super(script);
		this.skill = skill;
		this.req = req;
		this.missions = missions;
	}

	@Override
	public void checkReqs()
	{
		if(Login.getLoginState() == STATE.INGAME)
		{
			if(Skills.getActualLevel(skill) < req)
				super.missions.addAll(Arrays.asList(this.missions));
			
			hasCheckedReqs = true;
		}
	}

}
