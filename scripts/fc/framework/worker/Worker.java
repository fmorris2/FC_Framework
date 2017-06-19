package scripts.fc.framework.worker;

public interface Worker
{
	//Performs the "task" that this worker has to do
	public void work();
	
	//Called after work(). Gets the next worker to transition to
	public Worker getNextWorker();
	
	public String getName();
}
