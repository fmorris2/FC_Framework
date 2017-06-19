package scripts.fc.framework.quest;

import org.tribot.api2007.Player;

public class PlaneBool extends QuestBool
{
	private int plane;
	private Order order;
	
	public PlaneBool(boolean normal, int plane, Order order)
	{
		super(normal);
		this.plane = plane;
		this.order = order;
	}

	@Override
	public boolean value()
	{
		return order.evaluate(Player.getPosition().getPlane(), plane);
	}

}
