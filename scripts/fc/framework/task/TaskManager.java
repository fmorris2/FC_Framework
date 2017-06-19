package scripts.fc.framework.task;

import java.util.LinkedList;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.interfaces.Clickable;

import scripts.fc.api.interaction.EntityInteraction;
import scripts.fc.api.interaction.ItemInteraction;
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
	protected boolean running;
	public transient FCScript fcScript;
	
	public TaskManager(FCScript script)
	{
		this.tasks = getTaskList();
		this.fcScript = script;
	}

	public TaskManager(){} //for serialization / deserialization

	public boolean executeTasks()
	{
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
		Clickable entity = interaction.findClickable();
		
		General.println("Preparing predictable interaction for task " + aT.getStatus());
		
		if(entity == null)
		{
			General.println("handleTaskPreparation() - entity == null");
		}
		
		//ABC2 CHECK
		else if(/*fcScript.abc2.shouldHover() && */interaction.hoverEntity())
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
