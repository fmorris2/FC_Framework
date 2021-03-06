package scripts.fc.framework.task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Task - Represents a specific task for the bot to do. Ex. Banking, Chopping, Mining.
 * 
 * @author Worthy - Modifications by Final Calibur
 *
 */
public abstract class Task implements Serializable
{	
	private static final long serialVersionUID = 1L;
	
	public final Map<String, Boolean> FLAGS = new HashMap<>(); 

	/**
	 * Executes / processes the task
	 * 
	 * @return true if the task executed successfully, false if otherwise
	 */
	public abstract boolean execute();
		
	/**
	 * Determines whether or not the task should be executed
	 * 
	 * @return true if the task should be executed, false if otherwise
 	 */
	public abstract boolean shouldExecute();
		
	/**
	 * Generates the status associated with this task
	 * 
	 * @return The paint status associated with this task
	 */
	public abstract String getStatus();
}
