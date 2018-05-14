package scripts.fc.framework.requirement;

import java.util.Arrays;

import org.tribot.api2007.Game;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;

import scripts.fc.framework.quest.QuestMission;
import scripts.fc.framework.script.FCMissionScript;

public class QuestPointRequirement extends Requirement {

	final int QP_REQUIRED;
	final QuestMission[] MISSIONS;
	
	public QuestPointRequirement(FCMissionScript script, int requiredAmt, QuestMission... preReqMissions) {
		super(script);
		QP_REQUIRED = requiredAmt;
		MISSIONS = preReqMissions;
	}

	@Override
	public void checkReqs() {
		if(Login.getLoginState() == STATE.INGAME)
		{
			int requiredQuestPoints = getQuestPoints() - QP_REQUIRED;
			Arrays.stream(MISSIONS).forEach(mission -> {
				if(requiredQuestPoints > 0 && !mission.hasReachedEndingCondition())
					missions.add(mission);
			});
			
			hasCheckedReqs = true;
		}
	}
	
	private int getQuestPoints()
	{
		return Game.getSetting(101);
	}

}
