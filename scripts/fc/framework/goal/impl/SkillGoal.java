package scripts.fc.framework.goal.impl;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.fc.framework.goal.Goal;

public class SkillGoal implements Goal
{	
	private static final long serialVersionUID = 1955082994625550179L;
	
	private transient SKILLS skill;
	private int skillIndex;
	private int goal;
	
	public SkillGoal(SKILLS skill, int goal)
	{
		this.skill = skill;
		this.skillIndex = skill.ordinal();
		this.goal = goal;
	}

	@Override
	public boolean hasReached()
	{
		return Skills.getActualLevel(skill) >= goal;
	}

	@Override
	public String getCompletionMessage()
	{
		return "Goal Complete: Achieve " + goal + " " + getSkill().name().toLowerCase();
	}

	@Override
	public String getName()
	{
		return goal + " " + getSkill().name().toLowerCase() + " (" + Skills.getActualLevel(skill) + ")";
	}
	
	public String toString()
	{
		return goal + " " + getSkill().name().toLowerCase();
	}
	
	public SKILLS getSkill()
	{
		if(skill == null)
			skill = SKILLS.values()[skillIndex];
		
		return skill;
	}
	
	public int getGoal()
	{
		return goal;
	}

}
