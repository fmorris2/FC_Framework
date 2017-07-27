package scripts.fc.framework.requirement;

import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;

import scripts.fc.framework.quest.QuestScriptManager;
import scripts.fc.framework.script.FCMissionScript;

public abstract class QuestRequirement extends Requirement
{
	private QuestScriptManager quest;
	
	public QuestRequirement(FCMissionScript script, QuestScriptManager quest)
	{
		super(script);
		this.quest = quest;
	}
	
	@Override
	public void checkReqs()
	{
		if(Login.getLoginState() == STATE.INGAME)
		{
			if(!quest.hasReachedEndingCondition())
				missions.add(quest);
			
			hasCheckedReqs = true;
		}
	}
}
