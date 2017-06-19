package scripts.fc.framework.task;

/**
 * This class represents a Task which is anticipative in the respect
 * that it knows what Task it will need to execute next. An example
 * use case of this is a Tutorial Island task, where everything is
 * done in a linear fashion.
 * 
 * @author Final Calibur
 *
 */
public abstract class AnticipativeTask extends Task
{
	private static final long serialVersionUID = 2000257624147266884L;

	/**
	 * Provides the task that we will have to execute / prepare for immediately
	 * after this one
	 * 
	 * @return the next task to be executed / prepared for immediately after this one
	 */
	public abstract Task getNext();
	
	/**
	 * This method will be called after we prepare the
	 * interaction with the next entity.
	 * 
	 * Since I like to use conditional sleeps to verify
	 * that a task has completed before moving on / running
	 * the task again, we can no longer do that in the Task's
	 * execute() method. This is due to the fact that execute() is called,
	 * and then we prepare the next interaction if necessary. So if we wait
	 * in execute(), there is no point in preparing the interaction after that,
	 * since the next interaction will already be ready to execute.
	 */
	public abstract void waitForTaskComplete();
}
