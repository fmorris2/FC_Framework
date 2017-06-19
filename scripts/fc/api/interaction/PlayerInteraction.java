package scripts.fc.api.interaction;

import org.tribot.api2007.Players;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSPlayer;

import scripts.fc.api.abc.ABC2Helper;

public abstract class PlayerInteraction extends EntityInteraction
{
	protected RSPlayer player;
	
	public PlayerInteraction(String action, String name, int searchDistance)
	{
		super(action, name, searchDistance);
	}

	@Override
	protected void findEntity()
	{
		RSPlayer[] players = Players.findNearest(Filters.Players.nameEquals(name));
		
		if(players.length > 0)
		{
			player = ABC2Helper.shouldUseClosest(abcOne, players) ? players[0] : players[1];
			entity = player;
			position = player;
		}
	}

}
