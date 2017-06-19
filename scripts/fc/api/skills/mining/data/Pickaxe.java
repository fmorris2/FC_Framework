package scripts.fc.api.skills.mining.data;

/**
 *	Pickaxe - This enum holds data for all of the various pickaxe items
 * 		Pretty self explanatory / verbose
 * 
 *	@author Freddy
 *
 */
public enum Pickaxe
{
	BRONZE(1, 1, 1265),
	IRON(1, 1, 1267),
	STEEL(6, 5, 1269),
	BLACK(11, 10, 12297),
	MITHRIL(21, 20, 1273),
	ADAMANT(31, 30, 1271),
	RUNE(41, 40, 1275),
	DRAGON(61, 60, 15259);
	
	private int miningLevel; //Required mining level to use pickaxe
	private int attackLevel; //Required attack level to wield pickaxe
	private int itemId; //The itemId for the pickaxe (unnoted)
	
	Pickaxe(int miningLevel, int attackLevel, int itemId)
	{
		this.miningLevel = miningLevel;
		this.attackLevel = attackLevel;
		this.itemId = itemId;
	}
	
	public int getMiningLevel()
	{
		return this.miningLevel;
	}
	
	public int getAttackLevel()
	{
		return this.attackLevel;
	}
	
	public int getItemId()
	{
		return this.itemId;
	}
	
	public static Pickaxe get(int itemId)
	{
		for(Pickaxe p : values())
		{
			if(p.getItemId() == itemId)
			{
				return p;
			}
		}
		
		return null;
	}
	
	public static int[] getPickIds()
	{
		int[] ids = new int[values().length];
		
		for(int i = 0; i < values().length; i++)
			ids[i] = values()[i].getItemId();
		
		return ids;
	}
}
