package scripts.fc.framework.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.tribot.api.General;

import scripts.fc.framework.mission.Mission;
import scripts.fc.framework.mission.MissionManager;
import scripts.fc.framework.requirement.Requirement;
import scripts.fc.framework.requirement.item.ItemRequirement;
import scripts.fc.framework.requirement.item.ReqItem;
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
		General.println("[Prerequisites] Compiling pre-reqs for " + getMissionName());
		for(Requirement req : getRequirements())
		{
			if(req instanceof ItemRequirement) //Prepare for future quests... combine multiple GE trips into one
			{
				((ItemRequirement)(req)).reset();
				addFutureItemReqs((ItemRequirement)req);
			}
			
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
	
	private void addFutureItemReqs(ItemRequirement currentReq)
	{
		List<QuestScriptManager> futureMissions = new ArrayList<>();
		
		//add all of our future missions which extend QuestScriptManager to a list
		missionScript.getSetMissions().stream()
			.filter(m -> m instanceof QuestScriptManager && m != this)
			.forEach(m -> futureMissions.add((QuestScriptManager)m));
		
		//compile all of the future ItemRequirements
		List<ItemRequirement> futureItemReqs = new ArrayList<>();
		futureMissions.stream()
			.flatMap(m -> Arrays.stream(m.getRequirements())) //compile the requirements of every mission into a single stream
			.filter(r -> r instanceof ItemRequirement) //filter every requirement down to only the item requirements
			.forEach(i -> futureItemReqs.add((ItemRequirement)i)); //add the item requirements to a list
		
		General.println("[Requirements] Found " + futureItemReqs + " future quests with item requirements");
		
		//compile all of the future req items from all of the ItemRequirements into a list
		List<ReqItem> futureReqItems = futureItemReqs.stream()
			.flatMap(itemReq -> Arrays.stream(itemReq.getReqItems()))
			.collect(Collectors.toList());
		futureReqItems.stream().forEach(r -> {r.setIsFutureReq(true); General.println(r);});
			
		//add future req items to this current requirement's req items list
		int startingSize = currentReq.getSetReqItems().size();
		currentReq.getSetReqItems().addAll(futureReqItems);
		int endSize = currentReq.getSetReqItems().size();
		General.println("[Requirements] Was able to add " + (endSize - startingSize) + " future item requirements. Now we have " + endSize + " total requirements.");
	}
	
	public List<Mission> getPreReqMissions()
	{
		return preReqMissions;
	}
}
