package scripts.fc.api.interaction.impl.npcs;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSNPC;

import scripts.fc.api.interaction.NpcInteraction;
import scripts.fc.api.mouse.AccurateMouse;

public class ClickNpc extends NpcInteraction
{
	private boolean useFilter;
	
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
		/*
		if(isRightClicking)
			ThreadSettings.get().setAlwaysRightClick(true);
		
		boolean b = useFilter ? DynamicClicking.clickRSNPC(npc, nodeFilter()) : DynamicClicking.clickRSNPC(npc, action);
		
		ThreadSettings.get().setAlwaysRightClick(false);
		*/
		
		
		return AccurateMouse.click(npc, action);//b;
	}
	
	private Filter<RSMenuNode> nodeFilter()
	{
		return new Filter<RSMenuNode>()
		{
			@Override
			public boolean accept(RSMenuNode node)
			{
				return node.containsAction(ClickNpc.this.action) && node.correlatesTo(ClickNpc.this.npc);
			}
			
		};
	}
	
	public void setUseFilter(boolean b)
	{
		useFilter = b;
	}

}
