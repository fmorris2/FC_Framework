package scripts.fc.framework.goal;

import java.util.ArrayList;

import org.tribot.api.General;

import scripts.fc.framework.goal.impl.ResourceGoal;
import scripts.fc.framework.goal.impl.TimeGoal;

public class GoalList extends ArrayList<Goal>
{
	private static final long serialVersionUID = -6247167564246021589L;
	
	public GoalList(Goal... goals)
	{
		for(Goal g : goals)
			add(g);
	}
	
	public void updateResourceGoals(int id, int count)
	{
		for(Goal g : this)
			if(g instanceof ResourceGoal && ((ResourceGoal)g).containsId(id))
				((ResourceGoal)g).update(count);	
	}
	
	public boolean hasReachedGoals()
	{	
		for(int i = 0; i < size(); i++)
		{
			if(get(i).hasReached())
			{
				General.println(get(i).getCompletionMessage());
				remove(i);
			}
		}	
		return isEmpty();
	}
	
	public void resetTimeGoals()
	{
		for(Goal g : this)
			if(g instanceof TimeGoal)
				((TimeGoal)g).reset();
	}
	
	public String getRunningGoals()
	{
		String str = "";
		for(Goal g : this)
			str += "["+g.getName()+"]";
		
		return str;
	}
	
	public String toString()
	{
		String str = "";
		for(Goal g : this)
			str += "["+g+"]";
		
		return str;
	}
}
