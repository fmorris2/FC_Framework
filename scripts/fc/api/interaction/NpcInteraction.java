package scripts.fc.api.interaction;

import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSNPC;

import scripts.fc.api.abc.ABC2Helper;

public abstract class NpcInteraction extends EntityInteraction
{
	protected RSNPC npc;
	protected boolean isRightClicking;
	
	public NpcInteraction(String action, String name, int searchDistance)
	{
		super(action, name, searchDistance);
	}

	public NpcInteraction(String action, int id, int searchDistance)
	{
		super(action, id, searchDistance);
	}
	
	public NpcInteraction(String action, RSNPC npc)
	{
		super(action, npc);
		this.npc = npc;
		entity = npc;
		position = npc;
	}
	
	public NpcInteraction(String action, String name, boolean isRightClicking, int searchDistance)
	{
		super(action, name, searchDistance);
		this.isRightClicking = isRightClicking;
	}

	@Override
	protected abstract boolean interact();

	@Override
	protected void findEntity()
	{
		RSNPC[] npcs = null;
		
		if(id <= 0)
			npcs = NPCs.findNearest(Filters.NPCs.nameEquals(name));
		else
			npcs = NPCs.findNearest(id);
		
		if(npcs.length > 0)
		{
			npc = ABC2Helper.shouldUseClosest(abcOne, npcs) ? npcs[0] : npcs[1];
			entity = npc;
			position = npc;
		}
	}

}
