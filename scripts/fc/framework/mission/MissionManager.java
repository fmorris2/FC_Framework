package scripts.fc.framework.mission;

import scripts.fc.framework.script.FCMissionScript;
import scripts.fc.framework.task.TaskManager;

public abstract class MissionManager extends TaskManager implements Mission
{
	private boolean hasAddedPreReqs;
	protected FCMissionScript missionScript;
	
	public MissionManager(FCMissionScript script)
	{
		super(script);
		this.missionScript = script;
	}
	
	public MissionManager()
	{
		super();
	}

	public boolean hasAddedPreReqs()
	{
		return hasAddedPreReqs;
	}
	
	public void setHasAddedPreReqs(boolean b)
	{
		hasAddedPreReqs = b;
	}
}
