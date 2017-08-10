package scripts.fc.framework.task;

import java.util.List;


public abstract class ParentTask extends Task
{
	private static final long serialVersionUID = 3986957991804689745L;
	protected List<Task> childrenTasks;
	protected Task currentTask;
	
	public ParentTask()
	{
		super();
		childrenTasks = compileChildrenTasks();
	}
	
	protected abstract List<Task> compileChildrenTasks();
	
	@Override
	public boolean execute()
	{
		 currentTask = childrenTasks.stream()
				.filter(t -> t.shouldExecute())
				.findFirst()
				.orElse(null);
		 
		 return currentTask == null ? false : currentTask.execute();
	}
	
	@Override
	public String getStatus()
	{
		return currentTask == null ? this.getClass().getSimpleName() : currentTask.getStatus();
	}
	
}
