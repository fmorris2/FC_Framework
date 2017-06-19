package scripts.fc.api.skills.mining.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import scripts.fc.api.skills.ProgressionType;

/**
 *	RockType - This enum holds information about the various mining rock types
 *		
 *		It holds the color of the rock, found using RSObjectDefinition#getModifiedColors
 *			(so we can find the rocks, and cache the ids)
 *
 *		It also holds the item ID of the ore (unnoted)
 *		
 *	@author Freddy
 *
 */
public enum RockType implements Serializable
{
	AUTO_SELECT(new int[]{-1}, -1, -1, ProgressionType.NOT_WORTHY),
	CLAY(new int[]{6705, 6589}, 434, 1, ProgressionType.NOT_WORTHY),
	COPPER(new int[]{4645, 3776, 4510}, 436, 1, ProgressionType.EXPERIENCE, ProgressionType.MONEY),
	TIN(new int[]{53, 57}, 438, 1, ProgressionType.EXPERIENCE),
	IRON(new int[]{2576}, 440, 15, ProgressionType.EXPERIENCE),
	SILVER(new int[]{73663}, 442, 20, ProgressionType.MONEY),
	COAL(new int[]{10508}, 453, 30, ProgressionType.MONEY),
	GOLD(new int[]{8885, 8128}, 444, 40, ProgressionType.MONEY),
	MITHRIL(new int[]{-22239}, 447, 55, ProgressionType.MONEY),
	ADAMANTITE(new int[]{21662}, 449, 70, ProgressionType.MONEY),
	RUNITE(new int[]{-31437}, 451, 85, ProgressionType.MONEY);
	
	private int[] colors;
	private int itemId;
	private int levelReq;
	private List<ProgressionType> progressionTypes;
	private HashSet<Integer> ids = new HashSet<Integer>(); //The ids hashset contains all of the found object ids for our target rocks
	
	RockType(int[] colors, int itemId, int levelReq, ProgressionType... progressions)
	{
		this.colors = colors;
		this.itemId = itemId;
		this.levelReq = levelReq;
		this.progressionTypes = Arrays.asList(progressions);
	}
	
	public int[] getColors()
	{
		return colors;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public int getLevelReq()
	{
		return levelReq;
	}
	
	public HashSet<Integer> getIds()
	{
		return ids;
	}
	
	public List<ProgressionType> getProgressionTypes()
	{
		return progressionTypes;
	}
	
	public static int[] getAllItemIds()
	{
		int[] ids = new int[values().length - 1];
		for(int i = 1; i < values().length; i++)
			ids[i - 1] = values()[i].getItemId();
		
		return ids;
	}
}
