package scripts.fc.framework.quest;

import scripts.fc.framework.mission.Mission;

public interface QuestMission extends Mission
{
	public boolean canStart();
	public int getQuestPointReward();
}
