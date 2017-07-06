package scripts.fc.framework.quest;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.General;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.mission.MissionManager;
import scripts.fc.framework.requirement.Requirement;
import scripts.fc.framework.script.FCMissionScript;

public abstract class QuestScriptManager extends MissionManager implements QuestMission
{
	private List<Mission> preReqMissions = new ArrayList<Mission>();
	
	public QuestScriptManager(FCMissionScript fcScript)
	{
		super(fcScript);
	}

	public abstract Requirement[] getRequirements();
	
	@Override
	public String getCurrentTaskName()
	{
		return currentTask == null ? "null" : currentTask.getStatus();
	}
	
	@Override
	public void execute()
	{		
		executeTasks();
	}
	
	public void compilePreReqs()
	{
		for(Requirement req : getRequirements())
		{
			while(!req.hasCheckedReqs())
			{
				req.checkReqs();
				General.sleep(100);
			}
			
			if(req.cannotContinue())
			{
				running = false;
				return;
			}
			
			preReqMissions.addAll(req.getMissions());
		}
	}
	
	public List<Mission> getPreReqMissions()
	{
		return preReqMissions;
	}
}
