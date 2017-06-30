package scripts.fc.framework.mission;

public interface Mission
{
	public boolean hasReachedEndingCondition();
	
	public String getMissionName();
	
	public String getCurrentTaskName();
	
	public String getEndingMessage();
	
	public String[] getMissionSpecificPaint();
	
	public void execute();
	
	public void resetStatistics();
}
