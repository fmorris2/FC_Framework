package scripts.fc.api.skills;

public class SkillEquipment
{
	private int itemId;
	private boolean isStackable;
	private boolean isMandatory;
	
	public SkillEquipment(int itemId, boolean isStackable)
	{
		this.itemId = itemId;
		this.isStackable = isStackable;
		isMandatory = true;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public boolean isStackable()
	{
		return isStackable;
	}
	
	public boolean isMandatory()
	{
		return isMandatory;
	}
}
