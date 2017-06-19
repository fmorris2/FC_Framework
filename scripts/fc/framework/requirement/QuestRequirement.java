package scripts.fc.framework.requirement;

import org.tribot.api2007.Game;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;

import scripts.fc.framework.script.FCMissionScript;

public abstract class QuestRequirement extends Requirement
{
	public abstract int getSettingIndex();
	public abstract int getSettingValue();
	
	public QuestRequirement(FCMissionScript script)
	{
		super(script);
	}
	
	@Override
	public void checkReqs()
	{
		if(Login.getLoginState() == STATE.INGAME)
		{
			if(Game.getSetting(getSettingIndex()) != getSettingValue())
				missions.addAll(getReqMissions());
			
			hasCheckedReqs = true;
		}
	}
}
