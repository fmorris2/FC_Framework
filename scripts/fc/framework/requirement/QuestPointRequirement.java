package scripts.fc.framework.requirement;

import java.util.Arrays;

import org.tribot.api2007.Game;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;

import scripts.fc.framework.quest.QuestMission;
import scripts.fc.framework.script.FCMissionScript;

public class QuestPointRequirement extends Requirement {

	private final int QP_REQUIRED;
	private final QuestMission[] MISSIONS;
	
	private int requiredQuestPoints; 
	
	public QuestPointRequirement(FCMissionScript script, int requiredAmt, QuestMission... preReqMissions) {
		super(script);
		QP_REQUIRED = requiredAmt;
		MISSIONS = preReqMissions;
	}

	@Override
	public void checkReqs() {
		if(Login.getLoginState() == STATE.INGAME)
		{
			requiredQuestPoints = QP_REQUIRED - getQuestPoints();
			Arrays.stream(MISSIONS)
				.sorted((q1, q2) -> q2.getQuestPointReward() - q1.getQuestPointReward()) //sort by highest qp rewards first
				.forEach(mission -> {
					if(requiredQuestPoints > 0 && !mission.hasReachedEndingCondition())
						super.missions.add(mission);
						requiredQuestPoints -= mission.getQuestPointReward();
				});
			
			hasCheckedReqs = true;
		}
	}
	
	private int getQuestPoints()
	{
		return Game.getSetting(101);
	}

}
