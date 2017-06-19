package scripts.fc.framework.quest;

import org.tribot.api2007.Game;

public class SettingBool extends QuestBool
{
	private int index;
	private int setting;
	private Order order;
	
	public SettingBool(int index, int setting, boolean normal, Order order)
	{
		super(normal);
		this.index = index;
		this.setting = setting;
		this.order = order;
	}
	

	@Override
	public boolean value()
	{
		return order.evaluate(Game.getSetting(index), setting);
	}

}
