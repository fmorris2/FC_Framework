package scripts.fc.api.utils;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSPlayer;

public class NpcUtils
{	
	public static int getInteractingNpcId()
	{
		RSPlayer player = Player.getRSPlayer();
		if(player == null) return -1;
		
		RSCharacter interacting = player.getInteractingCharacter();
		if(interacting == null || !(interacting instanceof RSNPC)) return -1;
		
		int id = ((RSNPC)interacting).getID();
		
		return id;
	}
}
