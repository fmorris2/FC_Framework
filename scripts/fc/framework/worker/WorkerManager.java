package scripts.fc.framework.worker;

import scripts.fc.framework.goal.GoalManager;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.script.FCMissionScript;

public abstract class WorkerManager extends GoalManager implements Mission
{
	protected FCMissionScript missionScript;
	
	public WorkerManager(FCMissionScript script)
	{
		this.missionScript = script;
	}
}
