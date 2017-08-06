package scripts.fc.framework.script;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

import org.tribot.api.General;
import org.tribot.script.interfaces.Starting;

import scripts.fc.api.inventory.FCInventoryListener;
import scripts.fc.api.inventory.FCInventoryObserver;
import scripts.fc.api.utils.InterfaceUtils;
import scripts.fc.framework.goal.Goal;
import scripts.fc.framework.goal.GoalList;
import scripts.fc.framework.mission.GoalMission;
import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.quest.QuestScriptManager;
import scripts.fc.framework.statistic_tracking.StatTracker;
import scripts.fc.framework.statistic_tracking.StatTracker.STATUS;
import scripts.fc.framework.statistic_tracking.StatTracking;

public abstract class FCMissionScript extends FCScript implements FCInventoryListener, Starting
{
	protected Queue<Mission> missions = getMissions();
	protected GoalList goalList;
	protected Mission currentMission;
	protected String username;
	public final FCInventoryObserver INV_OBSERVER = new FCInventoryObserver(this);
	
	private StatTracker statTracker;
	private boolean statTrackingFailed;
	
	public boolean compilingPreReqs;
	
	protected abstract Queue<Mission> getMissions();
	
	@Override	
	protected int mainLogic()
	{	
		if(statTrackingFailed)
			return -1;
		
		if(currentMission == null)
		{
			if(missions.isEmpty())
			{
				println("No more missions... ending");
				return -1;
			}
			else
			{
				compilePreReqs();
				currentMission = missions.poll();
				updateGoals();
				
				if(currentMission instanceof FCInventoryListener)
					INV_OBSERVER.addListener((FCInventoryListener)currentMission);
				
				if(currentMission instanceof StatTracking)
				{
					statTracker = new StatTracker();
					if(!statTracker.reportOnline(currentMission.getMissionName()) && statTracker.getStatus() == STATUS.NOT_ALLOWED_FIREWALL)
					{
						println("You must enable the connection to " + StatTracker.SOCKET_URL + " for statistic tracking purposes.");
						statTrackingFailed = true;
						return 100;
					}
				}
			}
		}
		
		if(currentMission.hasReachedEndingCondition())
		{
			if(InterfaceUtils.isQuestInterfaceUp())
				InterfaceUtils.closeQuestInterface();
			
			reportStats();	
			println(currentMission.getEndingMessage());
			compilePreReqs();
			currentMission = missions.poll();
			updateGoals();
		}
		else
		{	
			currentMission.execute();
		}
		
		return 100;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		INV_OBSERVER.start();
	}
	
	@Override
	public void onEnd()
	{		
		super.onEnd();
		reportStats();
		INV_OBSERVER.isRunning = false;
	}
	
	private void reportStats()
	{
		if(currentMission instanceof StatTracking)
		{
			//stat tracking
			if(statTracker != null && statTracker.isOnline() && !statTracker.hasReported())
			{
				println("Reporting statistics to FCScripting database...");
				String statsArgs = ((StatTracking)(currentMission)).getStatsArgs();
				statTracker.report(statsArgs);
			}
		}
	}
	
	public void compilePreReqs()
	{
		LinkedList<Mission> normalMissions = new LinkedList<>(missions);
		
		if(normalMissions.isEmpty())
			return;
		
		Mission first = normalMissions.getFirst();
		
		if(first.hasReachedEndingCondition())
			return;
		
		if(first instanceof QuestScriptManager)
		{
			QuestScriptManager qsm = (QuestScriptManager)first;
			
			if(qsm.hasAddedPreReqs())
				return;
			
			compilingPreReqs = true;
			qsm.compilePreReqs();
			qsm.setHasAddedPreReqs(true);
			for(Mission req : qsm.getPreReqMissions())
			{
				General.println("ADDED PRE REQ MISSION FOR " + qsm.getMissionName().toUpperCase() + ": " + req.getMissionName() 
						+ ((req instanceof GoalMission) ? ", GOALS: " + ((GoalMission)(req)).getGoals() : ""));
				normalMissions.addFirst(req);
			}
		}
		
		missions.clear();
		missions.addAll(normalMissions);
		compilingPreReqs = false;
	}
	
	public Mission getCurrentMission()
	{
		return currentMission;
	}
	
	public void setCurrentMission(Mission m)
	{
		currentMission = m;
	}
	
	public Queue<Mission> getSetMissions()
	{
		return missions;
	}
	
	protected String[] basicPaint()
	{
		String currentMissionName = compilingPreReqs ? "Mission Prerequisites" : currentMission == null ? null : currentMission.getMissionName();
		String status = compilingPreReqs ? "Checking" : currentMission == null ? null : currentMission.getCurrentTaskName();
		
		String[] missionPaint;
		
		if(goalList != null && !goalList.isEmpty())
				missionPaint = new String[]{"Current mission: " + currentMissionName,
				"Goals: " + getGoalString(),
				"Current task: " + (status != null ? status : "null")};
		else
				missionPaint = new String[]{"Current mission: " + currentMissionName,
					"Current task: " + (status != null ? status : "null")};
		
		String[] basicPaint = Stream.concat(Arrays.stream(super.basicPaint()), Arrays.stream(missionPaint)).toArray(String[]::new);
		String[] currentMissionPaint = currentMission == null ? new String[0] : currentMission.getMissionSpecificPaint();
		
		return Stream.concat(Arrays.stream(basicPaint), Arrays.stream(currentMissionPaint)).toArray(String[]::new);
	}
	
	private String getGoalString()
	{
		String str = "";
		
		for(Goal g : goalList)
			str += "[" + g.getName() + "] ";
			
		return str;
	}
	
	private void updateGoals()
	{
		if(currentMission == null || !(currentMission instanceof GoalMission))
			return;
		
		GoalList gl = ((GoalMission)(currentMission)).getGoals();
		goalList = gl == null ? new GoalList() : gl;
	}
	
	public void inventoryAdded(int id, int count)
	{
		if(goalList != null)
			goalList.updateResourceGoals(id, count);
	}
	
	public void inventoryRemoved(int id, int count)
	{}
}
