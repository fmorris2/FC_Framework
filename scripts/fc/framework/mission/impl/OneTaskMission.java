package scripts.fc.framework.mission.impl;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.task.Task;

public class OneTaskMission implements Mission
{
	private Task task;
	private String missionName;

	public OneTaskMission(Task task, String missionName)
	{
		this.task = task;
		this.missionName = missionName;
	}
	
	@Override
	public boolean hasReachedEndingCondition()
	{
		return !task.shouldExecute();
	}

	@Override
	public String getMissionName()
	{
		return missionName;
	}

	@Override
	public String getCurrentTaskName()
	{
		return task.getStatus();
	}

	@Override
	public String getEndingMessage()
	{
		return missionName + " has ended.";
	}

	@Override
	public String[] getMissionSpecificPaint()
	{
		return new String[]{};
	}

	@Override
	public void execute()
	{
		task.execute();
	}

	@Override
	public void resetStatistics()
	{}

}
