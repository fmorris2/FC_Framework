package scripts.fc.api.interaction.impl.players;

import org.tribot.api.DynamicClicking;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.types.RSMenuNode;

import scripts.fc.api.interaction.PlayerInteraction;

public class ClickPlayer extends PlayerInteraction
{
	private Filter<RSMenuNode> MENU_FILTER = menuFilter();
	
	public ClickPlayer(String action, String name, int searchDistance)
	{
		super(action, name, searchDistance);
	}

	@Override
	protected boolean interact()
	{
		return DynamicClicking.clickRSNPC(player, MENU_FILTER);
	}

	private Filter<RSMenuNode> menuFilter()
	{
		return new Filter<RSMenuNode>()
		{
			@Override
			public boolean accept(RSMenuNode o)
			{
				return o.correlatesTo(player) && o.contains(action);
			}
			
		};
	}
}
