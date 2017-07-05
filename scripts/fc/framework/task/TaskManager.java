package scripts.fc.framework.task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.WebWalking;

import scripts.fc.api.banking.FCBanking;
import scripts.fc.api.generic.FCConditions;
import scripts.fc.api.interaction.EntityInteraction;
import scripts.fc.api.interaction.ItemInteraction;
import scripts.fc.api.items.FCItem;
import scripts.fc.api.items.FCItemList;
import scripts.fc.api.travel.Travel;
import scripts.fc.api.wrappers.FCTiming;
import scripts.fc.framework.goal.GoalManager;
import scripts.fc.framework.script.FCScript;

/**
 * This class manages the tasks a script has. It handles
 * the creation of the task list as well as the execution of it.
 * 
 * @author Freddy
 *
 */
public abstract class TaskManager extends GoalManager
{		
	protected List<Task> tasks;
	protected Task currentTask;
	protected boolean running = true;
	public transient FCScript fcScript;
	
	public TaskManager(FCScript script)
	{
		this.tasks = getTaskList();
		this.fcScript = script;
	}

	public TaskManager(){} //for serialization / deserialization

	public boolean executeTasks()
	{
		if(!running)
		{
			fcScript.setIsRunning(false);
			return false;
		}
		
		if(tasks == null)
			return false;
		
		//grab starting task
		currentTask = findExecutableTask();
		
		synchronized(tasks)
		{
			boolean success = false;
			
			Task oldTask = currentTask;
			
			if(currentTask == null)
				return false;
			
			//check if this task requires a certain amount of inventory space
			if(currentTask instanceof SpaceRequiredTask && !handleSpaceRequiredTask())
				return false;
			
			//check if this task requires items (used for quest stages)
			if(currentTask instanceof ItemsRequiredTask && !handleItemsRequiredTask())
				return false;
			
			//handle appropriate task type
			if(isAnticipativeTask(currentTask))
				success = handleAnticipativeTask((AnticipativeTask)currentTask);
			else
				success = handleNormalTask(currentTask);
			
			if(oldTask != currentTask)
				resetAbc2();
			
			return success;
		}
	}
	
	private boolean handleSpaceRequiredTask()
	{
		SpaceRequiredTask t = (SpaceRequiredTask)currentTask;
		if(currentTask.FLAGS.get("hasMadeSpace"+currentTask) != null || (28 - Inventory.getAll().length) >= t.getSpaceRequired())
			return true;
		
		General.println("Creating inventory space for task");
		
		if(!Banking.isInBank())
		{
			if(Travel.walkToBank() && !FCTiming.waitCondition(() -> Banking.isInBank(), 3500))
				WebWalking.walkToBank();
		}
		else if(Banking.isBankScreenOpen() || (Banking.openBank() && Timing.waitCondition(FCConditions.BANK_LOADED_CONDITION, 3500)))
			if(Banking.depositAll() > 0 && FCTiming.waitCondition(() -> (28 - Inventory.getAll().length) >= t.getSpaceRequired(), 2400))
				currentTask.FLAGS.put("hasMadeSpace"+currentTask, true);
			
		return false;
	}
	
	private boolean handleItemsRequiredTask()
	{
		//first, check if we have the items in our inventory
		ItemsRequiredTask reqTask = (ItemsRequiredTask)currentTask;
		FCItem[] requiredItems = reqTask.getRequiredItems();
		if(Arrays.stream(requiredItems).allMatch(req -> req.getInvCount(true) >= req.getAmt()))
		{
			General.println("We have the required items in our inventory!");
			return true;
		}
		
		return getRequiredItems(requiredItems);
	}
	
	private boolean getRequiredItems(FCItem[] reqItems)
	{
		General.println("Getting required items for task");
		boolean hasCheckedBank = fcScript.BANK_OBSERVER.hasCheckedBank;
		
		if(!Banking.isInBank())
			Travel.walkToBank();
		else if(Banking.isBankScreenOpen() || (Banking.openBank() && Timing.waitCondition(FCConditions.BANK_LOADED_CONDITION, 3500)))
		{
			if(!hasCheckedBank) //we won't immediately end the script if the bank observer hasn't been loaded yet
				return false;
			
			//check if we don't have one of the required items
			if(Arrays.stream(reqItems).anyMatch(req -> (req.getInvCount(true) + FCBanking.getAmount(req.getIds()[0]) < req.getAmt()) && req.isRequired()))
			{
				General.println("We don't have all of the required materials on the character!");
				running = false;
				return false;
			}
			
			return FCBanking.withdraw(new FCItemList(reqItems));
		}
			
		return false;
	}
	
	private boolean isAnticipativeTask(Task t)
	{
		return t instanceof AnticipativeTask;
	}
	
	private boolean handleAnticipativeTask(AnticipativeTask aT)
	{		
		boolean success = false;
		
		//execute current task
		if(aT.shouldExecute() && aT.execute())
		{
			General.println(aT.getClass().getSimpleName() + " should execute.");
			//get the task that we're anticipating....
			PredictableInteraction pI = (PredictableInteraction)aT.getNext();
			if(aT != null && pI != null)			
				success = handleTaskPreparation(aT, pI);		
		}
		
		//set next task
		currentTask = findExecutableTask();
		
		return success;
	}
	
	private boolean handleTaskPreparation(AnticipativeTask aT, PredictableInteraction pI)
	{
		boolean success = false;
		EntityInteraction interaction = pI.getInteractable();
		
		if(interaction == null)
			return false;
		
		Clickable entity = interaction.findClickable();
		
		General.println("Preparing predictable interaction for task " + aT.getStatus());
		
		if(entity == null)
		{
			General.println("handleTaskPreparation() - entity == null");
		}
		
		//ABC2 CHECK
		else if(fcScript.abc2.shouldHover() && interaction.hoverEntity())
		{
			General.println("[ABC2] Hover next anticipated");
			success = true;
			
			//check for menu open -- NOT FOR ITEM INTERACTIONS....
			if(!(interaction instanceof ItemInteraction) && fcScript.abc2.shouldOpenMenu())
			{
				if(interaction.openMenu())
					General.println("[ABC2] Opening menu on next anticipated");
				else
					success = false;
			}
		}
		
		General.println("Waiting for task to complete");
		aT.waitForTaskComplete();
		return success;
	}
	
	private void resetAbc2()
	{
		General.println("Resetting abc2 shouldHover and shouldOpenMenu");
		fcScript.abc2.resetShouldHover();
		fcScript.abc2.resetShouldOpenMenu();
	}
	
	private boolean handleNormalTask(Task t)
	{	
		boolean success = t.execute();
		currentTask = findExecutableTask();
		return success;
	}
	
	private Task findExecutableTask()
	{
		for(Task task : tasks)
		{
			if(task == null)
				continue;
			
			if(task.shouldExecute())
				return task;
		}
		
		return null;
	}
	
	public boolean removeTask(Task task)
	{
		synchronized(tasks)
		{
			return tasks.remove(task);
		}
	}
	
	public boolean addTask(Task task)
	{
		synchronized(tasks)
		{
			return tasks.add(task);
		}
	}
	
	public void setTaskList(List<Task> tasks)
	{
		synchronized(tasks)
		{
			this.tasks = tasks;
		}
	}
	
	public Task getCurrentTask()
	{
		return this.currentTask;
	}
	
	public abstract LinkedList<Task> getTaskList();

}
