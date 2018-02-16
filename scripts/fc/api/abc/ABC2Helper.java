package scripts.fc.api.abc;

import java.util.LinkedList;
import java.util.Queue;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;

import scripts.fc.framework.data.Vars;

/*
 *	Things that need to be done for ABC2 to be implemented:
 *		-Construct an ABC2Helper object in the mission
 *		-At the start of gathering, call the startGathering method, and tell it whether or not it needs to do the abc delay
 *		-While gathering, call the doActions method, and provide it with the next potential gathering node (or null, if it's a combat script, for example)
 *		-When done gathering, call the endGathering method
 *		-When there are no resources available, call moveToAnticipated
 *		-Whenever you need to find the next target, call selectNextTarget
 */
public class ABC2Helper
{
	private static final long ESTIMATED_GATHERING_TIME = 5000;
	private static final long ITEM_INTERACTION_TIME_THRESH = 3000; //if items are interacted with within 3 seconds of another, we could possibly delay after the interaction
	
	private static final int USE_CLOSEST_THRESH = 3;
	
	private PersistantABCUtil abc2;
	private Queue<Positionable> positions;
	private int totalAttemptCount;
	private long totalGatheringTime;
	
	private long currentGatheringStart = -1;
	private long lastItemInteraction = -1;
	
	private boolean hasCheckedMoveToAnticipated;
	private boolean hasHovered;
	
	public ABC2Helper()
	{
		abc2 = Vars.get().get("abc2");
		this.positions = new LinkedList<>();
	}
	
	/*********************************** START PUBLIC INTERFACE ***********************************/
	public void startGathering(boolean needsToWait)
	{	
		final long EST_WAIT = getEstimatedWait();
		
		if(needsToWait) //We don't need to do the ABC delay if we're coming back from the bank for example, or we failed clicking on a resource before this
		{
			final long REACTION_TIME = abc2.generateReactionTime(EST_WAIT);
			log("EST WAIT: " + EST_WAIT + ", REACTION TIME: " + REACTION_TIME);
			
			abc2.generateTrackers(EST_WAIT);
			abcLeaveGame();
			General.sleep(REACTION_TIME);
			abc2.generateTrackers(EST_WAIT);
		}
		
		hasHovered = false;
		currentGatheringStart = Timing.currentTimeMillis();		
		abc2.generateTrackers(EST_WAIT);
		resetAbc();
	}
	
	public void endGathering()
	{
		if(currentGatheringStart == -1)
			return;
		
		totalAttemptCount++;
		totalGatheringTime += Timing.timeFromMark(currentGatheringStart);
	}
	
	public void doActions(Positionable next)
	{
		if(!Vars.get().get("abc2Enabled", true)) {
			return;
		}
		
		abcLeaveGame();
			
		if(abc2.performTimedActions())
			log("Performing timed actions");
			
		if(!hasHovered && hoverNextResource(next))
			log("Hover next resource");
	}
	
	public void moveToAnticipated()
	{
		hasCheckedMoveToAnticipated = true;
		
		if(!hasCheckedMoveToAnticipated && abc2.shouldMoveToAnticipated())
			handleMoving();
	}
	
	public int selectNextTarget(Positionable[] p)
	{
		if(p == null)
			return 0;
		
		Positionable pos = abc2.selectNextTarget(p);
		
		for(int i = 0; i < p.length; i++)
			if(pos.equals(p[i]))
				return i;
		
		return 0;
	}
	
	public static boolean shouldUseClosest(ABCUtil abcOne, Positionable[] objs)
	{
		if(!Vars.get().get("abc2Enabled", true)) {
			return true;
		}
		
		if(objs.length < 2) //If there are not multiple objects to choose from
			return true;
		
		if(abcOne == null)
			return true; //Not using abc
		
		if(abcOne.BOOL_TRACKER.USE_CLOSEST.next() && Player.getPosition().distanceTo(objs[1]) < USE_CLOSEST_THRESH)
		{
			General.println("[ABC] Not using closest object");
			return false;
		}
		
		return true;
	}
	
	/*********************************** END PUBLIC INTERFACE ***********************************/
	
	
	/*********************************** START PRIVATE INTERFACE ***********************************/
	
	private void resetAbc()
	{
		abc2.resetShouldHover();	
		abc2.resetShouldOpenMenu();
		hasCheckedMoveToAnticipated = false;
	}
	
	private void abcLeaveGame()
	{
		if(!Vars.get().get("abc2Enabled", true)) {
			return;
		}
		
		if(abc2.shouldLeaveGame())
		{
			log("Leaving game");
			abc2.leaveGame();
		}
	}
	
	
	private void handleMoving()
	{
		if(positions.isEmpty())
			return;
		
		log("Moving to next anticipated resource");
		
		Positionable p = positions.poll();
		
		if(Player.getPosition().distanceTo(p) < 8)
		{
			if(!p.getPosition().isOnScreen())
				Camera.turnToTile(p);
			
			Walking.walkScreenPath(Walking.generateStraightScreenPath(p));
		}
		else if(Player.getPosition().distanceTo(p) < 13)
			Walking.blindWalkTo(p);
		
		positions.add(p);
	}
	
	private long getEstimatedWait()
	{
		long est = totalAttemptCount > 0 ? (int)Math.round(((double)totalGatheringTime / totalAttemptCount)) : ESTIMATED_GATHERING_TIME;
		
		return est < 0 ? ESTIMATED_GATHERING_TIME : est;
	}
	
	private boolean hoverNextResource(Positionable next)
	{
		if(next == null)
			return false;
		
		if(!positions.contains(next))
			positions.add(next);
		
		if(abc2.shouldHover())
		{	
			hasHovered = true;
			return next.getAnimablePosition().hover();
		}
		
		
		return false;
	}
	
	private void log(String s)
	{
		General.println("[ABC2]: " + s);
	}
	
	/*********************************** END PRIVATE INTERFACE ***********************************/
}
