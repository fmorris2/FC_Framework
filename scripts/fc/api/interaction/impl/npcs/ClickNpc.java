package scripts.fc.api.interaction.impl.npcs;

import org.tribot.api2007.types.RSNPC;

import scripts.fc.api.interaction.NpcInteraction;
import scripts.fc.api.mouse.AccurateMouse;

public class ClickNpc extends NpcInteraction
{	
	public ClickNpc(String action, String name, int searchDistance)
	{
		super(action, name, searchDistance);
	}
	
	public ClickNpc(String action, int id, int searchDistance)
	{
		super(action, id, searchDistance);
	}
	
	public ClickNpc(String action, RSNPC npc)
	{
		super(action, npc);
	}
	
	public ClickNpc(String action, String npc, boolean rightClick, int searchDist)
	{
		super(action, npc, rightClick, searchDist);
	}
	
	@Override
	protected boolean interact()
	{
		return AccurateMouse.click(npc, action);
	}

}
