package scripts.fc.framework.quest;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;

public class AreaBool extends QuestBool
{
	private RSArea area;
	public AreaBool(boolean normal, RSArea area)
	{
		super(normal);
		this.area = area;
	}

	@Override
	public boolean value()
	{
		return area.contains(Player.getPosition());
	}

}
