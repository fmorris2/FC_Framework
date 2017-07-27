package scripts.fc.framework.requirement;

import java.util.ArrayList;
import java.util.List;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.script.FCMissionScript;

public abstract class Requirement
{
	protected List<Mission> missions = new ArrayList<Mission>(); //The list of missions that will satisfy the requirements
	protected boolean hasCheckedReqs;
	protected FCMissionScript script;
	protected boolean cannotContinue;
	
	public abstract void checkReqs();
	
	public Requirement(FCMissionScript script)
	{
		this.script = script;
	}
	
	public List<Mission> getMissions()
	{
		return missions;
	}
	
	public boolean hasCheckedReqs()
	{
		return hasCheckedReqs;
	}
	
	public boolean cannotContinue()
	{
		return cannotContinue;
	}
	
}
