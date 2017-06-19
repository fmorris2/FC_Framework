package scripts.fc.api.skills.woodcutting.data;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

public enum Hatchet
{
	BRONZE(1351, 1, 1),
	IRON(1349, 1, 1),
	STEEL(1353, 6, 5),
	BLACK(1361, 6, 10),
	MITHRIL(1355, 21, 20),
	ADAMANT(1357, 31, 30),
	RUNE(1359, 41, 40);
	//DRAGON(6739, 61, 60);
	
	private int id;
	private int wcLvl;
	private int attLvl;
	
	Hatchet(int id, int wcLvl, int attLvl)
	{
		this.id = id;
		this.wcLvl = wcLvl;
		this.attLvl = attLvl;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getWcLvl()
	{
		return wcLvl;
	}
	
	public int attLvl()
	{
		return attLvl;
	}
	
	public static Hatchet getAppropriate()
	{
		Hatchet best = null;
		
		for(Hatchet h : values())
		{
			if(h.getWcLvl() <= Skills.getActualLevel(SKILLS.WOODCUTTING))
				best = h;
		}
		
		return best;
	}
}
