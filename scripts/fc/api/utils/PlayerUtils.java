package scripts.fc.api.utils;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSPlayer;

public class PlayerUtils
{
	public static String getInteractingCharacterName()
	{
		RSPlayer player = Player.getRSPlayer();
		if(player == null) return null;
		
		RSCharacter interacting = player.getInteractingCharacter();
		if(interacting == null) return null;
		
		String name = interacting.getName();
		return name;
	}
}
