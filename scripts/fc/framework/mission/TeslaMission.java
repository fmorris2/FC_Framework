package scripts.fc.framework.mission;

public interface TeslaMission extends GoalMission
{
	public boolean needsToMule();
	
	public void resetOrder();
	
	public int[] getOrderItems();
}
