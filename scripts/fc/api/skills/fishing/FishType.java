package scripts.fc.api.skills.fishing;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.fc.api.skills.SkillEquipment;
import scripts.fc.api.utils.PriceUtils;

public enum FishType
{
	SHRIMPS(1, 317, "Net", new SkillEquipment(303, false)),
	ANCHOVIES(15, 321, "Net", new SkillEquipment(303, false)),
	TROUT(20, 335, "Lure", new SkillEquipment(309, false), new SkillEquipment(314, true)),
	SALMON(30, 331, "Lure", new SkillEquipment(309, false), new SkillEquipment(314, true)),
	LOBSTER(40, 377, "Cage", new SkillEquipment(301, false)),
	SWORDFISH(50, 371, "Harpoon", new SkillEquipment(311, false));
	
	public int reqLvl;
	public int fishId;
	public int price;
	public String action;
	public SkillEquipment[] equipment;
	
	FishType(int reqLvl, int fishId, String action, SkillEquipment... equipment)
	{
		this.reqLvl = reqLvl;
		this.equipment = equipment;
		this.fishId = fishId;
		this.action = action;
		price = PriceUtils.getPrice(fishId);
	}
	
	public static FishType getAppropriate()
	{
		final int LVL = Skills.getActualLevel(SKILLS.FISHING);
		
		for(int i = values().length - 1; i >= 0; i--)
			if(values()[i].reqLvl <= LVL)
				return values()[i];
		
		return null;
	}
	
	public int[] getEquipmentIds()
	{
		int[] ids = new int[equipment.length];
		
		for(int i = 0; i < equipment.length; i++)
			ids[i] = equipment[i].getItemId();
		
		return ids;
	}
}
