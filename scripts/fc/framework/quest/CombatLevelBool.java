package scripts.fc.framework.quest;

import org.tribot.api2007.Player;

public class CombatLevelBool extends QuestBool
{
	private final int LEVEL;
	
	public CombatLevelBool(int lvl, boolean normal)
	{
		super(normal);
		LEVEL = lvl;
	}

	@Override
	public boolean value()
	{
		return Player.getRSPlayer().getCombatLevel() >= LEVEL;
	}

}
